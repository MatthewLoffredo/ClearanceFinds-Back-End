package clearancegood.repositories;

import clearancegood.entities.Good;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodRepository extends JpaRepository<Good, Long> {
    boolean existsByPicture(String picture);

    boolean existsByName(String name);
}
