package magicofconch.sorawebflux.review.service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sorawebflux.enums.FeedbackType;
import magicofconch.sorawebflux.review.dto.ReviewRes;
import magicofconch.sorawebflux.review.dto.SaveReq;
import magicofconch.sorawebflux.review.dto.SubmitReq;
import magicofconch.sorawebflux.review.dto.UsernameRes;
import magicofconch.sorawebflux.security.JwtUtil;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoraService {

    private final OpenAiChatModel chatClient;
    private final WebClient webClient;
    private final JwtUtil jwtUtil;


    @Value("${prompt.sora-type.T}")
    private String promptAsT;

    @Value("${prompt.sora-type.F}")
    private String promptAsF;

    /**
     * open-ai 스트리밍 메서드
     *
     * @param req
     * @return
     */
    public Flux<ReviewRes> streamReview(SubmitReq req, String token) {
        if (token == null || !jwtUtil.validateToken(token) || jwtUtil.isExpired(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String username = jwtUtil.getUsername(token);
        String prompt = (req.getType() == FeedbackType.FEELING ? promptAsF : promptAsT)
                .replace("{name}", username) + "\n" + req.getBody();

        AtomicReference<StringBuilder> bufferRef = new AtomicReference<>(new StringBuilder());

        return chatClient.stream(prompt)
                .doOnNext(text -> bufferRef.get().append(text))
                .index()
                .map(tuple -> ReviewRes.builder()
                        .seq(tuple.getT1().intValue())
                        .value(tuple.getT2())
                        .build())
                .doOnComplete(() -> {
                    String feedback = bufferRef.get().toString();
                    sendToServer(req, feedback, token);
                });
    }


    /**
     *
     * @param req
     * @param feedback
     * @param token
     */
    private void sendToServer(SubmitReq req, String feedback, String token) {

        log.info(token);

        SaveReq saveReq = new SaveReq(req.getBody(), req.getType(), req.getReviewDate(), feedback);


        webClient.post()
                .uri("http://sora-app-1:8080/auth/user/review")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(saveReq)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }



}