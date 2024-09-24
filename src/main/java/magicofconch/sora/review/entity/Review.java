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
import magicofconch.sora.review.enums.FeedbackType;
import magicofconch.sora.user.entity.UserInfo;
import magicofconch.sora.util.BaseEntity;

@Entity
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
}
