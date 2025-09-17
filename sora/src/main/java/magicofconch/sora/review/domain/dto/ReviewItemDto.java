package magicofconch.sora.review.domain.dto;

import java.time.LocalDate;

import magicOfConch.enums.FeedbackType;

public record ReviewItemDto(FeedbackType feedbackType, String body, LocalDate reviewDate) {
}
