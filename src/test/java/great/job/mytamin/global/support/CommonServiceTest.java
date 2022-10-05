package great.job.mytamin.global.support;

import great.job.mytamin.domain.care.repository.CareRepository;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.enumerate.Provider;
import great.job.mytamin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static great.job.mytamin.domain.user.enumerate.Provider.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommonServiceTest {

    public MockMvc mockMvc;

    @Autowired public UserRepository userRepository;
    @Autowired public MytaminRepository mytaminRepository;
    @Autowired public CareRepository careRepository;

    // Mock Data
    public User user;

    @BeforeEach
    public void setUp() {
        user = new User(
                "tester@mock.com",
                "{{ENCODED_PASSWORD}}",
                "테스터",
                DEFAULT,
                null,
                null,
                false
        );
        userRepository.save(user);
    }

}
