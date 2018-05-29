package hotelapp.rest;

import hotelapp.models.*;
import hotelapp.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TaskRestController {
    public static final Logger LOGGER = LoggerFactory.getLogger(TaskRestController.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private BossService bossService;
    @Autowired
    private WorkerService workerService;

    // -------------------Retrieve All Tasks---------------------------------------------

    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Task>> listAllTasks() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boss boss = this.bossService.findByEmail(principal.getUsername());
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        List<Task> tasks = new ArrayList<>();

        if(boss != null) {
            tasks = new ArrayList<>(boss.getTasks());
        } else if(worker != null) {
            tasks = new ArrayList<>(worker.getBoss().getTasks()
            .stream()
            .filter(t -> t.getType().equals(worker.getType())).collect(Collectors.toList()));
        }

        if (tasks.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    }

    // -------------------Retrieve Single Task------------------------------------------

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTask(@PathVariable("id") Integer id) {
        LOGGER.info("Fetching Task with id {}", id);

        if(!this.taskService.taskExists(id)) {
            LOGGER.error("Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Task with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boss boss = this.bossService.findByEmail(principal.getUsername());
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        if(boss != null) {
            if(!boss.getTasks().contains(task)) {
                LOGGER.error("Task with id {} not found.", id);
                return new ResponseEntity(new CustomErrorType("Task with id " + id
                        + " not found"), HttpStatus.NOT_FOUND);
            }
        } else if(worker != null) {
            if(!worker.getBoss().getTasks().contains(task)) {
                LOGGER.error("Task with id {} not found.", id);
                return new ResponseEntity(new CustomErrorType("Task with id " + id
                        + " not found"), HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    // -------------------Create a Task-------------------------------------------

    private static final class TaskType {
        private Task task;
        private String typeName;

        public TaskType() {
        }

        public TaskType(Task task, String typeName) {
            this.task = task;
            this.typeName = typeName;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public ResponseEntity<?> createTask(@RequestBody TaskType taskType, UriComponentsBuilder ucBuilder) {
        Task task = taskType.getTask();
        String typeName = taskType.getTypeName();

        LOGGER.info("Creating Task : {}", task);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        Type type = this.typeService.findTypeByName(boss, typeName);

        if(type == null) {
            LOGGER.error("Unable to create. Type with name {} not found.", typeName);
            return new ResponseEntity(new CustomErrorType("Unable to create. Type with name " + typeName + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        task.setAssignor(boss);
        task.setType(type);

        this.taskService.saveTask(task);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/task/{id}").buildAndExpand(task.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    // -------------------Update a Task------------------------------------------------

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public ResponseEntity<?> updateTask(@PathVariable("id") Integer id, @RequestBody TaskType taskType) {
        Task task = taskType.getTask();
        String typeName = taskType.getTypeName();

        LOGGER.info("Updating Task with id {}", id);

        if(!this.taskService.taskExists(id)) {
            LOGGER.error("Unable to update. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to update. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Task currentTask = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getTasks().contains(currentTask)) {
            LOGGER.error("Unable to update. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to update. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(task.getDescription() != null) {
            currentTask.setDescription(task.getDescription());
        }

        Type type = this.typeService.findTypeByName(boss, typeName);

        if(type == null) {
            LOGGER.error("Unable to update. Type with name {} not found.", typeName);
            return new ResponseEntity(new CustomErrorType("Unable to update. Type with name " + typeName + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        currentTask.setType(type);

        this.taskService.saveTask(currentTask);
        return new ResponseEntity<Task>(currentTask, HttpStatus.OK);
    }

    // -------------------Delete a Task-----------------------------------------

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public ResponseEntity<?> deleteTask(@PathVariable("id") Integer id) {
        LOGGER.info("Fetching & Deleting Task with id {}", id);

        if(!this.taskService.taskExists(id)) {
            LOGGER.error("Unable to delete. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getTasks().contains(task)) {
            LOGGER.error("Unable to delete. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        this.taskService.deleteTask(task);

        return new ResponseEntity<Task>(HttpStatus.NO_CONTENT);
    }

    // -------------------Handle a Task by logged in Worker-----------------------------------------
    @RequestMapping(value = "/tasks/{id}/workers", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_WORKER')")
    public ResponseEntity<?> handleTaskByWorker(@PathVariable("id") Integer id) {
        LOGGER.info("Handle Task with id {} by Worker.", id);

        if(!this.taskService.taskExists(id)) {
            LOGGER.error("Unable to handle Task by Worker. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to handle Task by Worker. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        if(!worker.getBoss().getTasks().contains(task)) {
            LOGGER.error("Unable to handle Task by Worker. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to handle Task by Worker. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        task.setStatus("Doing");
        task.setWorker(worker);

        worker.addTask(task);
        worker.setBusy(true);

        this.taskService.saveTask(task);

        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    // -------------------Reject a Task by logged in Worker-----------------------------------------
    @RequestMapping(value = "/tasks/{id}/workers", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ROLE_WORKER')")
    public ResponseEntity<?> rejectTaskByWorker(@PathVariable("id") Integer id) {
        LOGGER.info("Reject Task with id {} by Worker.", id);

        if(!this.taskService.taskExists(id)) {
            LOGGER.error("Unable to reject Task by Worker. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to reject Task by Worker. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        if(!worker.getBoss().getTasks().contains(task)) {
            LOGGER.error("Unable to reject Task by Worker. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to reject Task by Worker. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(!worker.getTasks().contains(task)) {
            LOGGER.error("Unable to reject Task by Worker. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to reject Task by Worker. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        task.setStatus("To do");
        task.setWorker(null);

        worker.removeTask(task);
        worker.setBusy(false);

        this.taskService.saveTask(task);

        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    // -------------------Finish a Task by logged in Worker-----------------------------------------
    @RequestMapping(value = "/tasks/{id}/workers", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('ROLE_WORKER')")
    public ResponseEntity<?> finishTaskByWorker(@PathVariable("id") Integer id) {
        LOGGER.info("Finish Task with id {} by Worker.", id);

        if(!this.taskService.taskExists(id)) {
            LOGGER.error("Unable to finish Task by Worker. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to finish Task by Worker. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        if(!worker.getBoss().getTasks().contains(task)) {
            LOGGER.error("Unable to finish Task by Worker. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to finish Task by Worker. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(!worker.getTasks().contains(task)) {
            LOGGER.error("Unable to finish Task by Worker. Task with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to finish Task by Worker. Task with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        task.setStatus("Done");

        this.taskService.saveTask(task);

        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }
}