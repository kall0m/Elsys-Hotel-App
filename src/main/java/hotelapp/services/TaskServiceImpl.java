package hotelapp.services;

import hotelapp.models.Task;
import hotelapp.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public boolean taskExists(Integer id) {
        return taskRepository.exists(id);
    }

    public Task findTask(Integer id) {
        return taskRepository.findOne(id);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    public void saveTask(Task task) {
        taskRepository.saveAndFlush(task);
    }
}
