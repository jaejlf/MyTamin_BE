package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.request.InitRequest;
import great.job.mytamin.domain.user.dto.request.ProfileUpdateRequest;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.service.UserService;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public String health(@AuthenticationPrincipal User user) {
        return user.getNickname();
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(@AuthenticationPrincipal User user) {
        ProfileResponse profileResponse = userService.getProfile(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("프로필 조회", profileResponse));
    }

    @PutMapping("/profile")
    public ResponseEntity<Object> updateProfile(@AuthenticationPrincipal User user,
                                                @ModelAttribute ProfileUpdateRequest profileUpdateRequest) {
        userService.updateProfile(user, profileUpdateRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("프로필 편집 완료"));
    }

    @GetMapping("/created-at")
    public ResponseEntity<Object> getCreatedAt(@AuthenticationPrincipal User user) {
        LocalDateTime createdAt = user.getCreatedAt();
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("year", createdAt.getYear());
        map.put("month", createdAt.getMonth().getValue());
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("가입 날짜 조회", map));
    }

    @PutMapping("/password")
    public ResponseEntity<Object> changePassword(@AuthenticationPrincipal User user,
                                                 @RequestBody Map<String, String> map) {
        userService.changePassword(user, map.get("password"));
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("비밀번호 변경"));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Object> logout(@AuthenticationPrincipal User user) {
        userService.logout(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("로그아웃"));
    }

    @DeleteMapping("/init")
    public ResponseEntity<Object> initData(@AuthenticationPrincipal User user,
                                            @RequestBody InitRequest initRequest) {
        userService.initData(user, initRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("기록 초기화"));
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<Object> withdraw(@AuthenticationPrincipal User user) {
        userService.withdraw(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("회원 탈퇴"));
    }

}
