package magicofconch.soraadmin.admin;

import magicOfConch.user.Admin;
import magicofconch.soraadmin.admin.controller.dto.AdminSignUpForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin registerAdmin(AdminSignUpForm form) {
        if (adminRepository.findByAdminName(form.getAdminName()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 관리자 이름입니다.");
        }

        Admin admin = new Admin();
        admin.setId(null); // ID 자동 생성이라면 null로 설정
        admin.setAdminName(form.getAdminName());
        admin.setEmail(form.getEmail());
        admin.setPassword(passwordEncoder.encode(form.getPassword())); // 암호화!

        return adminRepository.save(admin);
    }

}
