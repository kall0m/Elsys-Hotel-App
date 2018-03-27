package hotelapp.repositories;

import hotelapp.models.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    Worker findByEmail(String email);
}
