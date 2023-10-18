package com.kmarinos.externalsqltablemonitoring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EmailSettingTemplate {
  @Id
  @UuidGenerator
  @Include
  String id;
  @ManyToOne
  @JsonIgnore
  NotificationCategoryTemplate notificationCategoryTemplate;
  String nameKey;
  Integer sortOrder;
  boolean enabledByDefault;
  @ElementCollection
  private Map<String,String> nameTranslations;
  String eventClass;

}
