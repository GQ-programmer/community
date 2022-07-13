package pub.zgq.community.service;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import pub.zgq.community.dao.DiscussPostMapper;
import pub.zgq.community.dao.UserMapper;
import pub.zgq.community.entity.DiscussPost;
import pub.zgq.community.entity.User;
import pub.zgq.community.util.CommunityUtil;

/**
 * @Author 孑然
 */
@Service
public class AlphaService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    //- REQUIRED：若A有事务，则B就在这个事务中运行，否则创建新事务
    //- REQUIRES：NEW：无论A是否有事务，B都会创建新事务，若A有事务，A事务将会挂起
    //- NESTED：若A有事务，则B会创建事务嵌套在A事务中执行，若无，也会创建新事务

    /**
     * 模拟每新增一个用户，就自动发一个帖子（构成事务）
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,3));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl("fsadlkfjlskd.jpg");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost pos = new DiscussPost();
        pos.setUserId(user.getId());
        pos.setTitle("hello");
        pos.setContent("新人报道!");
        pos.setCreateTime(new Date());

        discussPostMapper.insertDiscussPost(pos);

        // 模拟出错
        Integer.valueOf("abc");

        return "ok";
    }

    /**
     * 使用编程式事务
     */
    public Object save2() {
        // 设置 隔离级别
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置 传播机制
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {

                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,3));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("alpha@qq.com");
                user.setType(0);
                user.setStatus(0);
                user.setHeaderUrl("fsadlkfjlskd.jpg");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost pos = new DiscussPost();
                pos.setUserId(user.getId());
                pos.setTitle("hello");
                pos.setContent("新人报道!");
                pos.setCreateTime(new Date());

                discussPostMapper.insertDiscussPost(pos);

                // 模拟出错
                Integer.valueOf("abc");

                return "ok";
            }
        });
    }
}
