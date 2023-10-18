package com.kmarinos.externalsqltablemonitoring.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EmailSetting {
  @Id
  @UuidGenerator
  @Include
  String id;
  boolean enabled;
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  NotificationCategory category;
  @Transient
  String languageCode;
  @ManyToOne
  @JsonIgnore
  EmailSettingTemplate template;

  @JsonGetter
  public String getName(){
    return template.getNameTranslations().getOrDefault(languageCode, template.getNameKey());
  }

}
