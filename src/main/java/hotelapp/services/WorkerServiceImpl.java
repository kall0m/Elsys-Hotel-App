package hotelapp.services;

import hotelapp.models.Worker;
import hotelapp.repositories.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerServiceImpl implements WorkerService {
    private WorkerRepository workerRepository;

    @Autowired
    public WorkerServiceImpl(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    public Worker findByEmail(String email) {
        return workerRepository.findByEmail(email);
    }

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public boolean workerExists(Integer id) {
        return workerRepository.exists(id);
    }

    public Worker findWorker(Integer id) {
        return workerRepository.findOne(id);
    }

    public void deleteWorker(Worker worker) {
        workerRepository.delete(worker);
    }

    public void saveWorker(Worker worker) {
        workerRepository.saveAndFlush(worker);
    }
}
