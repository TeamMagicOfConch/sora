package magicofconch.sora.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import magicofconch.sora.user.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

	Optional<UserInfo> findUserInfoByUuid(String uuid);
}
