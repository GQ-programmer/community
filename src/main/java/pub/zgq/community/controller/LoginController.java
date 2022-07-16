package pub.zgq.community.controller;

import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pub.zgq.community.entity.User;
import pub.zgq.community.service.UserService;
import pub.zgq.community.util.CommunityConstant;
import pub.zgq.community.util.CommunityUtil;
import pub.zgq.community.util.RedisKeyUtil;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author 孑然
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 跳转注册页面
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegister() {
        return "/site/register";
    }

    /**
     * 跳转登录页面
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getloginPage() {
        return "/site/login";
    }

    /**
     * 注册方法
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        String password = user.getPassword();
        Map<String, Object> map = null;
        try {
            map = userService.register(user);
        } catch (Exception e) {
            model.addAttribute("emailMsg", "注册失败，邮箱无效！");
            // 恢复user中的密码
            user.setPassword(password);
            e.printStackTrace();
            logger.error("注册失败，邮箱可能无效！" + e.getMessage());
            return "/site/register";
        }
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg","注册成功, 我们已经向您的邮箱发送了一封激活邮件，请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 激活邮箱方法
     * @param model
     * @param userId
     * @param code
     * @return
     */
    // http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable String code){
        int res = userService.activation(userId, code);
        if (res == ACTIVATION_SUCCESS) {
            model.addAttribute("msg","激活成功, 您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (res == ACTIVATION_REPEAT) {
            model.addAttribute("msg","无效操作, 该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg","激活失败, 您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**
     * 生成验证码方法
     * @param response
     * @param session
     */
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码内容存入session中
        session.setAttribute("kaptcha", text);
        System.out.println(session.getAttribute("kaptcha"));

        //// 将验证码存入redis中
        //// 生成验证码的归属标记
        //String kaptchaOwner = CommunityUtil.generateUUID();
        //Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        //cookie.setMaxAge(60);
        //cookie.setPath(contextPath);
        //response.addCookie(cookie);
        //
        //// 传入redis中 过期时间60秒
        //// 根据归属标记生成key
        //String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        //redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }

    }
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(@CookieValue(value = "kaptchaOwner",required = false) String kaptchaOwner, String username, String password, String code, boolean rememberme,
                        Model model,HttpSession session, HttpServletResponse response) {
        // 从Session中获取验证码
        String kaptcha = (String) session.getAttribute("kaptcha");

        // 优化 从redis中获取验证码
        // 从cookie中获取验证码归属标记 kaptchaOwner
        // 获得key
        //String kaptcha = null;
        //if (StringUtils.isNotBlank(kaptchaOwner)) {
        //    String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        //    kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        //}
        // 判断验证码
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        // 判断账号，密码(登录)
        int expiredSeconds = rememberme ? REMEMBERME_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        if (map.containsKey("ticket")) {
            // 登录成功
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            // 账号密码有误
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }



    //// cookie示例
    //@RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    //@ResponseBody
    //public String setCookie(HttpServletResponse response){
    //    // 创建Cookie
    //    Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
    //    // 设置Cookie生效的范围
    //    cookie.setPath("/community/cookie");
    //    // 设置 cookie的生存时间
    //    cookie.setMaxAge(60 * 10);
    //    // 发送cookie
    //    response.addCookie(cookie);
    //    return "set cookie";
    //}
    //
    //// 获取Cookie
    //
    //@RequestMapping(path = "/cookie/get")
    //@ResponseBody
    //public String getCookie(@CookieValue("code") String code){
    //    System.out.println(code);
    //    return "get cookie" ;
    //}
    //
    //// session示例
    //@RequestMapping(path = "/session/set", method = RequestMethod.GET)
    //@ResponseBody
    //public String setSession(HttpSession session){
    //    session.setAttribute("id", 1);
    //    session.setAttribute("name", "Test");
    //    return "set sesion";
    //}
    //
    //// session示例
    //@RequestMapping(path = "/session/get", method = RequestMethod.GET)
    //@ResponseBody
    //public String getSession(HttpSession session){
    //    System.out.println(session.getAttribute("id"));
    //    System.out.println(session.getAttribute("name"));
    //    return "set sesion";
    //}
}
