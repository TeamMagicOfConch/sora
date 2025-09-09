package magicOfConch.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "streak_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StreakInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "streak_info_id")
	private Long id;

	@OneToOne(mappedBy = "streakInfo", fetch = FetchType.LAZY)
	private UserInfo userInfo;

	@Column(length = 25)
	@Comment("사용자가 정의한 리뷰 작성시간명")
	private String reviewTime;

	@Comment("사용자가 정의한 리뷰 작성 시간")
	private LocalDateTime reviewAt;

	@Column(length = 25)
	@Comment("희망 리뷰 작성 장소")
	private String writeLocation;

	@Column(length = 25)
	@Comment("사용자 목표 정체성")
	private String aspiration;

	@Builder
	public StreakInfo(String reviewTime, LocalDateTime reviewAt, String writeLocation, String aspiration) {
		this.reviewTime = reviewTime;
		this.reviewAt = reviewAt;
		this.writeLocation = writeLocation;
		this.aspiration = aspiration;
	}
}
