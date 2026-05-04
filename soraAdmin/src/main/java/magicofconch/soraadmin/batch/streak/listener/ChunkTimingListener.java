package magicofconch.soraadmin.batch.streak.listener;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChunkTimingListener implements ChunkListener {

	private long chunkStart;
	private final AtomicInteger chunkNumber = new AtomicInteger(0);
	private final AtomicLong lastChunkDuration = new AtomicLong(0);

	public ChunkTimingListener(MeterRegistry registry) {
		Gauge.builder("batch.chunk.number", chunkNumber, AtomicInteger::get)
			.description("Current chunk number")
			.register(registry);

		Gauge.builder("batch.chunk.duration.ms", lastChunkDuration, AtomicLong::get)
			.description("Last chunk duration in milliseconds")
			.register(registry);
	}

	@Override
	public void beforeChunk(ChunkContext context) {
		chunkStart = System.currentTimeMillis();
	}

	@Override
	public void afterChunk(ChunkContext context) {
		long duration = System.currentTimeMillis() - chunkStart;
		int num = chunkNumber.incrementAndGet();
		lastChunkDuration.set(duration);
		log.info("chunk={}, duration={}ms", num, duration);
	}
}
