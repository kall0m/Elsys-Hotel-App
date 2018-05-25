package hotelapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import hotelapp.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findById(Integer id);
}
