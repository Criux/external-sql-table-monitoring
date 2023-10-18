package com.kmarinos.externalsqltablemonitoring.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationCategory {

  @Id
  @UuidGenerator
  @Include
  String id;
  @ManyToOne
  @OnDelete(action= OnDeleteAction.CASCADE)
  @JsonIgnore
  NotificationSettings settings;
  @OneToMany(mappedBy = "category")
  @SortComparator(EmailSettingsComparator.class)
  @Builder.Default
  SortedSet<EmailSetting> notifications = new TreeSet<>();
  @ManyToOne
  @JsonIgnore
  NotificationCategoryTemplate template;
  @Transient
  String languageCode;
  @JsonGetter
  public String getName(){return template.getNameTranslations().getOrDefault(languageCode,template.getNameKey());}
  public void setLanguageCode(String languageCode){
    this.languageCode=languageCode;
    if(this.notifications!=null){
      this.notifications.forEach(es->es.setLanguageCode(languageCode));
    }
  }

}
