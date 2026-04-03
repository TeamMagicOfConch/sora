package magicOfConch.dto;

import java.time.LocalDate;

import magicOfConch.user.UserInfo;

public record UserStreakItem(UserInfo userInfo, LocalDate firstReviewDate, long reviewCount) {
}
