package org.example.userservice;

import org.springframework.boot.SpringApplication;

public class TestSpringExerciseApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringExerciseApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
