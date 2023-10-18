package com.kmarinos.externalsqltablemonitoring.service;

import com.kmarinos.externalsqltablemonitoring.model.EmailSetting;
import com.kmarinos.externalsqltablemonitoring.model.EmailSettingTemplate;
import com.kmarinos.externalsqltablemonitoring.model.NotificationCategory;
import com.kmarinos.externalsqltablemonitoring.model.NotificationCategoryTemplate;
import com.kmarinos.externalsqltablemonitoring.model.NotificationSettings;
import com.kmarinos.externalsqltablemonitoring.model.User;
import com.kmarinos.externalsqltablemonitoring.model.repo.EmailSettingRepository;
import com.kmarinos.externalsqltablemonitoring.model.repo.EmailSettingTemplateRepository;
import com.kmarinos.externalsqltablemonitoring.model.repo.NotificationCategoryRepository;
import com.kmarinos.externalsqltablemonitoring.model.repo.NotificationCategoryTemplateRepository;
import com.kmarinos.externalsqltablemonitoring.model.repo.NotificationSettingsRepository;
import com.kmarinos.externalsqltablemonitoring.monitored.film.event.FilmOutOfStockEvent;
import com.kmarinos.externalsqltablemonitoring.monitored.film.event.FilmReleasedEvent;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.event.DvdRentedEvent;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.event.DvdReturnedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
  private final EmailSettingRepository emailSettingRepository;
  private final EmailSettingTemplateRepository emailSettingTemplateRepository;
  private final NotificationCategoryRepository notificationCategoryRepository;
  private final NotificationCategoryTemplateRepository notificationCategoryTemplateRepository;
  private final NotificationSettingsRepository notificationSettingsRepository;

  public void initializeSettingsForUser(User user){
    var settings = createSettingsForUser(user);
    var categoryTemplates = fetchCategoryTemplates();
    var userSettingsList = new ArrayList<EmailSetting>();
    for (var categoryTemplate: categoryTemplates){
      var category=createCategoryForUser(categoryTemplate,settings);
      var emailTemplates = emailSettingTemplateRepository.fetchEmailTemplatesForCategoryTemplate(categoryTemplate);
      for(var emailTemplate:emailTemplates){
        createEmailSettingForUser(emailTemplate,category);
      }
    }
  }
  public void createTemplates(){
    var templateCat1 = NotificationCategoryTemplate.builder()
        .nameKey("films")
        .nameTranslations(Map.of("EN", "Films","DE","Filme"))
        .sortOrder(100)
        .build();
    var templateCat2 = NotificationCategoryTemplate.builder()
        .nameKey("rentals")
        .nameTranslations(Map.of("EN", "Rentals"))
        .sortOrder(200)
        .build();

    notificationCategoryTemplateRepository.save(templateCat1);
    notificationCategoryTemplateRepository.save(templateCat2);

    var templateEs11 = EmailSettingTemplate.builder()
        .notificationCategoryTemplate(templateCat1)
        .nameKey("film_released")
        .nameTranslations(Map.of("EN","Film released","DE","Film veröffentlicht"))
        .enabledByDefault(false)
        .eventClass(FilmReleasedEvent.class.getName())
        .sortOrder(100)
        .build();
    var templateEs12 = EmailSettingTemplate.builder()
        .notificationCategoryTemplate(templateCat1)
        .nameKey("film_out_of_stock")
        .nameTranslations(Map.of("EN","Film out of stock","DE","Film nicht vorrätig"))
        .enabledByDefault(true)
        .eventClass(FilmOutOfStockEvent.class.getName())
        .sortOrder(200)
        .build();
    var templateEs21 = EmailSettingTemplate.builder()
        .notificationCategoryTemplate(templateCat2)
        .nameKey("dvd_rented")
        .nameTranslations(Map.of("EN","Dvd rented","DE","DVD ausgeliehen"))
        .enabledByDefault(true)
        .eventClass(DvdRentedEvent.class.getName())
        .sortOrder(100)
        .build();
    var templateEs22 = EmailSettingTemplate.builder()
        .notificationCategoryTemplate(templateCat2)
        .nameKey("dvd_returned")
        .nameTranslations(Map.of("EN","Dvd returned","DE","DVD zurückgegeben"))
        .enabledByDefault(true)
        .eventClass(DvdReturnedEvent.class.getName())
        .sortOrder(200)
        .build();

    emailSettingTemplateRepository.save(templateEs11);
    emailSettingTemplateRepository.save(templateEs12);
    emailSettingTemplateRepository.save(templateEs21);
    emailSettingTemplateRepository.save(templateEs22);
  }
  private void createEmailSettingForUser(EmailSettingTemplate emailSettingTemplate,
      NotificationCategory notificationCategory){
    var emailSetting = EmailSetting.builder()
        .template(emailSettingTemplate)
        .enabled(emailSettingTemplate.isEnabledByDefault())
        .category(notificationCategory)
        .build();
    emailSettingRepository.save(emailSetting);
  }
  private NotificationCategory createCategoryForUser(NotificationCategoryTemplate categoryTemplate,
      NotificationSettings settings){
    var category = NotificationCategory.builder()
        .template(categoryTemplate)
        .settings(settings)
        .build();
    return notificationCategoryRepository.save(category);
  }
  private NotificationSettings createSettingsForUser(User user){
    var settings = NotificationSettings.builder()
        .enableNotifications(true)
        .user(user)
        .build();
    return notificationSettingsRepository.save(settings);
  }
  private List<NotificationCategoryTemplate> fetchCategoryTemplates(){
    return notificationCategoryTemplateRepository.findAll();
  }
}
