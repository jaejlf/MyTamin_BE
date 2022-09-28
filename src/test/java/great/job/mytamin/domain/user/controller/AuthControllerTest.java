package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.request.LoginRequest;
import great.job.mytamin.domain.user.dto.request.ReissueRequest;
import great.job.mytamin.domain.user.dto.request.SignUpRequest;
import great.job.mytamin.domain.user.dto.response.TokenResponse;
import great.job.mytamin.domain.user.dto.response.UserResponse;
import great.job.mytamin.domain.user.service.UserService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static great.job.mytamin.global.exception.ErrorMap.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@DisplayName("Auth 컨트롤러")
class AuthControllerTest extends CommonControllerTest {

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("회원 가입")
    class SignUpTest {

        @DisplayName("성공")
        @Test
        void signup(TestInfo testInfo) throws Exception {
            //given
            SignUpRequest signUpRequest = new SignUpRequest(
                    "mytamin@naver.com",
                    "password1234",
                    "강철멘탈",
                    "22",
                    "00"
            );
            given(userService.signup(any())).willReturn(UserResponse.of(user));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호"),
                                    fieldWithPath("nickname").description("*닉네임"),
                                    fieldWithPath("mytaminHour").description("마이타민 섭취 시간 HH (24시간)"),
                                    fieldWithPath("mytaminMin").description("마이타민 섭취 시간 MM")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.email").description("이메일"),
                                    fieldWithPath("data.nickname").description("닉네임"),
                                    fieldWithPath("data.profileImgUrl").description("프로필 이미지 URL (초기값 = \"\")"),
                                    fieldWithPath("data.beMyMessage").description("'되고싶은 내 모습' 메세지 (초기값 = \"\")"),
                                    fieldWithPath("data.mytaminHour").description("마이타민 섭취 시간 HH (24시간)"),
                                    fieldWithPath("data.mytaminMin").description("마이타민 섭취 시간 MM")
                            ))
                    );
        }

        @DisplayName("이메일 형식 오류")
        @Test
        void signup_2000(TestInfo testInfo) throws Exception {
            //given
            SignUpRequest signUpRequest = new SignUpRequest(
                    "XXX email pattern XXX",
                    "password1234",
                    "강철멘탈",
                    "22",
                    "00"
            );
            given(userService.signup(any())).willThrow(new MytaminException(EMAIL_PATTERN_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(2000))
                    .andExpect(jsonPath("errorName").value("EMAIL_PATTERN_ERROR"))
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호"),
                                    fieldWithPath("nickname").description("*닉네임"),
                                    fieldWithPath("mytaminHour").description("마이타민 섭취 시간 HH (24시간)"),
                                    fieldWithPath("mytaminMin").description("마이타민 섭취 시간 MM")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("비밀번호 형식 오류")
        @Test
        void signup_2001(TestInfo testInfo) throws Exception {
            //given
            SignUpRequest signUpRequest = new SignUpRequest(
                    "mytamin@naver.com",
                    "0000",
                    "강철멘탈",
                    "22",
                    "00"
            );
            given(userService.signup(any())).willThrow(new MytaminException(PASSWORD_PATTERN_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(2001))
                    .andExpect(jsonPath("errorName").value("PASSWORD_PATTERN_ERROR"))
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호"),
                                    fieldWithPath("nickname").description("*닉네임"),
                                    fieldWithPath("mytaminHour").description("마이타민 섭취 시간 HH (24시간)"),
                                    fieldWithPath("mytaminMin").description("마이타민 섭취 시간 MM")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("이미 가입된 유저")
        @Test
        void signup_2002(TestInfo testInfo) throws Exception {
            //given
            SignUpRequest signUpRequest = new SignUpRequest(
                    "mytamin@naver.com",
                    "password1234",
                    "강철멘탈",
                    "22",
                    "00"
            );
            given(userService.signup(any())).willThrow(new MytaminException(USER_ALREADY_EXIST_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(2002))
                    .andExpect(jsonPath("errorName").value("USER_ALREADY_EXIST_ERROR"))
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호"),
                                    fieldWithPath("nickname").description("*닉네임"),
                                    fieldWithPath("mytaminHour").description("마이타민 섭취 시간 HH (24시간)"),
                                    fieldWithPath("mytaminMin").description("마이타민 섭취 시간 MM")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("이미 사용 중인 닉네임")
        @Test
        void signup_2003(TestInfo testInfo) throws Exception {
            //given
            SignUpRequest signUpRequest = new SignUpRequest(
                    "mytamin@naver.com",
                    "password1234",
                    "강철멘탈",
                    "22",
                    "00"
            );
            given(userService.signup(any())).willThrow(new MytaminException(NICKNAME_DUPLICATE_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(2003))
                    .andExpect(jsonPath("errorName").value("NICKNAME_DUPLICATE_ERROR"))
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호"),
                                    fieldWithPath("nickname").description("*닉네임"),
                                    fieldWithPath("mytaminHour").description("마이타민 섭취 시간 HH (24시간)"),
                                    fieldWithPath("mytaminMin").description("마이타민 섭취 시간 MM")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("기본 로그인")
    class DefaultLoginTest {

        @DisplayName("성공")
        @Test
        void defaultLogin(TestInfo testInfo) throws Exception {
            //given
            LoginRequest loginRequest = new LoginRequest(
                    "mytamin@naver.com",
                    "password1234"
            );
            given(userService.defaultLogin(any())).willReturn(new TokenResponse(
                    "{{ACCESS_TOKEN}}",
                    "{{REFRESH_TOKEN}}"
            ));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/default/login")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.accessToken").description("액세스 토큰 (유효 기간 : 30일)"),
                                    fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (유효 기간 : 180일)")
                            ))
                    );
        }

        @DisplayName("가입되지 않은 유저")
        @Test
        void defaultLogin_3000(TestInfo testInfo) throws Exception {
            //given
            LoginRequest loginRequest = new LoginRequest(
                    "mytamin@naver.com",
                    "password1234"
            );
            given(userService.defaultLogin(any())).willThrow(new MytaminException(USER_NOT_FOUND_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/default/login")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(3000))
                    .andExpect(jsonPath("errorName").value("USER_NOT_FOUND_ERROR"))
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("잘못된 비밀번호")
        @Test
        void defaultLogin_3001(TestInfo testInfo) throws Exception {
            //given
            LoginRequest loginRequest = new LoginRequest(
                    "mytamin@naver.com",
                    "XXX password XXX"
            );
            given(userService.defaultLogin(any())).willThrow(new MytaminException(PASSWORD_MISMATCH_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/default/login")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(3001))
                    .andExpect(jsonPath("errorName").value("PASSWORD_MISMATCH_ERROR"))
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @DisplayName("이메일 중복 체크")
    @Test
    void checkEmailDuplication(TestInfo testInfo) throws Exception {
        //given
        String email = "mytamin@naver.com";
        given(userService.checkEmailDuplication(any())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(get("/auth/check/email/{email}", email));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                        pathParameters(
                                parameterWithName("email").description("*이메일")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").description("true : 이미 사용 중, false : 사용 가능")
                        ))
                );
    }

    @DisplayName("닉네임 중복 체크")
    @Test
    void checkNicknameDuplication(TestInfo testInfo) throws Exception {
        //given
        String nickname = "mental-zzang";
        given(userService.checkNicknameDuplication(any())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(get("/auth/check/nickname/{nickname}", nickname));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                        pathParameters(
                                parameterWithName("nickname").description("*닉네임")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").description("true : 이미 사용 중, false : 사용 가능")
                        ))
                );
    }

    @Nested
    @DisplayName("토큰 재발급")
    class ReissueTest {

        @DisplayName("성공")
        @Test
        void reissue(TestInfo testInfo) throws Exception {
            //given
            ReissueRequest tokenRequest = new ReissueRequest(
                    "mytamin@naver.com",
                    "{{REFRESH_TOKEN}}"
            );
            given(userService.tokenReIssue(any())).willReturn(new TokenResponse(
                    "{{ACCESS_TOKEN}}",
                    "{{REFRESH_TOKEN}}"
            ));

            //when
            ResultActions actions = mockMvc.perform(get("/auth/reissue")
                    .content(objectMapper.writeValueAsString(tokenRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("refreshToken").description("*리프레쉬 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.accessToken").description("액세스 토큰 (유효 기간 : 30일)"),
                                    fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (유효 기간 : 180일)")
                            ))
                    );
        }

        @DisplayName("가입되지 않은 유저")
        @Test
        void reissue_3000(TestInfo testInfo) throws Exception {
            //given
            ReissueRequest tokenRequest = new ReissueRequest(
                    "mytamin@naver.com",
                    "{{REFRESH_TOKEN}}"
            );
            given(userService.tokenReIssue(any())).willThrow(new MytaminException(USER_NOT_FOUND_ERROR));

            //when
            ResultActions actions = mockMvc.perform(get("/auth/reissue")
                    .content(objectMapper.writeValueAsString(tokenRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(3000))
                    .andExpect(jsonPath("errorName").value("USER_NOT_FOUND_ERROR"))
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("refreshToken").description("*리프레쉬 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("DB에 저장된 토큰과 불일치")
        @Test
        void reissue_1001(TestInfo testInfo) throws Exception {
            //given
            ReissueRequest tokenRequest = new ReissueRequest(
                    "mytamin@naver.com",
                    "{{REFRESH_TOKEN}}"
            );
            given(userService.tokenReIssue(any())).willThrow(new MytaminException(INVALID_TOKEN_ERROR));

            //when
            ResultActions actions = mockMvc.perform(get("/auth/reissue")
                    .content(objectMapper.writeValueAsString(tokenRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("errorCode").value(1001))
                    .andExpect(jsonPath("errorName").value("INVALID_TOKEN_ERROR"))
                    .andDo(document("/auth/" + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("refreshToken").description("*리프레쉬 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

}