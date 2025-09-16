package magicofconch.sora.review.api.dto.res;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import magicofconch.sora.review.domain.dto.ReviewItemDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursorBaseReviewRes {
	private List<ReviewItemDto> reviews;
}
