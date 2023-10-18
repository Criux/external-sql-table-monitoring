package com.kmarinos.externalsqltablemonitoring.service;

import com.kmarinos.externalsqltablemonitoring.core.notification.domain.SendNotification;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender sender;
  private final Parser parser;
  private final HtmlRenderer renderer;
  @Value("${monitoring.notification.sender.email:notifications@kmarinos.com}")
  String senderAddress;
  @Value("${monitoring.notification.sender.name:SQL Monitoring}")
  String senderName;
  @Value("${monitoring.web.root:https://localhost:8080}")
  String webRoot;

  //@formatter:off
  private final String companyLogo="";
  private final String html1="";
  private final String html2="";
  private final String disclaimerHead="";
  private final String disclaimerElement="%s";
  private final String disclaimerSeperator="";
  private final String disclaimerFoot="";
  private final String html3="";
  private final String html4="";
  //formatter:on

  public void sendNotificationAsEmail(SendNotification<?> sendNotification){
    if(sendNotification==null){
      log.warn("Cannot send Email without an instance of {}",SendNotification.class.getName());
      return;
    }
    MimeMessage message = sender.createMimeMessage();
    MimeMessageHelper helper = null;
    try{
      helper = new MimeMessageHelper(message,true);
      helper.setFrom(senderAddress);
      if(sendNotification.getContactPerson()!=null&& sendNotification.getContactPerson().getEmail()!=null){
        String replyToEmail = sendNotification.getContactPerson().getEmail();
        String replyToName = "%s %s".formatted(sendNotification.getContactPerson().getFirstname(),sendNotification.getContactPerson().getLastname());
        helper.setReplyTo(replyToEmail,replyToName);
      }
      helper.setTo(sendNotification.getToNotify().getEmail());
      helper.setSubject(sendNotification.getTitle());
      helper.setText(getEmailText(sendNotification),true);
      sender.send(message);
    }catch (MessagingException| UnsupportedEncodingException e){
      throw new RuntimeException(e);
    }
  }
  private String getEmailText(SendNotification<?> sendNotification){
    String textBody = convertToHtml(sendNotification.getMessage());
    String disclaimerText = (String)sendNotification.getDisclaimer().stream().map(s->disclaimerElement.formatted(convertToHtml(s))).collect(Collectors.joining(disclaimerSeperator,disclaimerHead,disclaimerFoot));
    String contactText = convertToHtml(sendNotification.getContact());
    return html1 + textBody + html2 + disclaimerText + html3 + contactText + html4;
  }
  private String convertToHtml(String text){
    text = replaceOccurrences(text,"markdown"); //markdown or standard
    return renderer.render(parser.parse(text));
  }
  private String replaceOccurrences(String text, String type){
    if(type.equals("markdown")){
      text = replaceOccurrencesMarkdown(text,System.lineSeparator());
      text = replaceOccurrencesMarkdown(text,"\n");
      return text;
    }
    text = replaceOccurrencesStandard(text,System.lineSeparator());
    text = replaceOccurrencesStandard(text,"\n");
    return text;
  }
  private String replaceOccurrencesStandard(String text, String replaceable){
    return text.replace(replaceable,"<br>");
  }
  /*
  Creates line breaks that are similar to the markdown convention, i.e. a single occurrence of a line break
  translates to just a space and n occurrences are translated to n -1 (maximum 2) line breaks.

  This helps with formatting the text while editing without affecting the final result.
   */
  private static String replaceOccurrencesMarkdown(String text, String replaceable){
    var singleOccurrence = " ";
    var multipleOccurrences = "<br>";

    while(text.contains(replaceable)){
      var firstBreak = text.indexOf(replaceable);
      var firstNonBreak = firstBreak + replaceable.length();
      var substring = text.substring(firstNonBreak);
      while (substring.startsWith(replaceable)){
        firstNonBreak += replaceable.length();
        substring = text.substring(firstNonBreak);
      }
      var toReplace = text.substring(firstBreak, firstNonBreak);
      var occurrences = (firstNonBreak - firstBreak)/replaceable.length();
      if(occurrences>1){
        text = text.replaceFirst(toReplace,IntStream.range(1,occurrences>2?3:occurrences)
            .mapToObj(i->multipleOccurrences).collect(Collectors.joining("")));
      }else{
        text = text.replaceFirst(toReplace,singleOccurrence);
      }
    }
    return text;
  }
}
