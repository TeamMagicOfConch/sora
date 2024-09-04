package magicofconch.sora.review.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.review.dto.req.SoraReviewReq;
import magicofconch.sora.review.dto.res.SoraReviewRes;
import magicofconch.sora.review.service.ReviewService;
import magicofconch.sora.util.Response;

@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/test/api/request/review")
	public Response<SoraReviewRes> requestSora(@RequestBody SoraReviewReq req){
		String soraWrite = reviewService.requestSora(req);
		SoraReviewRes response = new SoraReviewRes();
		response.setSoraWrite(soraWrite);

		return Response.ok(response);
	}
}
