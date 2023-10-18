package com.kmarinos.externalsqltablemonitoring.model.repo;

import com.kmarinos.externalsqltablemonitoring.model.EmailSettingTemplate;
import com.kmarinos.externalsqltablemonitoring.model.NotificationCategoryTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailSettingTemplateRepository extends JpaRepository<EmailSettingTemplate,String> {
  @Query("select t from EmailSettingTemplate t where t.notificationCategoryTemplate= :cTemplate")
  List<EmailSettingTemplate> fetchEmailTemplatesForCategoryTemplate(@Param("cTemplate")
      NotificationCategoryTemplate notificationCategoryTemplate);

}
