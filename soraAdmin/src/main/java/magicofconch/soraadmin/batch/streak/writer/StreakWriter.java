package magicofconch.soraadmin.batch.streak.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import magicofconch.soraadmin.batch.streak.dto.FcmRequest;
import magicofconch.soraadmin.util.FcmUtil;

@Component
@RequiredArgsConstructor
public class StreakWriter implements ItemWriter<FcmRequest> {

	private final FcmUtil fcmUtil;

	@Override
	public void write(Chunk<? extends FcmRequest> chunk) throws Exception {

		for (FcmRequest request : chunk) {
			switch (request.osType()) {
				case IOS:
					// fcmUtil.sendIos(request);
					break;
				case ANDROID:
					// fcmUtil.sendAndroid(request);
					break;
				case WINDOW:
					// fcmUtil.sendWeb(request);
					break;
			}
		}
	}
}
