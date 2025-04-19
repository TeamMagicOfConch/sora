package magicofconch.sora.review.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import magicOfConch.review.Review;
import magicOfConch.user.UserInfo;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	@Query("SELECT r FROM Review r WHERE r.userInfo.id = :userId AND FUNCTION('MONTH', r.reviewDate) = :month AND FUNCTION('YEAR', r.reviewDate) = :year")
	List<Review> findByUserInfoIdAndUpdatedAtBetween(
		@Param("userId") Long userId,
		@Param("month") int month,
		@Param("year") int year);

	boolean existsByReviewDateAndUserInfo(LocalDate reviewDate, UserInfo userInfo);

	Optional<Review> findReviewByReviewDateAndUserInfo(LocalDate reviewDate, UserInfo userInfo);

	List<Review> findByUserInfo(UserInfo userInfo);
}
