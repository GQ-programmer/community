package pub.zgq.community.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @Author 孑然
 *
 * 发送邮件类
 */
@Component
public class MailClient {
    public static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;



    /**
     * 发送邮件
     * @param to 发送目标
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    public void sendMail(String to, String subject, String content) throws MessagingException {

            // 构建邮件
            MimeMessage message = mailSender.createMimeMessage();

            //使用邮件助手构建邮件内容
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            //发送邮件
            mailSender.send(helper.getMimeMessage());

    }
}
