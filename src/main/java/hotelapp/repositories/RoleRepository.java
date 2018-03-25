package hotelapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import hotelapp.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
