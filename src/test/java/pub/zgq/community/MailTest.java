package pub.zgq.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pub.zgq.community.util.MailClient;

import javax.annotation.Resource;


/**
 * @Author 孑然
 */
@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    /**
     * Thymeleaf模板引擎
     */
    @Resource
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("3213821843@qq.com", "TEST", "你好啊！");
    }

    @Test
    public void testHtmlMail() {
        //向html中传入数据
        Context context = new Context();
        context.setVariable("username", "sunday");

        //使用Thymeleaf模板引擎  渲染数据 并格式化邮件内容
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        //发送邮件
        mailClient.sendMail("gq_2002@qq.com", "html邮件", content);

    }

}
