package magicofconch.soraadmin.batch.streak.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import magicOfConch.dto.FcmRequest;
import magicofconch.soraadmin.batch.streak.dto.UserStreakItem;
import magicofconch.soraadmin.batch.streak.listener.ChunkTimingListener;
import magicofconch.soraadmin.batch.streak.listener.StreakJobListener;
import magicofconch.soraadmin.batch.streak.partitioner.StreakPartitioner;

@Configuration
@RequiredArgsConstructor
public class StreakJob {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final StreakJobListener streakJobListener;
	private final ChunkTimingListener chunkTimingListener;
	private final EntityManagerFactory entityManagerFactory;

	@Value("${batch.partition.grid-size:1}")
	private int gridSize;

	@Bean
	public Job streakAlertJob(JobRepository jobRepository, Step partitionStep) {
		return new JobBuilder("streakAlertJob", jobRepository)
			.listener(streakJobListener)
			.start(partitionStep)
			.build();
	}

	@Bean
	public Step partitionStep(Step workerStep) {
		return new StepBuilder("partitionStep", jobRepository)
			.partitioner("workerStep", streakPartitioner())
			.step(workerStep)
			.taskExecutor(batchTaskExecutor())
			.gridSize(gridSize)
			.build();
	}

	@Bean
	public Step workerStep(ItemStreamReader<UserStreakItem> streakKeySetReader,
		ItemProcessor<UserStreakItem, FcmRequest> streakProcessor, ItemWriter<FcmRequest> streakWriter) {
		return new StepBuilder("workerStep", jobRepository)
			.<UserStreakItem, FcmRequest>chunk(1000, transactionManager)
			.reader(streakKeySetReader)
			.processor(streakProcessor)
			.writer(streakWriter)
			.listener(chunkTimingListener)
			.build();
	}

	@Bean
	public Partitioner streakPartitioner() {
		return new StreakPartitioner(entityManagerFactory);
	}

	@Bean
	public TaskExecutor batchTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(gridSize);
		executor.setMaxPoolSize(gridSize);
		executor.setThreadNamePrefix("partition-");
		executor.initialize();
		return executor;
	}
}
