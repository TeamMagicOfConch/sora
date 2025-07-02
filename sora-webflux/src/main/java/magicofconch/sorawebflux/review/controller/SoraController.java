package magicofconch.sorawebflux.review.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sorawebflux.review.dto.ReviewRes;
import magicofconch.sorawebflux.review.dto.SubmitReq;
import magicofconch.sorawebflux.review.service.SoraService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SoraController {

    private final SoraService soraService;

    @PostMapping(value = "/stream/review", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ReviewRes> streamReview(@RequestBody SubmitReq req) {
        log.info("hello world");
        return soraService.streamReview(req);
    }
}