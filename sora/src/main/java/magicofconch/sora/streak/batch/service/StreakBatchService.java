package magicofconch.sora.streak.batch.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.user.repository.StreakInfoRepository;

@Service
@RequiredArgsConstructor
public class StreakBatchService {

	private final StreakInfoRepository streakInfoRepository;

	/**
	 *
	 */
	// @Scheduled(cron = "0 */10 * * * *")
	// public void sendReviewStreakAlert() {
	// 	LocalDateTime now = LocalDateTime.now();
	//
	// }
}
