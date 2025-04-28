package magicofconch.soraadmin.admin.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import magicOfConch.review.Review;
import magicofconch.soraadmin.admin.service.dto.ReviewCsvDto;
import magicofconch.soraadmin.repository.ReviewRepository;
import magicofconch.soraadmin.util.EncryptionUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ReviewAnalyzeService {

    private final ReviewRepository reviewRepository;
    private final EncryptionUtil encryptionUtil;

    public void exportReviewsToCsv(LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException {
        List<Review> reviews = reviewRepository.findByReviewDateBetween(startDate, endDate);

        try{
            List<ReviewCsvDto> csvData = reviews.stream()
                    .map(r -> {
                        try {
                            return new ReviewCsvDto(
                                    r.getUserInfo().getUuid(),
                                    encryptionUtil.decrypt(r.getFeedback()).replaceAll("[,\\r\\n\\t\"]", ""),
                                    r.getReviewDate(),
                                    r.getFeedbackType().name()
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            response.setContentType("text/csv");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"review_export.csv\"");

            // BOM 추가 - UTF 인코딩
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(("\uFEFF").getBytes(StandardCharsets.UTF_8));

            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

            StatefulBeanToCsv<ReviewCsvDto> csvWriter = new StatefulBeanToCsvBuilder<ReviewCsvDto>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(',')
                    .build();

            csvWriter.write(csvData);
            writer.flush();
        }

        catch ( Exception e ){
            e.printStackTrace();
        }
    }

    public String decrypt(String cryptogram) {
        try {
            return encryptionUtil.decrypt(cryptogram);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "err";
    }

}
