package hotelapp.services;

import hotelapp.models.Worker;

import java.util.List;

public interface WorkerService {
    Worker findByEmail(String email);

    List<Worker> getAllWorkers();

    boolean workerExists(Integer id);

    Worker findWorker(Integer id);

    void deleteWorker(Worker worker);

    void saveWorker(Worker worker);
}
