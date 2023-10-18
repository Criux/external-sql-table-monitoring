package com.kmarinos.externalsqltablemonitoring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationSettings implements Serializable {

  @Id
  @UuidGenerator
  @Include
  String id;
  @ManyToOne
  @JsonIgnore
  User user;
  @Transient
  String languageCode;
  @JsonProperty("enabled")
  boolean enableNotifications;
  @OneToMany(mappedBy = "settings")
  @SortComparator(NotificationCategoryComparator.class)
  @Builder.Default
  SortedSet<NotificationCategory> categories = new TreeSet<>();
  //can also be implemented as a translation map like in notification category
  public String getName(){return "DE".equals(languageCode)?"E-Mail-Einstellungen":"E-mail settings";}
  public void setLanguageCode(String languageCode){
    this.languageCode= languageCode;
    if(categories!=null){
      categories.forEach(c->c.setLanguageCode(languageCode));
    }
  }
}
