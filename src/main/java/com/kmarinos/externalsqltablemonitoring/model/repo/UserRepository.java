package com.kmarinos.externalsqltablemonitoring.model.repo;

import com.kmarinos.externalsqltablemonitoring.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

  @Query("select user from User user where user.username = :username")
  Optional<User> fetchUserWithUsername(@Param("username") String username);

  @Query("select user from User user where user.firstname = :firstname and user.lastname = :lastname")
  List<User> fetchUsersWithFirstAndLastName(@Param("firstname") String firstname,
      @Param("lastname") String lastname);

  @Query("select es.category.settings.user from EmailSetting es where es.enabled=true and es.category.settings.enableNotifications = true and es.template.eventClass=:eventClass")
  List<User> fetchUsersReceivingNotificationsForEvent(@Param("eventClass") String eventClass);

  @Query("select case when count(es)>0 then true else false end from EmailSetting es where es.enabled=true and es.category.settings.enableNotifications=true and es.template.eventClass=:eventClass and es.category.settings.user=:user")
  Boolean isUserReceivingNotificationsForEvent(@Param("user") User user,
      @Param("eventClass") String eventClass);

}
