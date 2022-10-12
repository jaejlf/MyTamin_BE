package great.job.mytamin.topic.myday.repository;

import great.job.mytamin.topic.myday.entity.Wish;
import great.job.mytamin.topic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findByUserAndIsHidden(User user, Boolean isHidden);
    Optional<Wish> findByUserAndWishText(User user, String wishText);
}