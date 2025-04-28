package magicofconch.sora.review.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import magicOfConch.enums.FeedbackType;

@Getter
@Setter
@NoArgsConstructor
public class SubmitReq {
    private String body;

    private FeedbackType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate reviewDate;
}
