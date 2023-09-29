package com.knucapstone.rudoori;

import com.knucapstone.rudoori.controller.ChatRoomController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableMongoRepositories
@EnableMongoAuditing
public class DooriApplication {

    public static void main(String[] args) {
        SpringApplication.run(DooriApplication.class, args);

    }

}
