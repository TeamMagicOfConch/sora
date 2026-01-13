package magicofconch.soraadmin.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import magicofconch.soraadmin.admin.AdminRepository;

@Slf4j
@Service
public class AdminDetailsService implements UserDetailsService {

	private final AdminRepository adminRepository;

	public AdminDetailsService(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return adminRepository.findByAdminName(username)
			.map(CustomAdminDetails::new)
			.orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
	}
}

