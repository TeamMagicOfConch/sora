package magicofconch.sora.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import magicOfConch.user.StreakInfo;

public interface StreakInfoRepository extends JpaRepository<StreakInfo, Long> {
}
