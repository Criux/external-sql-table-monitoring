package com.kmarinos.externalsqltablemonitoring.model.repo;

import com.kmarinos.externalsqltablemonitoring.model.NotificationSettings;
import com.kmarinos.externalsqltablemonitoring.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings,String> {
  @Query("select settings from NotificationSettings settings where settings.user=:user")
  public List<NotificationSettings> fetchSettingsFromUser(@Param("user") User user);

}
