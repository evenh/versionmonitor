package net.evenh.versionmonitor.services;

import net.evenh.versionmonitor.config.VersionmonitorConfiguration;
import net.evenh.versionmonitor.domain.User;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.util.Locale;

import javax.mail.internet.MimeMessage;

/**
 * Service for sending e-mails. <p> We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {
  private final Logger log = LoggerFactory.getLogger(MailService.class);

  @Autowired
  private VersionmonitorConfiguration props;

  @Autowired
  private JavaMailSenderImpl javaMailSender;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private SpringTemplateEngine templateEngine;

  @Async
  private void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
    log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
      isMultipart, isHtml, to, subject, content);

    // Prepare message using a Spring helper
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    try {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
      message.setTo(to);
      message.setFrom(props.getMail().getFrom());
      message.setSubject(subject);
      message.setText(content, isHtml);
      javaMailSender.send(mimeMessage);
      log.debug("Sent e-mail to User '{}'", to);
    } catch (Exception e) {
      log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
    }
  }

  @Async
  public void sendActivationEmail(User user, String baseUrl) {
    log.debug("Sending activation e-mail to '{}'", user.getEmail());

    Locale locale = Locale.ENGLISH;
    Context context = new Context(locale);
    context.setVariable("user", user);
    context.setVariable("baseUrl", baseUrl);
    String content = templateEngine.process("activationEmail", context);
    String subject = messageSource.getMessage("email.activation.title", null, locale);
    sendEmail(user.getEmail(), subject, content, false, true);
  }

  @Async
  public void sendCreationEmail(User user, String baseUrl) {
    log.debug("Sending creation e-mail to '{}'", user.getEmail());
    Locale locale = Locale.ENGLISH;
    Context context = new Context(locale);
    context.setVariable("user", user);
    context.setVariable("baseUrl", baseUrl);
    String content = templateEngine.process("creationEmail", context);
    String subject = messageSource.getMessage("email.activation.title", null, locale);
    sendEmail(user.getEmail(), subject, content, false, true);
  }

  @Async
  public void sendPasswordResetMail(User user, String baseUrl) {
    log.debug("Sending password reset e-mail to '{}'", user.getEmail());
    Locale locale = Locale.ENGLISH;
    Context context = new Context(locale);
    context.setVariable("user", user);
    context.setVariable("baseUrl", baseUrl);
    String content = templateEngine.process("passwordResetEmail", context);
    String subject = messageSource.getMessage("email.reset.title", null, locale);
    sendEmail(user.getEmail(), subject, content, false, true);
  }

}
