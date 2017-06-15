package org.zalando.catwatch.backend.scheduler;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.text.MessageFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;
import org.zalando.catwatch.backend.mail.MailSender;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "mail.port=2500",
        "mail.host=localhost",
        "mail.from=from@test.de",
        "mail.to=to@test.de",
        "fetcher.maxAttempts=1"})
@SpringBootTest(webEnvironment=RANDOM_PORT)
public class MailSenderTest {

    private Wiser wiser;

    @Autowired
    private MailSender mailSender;

    @MockBean
    private Fetcher fetcher;

    @Before
    public void setUp() throws Exception {
        wiser = new Wiser();
        wiser.setPort(2500);
        wiser.start();
    }

    @After
    public void tearDown() throws Exception {
        wiser.stop();
    }

    @Test
    public void send() throws Exception {
        mailSender.send(new RuntimeException());
        // assert
        WiserAssertions.assertReceivedMessage(wiser)
                .from("from@test.de")
                .to("to@test.de")
                .withSubject("GitHub crawler failed to fetch data");
    }

    public static class WiserAssertions {
//
        private final List<WiserMessage> messages;

        public static WiserAssertions assertReceivedMessage(Wiser wiser) {
            return new WiserAssertions(wiser.getMessages());
        }

        private WiserAssertions(List<WiserMessage> messages) {
            this.messages = messages;
        }

        public WiserAssertions from(String from) {
            findFirstOrElseThrow(m -> m.getEnvelopeSender().equals(from),
                    assertionError("No message from [{0}] found!", from));
            return this;
        }

        public WiserAssertions to(String to) {
            findFirstOrElseThrow(m -> m.getEnvelopeReceiver().equals(to),
                    assertionError("No message to [{0}] found!", to));
            return this;
        }

        public WiserAssertions withSubject(String subject) {
            Predicate<WiserMessage> predicate = m -> subject.equals(unchecked(getMimeMessage(m)::getSubject));
            findFirstOrElseThrow(predicate,
                    assertionError("No message with subject [{0}] found!", subject));
            return this;
        }

        public WiserAssertions withContent(String content) {
            findFirstOrElseThrow(m -> {
                ThrowingSupplier<String> contentAsString =
                        () -> ((String) getMimeMessage(m).getContent()).trim();
                return content.equals(unchecked(contentAsString));
            }, assertionError("No message with content [{0}] found!", content));
            return this;
        }

        private void findFirstOrElseThrow(Predicate<WiserMessage> predicate, Supplier<AssertionError> exceptionSupplier) {
            messages.stream().filter(predicate)
                    .findFirst().orElseThrow(exceptionSupplier);
        }

        private MimeMessage getMimeMessage(WiserMessage wiserMessage) {
            return unchecked(wiserMessage::getMimeMessage);
        }

        private static Supplier<AssertionError> assertionError(String errorMessage, String... args) {
            return () -> new AssertionError(MessageFormat.format(errorMessage, args));
        }

        public static <T> T unchecked(ThrowingSupplier<T> supplier) {
            try {
                return supplier.get();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        interface ThrowingSupplier<T> {
            T get() throws Throwable;
        }
    }
}