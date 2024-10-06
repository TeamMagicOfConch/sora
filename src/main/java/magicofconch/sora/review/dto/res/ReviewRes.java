package magicofconch.sora.review.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewRes {
	private String value;
	private int seq;
}
