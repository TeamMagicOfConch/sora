package magicofconch.sora.review.dto.res;

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
