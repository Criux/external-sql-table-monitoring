package com.kmarinos.externalsqltablemonitoring.service;

import com.kmarinos.externalsqltablemonitoring.MonitoredDBClient;
import com.kmarinos.externalsqltablemonitoring.model.User;
import com.kmarinos.externalsqltablemonitoring.model.repo.NotificationSettingsRepository;
import com.kmarinos.externalsqltablemonitoring.model.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final UserRepository userRepository;
  private final MonitoredDBClient monitoredDBClient;
  private final NotificationService notificationService;

  @Scheduled(fixedRate = 10_000L)
  private void updateUsers(){
    for(User registeredUser:monitoredDBClient.getAllRegisteredUsers()){
      var user = userRepository.save(registeredUser);
      if(notificationSettingsRepository.fetchSettingsFromUser(user).isEmpty()){
        log.info("Creating standard settings for user {}",user.getUsername());
        notificationService.initializeSettingsForUser(user);
      }
    }
  }

  @Scheduled(fixedRate = 60_000L)
  private void cleanupUsers(){
    for (User user: userRepository.findAll()){
      var registeredUsers = monitoredDBClient.getAllRegisteredUsers();
      if(!registeredUsers.contains(user)){
        log.info("Deleting notification settings for user {}", user.getUsername());
        notificationSettingsRepository.deleteAll(
            notificationSettingsRepository.fetchSettingsFromUser(user));
        userRepository.delete(user);
      }
    }
  }
}
