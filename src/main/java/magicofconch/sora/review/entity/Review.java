package magicofconch.sora.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import magicofconch.sora.review.enums.ReviewType;

@Entity
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private ReviewType type;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String myWrite;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String conchWrite;
}
