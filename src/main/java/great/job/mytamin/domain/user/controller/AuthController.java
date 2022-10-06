package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.service.AuthService;
import great.job.mytamin.domain.user.dto.request.LoginRequest;
import great.job.mytamin.domain.user.dto.request.ReissueRequest;
import great.job.mytamin.domain.user.dto.request.SignUpRequest;
import great.job.mytamin.domain.user.util.UserUtil;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.domain.user.dto.response.TokenResponse;
import great.job.mytamin.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserUtil userUtil;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody SignUpRequest signUpRequest) {
        UserResponse newUser = authService.signup(signUpRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("회원 가입", newUser));
    }

    @PostMapping("/default/login")
    public ResponseEntity<Object> defaultLogin(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenSet = authService.defaultLogin(loginRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("기본 로그인", tokenSet));
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<Object> checkEmailDuplication(@PathVariable String email) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이메일 중복 체크", userUtil.checkEmailDuplication(email)));
    }

    @GetMapping("/check/nickname/{nickname}")
    public ResponseEntity<Object> checkNicknameDuplication(@PathVariable String nickname) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("닉네임 중복 체크", userUtil.checkNicknameDuplication(nickname)));
    }

    @GetMapping("/reissue")
    public ResponseEntity<Object> tokenReIssue(@RequestBody ReissueRequest reissueRequest) {
        TokenResponse tokenResponse = authService.tokenReIssue(reissueRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("토큰 재발급", tokenResponse));
    }

}
