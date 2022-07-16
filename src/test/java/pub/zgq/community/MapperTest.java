package pub.zgq.community;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pub.zgq.community.dao.DiscussPostMapper;
import pub.zgq.community.dao.LoginTicketMapper;
import pub.zgq.community.dao.MessageMapper;
import pub.zgq.community.dao.UserMapper;
import pub.zgq.community.entity.DiscussPost;
import pub.zgq.community.entity.LoginTicket;
import pub.zgq.community.entity.Message;
import pub.zgq.community.entity.User;
import pub.zgq.community.util.CommunityConstant;

/**
 * @Author 孑然
 */
@SpringBootTest
public class MapperTest implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        //user.setId(0);
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcodder/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://www.nowcodder/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"654321");
        System.out.println(rows);

    }

    @Test
    public void testSelectDiscussPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        int res = loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

    }

    @Test
    public void testExpired(){
        int default_epired = DEFAULT_EXPIRED_SECONDS;
        int remember_expired = REMEMBERME_EXPIRED_SECONDS;
        //                                  毫秒                      秒*1000
        System.out.println(new Date(System.currentTimeMillis() + remember_expired * 1000));
        System.out.println(new Date((System.currentTimeMillis() + remember_expired) * 1000));
        System.out.println(new Date(System.currentTimeMillis() + remember_expired ));
        System.out.println(new Date(System.currentTimeMillis() * 1000 ));
        System.out.println(new Date(System.currentTimeMillis() ));
        System.out.println(default_epired);
        System.out.println(remember_expired);
    }

    @Test
    public void testConversations() {
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> list1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list1) {
            System.out.println(message);
        }

        System.out.println(messageMapper.selectLettersCount("111_112"));

        int count1 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count1);
    }
}
