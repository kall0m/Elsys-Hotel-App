package hotelapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService {
    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        mailSender.send(email);
    }

    public SimpleMailMessage createEmail(String receiver, String subject, String content, String sender) {
        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(receiver);
        email.setSubject(subject);
        email.setText(content);
        email.setFrom(sender);

        return email;
    }
}
