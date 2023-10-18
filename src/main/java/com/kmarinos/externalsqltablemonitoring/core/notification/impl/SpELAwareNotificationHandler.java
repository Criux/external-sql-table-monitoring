package com.kmarinos.externalsqltablemonitoring.core.notification.impl;

import com.kmarinos.externalsqltablemonitoring.core.notification.NotificationHandler;
import com.kmarinos.externalsqltablemonitoring.core.notification.domain.SendNotification;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
@Slf4j
public abstract class SpELAwareNotificationHandler implements NotificationHandler {
  private final SpelExpressionParser expressionParser;
  private final Map<String, DateTimeFormatter> dtf;

  protected SpELAwareNotificationHandler(SpelExpressionParser expressionParser){
    this.expressionParser=expressionParser;
    dtf = new HashMap<>();
    dtf.put("DE",DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    dtf.put("EN",DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }

  public abstract void sendConverted(SendNotification<?> notification);

  @Override
  public void send(SendNotification<?> notification){
    parseExpressions(notification);
    sendConverted(notification);
  }
  //TODO change implementation of parseExpressions to find all string fields and lists with reflection and call replaceExpressions on all of them.
  private void parseExpressions(SendNotification<?>notification){
    notification.setTitle(replaceExpressions(notification.getTitle(),notification));
    notification.setMessage(replaceExpressions(notification.getMessage(),notification));
    notification.setDisclaimer(Optional.ofNullable(notification.getDisclaimer()).orElse(new ArrayList<>()).stream().map(s->replaceExpressions(s,notification)).collect(
        Collectors.toList()));
    notification.setContact(replaceExpressions(notification.getContact(),notification));
  }
  private String replaceExpressions(String str,SendNotification notification){
    //search for placeholders in the notification object
    String str1 = replaceExpressions(str,notification,notification.getMessageData(),notification.getLanguage());
    //then search for placeholders in the event
    return replaceExpressions(str1,notification.getCtx(),notification.getMessageData(),notification.getLanguage());
  }
  private String replaceExpressions(String str, Object ctx, Map<String, Supplier<?>> secondaryValues,String language){
    if(str == null){
      return null;
    }
    int idx = -1;
    Map<String,String> params = new HashMap<>();
    var result = str;
    while((idx=str.indexOf("${"))>-1){
      var part1 = str.substring(idx);
      if(part1.contains("}")){
        var exp = part1.substring(2).split("}",2)[0];
        String toReplace = "${"+exp+"}";
        String newValue = null;
        try{
          newValue = asString(expressionParser.parseExpression(exp).getValue(),language);
        }catch (SpelEvaluationException e){
          newValue = asString(secondaryValues.getOrDefault(exp,()->toReplace).get(),language);
        }
        params.put(toReplace,newValue);
        str = str.replace(toReplace,"replaced");
      }
    }
    for(Map.Entry<String,String>entry:params.entrySet()){
      if(!entry.getKey().equals(entry.getValue())){
        log.debug("Replacing {} with {}",entry.getKey(),entry.getValue());
      }
      result = result.replace(entry.getKey(),entry.getValue());
    }
    return result;
  }
  private String asString(Object object,String language){
    if(object == null){
      return "";
    }else if(object instanceof Timestamp){
      return ((Timestamp)object).toInstant().atZone(ZoneId.of("Europe/Berlin")).format(dtf.getOrDefault(language,dtf.get("EN")));
    }else {
      return object.toString();
    }
  }

}
