package magicofconch.sora.review.enums;

import lombok.Getter;

@Getter
public enum FeedbackType {
	FEELING("Feeling"),
	THINKING("Thinking");

	private final String value;

	FeedbackType(String value){
		this.value = value;
	}
}
