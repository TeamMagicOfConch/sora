package magicofconch.soraadmin.batch.streak.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicOfConch.user.UserInfo;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreakProcessor implements ItemProcessor<UserInfo, Void> {

	@Override
	public Void process(UserInfo userInfo) throws Exception {
		log.info("Processing user info {}", userInfo);
		return null;
	}
}
