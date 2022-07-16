package pub.zgq.community.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pub.zgq.community.dao.LoginTicketMapper;
import pub.zgq.community.dao.UserMapper;
import pub.zgq.community.entity.LoginTicket;
import pub.zgq.community.entity.User;
import pub.zgq.community.util.CommunityConstant;
import pub.zgq.community.util.CommunityUtil;
import pub.zgq.community.util.MailClient;
import pub.zgq.community.util.RedisKeyUtil;


import javax.mail.MessagingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author 孑然
 */
@Service
public class UserService implements CommunityConstant {

    public static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper  loginTicketMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 项目路径
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 域名（IP）
     * @param id
     * @return
     */
    @Value("${community.path.domain}")
    private String domain;

    public User findUserById(int id){
        return userMapper.selectById(id);
        // 先从redis中获取
        //User user = getCache(id);
        //if (user == null) {
        //    //初始化数据进redis
        //    user = initCache(id);
        //}
        //return user;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Map<String, Object> register(User user) throws Exception {
        Map<String, Object> map = new HashMap<>();

        // 控制处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 判断传入值的合法性
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("usernameMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("usernameMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号是否存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }
        // 验证邮箱是否已注册
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已注册!");
            return map;
        }

        // 注册用户
        // 生成 盐
        String salt = CommunityUtil.generateUUID().substring(0, 5);
        user.setSalt(salt);
        user.setPassword(CommunityUtil.md5(user.getPassword() + salt));
        // 默认用户
        user.setType(0);
        // 状态未激活
        user.setStatus(0);
        // 设置激活码
        user.setActivationcode(CommunityUtil.generateUUID());
        // 设置默认头像
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        // 先发送邮箱，后注册（可能存在无效邮箱）
        userMapper.insertUser(user);
        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 激活邮件点击地址
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationcode();
        context.setVariable("url", url);

        // 生成邮件内容
        String content = templateEngine.process("/mail/activation", context);

        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;


    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user == null){
            // 激活失败
            return ACTIVATION_FAILURE;
        }
        if (user.getStatus() == 1) {
            //重复激活
            return ACTIVATION_REPEAT;
        }
        if (user.getActivationcode().equals(code)){
            //激活成功
            userMapper.updateStatus(userId, 1);
            // 清理User缓存
            //clearCache(userId);
            return ACTIVATION_SUCCESS;
        }
        // 激活失败
        return ACTIVATION_FAILURE;

    }

    public Map<String, Object> login(String username, String password, long expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        // 查询验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!password.equals(user.getPassword())) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        // 当前时间 + 往后推移时间秒数     expiredSeconds 注意使用 long类型， 否则会操过int范围
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        // 存入数据库中
        loginTicketMapper.insertLoginTicket(loginTicket);

        // 存入redis中
        //String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        //redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;

    }

    /**
     * 退出登录
     * @param ticket
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);

        // 改为redis
        //String redisKey = RedisKeyUtil.getTicketKey(ticket);
        //// 更改当前ticke的状态 (失效)
        //LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        //loginTicket.setStatus(1);
        //redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    /**
     * 查询凭证
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
        // 改为redis
        //String redisKey = RedisKeyUtil.getTicketKey(ticket);
        //return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        // 清理缓存
        //clearCache(userId);
        return rows;
    }

    public Map<String, Object> updatePassword(User user, String newPassword, String oldPassword) {
        Map<String, Object> map = new HashMap<>();
        // 验证原密码正确性
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!oldPassword.equals(user.getPassword())) {
            // 输入原密码有误
            map.put("oldPasswordMsg", "原密码不正确!");
            return map;
        }
        // 加密新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(user.getId(), newPassword);
        // 清理缓存
        //clearCache(user.getId());
        return map;
    }

    public User findUserByUsername(String username) {
        return userMapper.selectByName(username);
    }

    /**
     * 优先 根据id从缓存中取User
     * @param userId
     * @return
     */
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 取不到时初始化缓存数据
     * @param userId
     * @return
     */
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 数据变更时，清除缓存数据
     * @param userId
     */
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
