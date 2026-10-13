package com.example.cordyscrm;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("CRM.mapper")
public class CordysCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CordysCrmApplication.class, args);
    }

}
