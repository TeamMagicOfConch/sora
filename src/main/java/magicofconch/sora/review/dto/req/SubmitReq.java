package magicofconch.sora.review.dto.req;

import java.time.LocalDate;

import lombok.Getter;
import magicofconch.sora.review.enums.FeedbackType;

@Getter
public class SubmitReq {
	private String body;
	private FeedbackType type;
	private LocalDate reviewDate;
}
