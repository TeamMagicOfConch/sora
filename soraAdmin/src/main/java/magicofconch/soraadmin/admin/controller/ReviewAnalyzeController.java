package magicofconch.soraadmin.admin.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import magicOfConch.review.Review;
import magicofconch.soraadmin.admin.service.ReviewAnalyzeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/review")
public class ReviewAnalyzeController {

    private final ReviewAnalyzeService reviewAnalyzeService;

    @GetMapping("/export")
    public void exportReviewCsv(
                @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            HttpServletResponse response
    ) throws IOException {
        reviewAnalyzeService.exportReviewsToCsv(startDate, endDate, response);
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody Map<String, String> payload) throws Exception {
        String cryptogram = payload.get("cryptogram");
        return reviewAnalyzeService.decrypt(cryptogram);
    }
}
