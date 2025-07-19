package magicofconch.sora.review.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import magicOfConch.enums.FeedbackType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SaveReq {
    private String body;

    private FeedbackType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate reviewDate;

    private String feedback;
}
