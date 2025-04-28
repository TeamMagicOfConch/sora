package magicofconch.soraadmin.admin.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.RequestParam;

public interface ReviewApiDocs {
    @Operation(summary = "Review 분석 CSV 생성 API", description = "특정 기간 동안의 사용자 review 데이터를 CSV 파일로 변환하여 응답합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV 파일 생성 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    void exportReviewCsv(
            @Parameter(description = "조회 시작일 (yyyy-MM-dd)") @RequestParam LocalDate startDate,
            @Parameter(description = "조회 종료일 (yyyy-MM-dd)") @RequestParam LocalDate endDate,
            HttpServletResponse response
    ) throws IOException;
}
