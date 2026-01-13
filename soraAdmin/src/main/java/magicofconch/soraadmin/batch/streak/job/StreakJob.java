package magicofconch.soraadmin.batch.streak.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import magicOfConch.user.UserInfo;

@Configuration
@RequiredArgsConstructor
public class StreakJob {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job streakAlertJob(JobRepository jobRepository, Step streakAlertStep) {
		return new JobBuilder("streakAlertJob", jobRepository)
			.start(streakAlertStep)
			.build();
	}

	@Bean
	public Step streakAlertStep(EntityManagerFactory entityManagerFactory, JpaCursorItemReader<UserInfo> streakReader,
		ItemProcessor<UserInfo, Void> streakProcessor, ItemWriter<Void> streakWriter) {
		return new StepBuilder("streakAlertStep", jobRepository)
			.<UserInfo, Void>chunk(50, transactionManager)
			.reader(streakReader)
			.processor(streakProcessor)
			.writer(streakWriter)
			.build();
	}

	@Bean
	public ItemWriter<Void> streakWriter() {
		return items -> {
			// 아무것도 하지 않음
		};
	}

	@Bean
	public JpaCursorItemReader<UserInfo> streakReader(EntityManagerFactory entityManagerFactory) {
		return new JpaCursorItemReaderBuilder<UserInfo>()
			.name("streakUserInfoReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT u FROM UserInfo u "
				+ "LEFT JOIN FETCH u.osAuthInfo "
				+ "LEFT JOIN FETCH u.streakInfo "
				+ "ORDER BY u.id")
			.build();
	}

}