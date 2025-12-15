package magicofconch.sora.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import magicOfConch.user.OsAuthInfo;

public interface OsAuthInfoRepository extends JpaRepository<OsAuthInfo, Long> {
	Optional<OsAuthInfo> findOsAuthInfoByOsId(String osId);

	@Query("""
		    SELECT o
		    FROM OsAuthInfo o
		    WHERE o.userInfo.id = :userInfoId
		      AND o.osId = :osId
		""")
	Optional<OsAuthInfo> findByUserIdAndOsId(
		@Param("userInfoId") Long userInfoId,
		@Param("osId") String osId
	);

	Boolean existsByOsId(String identifier);
}
