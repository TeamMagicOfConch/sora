package magicofconch.soraadmin.batch.streak.listener;

import java.time.LocalDateTime;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class StreakJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {

		ExecutionContext executionContext = jobExecution.getExecutionContext();

		LocalDateTime triggerTime = LocalDateTime.now().withSecond(0).withNano(0);

		executionContext.put("alertTime", triggerTime);
	}
}
