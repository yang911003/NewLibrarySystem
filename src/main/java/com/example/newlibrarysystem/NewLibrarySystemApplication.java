package com.example.newlibrarysystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class NewLibrarySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewLibrarySystemApplication.class, args);
    }

}
