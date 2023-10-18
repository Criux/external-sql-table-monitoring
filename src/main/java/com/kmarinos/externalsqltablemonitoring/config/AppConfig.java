package com.kmarinos.externalsqltablemonitoring.config;

import com.kmarinos.externalsqltablemonitoring.sql.SQLClient;
import com.kmarinos.externalsqltablemonitoring.sql.SQLClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Value("${target-db.url:}")
  String targetDBUrl;
  @Value("${target-db.username:}")
  String targetDBUsername;
  @Value("${target-db.password:}")
  String targetDBPassword;

  @Autowired
  SQLClientFactory sqlClientFactory;



  @Bean
  public SQLClient SQLClient(){
    return sqlClientFactory.connectTo(targetDBUrl,targetDBUsername,targetDBPassword);
  }
}
