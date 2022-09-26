package great.job.mytamin.controller;

import great.job.mytamin.domain.User;
import great.job.mytamin.dto.request.CareRequest;
import great.job.mytamin.dto.request.ReportRequest;
import great.job.mytamin.dto.response.ResultResponse;
import great.job.mytamin.service.MytaminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mytamin")
public class MytaminController {

    private final MytaminService mytaminService;

    @PostMapping("/report")
    public ResponseEntity<Object> reportToday(@AuthenticationPrincipal User user,
                                              @RequestBody ReportRequest reportRequest) {
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("하루 진단하기", mytaminService.reportToday(user, reportRequest)));
    }

    @PostMapping("/care")
    public ResponseEntity<Object> careToday(@AuthenticationPrincipal User user,
                                            @RequestBody CareRequest careRequest) {
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("칭찬 처방하기", mytaminService.careToday(user, careRequest)));
    }

}