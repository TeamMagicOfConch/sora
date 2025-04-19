package magicOfConch.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import magicOfConch.review.Review;
import magicOfConch.BaseEntity;


@Getter
@Entity
@Table(name = "user_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo extends BaseEntity {
    @Id
    @Column(name = "user_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy ="userInfo", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<Review>();

    private String uuid;
    private String socialId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_info_id")
    private List<OsAuthInfo> osAuthInfo = new ArrayList<OsAuthInfo>();

    private int initialReviewCount;

    private String username;

    private String role;

    private String refreshToken;

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }


    @Builder
    public UserInfo(String uuid, String socialId, OsAuthInfo osAuthInfo, String role, String username, int initialReviewCount, String refreshToken){
        this.uuid = uuid;
        this.socialId = socialId;
        this.osAuthInfo.add(osAuthInfo);
        this.role = role;
        this.username = username;
        this.initialReviewCount = initialReviewCount;
        this.refreshToken = refreshToken;
    }


}
