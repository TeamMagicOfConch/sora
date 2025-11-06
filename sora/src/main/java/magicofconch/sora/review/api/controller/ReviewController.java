package magicofconch.sora.review.api.controller;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.review.api.dto.req.SaveReq;
import magicofconch.sora.review.api.dto.res.CursorBaseReviewRes;
import magicofconch.sora.review.api.dto.res.InquiryDayRes;
import magicofconch.sora.review.api.dto.res.InquiryMonthRes;
import magicofconch.sora.review.service.ReviewService;
import magicofconch.sora.review.service.SoraService;
import magicofconch.sora.util.Response;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
	private final DataSource dataSource;
	private final SoraService soraService;

	@GetMapping("/auth/user/api/review/inquiry/day")
	public Response<InquiryDayRes> inquiryDate(@RequestParam int year, int month, int day) {
		LocalDate date = LocalDate.of(year, month, day);

		return Response.ok(reviewService.inquiryDay(date));
	}

	@GetMapping("/auth/user/api/review/inquiry/month")
	public Response<List<InquiryMonthRes>> inquiryMonth(@RequestParam int year, int month) {
		log.info("Inquiry month controller");
		return Response.ok(reviewService.inquiryMonthly(year, month));
	}

	@PostMapping("/auth/user/review")
	public ResponseEntity<?> saveReview(@RequestBody SaveReq saveReq) {
		reviewService.saveReview(saveReq);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/auth/user/review")
	@Operation(summary = "사용자 리뷰 커서기반 최신순 조회", description = "사용자의 리뷰가 요청 날짜를 포함하여 오름차순(최신순)으로 10개 응답, 다음 데이터가 있다면 hasNext=true, nextCursor=YYYY-MM-DD")
	public ResponseEntity<Response<CursorBaseReviewRes>> list(
		@RequestParam(required = false, name = "after") String after) {
		CursorBaseReviewRes res = reviewService.listByDateDesc(after);
		return ResponseEntity.ok(Response.ok(res));
	}

}
