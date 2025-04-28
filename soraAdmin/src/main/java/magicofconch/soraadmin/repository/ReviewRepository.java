package magicofconch.soraadmin.repository;

import java.time.LocalDate;
import java.util.List;
import magicOfConch.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewDateBetween(LocalDate start, LocalDate end);
}
