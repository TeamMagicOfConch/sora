	package magicofconch.sora.review.entity;

	import java.time.LocalDate;

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
	import jakarta.persistence.Table;
	import lombok.AccessLevel;
	import lombok.Builder;
	import lombok.Getter;
	import lombok.NoArgsConstructor;
	import magicofconch.sora.review.enums.FeedbackType;
	import magicofconch.sora.user.entity.UserInfo;
	import magicofconch.sora.util.BaseEntity;

	@Entity
	@Getter
	@Table(name = "review")
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public class Review extends BaseEntity {
		@Id
		@Column(name = "review_id")
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "user_info_id")
		private UserInfo userInfo;

		@Enumerated(EnumType.STRING)
		private FeedbackType feedbackType;

		@Column(columnDefinition = "TEXT", nullable = false)
		private String body;

		@Column(columnDefinition = "TEXT", nullable = false)
		private String feedback;

		private LocalDate reviewDate;

		@Builder
		public Review(UserInfo userInfo, FeedbackType feedbackType, String body, String feedback, LocalDate reviewDate){
			this.userInfo = userInfo;
			this.feedbackType = feedbackType;
			this.body = body;
			this.feedback = feedback;
			this.reviewDate = reviewDate;
		}
	}
