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
import magicofconch.sora.review.enums.ReviewType;
import magicofconch.sora.user.entity.UserInfo;

@Entity
public class Review {
	@Id
	@Column(name = "review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userinfo_id")
	private UserInfo userInfo;

	@Enumerated(EnumType.STRING)
	private ReviewType type;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String myWrite;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String conchWrite;
}
