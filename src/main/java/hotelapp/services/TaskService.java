package hotelapp.services;

import hotelapp.models.Task;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();

    boolean taskExists(Integer id);

    Task findTask(Integer id);

    void deleteTask(Task board);

    void saveTask(Task board);
}
