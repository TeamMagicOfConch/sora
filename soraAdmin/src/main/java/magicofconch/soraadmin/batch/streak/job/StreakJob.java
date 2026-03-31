package magicofconch.soraadmin.batch.streak.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import magicofconch.soraadmin.batch.streak.dto.FcmRequest;
import magicofconch.soraadmin.batch.streak.dto.UserStreakItem;
import magicofconch.soraadmin.batch.streak.listener.ChunkTimingListener;
import magicofconch.soraadmin.batch.streak.listener.StreakJobListener;

@Configuration
@RequiredArgsConstructor
public class StreakJob {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final StreakJobListener streakJobListener;
	private final ChunkTimingListener chunkTimingListener;
	// private final StreakKeySetReader streakKeySetReader;

	@Bean
	public Job streakAlertJob(JobRepository jobRepository, Step streakAlertStep) {
		return new JobBuilder("streakAlertJob", jobRepository)
			.listener(streakJobListener)
			.start(streakAlertStep)
			.build();
	}

	@Bean
	public Step streakAlertStep(ItemStreamReader<UserStreakItem> streakKeySetReader,
		ItemProcessor<UserStreakItem, FcmRequest> streakProcessor, ItemWriter<FcmRequest> streakWriter) {
		return new StepBuilder("streakAlertStep", jobRepository)
			.<UserStreakItem, FcmRequest>chunk(1000, transactionManager)
			.reader(streakKeySetReader)
			.processor(streakProcessor)
			.writer(streakWriter)
			.listener(chunkTimingListener)
			.build();
	}

	// TODO: Cursor Reader (Phase 1 baseline용, Keyset 전환 후 미사용)
	// @Bean
	// @StepScope
	// public ItemStreamReader<UserStreakItem> streakReader(EntityManagerFactory entityManagerFactory,
	// 	@Value("#{jobExecutionContext['alertTime']}") LocalDateTime alertTime) { ... }
}