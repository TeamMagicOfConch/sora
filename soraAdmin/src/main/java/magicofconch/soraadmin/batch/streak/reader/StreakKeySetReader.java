package magicofconch.soraadmin.batch.streak.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import magicOfConch.user.UserInfo;
import magicOfConch.dto.UserStreakItem;

@Slf4j
@Component
@StepScope
public class StreakKeySetReader implements ItemStreamReader<UserStreakItem> {

	private static final String LAST_ID_KEY = "streak.reader.lastId";
	private static final int PAGE_SIZE = 1000;

	// 1단계: ID만 조회 (컬렉션 fetch 없이 LIMIT 가능)
	private static final String ID_JPQL = "SELECT u.id FROM UserInfo u "
		+ "JOIN u.streakInfo si "
		+ "WHERE si.reviewAt >= :start "
		+ "AND si.reviewAt < :end "
		+ "AND u.id > :lastId "
		+ "ORDER BY u.id";

	// 2단계: ID 목록으로 엔티티 + 연관관계 fetch
	private static final String FETCH_JPQL = "SELECT u, "
		+ "(SELECT MIN(r.reviewDate) FROM Review r WHERE r.userInfo = u), "
		+ "(SELECT COUNT(r) FROM Review r WHERE r.userInfo = u) "
		+ "FROM UserInfo u "
		+ "LEFT JOIN FETCH u.osAuthInfo oi "
		+ "LEFT JOIN FETCH u.streakInfo si "
		+ "WHERE u.id IN :ids "
		+ "ORDER BY u.id";

	private final EntityManagerFactory entityManagerFactory;

	@Value("#{jobExecutionContext['alertTime']}")
	private LocalDateTime alertTime;

	private long lastId = 0;
	private Iterator<UserStreakItem> buffer;
	private boolean exhausted = false;

	public StreakKeySetReader(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		if (executionContext.containsKey(LAST_ID_KEY)) {
			lastId = executionContext.getLong(LAST_ID_KEY);
			log.info("Restarting from lastId={}", lastId);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putLong(LAST_ID_KEY, lastId);
	}

	@Override
	public void close() throws ItemStreamException {
		buffer = null;
		exhausted = false;
		lastId = 0;
	}

	@Override
	public UserStreakItem read() throws Exception {
		if (buffer != null && buffer.hasNext()) {
			return buffer.next();
		}

		if (exhausted) {
			return null;
		}

		List<UserStreakItem> page = fetchNextPage();

		if (page.isEmpty()) {
			exhausted = true;
			return null;
		}

		if (page.size() < PAGE_SIZE) {
			exhausted = true;
		}

		buffer = page.iterator();
		return buffer.next();
	}

	private List<UserStreakItem> fetchNextPage() {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			LocalTime start = alertTime.toLocalTime().withSecond(0).withNano(0);
			LocalTime end = start.plusMinutes(1);

			// 1단계: ID만 조회 (LIMIT 정상 작동)
			List<Long> ids = em.createQuery(ID_JPQL, Long.class)
				.setParameter("start", start)
				.setParameter("end", end)
				.setParameter("lastId", lastId)
				.setMaxResults(PAGE_SIZE)
				.getResultList();

			if (ids.isEmpty()) {
				return List.of();
			}

			// 2단계: ID 목록으로 엔티티 + 연관관계 fetch (LIMIT 없어서 컬렉션 fetch 가능)
			List<Object[]> results = em.createQuery(FETCH_JPQL, Object[].class)
				.setParameter("ids", ids)
				.getResultList();

			return results.stream()
				.map(row -> {
					UserInfo userInfo = (UserInfo)row[0];
					LocalDate firstReviewDate = (LocalDate)row[1];
					Long reviewCount = (Long)row[2];
					lastId = userInfo.getId();
					return new UserStreakItem(userInfo, firstReviewDate, reviewCount);
				})
				.toList();
		} finally {
			em.close();
		}
	}
}
