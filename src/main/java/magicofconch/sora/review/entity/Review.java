package magicofconch.sora.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import magicofconch.sora.review.enums.FeedbackType;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.util.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
	@Id
	@Column(name = "review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", insertable = false, updatable = false)
	private UserInfo userInfo;

	@Enumerated(EnumType.STRING)
	private FeedbackType feedbackType;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String body;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String feedback;

	@Builder
	public Review(UserInfo userInfo, FeedbackType feedbackType, String body, String feedback){
		this.userInfo = userInfo;
		this.feedbackType = feedbackType;
		this.body = body;
		this.feedback = feedback;
	}
}
