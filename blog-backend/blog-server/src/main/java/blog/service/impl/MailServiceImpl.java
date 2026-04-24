package blog.service.impl;

import blog.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 邮件通知服务实现
 *
 * 使用 Spring Boot Mail 发送纯文本通知邮件
 * 通过 @Async 异步发送，不阻塞评论提交主流程
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${blog.owner.nickname:博主}")
    private String blogName;

    @Override
    @Async
    public void sendReplyNotification(String toEmail, String toNickname,
                                       String fromNickname, String replyContent,
                                       String articleTitle) {
        if (mailSender == null) {
            log.warn("JavaMailSender 未配置，跳过邮件发送");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("【" + blogName + "的博客】您收到了一条新回复");

        String text = String.format(
                "%s，您好！\n\n" +
                "在文章「%s」中，%s 回复了您的评论：\n\n" +
                "%s\n\n" +
                "—— 来自 %s 的博客",
                toNickname, articleTitle, fromNickname, replyContent, blogName
        );
        message.setText(text);

        try {
            mailSender.send(message);
            log.info("回复通知邮件已发送至: {}", toEmail);
        } catch (Exception e) {
            log.error("邮件发送失败: to={}, error={}", toEmail, e.getMessage());
        }
    }
}
