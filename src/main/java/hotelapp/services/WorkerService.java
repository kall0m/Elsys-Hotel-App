package hotelapp.services;

import hotelapp.models.Worker;

import java.util.List;

public interface WorkerService {
    Worker findByEmail(String email);

    List<Worker> getAllWorkers();

    void saveWorker(Worker worker);
}
