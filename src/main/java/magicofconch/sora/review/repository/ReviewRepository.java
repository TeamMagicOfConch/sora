package magicofconch.sora.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import magicofconch.sora.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
