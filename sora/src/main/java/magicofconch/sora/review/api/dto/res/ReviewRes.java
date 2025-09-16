package magicofconch.sora.review.api.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewRes {
	private String value;
	private int seq;
}
