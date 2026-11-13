package cl.pagoflex.compromisos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CompromisosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompromisosServiceApplication.class, args);
    }
}
