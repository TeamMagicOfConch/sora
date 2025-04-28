package magicofconch.soraadmin.security.controller;

import magicofconch.soraadmin.admin.service.AdminService;
import magicofconch.soraadmin.security.controller.dto.AdminSignUpForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api")
public class AdminSignupController {

    private final AdminService adminService;

    public AdminSignupController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AdminSignUpForm form) {
        try {
            adminService.registerAdmin(form);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
        }
    }
}