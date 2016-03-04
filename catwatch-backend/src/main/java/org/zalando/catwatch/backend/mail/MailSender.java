package org.zalando.catwatch.backend.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Service
public class MailSender {

    private final JavaMailSender javaMailSender;
    private final String destinationAddress;
    private final String sourceAddress;

    @Autowired
    public MailSender(JavaMailSender javaMailSender,
                      @Value("${mail.to}") String destinationAddress,
                      @Value("${mail.from}") String sourceAddress) {
        this.javaMailSender = javaMailSender;
        this.destinationAddress = destinationAddress;
        this.sourceAddress = sourceAddress;
    }

    public boolean send(Throwable e) {
        javaMailSender.send(createMessageFor(e));
        return true;
    }

    private SimpleMailMessage createMessageFor(Throwable e) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(destinationAddress);
        mailMessage.setFrom(sourceAddress);
        mailMessage.setSubject("GitHub crawler failed to fetch data");
        mailMessage.setText(format("%s\n%s", e.getMessage(), stream(e.getStackTrace()).map(StackTraceElement::toString).collect(joining("\n"))));
        return mailMessage;
    }

}
