package hotelapp.repositories;

import hotelapp.models.Boss;
import hotelapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BossRepository extends JpaRepository<Boss, Integer> {
    Boss findByEmail(String email);
}
