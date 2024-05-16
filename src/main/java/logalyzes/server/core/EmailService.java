package logalyzes.server.core;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import logalyzes.server.templates.MailTemplate;
import logalyzes.server.utils.Config;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailService {

    private static volatile EmailService instance;
    private final Properties properties;
    private final String mailFrom;
    private final String username;
    private final String password;
    private final ExecutorService executorService;

    private final Configuration freemarkerConfig;


    private EmailService() {
        String host = Config.MAIL_HOST;
        String port = Config.MAIL_PORT;
        username = Config.MAIL_USERNAME;
        password = Config.MAIL_PASSWORD;
        mailFrom = Config.MAIL_FROM;

        properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        executorService = Executors.newCachedThreadPool();


        // FreeMarker configuration
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }


    public static EmailService getInstance(){
        if(instance == null){
            synchronized ( EmailService.class){
                if(instance == null){
                    instance = new EmailService();
                }
            }
        }
        return  instance;
    }


    public void sendEmail(String toAddress, String subject, String message) {
        executorService.submit(() -> {
            try {
                Session session = Session.getInstance(properties, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(mailFrom));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
                msg.setSubject(subject);
                msg.setSentDate(new Date());
                msg.setText(message);

                Transport.send(msg);
                System.out.println("Email sent successfully to " + toAddress);

            } catch (MessagingException e) {
                e.printStackTrace();
                System.out.println("Failed to send email.");
            }
        });
    }


    public void sendEmail(String toAddress, String subject, MailTemplate temp) {
        executorService.submit(() -> {
            try {
                Session session = Session.getInstance(properties, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(mailFrom));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
                msg.setSubject(subject);
                msg.setSentDate(new Date());
                msg.setText(temp.toString());

                Transport.send(msg);
                System.out.println("Email sent successfully to " + toAddress);

            } catch (MessagingException e) {
                e.printStackTrace();
                System.out.println("Failed to send email.");
            }
        });
    }




}
