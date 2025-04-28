package magicofconch.soraadmin.admin.service.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCsvDto {
    private String uuid;
    private String feedback;
    private LocalDate reviewDate;
    private String feedbackType;
}
