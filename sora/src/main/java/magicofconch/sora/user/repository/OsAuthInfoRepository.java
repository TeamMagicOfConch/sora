package magicofconch.sora.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import magicOfConch.user.OsAuthInfo;

public interface OsAuthInfoRepository extends JpaRepository<OsAuthInfo, Long> {
	Optional<OsAuthInfo> findOsAuthInfoByOsId(String osId);

	Boolean existsByOsId(String identifier);
}
