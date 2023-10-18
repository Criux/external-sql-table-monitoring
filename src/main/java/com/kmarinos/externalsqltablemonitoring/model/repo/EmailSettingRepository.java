package com.kmarinos.externalsqltablemonitoring.model.repo;

import com.kmarinos.externalsqltablemonitoring.model.EmailSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSettingRepository extends JpaRepository<EmailSetting,String> {

}
