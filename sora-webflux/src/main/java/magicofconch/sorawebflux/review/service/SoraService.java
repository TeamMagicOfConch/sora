package magicofconch.sorawebflux.review.service;

import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import magicofconch.sorawebflux.enums.FeedbackType;
import magicofconch.sorawebflux.review.dto.ReviewRes;
import magicofconch.sorawebflux.review.dto.SubmitReq;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class SoraService {

    private final OpenAiChatModel chatClient;
    private final WebClient webClient; // WebClient Bean 주입


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
    public Flux<ReviewRes> streamReview(SubmitReq req) {
        String prompt = (req.getType() == FeedbackType.FEELING ? promptAsF : promptAsT)
                .replace("{name}", "username_test") + "\n" + req.getBody();

        AtomicReference<StringBuilder> bufferRef = new AtomicReference<>(new StringBuilder());

        return chatClient.stream(prompt)
                .doOnNext(text -> bufferRef.get().append(text))
                .index()
                .map(tuple -> ReviewRes.builder()
                        .seq(tuple.getT1().intValue())
                        .value(tuple.getT2())
                        .build())
                .doOnComplete(() -> {
                    String result = bufferRef.get().toString();
                    sendToServer(result);
                });
    }

    /**
     * platform 서버로 save 요청 보내기
     * Todo: WebClient 통신 구현
     *
     * @param result
     */
    private void sendToServer(String result) {
        System.out.println(result);

//        webClient.post()
//                .uri("http://localhost:8081/save")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(Map.of("result", result)) // JSON {"result": "..."}
//                .retrieve()
//                .bodyToMono(Void.class)
//                .subscribe(); // 비동기 처리
    }


}