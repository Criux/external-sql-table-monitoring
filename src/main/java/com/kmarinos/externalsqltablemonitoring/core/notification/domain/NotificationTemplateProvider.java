package com.kmarinos.externalsqltablemonitoring.core.notification.domain;

import com.kmarinos.externalsqltablemonitoring.model.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplateProvider {
  public String getStandardMessage(String languageCode, User forUser,String forText){
    if(languageCode.equals("DE")){
      return getMessageDE(forUser,forText);
    }
    return getMessageEN(forUser,forText);
  }
  public String getStandardContact(String languageCode){
    if(languageCode.equals("DE")){
      return getContactInfoDE();
    }
    return getContactInfoEN();
  }
  public List<String>getStandardDisclaimer(String languageCode){
    if(languageCode.equals("DE")){
      return getDisclaimerDE();
    }
    return getDisclaimerEN();
  }
  private List<String>getDisclaimerDE(){
    return List.of("Diese E-Mail wurde automatisch generiert.",
        "Falls Sie diese E-Mail nicht mehr erhalten möchten, können Sie dies in [Ihren persönlichen Einstellungen](${platform.url}/profile/${user.slug}/preferences) abwählen.");
  }
  private List<String> getDisclaimerEN(){
    return List.of("This e-mail was generated automatically.",
        "If you do not wish to receive these e-mails, you can opt out in [your preferences](${platform.url}/profile/${user.slug}/preferences).");
  }
  private String getContactInfoDE(){
    return """
        **Kontakt (${platform.company.getName("DE")}):**
        
        ${platform.admin.firstName} ${platform.admin.lastName}
        
        [${platform.admin.email](mailto:${platform.admin.email})
        """;
  }
  private String getContactInfoEN(){
    return """
        **Contact (${platform.company.getName("EN")}):**
        
        ${platform.admin.firstName} ${platform.admin.lastName}
        
        [${platform.admin.email](mailto:${platform.admin.email})
        """;
  }
  private String getMessageDE(User forUser,String forText){
    return """
        %s,
        
        
        %s,
        
        
        %s
        """.formatted(getAppropriateGreetingDE(forUser),forText,getAppropriateClosingDE(forUser));
  }
  private String getMessageEN(User forUser,String forText){
    return """
        %s,
        
        
        %s,
        
        
        %s
        """.formatted(getAppropriateGreetingEN(forUser),forText,getAppropriateClosingEN(forUser));
  }
  private String getAppropriateGreetingDE(User user){
    String defaultGreeting = "Sehr geehrte Damen und Herren";
    if(user == null){
      return defaultGreeting;
    }
    if(user.getEmail().endsWith("@kmarinos.com")){
      if(user.getFirstname() != null && !user.getFirstname().isEmpty()){
        return "Hallo ${toNotify.firstname}";
      }else{
        return "Liebe Kolleginnen, liebe Kollegen";
      }
    }
    return defaultGreeting;
  }
  private String getAppropriateClosingDE(User user){
    String defaultClosing = "Mit freundlichen Grüßen," +System.lineSeparator()+System.lineSeparator()+
        "${platform.company.getName(\"DE\")}";
    if(user == null){
      return defaultClosing;
    }
    if(user.getEmail().endsWith("@kmarinos.com")){
      return "Ihre ${platform.company.getName(\"DE\")}";
    }
    return defaultClosing;
  }
  private String getAppropriateGreetingEN(User user){
    String defaultGreeting = "Dear Sir or Madam";
    if(user == null){
      return defaultGreeting;
    }
    if(user.getEmail().endsWith("@kmarinos.com")){
      if(user.getFirstname() != null && !user.getFirstname().isEmpty()){
        return "Hello ${toNotify.firstname}";
      }else{
        return "Dear colleagues";
      }
    }
    return defaultGreeting;
  }
  private String getAppropriateClosingEN(User user){
    String defaultClosing = "Kind regards," +System.lineSeparator()+System.lineSeparator()+
        "${platform.company.getName(\"DE\")}";
    if(user == null){
      return defaultClosing;
    }
    if(user.getEmail().endsWith("@kmarinos.com")){
      return "Your ${platform.company.getName(\"DE\")}";
    }
    return defaultClosing;
  }
}
