package magicofconch.soraadmin.batch.streak.partitioner;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class StreakPartitioner implements Partitioner {

	private final EntityManagerFactory entityManagerFactory;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			Long minId = em.createQuery("SELECT MIN(u.id) FROM UserInfo u", Long.class).getSingleResult();
			Long maxId = em.createQuery("SELECT MAX(u.id) FROM UserInfo u", Long.class).getSingleResult();

			if (minId == null || maxId == null) {
				return Map.of();
			}

			long range = (maxId - minId) / gridSize + 1;
			Map<String, ExecutionContext> partitions = new HashMap<>();

			for (int i = 0; i < gridSize; i++) {
				ExecutionContext context = new ExecutionContext();
				long partitionMinId = minId + (range * i);
				long partitionMaxId = (i == gridSize - 1) ? maxId : minId + (range * (i + 1)) - 1;

				context.putLong("minId", partitionMinId);
				context.putLong("maxId", partitionMaxId);

				partitions.put("partition" + i, context);
				log.info("Partition {}: id {} ~ {}", i, partitionMinId, partitionMaxId);
			}

			return partitions;
		} finally {
			em.close();
		}
	}
}
