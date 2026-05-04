package magicofconch.soraadmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import magicOfConch.user.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
	
}
