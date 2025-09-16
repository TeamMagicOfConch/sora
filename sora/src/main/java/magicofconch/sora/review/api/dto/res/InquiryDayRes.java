package magicofconch.sora.review.api.dto.res;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryDayRes {
	private String body;
	private String feedback;
	private LocalDate date;
}
