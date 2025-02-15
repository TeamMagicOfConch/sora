package magicofconch.sora.review.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum FeedbackType {
    FEELING("Feeling"),
    THINKING("Thinking");

    private final String value;

    FeedbackType(String value) {
        this.value = value;
    }
}
