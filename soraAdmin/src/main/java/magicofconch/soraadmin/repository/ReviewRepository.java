package magicofconch.soraadmin.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import magicOfConch.review.Review;
import magicOfConch.user.UserInfo;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByReviewDateBetween(LocalDate start, LocalDate end);

	Integer countByUserInfo(UserInfo userInfo);
}
