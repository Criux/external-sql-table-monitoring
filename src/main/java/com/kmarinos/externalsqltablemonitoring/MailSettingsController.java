package com.kmarinos.externalsqltablemonitoring;

import com.kmarinos.externalsqltablemonitoring.exceptionhandling.exceptions.EntityNotFoundException;
import com.kmarinos.externalsqltablemonitoring.exceptionhandling.exceptions.OperationNotAllowedException;
import com.kmarinos.externalsqltablemonitoring.model.EmailSetting;
import com.kmarinos.externalsqltablemonitoring.model.NotificationCategory;
import com.kmarinos.externalsqltablemonitoring.model.NotificationSettings;
import com.kmarinos.externalsqltablemonitoring.model.User;
import com.kmarinos.externalsqltablemonitoring.model.repo.EmailSettingRepository;
import com.kmarinos.externalsqltablemonitoring.model.repo.NotificationSettingsRepository;
import com.kmarinos.externalsqltablemonitoring.model.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mails")
@RequiredArgsConstructor
@Slf4j
public class MailSettingsController {
  @Value("${monitoring.mails.allow-from:}")
  List<String> allowedFrom;
  private final MonitoredDBClient monitoredDBClient;
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final EmailSettingRepository emailSettingRepository;
  private final UserRepository userRepository;

  @PostMapping("update-settings")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void updateSetting(@RequestParam("lng")String languageCode,@RequestParam("ssid")String userSessionId,@RequestHeader(value = "referer")String referer,@RequestBody
      EmailSetting setting){
    authenticateClient(referer);
    var user = authenticateUser(userSessionId);
    log.info("User {} request a settings update",user.getUsername());
    if(setting == null){
      throw new ValidationException("EmailSetting is not valid");
    }
    notificationSettingsRepository.findById(setting.getId()).ifPresent(s->{
      s.setEnableNotifications(setting.isEnabled());
      notificationSettingsRepository.save(s);
    });
    emailSettingRepository.findById(setting.getId())
        .ifPresent(settingToUpdate->{
          if(settingToUpdate.getCategory().getSettings().isEnableNotifications()&&user.equals(settingToUpdate.getCategory().getSettings().getUser())){
            settingToUpdate.setEnabled(setting.isEnabled());
            emailSettingRepository.save(settingToUpdate);
            log.debug("Setting {} update to {}",settingToUpdate.getName(),(settingToUpdate.isEnabled()?"active":"inactive"));
          }else{
            throw new OperationNotAllowedException("User "+user.getUsername()+" is not allowed to change the setting with id "+setting.getId())
          }
        });
  }
  @GetMapping("my-settings")
  public ResponseEntity<NotificationSettings> getSettingsForLoggedInUser(@RequestParam("lng")String languageCode,@RequestParam("ssid")String userSessionId,@RequestHeader
      HttpHeaders headers,@RequestHeader(value = "referer")String referer, HttpServletRequest request){
    authenticateClient(referer);
    var user = authenticateUser(userSessionId);
    log.info("User {} requested their settings for language {}",user.getUsername(),languageCode);
    var settings = notificationSettingsRepository.fetchSettingsFromUser(user).stream().findFirst().map(s->{s.setLanguageCode(languageCode);return s;}).map(s->{
      if(!s.isEnableNotifications()){
        return getDisabledSettings(s);
      }
      return s;
    });
    return ResponseEntity.ok().body(settings.orElse(new NotificationSettings()));
  }
  private NotificationSettings getDisabledSettings(NotificationSettings settings){
    for(NotificationCategory category:settings.getCategories()){
      for(EmailSetting notification:category.getNotifications()){
        notification.setEnabled(false);
      }
    }
    return settings;
  }
  private void authenticateClient(String referer){
    if(referer==null||!allowedFrom.contains(referer)){
      throw new OperationNotAllowedException("Not allowed from source "+referer);
    }
  }
  private User authenticateUser(String userSessionId){
    return monitoredDBClient.getuserFromSession(userSessionId).orElseThrow(()->new EntityNotFoundException(User.class,"sessionId",userSessionId));
  }
}
