package magicofconch.soraadmin.batch.streak;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StreakJobRunner implements ApplicationRunner {
	private final JobLauncher jobLauncher;
	private final Job streakAlertJob;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addLong("run.id", System.currentTimeMillis())
			.toJobParameters();

		jobLauncher.run(streakAlertJob, params);
	}

}
