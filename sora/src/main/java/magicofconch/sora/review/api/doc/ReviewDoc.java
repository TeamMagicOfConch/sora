package magicofconch.sora.review.api.doc;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import magicofconch.sora.review.api.dto.req.SaveReq;
import magicofconch.sora.review.api.dto.res.CursorBaseReviewRes;
import magicofconch.sora.review.api.dto.res.InquiryDayRes;
import magicofconch.sora.review.api.dto.res.InquiryMonthRes;
import magicofconch.sora.util.Response;

@Tag(name = "Review API", description = "사용자 회고 관련 API")
public interface ReviewDoc {

	@Operation(summary = "특정 일자의 리뷰 조회", description = "지정된 날짜(연,월,일)의 회고를 조회합니다.")
	Response<InquiryDayRes> inquiryDate(
		@Parameter(description = "조회 연도") @RequestParam int year,
		@Parameter(description = "조회 월") @RequestParam int month,
		@Parameter(description = "조회 일") @RequestParam int day
	);

	@Operation(summary = "특정 월의 리뷰 조회", description = "지정된 연월의 모든 회고를 조회합니다.")
	Response<List<InquiryMonthRes>> inquiryMonth(
		@Parameter(description = "조회 연도") @RequestParam int year,
		@Parameter(description = "조회 월") @RequestParam int month
	);

	@Operation(summary = "회고 등록", description = "사용자의 회고를 저장합니다.")
	ResponseEntity<?> saveReview(
		@RequestBody SaveReq saveReq
	);

	@Operation(
		summary = "사용자 회고 커서 기반 최신순 조회",
		description = "사용자의 리뷰를 요청 날짜를 포함하여 최신순으로 10개 조회합니다. " +
			"다음 데이터가 존재하면 hasNext=true, nextCursor=YYYY-MM-DD 값을 반환합니다."
	)
	ResponseEntity<Response<CursorBaseReviewRes>> list(
		@Parameter(description = "커서 기준 날짜 (YYYY-MM-DD)")
		@RequestParam(required = false, name = "after") String after
	);
}
