package magicofconch.sorawebflux.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewRes {
    private String value;
    private int seq;
}
