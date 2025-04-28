package magicofconch.soraadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"magicofconch", "magicOfConch"})
public class SoraAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoraAdminApplication.class, args);
    }

}
