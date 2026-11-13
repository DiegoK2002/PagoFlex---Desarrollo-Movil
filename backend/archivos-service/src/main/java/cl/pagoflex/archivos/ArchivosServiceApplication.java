package cl.pagoflex.archivos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ArchivosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchivosServiceApplication.class, args);
    }
}
