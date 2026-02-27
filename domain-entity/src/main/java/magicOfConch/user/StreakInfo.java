package magicOfConch.user;

import java.time.LocalDate;
import java.time.LocalTime;

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

	/**
	 * @deprecated : 사용하지 않을 컬럼
	 */
	@Column(length = 25)
	@Comment("사용자가 정의한 리뷰 작성시간명")
	@Deprecated
	private String reviewTime;

	@Comment("사용자가 정의한 리뷰 작성 시간")
	private LocalTime reviewAt;

	@Column(length = 25)
	@Comment("희망 리뷰 작성 장소")
	private String writeLocation;

	@Column(length = 25)
	@Comment("사용자 목표 정체성")
	private String aspiration;

	@Comment("사용자 streak 알림 수신 여부")
	private Boolean isReceiveNotification = false;

	@Comment("사용자가 마지막으로 회고를 작성한 날짜")
	private LocalDate lastWriteDate;

	@Comment("lastWriteDate 기준 연속으로 작성한 회고 수")
	private Integer reviewStreakCount = 0;

	@Builder
	public StreakInfo(String reviewTime, LocalTime reviewAt, String writeLocation, String aspiration,
		LocalDate lastWriteDate, Integer reviewStreakCount, Boolean isReceiveNotification) {
		this.reviewTime = reviewTime;
		this.reviewAt = reviewAt;
		this.writeLocation = writeLocation;
		this.aspiration = aspiration;
		this.isReceiveNotification = isReceiveNotification;
		this.lastWriteDate = lastWriteDate;
		this.reviewStreakCount = reviewStreakCount;
	}

	public void clearStreakCount() {
		this.reviewStreakCount = 1;
	}

	public void addStreakCount() {
		this.reviewStreakCount++;
	}

	public void receiveNotification() {
		this.isReceiveNotification = true;
	}
}
