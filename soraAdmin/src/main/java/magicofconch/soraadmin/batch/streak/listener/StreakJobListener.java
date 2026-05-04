package magicofconch.soraadmin.batch.streak.listener;

import java.time.LocalDateTime;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StreakJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {

		ExecutionContext executionContext = jobExecution.getExecutionContext();

		// TODO: 성능 테스트용 고정 시간. 테스트 완료 후 LocalDateTime.now()로 복원
		LocalDateTime triggerTime = LocalDateTime.now().withHour(22).withMinute(0).withSecond(0).withNano(0);
		log.info("start");
		executionContext.put("alertTime", triggerTime);
	}
}
