package com.yupi.lingerinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author 86136
 */
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class LingerInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LingerInterfaceApplication.class, args);
    }

}
