package magicofconch.sorawebflux.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sorawebflux.review.dto.ReviewRes;
import magicofconch.sorawebflux.review.dto.SubmitReq;
import magicofconch.sorawebflux.review.service.SoraService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SoraController {

    private final String BEARER_TOKEN = "Bearer ";

    private final SoraService soraService;

    @PostMapping(value = "/stream/review",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ReviewRes> streamReview(@RequestBody SubmitReq req, ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = (authHeader != null && authHeader.startsWith(BEARER_TOKEN)) ? authHeader.substring(BEARER_TOKEN.length()) : null;

        return soraService.streamReview(req, token);
    }
}
