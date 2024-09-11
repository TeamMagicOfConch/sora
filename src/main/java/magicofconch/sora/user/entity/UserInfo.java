package magicofconch.sora.user.entity;

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
import magicofconch.sora.review.entity.Review;

@Entity
public class UserInfo {
    @Id
    @Column(name = "user_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "review_id")
    private List<Review> reviews = new ArrayList<Review>();
    private String uuid;
    private String socialId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_info_id")
    private List<OsAuthInfo> osAuthInfo = new ArrayList<OsAuthInfo>();

    private Integer initialReviewCount;
}
