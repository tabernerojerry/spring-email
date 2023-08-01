package me.tabernerojerry.service.impl;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.tabernerojerry.service.IEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import static me.tabernerojerry.utils.EmailUtils.getEmailMessage;
import static me.tabernerojerry.utils.EmailUtils.getVerificationUrl;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String EMAIL_TEMPLATE = "emailtemplate";
    public static final String TEXT_HTML_ENCODING = "text/html";

    @Value("${spring.mail.verify.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Override
    @Async // Run the method into separate thread make sure to add the @EnableAsync annotation in application
    public void sendSimpleMailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(getEmailMessage(name, host, token));
            mailSender.send(message);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithAttachments(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(getEmailMessage(name, host, token));

            // Add the Attachments
            FileSystemResource imageGif = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/attachment-test-files/test.gif"));
            FileSystemResource textFile = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/attachment-test-files/test.txt"));

            helper.addAttachment(imageGif.getFilename(), imageGif);
            helper.addAttachment(textFile.getFilename(), textFile);

            mailSender.send(message);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(getEmailMessage(name, host, token));

            // Add the Attachments
            FileSystemResource imageGif = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/attachment-test-files/test.gif"));
            FileSystemResource textFile = new FileSystemResource(new File(System.getProperty("user.home") + "/Downloads/attachment-test-files/test.txt"));

            helper.addInline(getContentId(imageGif.getFilename()), imageGif);
            helper.addInline(getContentId(textFile.getFilename()), textFile);

            mailSender.send(message);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmail(String name, String to, String token) {
        try {
            Context context = new Context();
//            context.setVariable("name", name);
//            context.setVariable("url", getVerificationUrl(host, token));
            context.setVariables(Map.of(
                    "name", name,
                    "url", getVerificationUrl(host, token)
            ));

            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);

            Context context = new Context();
            context.setVariables(Map.of(
                    "name", name,
                    "url", getVerificationUrl(host, token)
            ));

            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            // Add HTML Email Body
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, TEXT_HTML_ENCODING);
            mimeMultipart.addBodyPart(messageBodyPart);

            // Add images to the email body
            BodyPart imageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(System.getProperty("user.home") + "/Downloads/attachment-test-files/verification.jpg");
            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart(imageBodyPart);

            message.setContent(mimeMultipart);

            mailSender.send(message);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }

    private String getContentId(String fileName) {
        return MessageFormat.format("<{0}>", fileName);
    }
}
