package magicofconch.sora.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.review.dto.req.SubmitReq;
import magicofconch.sora.review.dto.res.InquiryDayRes;
import magicofconch.sora.review.dto.res.InquiryMonthRes;
import magicofconch.sora.review.dto.res.ReviewRes;
import magicofconch.sora.review.service.ReviewService;
import magicofconch.sora.util.Response;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtil securityUtil;

    @GetMapping("/auth/user/api/review/inquiry/day")
    public Response<InquiryDayRes> inquiryDate(@RequestParam int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);

        return Response.ok(reviewService.inquiryDay(date));
    }

    @GetMapping("/auth/user/api/review/inquiry/month")
    public Response<List<InquiryMonthRes>> inquiryMonth(@RequestParam int year, int month) {
        log.info("year = {} , month = {}", year, month);
        return Response.ok(reviewService.inquiryMonthly(year, month));
    }

    @PostMapping(value = "/auth/user/api/review/submit", produces = MediaType.TEXT_EVENT_STREAM_VALUE, consumes = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter submitReview(@RequestBody String requestBody, HttpServletResponse response) {
        SubmitReq req;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            req = objectMapper.readValue(requestBody, SubmitReq.class);

        } catch (Exception e) {
            throw new BusinessException(ResponseCode.REVIEW_JSON_ERROR);
        }

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

        response.setHeader("X-Accel-Buffering", "no");
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);

        SseEmitter emitter = new SseEmitter(60 * 1000L);

        emitter.onCompletion(() -> {
            System.out.println("SSE connection completed");
        });

        emitter.onTimeout(() -> {
            System.out.println("SSE connection timed out");
            emitter.complete();
        });

        String feedback = reviewService.requestSora(req);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService delegatingExecutor = new DelegatingSecurityContextExecutorService(executorService);
        delegatingExecutor.submit(() -> {
            try {
                int seq = 0;
                for (char part : feedback.toCharArray()) {
                    ReviewRes res = ReviewRes.builder()
                            .value(String.valueOf(part))
                            .seq(seq)
                            .build();

                    emitter.send(res);
                    seq++;
                }
                emitter.complete();

            } catch (IOException | AccessDeniedException e) {
                log.warn("sse execute error");
                emitter.completeWithError(e);
            } finally {
                executorService.shutdown();
            }
        });
        return emitter;
    }
}
