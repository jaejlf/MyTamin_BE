package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.service.UserService;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/nickname/{nickname}")
    public ResponseEntity<Object> updateNickname(@AuthenticationPrincipal User user,
                                                 @PathVariable String nickname) {
        userService.updateNickname(user, nickname);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("닉네임 수정"));
    }

    @PatchMapping("/bemy/{msg}")
    public ResponseEntity<Object> updateBeMyMessage(@AuthenticationPrincipal User user,
                                                    @PathVariable String msg) {
        userService.updateBeMyMessage(user, msg);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("'되고 싶은 나' 메세지 수정"));
    }

}
