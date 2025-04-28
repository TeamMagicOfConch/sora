package magicOfConch.review;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import magicOfConch.user.UserInfo;
import magicOfConch.enums.FeedbackType;
import magicOfConch.BaseEntity;


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
    public Review(UserInfo userInfo, FeedbackType feedbackType, String body, String feedback, LocalDate reviewDate) {
        this.userInfo = userInfo;
        this.feedbackType = feedbackType;
        this.body = body;
        this.feedback = feedback;
        this.reviewDate = reviewDate;
    }
}
