package magicOfConch.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import magicOfConch.BaseEntity;
import magicOfConch.enums.UserRole;
import magicOfConch.review.Review;

@Getter
@Entity
@Table(name = "user_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo extends BaseEntity {
	@Id
	@Column(name = "user_info_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "userInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Review> reviews = new ArrayList<Review>();

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "streak_info_id")
	private StreakInfo streakInfo;

	private String uuid;
	private String socialId;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_info_id")
	private List<OsAuthInfo> osAuthInfo = new ArrayList<OsAuthInfo>();

	private int initialReviewCount;

	private String username;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(length = 500)
	private String refreshToken;

	@Builder
	public UserInfo(String uuid, String socialId, OsAuthInfo osAuthInfo, UserRole role, String username,
		int initialReviewCount, String refreshToken, StreakInfo streakInfo) {
		this.uuid = uuid;
		this.socialId = socialId;
		this.osAuthInfo.add(osAuthInfo);
		this.role = role;
		this.username = username;
		this.initialReviewCount = initialReviewCount;
		this.refreshToken = refreshToken;
		this.streakInfo = streakInfo;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
