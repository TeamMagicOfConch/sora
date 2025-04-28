package magicofconch.soraadmin.admin;

import magicOfConch.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminName(String adminName);
}
