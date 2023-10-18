package com.kmarinos.externalsqltablemonitoring.model.repo;

import com.kmarinos.externalsqltablemonitoring.model.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCategoryRepository extends JpaRepository<NotificationCategory,String> {

}
