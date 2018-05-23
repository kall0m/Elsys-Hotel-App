package hotelapp.repositories;

import hotelapp.models.Boss;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BossRepository extends JpaRepository<Boss, Integer> {
    Boss findByEmail(String email);
    Boss findByConfirmationToken(String confirmationToken);
    Boss findByForgotPasswordToken(String forgotPasswordToken);
}
