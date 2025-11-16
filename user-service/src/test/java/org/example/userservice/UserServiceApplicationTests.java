package org.example.userservice;

import org.example.userservice.kafka.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UserServiceApplicationTests {

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Test
    void contextLoads() {
    }

}