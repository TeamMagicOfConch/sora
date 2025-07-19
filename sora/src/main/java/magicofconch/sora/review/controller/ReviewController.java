package magicofconch.sora.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.review.dto.req.SaveReq;
import magicofconch.sora.review.dto.req.SubmitReq;
import magicofconch.sora.review.dto.res.InquiryDayRes;
import magicofconch.sora.review.dto.res.InquiryMonthRes;
import magicofconch.sora.review.dto.res.ReviewRes;
import magicofconch.sora.review.service.ReviewService;
import magicofconch.sora.review.service.SoraService;
import magicofconch.sora.util.Response;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.exception.BusinessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        log.info("year = {} , month = {}", year, month);
        return Response.ok(reviewService.inquiryMonthly(year, month));
    }

    @GetMapping("/auth/user/test/security")
    public ResponseEntity<?> testSecurity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(authentication);
    }

    @PostMapping("/auth/user/review")
    public ResponseEntity<?> saveReview(@RequestBody SaveReq saveReq) {
        reviewService.saveReview(saveReq);

        return ResponseEntity.ok().build();
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

        emitter.onCompletion(() -> System.out.println("SSE connection completed"));
        emitter.onTimeout(() -> {
            System.out.println("SSE connection timed out");
            emitter.complete();
        });

        // ✅ DB Connection 개수 출력
        printDbConnectionStatus("Before requestSora");

        String feedback = soraService.requestSora(req);

        // ✅ DB Connection 개수 출력
        printDbConnectionStatus("After requestSora");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService delegatingExecutor = new DelegatingSecurityContextExecutorService(executorService);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        delegatingExecutor.submit(() -> {
            SecurityContextHolder.setContext(securityContext);
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

        // ✅ DB Connection 개수 출력
        printDbConnectionStatus("After SSE execution");

        return emitter;
    }

    private void printDbConnectionStatus(String phase) {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            System.out.println("==== [" + phase + "] DB Connection Status ====");
            System.out.println("Active connections: " + hikariDataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("Idle connections: " + hikariDataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("Total connections: " + hikariDataSource.getHikariPoolMXBean().getTotalConnections());
            System.out.println("Threads waiting: " + hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
            System.out.println("========================================");
        }
    }

}
