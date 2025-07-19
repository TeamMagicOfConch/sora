package magicofconch.sora.review.service;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.review.dto.req.SaveReq;
import magicofconch.sora.review.dto.req.SubmitReq;
import magicofconch.sora.review.dto.res.InquiryDayRes;
import magicofconch.sora.review.dto.res.InquiryMonthRes;
import magicOfConch.review.Review;
import magicofconch.sora.review.repository.ReviewRepository;
import magicOfConch.user.UserInfo;
import magicofconch.sora.util.Encryption.EncryptionUtil;
import magicofconch.sora.util.ResponseCode;
import magicofconch.sora.util.SecurityUtil;
import magicofconch.sora.util.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SecurityUtil securityUtil;
    private final EncryptionUtil encryptionUtil;
    private final DataSource dataSource;

    @Value("${prompt.sora-type.T}")
    private String promptAsT;

    @Value("${prompt.sora-type.F}")
    private String promptAsF;

    /**
     * 해당 Month 날짜별
     *
     * @param year
     * @param month
     * @return
     */
    @Transactional
    public List<InquiryMonthRes> inquiryMonthly(int year, int month) {
        List<InquiryMonthRes> inquiryReviewResList = new ArrayList<>();
        UserInfo userInfo = securityUtil.getCurrentUsersEntity();
        List<Review> reviews = reviewRepository.findByUserInfoIdAndUpdatedAtBetween(userInfo.getId(), month, year);

        for (Review review : reviews) {
            InquiryMonthRes res = new InquiryMonthRes(review);
            inquiryReviewResList.add(res);
        }

        return inquiryReviewResList;
    }

    public void printDbConnectionStatus(String phase) {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            System.out.println("==== [" + phase + "] DB Connection Status ====");
            System.out.println("Active connections: " + hikariDataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("Idle connections: " + hikariDataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("Total connections: " + hikariDataSource.getHikariPoolMXBean().getTotalConnections());
            System.out.println("Threads waiting: " + hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
            System.out.println("========================================");
        }
    }

    @Transactional
    public void saveReview(SubmitReq req, String feedback, UserInfo userInfo) {
        String encryptFeedback;
        String encryptBody;
        try {
            encryptBody = encryptionUtil.encrypt(req.getBody());
            encryptFeedback = encryptionUtil.encrypt(feedback);
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.REVIEW_GENERAL_FAIL);
        }

        Review review = Review.builder()
                .userInfo(userInfo)
                .body(encryptBody)
                .feedbackType(req.getType())
                .feedback(encryptFeedback)
                .reviewDate(req.getReviewDate())
                .build();

        reviewRepository.save(review);
    }

    @Transactional
    public void saveReview(SaveReq req) {
        String encryptFeedback;
        String encryptBody;
        try {
            encryptBody = encryptionUtil.encrypt(req.getBody());
            encryptFeedback = encryptionUtil.encrypt(req.getFeedback());
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.REVIEW_GENERAL_FAIL);
        }

        UserInfo userInfo = securityUtil.getCurrentUsersEntity();
        Review review = Review.builder()
                .userInfo(userInfo)
                .body(encryptBody)
                .feedbackType(req.getType())
                .feedback(encryptFeedback)
                .reviewDate(req.getReviewDate())
                .build();

        reviewRepository.save(review);
    }


    @Transactional
    public InquiryDayRes inquiryDay(LocalDate date) {
        UserInfo user = securityUtil.getCurrentUsersEntity();

        Review review = reviewRepository.findReviewByReviewDateAndUserInfo(date, user)
                .orElseThrow(() -> new BusinessException(ResponseCode.REVIEW_NOT_EXIST));

        String feedbackPlain;
        String bodyPlain;
        try {
            bodyPlain = encryptionUtil.decrypt(review.getBody());
            feedbackPlain = encryptionUtil.decrypt(review.getFeedback());

        } catch (Exception e) {
            throw new BusinessException(ResponseCode.REVIEW_GENERAL_FAIL);
        }

        return InquiryDayRes.builder()
                .feedback(feedbackPlain)
                .body(bodyPlain)
                .date(review.getReviewDate())
                .build();
    }
}
