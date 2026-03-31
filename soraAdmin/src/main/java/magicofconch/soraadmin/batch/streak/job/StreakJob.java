package magicofconch.soraadmin.batch.streak.job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import magicOfConch.user.UserInfo;
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

	@Bean
	public Job streakAlertJob(JobRepository jobRepository, Step streakAlertStep) {
		return new JobBuilder("streakAlertJob", jobRepository)
			.listener(streakJobListener)
			.start(streakAlertStep)
			.build();
	}

	@Bean
	public Step streakAlertStep(EntityManagerFactory entityManagerFactory,
		ItemStreamReader<UserStreakItem> streakReader,
		ItemProcessor<UserStreakItem, FcmRequest> streakProcessor, ItemWriter<FcmRequest> streakWriter) {
		return new StepBuilder("streakAlertStep", jobRepository)
			.<UserStreakItem, FcmRequest>chunk(50, transactionManager)
			.reader(streakReader)
			.processor(streakProcessor)
			.writer(streakWriter)
			.listener(chunkTimingListener)
			.build();
	}

	@Bean
	@StepScope
	public ItemStreamReader<UserStreakItem> streakReader(EntityManagerFactory entityManagerFactory,
		@Value("#{jobExecutionContext['alertTime']}") LocalDateTime alertTime) {

		JpaCursorItemReader<Object[]> delegate = new JpaCursorItemReaderBuilder<Object[]>()
			.name("streakUserInfoReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT u, "
				+ "(SELECT MIN(r.reviewDate) FROM Review r WHERE r.userInfo = u), "
				+ "(SELECT COUNT(r) FROM Review r WHERE r.userInfo = u) "
				+ "FROM UserInfo u "
				+ "LEFT JOIN FETCH u.osAuthInfo oi "
				+ "LEFT JOIN FETCH u.streakInfo si "
				+ "WHERE si.reviewAt >= :start "
				+ "AND si.reviewAt < :end "
				+ "ORDER BY u.id")
			.parameterValues(Map.of(
				"start", alertTime.toLocalTime().withSecond(0).withNano(0),
				"end", alertTime.toLocalTime().withSecond(0).withNano(0).plusMinutes(1)
			))
			.build();

		try {
			delegate.afterPropertiesSet();
		} catch (Exception e) {
			throw new ItemStreamException("Failed to initialize delegate reader", e);
		}

		return new ItemStreamReader<>() {
			@Override
			public void open(ExecutionContext executionContext) throws ItemStreamException {
				delegate.open(executionContext);
			}

			@Override
			public void update(ExecutionContext executionContext) throws ItemStreamException {
				delegate.update(executionContext);
			}

			@Override
			public void close() throws ItemStreamException {
				delegate.close();
			}

			@Override
			public UserStreakItem read() throws Exception {
				Object[] item = delegate.read();
				if (item == null)
					return null;
				return new UserStreakItem((UserInfo)item[0], (LocalDate)item[1], (Long)item[2]);
			}
		};
	}
}