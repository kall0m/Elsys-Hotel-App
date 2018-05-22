package hotelapp.rest;

import hotelapp.models.Board;
import hotelapp.models.Boss;
import hotelapp.models.Task;
import hotelapp.models.Worker;
import hotelapp.services.BoardService;
import hotelapp.services.BossService;
import hotelapp.services.TaskService;
import hotelapp.services.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
public class TaskRestController {
    public static final Logger LOGGER = LoggerFactory.getLogger(BoardRestController.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private BossService bossService;
    @Autowired
    private WorkerService workerService;

    // -------------------Retrieve All Tasks---------------------------------------------

    @RequestMapping(value = "/board/{id}/task.json", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Task>> listAllTasks(@PathVariable("id") Integer id) {
        if(!this.boardService.boardExists(id)) {
            LOGGER.error("Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Board with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        Board board = this.boardService.findBoard(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getBoards().contains(board)) {
            LOGGER.error("Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Board with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        List<Task> tasks = new ArrayList<>(board.getTasks());

        if (tasks.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    }

    // -------------------Retrieve Single Task------------------------------------------

    @RequestMapping(value = "/board/{id}/task/{id2}.json", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getBoard(@PathVariable("id") Integer id, @PathVariable("id2") Integer id2) {
        LOGGER.info("Fetching Task with id {}", id2);

        if(!this.boardService.boardExists(id)) {
            LOGGER.error("Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Board with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        if(!this.taskService.taskExists(id2)) {
            LOGGER.error("Task with id {} not found.", id2);
            return new ResponseEntity(new CustomErrorType("Task with id " + id2
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        Board board = this.boardService.findBoard(id);

        Task task = this.taskService.findTask(id2);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getBoards().contains(board)) {
            LOGGER.error("Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Board with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        if(!board.getTasks().contains(task)) {
            LOGGER.error("Task with id {} not found.", id2);
            return new ResponseEntity(new CustomErrorType("Task with id " + id2
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    // -------------------Create a Task-------------------------------------------

    @RequestMapping(value = "/board/{id}/task.json", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createTask(@PathVariable("id") Integer id, @RequestBody Task task, UriComponentsBuilder ucBuilder) {
        LOGGER.info("Creating Task : {}", task);

        if(!this.boardService.boardExists(id)) {
            LOGGER.error("Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Board with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        Board board = this.boardService.findBoard(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getBoards().contains(board)) {
            LOGGER.error("Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Board with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }

        if(!this.bossService.checkTasksInBoardCount(board)) {
            LOGGER.error("Unable to create Task {}. Maximal task count reached. Update subscription.", task);
            return new ResponseEntity(new CustomErrorType("Unable to create Task " + task
                    + ". Maximal task count reached. Update subscription."), HttpStatus.CONFLICT);
        }

        task.setAssignor(boss);
        task.setBoard(board);

        this.taskService.saveTask(task);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/boards/{id}/task/{id2}.json").buildAndExpand(board.getId(), task.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    // ------------------- Update a Task ------------------------------------------------

    @RequestMapping(value = "/board/{id}/task/{id2}.json", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateTask(@PathVariable("id") Integer id, @PathVariable("id2") Integer id2, @RequestBody Task task) {
        LOGGER.info("Updating Task with id {}", id2);

        if(!this.boardService.boardExists(id)) {
            LOGGER.error("Unable to update. Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to update. Board with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(!this.taskService.taskExists(id2)) {
            LOGGER.error("Unable to update. Task with id {} not found.", id2);
            return new ResponseEntity(new CustomErrorType("Unable to update. Task with id " + id2 + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Board board = this.boardService.findBoard(id);

        Task currentTask = this.taskService.findTask(id2);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getBoards().contains(board)) {
            LOGGER.error("Unable to update. Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to update. Board with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(!board.getTasks().contains(currentTask)) {
            LOGGER.error("Unable to update. Task with id {} not found.", id2);
            return new ResponseEntity(new CustomErrorType("Unable to update. Task with id " + id2 + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(task.getDescription() != null) {
            currentTask.setDescription(task.getDescription());
        }

        this.taskService.saveTask(currentTask);
        return new ResponseEntity<Task>(currentTask, HttpStatus.OK);
    }

    // ------------------- Delete a Task-----------------------------------------

    @RequestMapping(value = "/board/{id}/task/{id2}.json", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteTask(@PathVariable("id") Integer id, @PathVariable("id2") Integer id2) {
        LOGGER.info("Fetching & Deleting Task with id {}", id2);

        if(!this.boardService.boardExists(id)) {
            LOGGER.error("Unable to delete. Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Board with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(!this.taskService.taskExists(id2)) {
            LOGGER.error("Unable to delete. Task with id {} not found.", id2);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Task with id " + id2 + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Board board = this.boardService.findBoard(id);

        Task task = this.taskService.findTask(id2);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getBoards().contains(board)) {
            LOGGER.error("Unable to delete. Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Board with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(!board.getTasks().contains(task)) {
            LOGGER.error("Unable to delete. Task with id {} not found.", id2);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Task with id " + id2 + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        this.taskService.deleteTask(task);

        return new ResponseEntity<Task>(HttpStatus.NO_CONTENT);
    }
}