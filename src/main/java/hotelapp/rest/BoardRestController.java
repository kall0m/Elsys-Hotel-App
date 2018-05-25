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
@RequestMapping("/api")
public class BoardRestController {
    public static final Logger LOGGER = LoggerFactory.getLogger(BoardRestController.class);

    @Autowired
    private BoardService boardService;
    @Autowired
    private BossService bossService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private TaskService taskService;

    // -------------------Retrieve All Boards---------------------------------------------

    @RequestMapping(value = "/board.json/", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Board>> listAllBoards() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        List<Board> boards = new ArrayList<>(boss.getBoards());

        if (boards.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Board>>(boards, HttpStatus.OK);
    }

    // -------------------Retrieve Single Board------------------------------------------

    @RequestMapping(value = "/board/{id}.json", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getBoard(@PathVariable("id") Integer id) {
        LOGGER.info("Fetching Board with id {}", id);

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

        return new ResponseEntity<Board>(board, HttpStatus.OK);
    }

    // -------------------Create a Board-------------------------------------------

    private static final class BoardWorkers {
        private Board board;
        private List<String> workers;

        public BoardWorkers() { }

        public BoardWorkers(Board board, List<String> workers) {
            this.board = board;
            this.workers = workers;
        }

        public Board getBoard() {
            return board;
        }

        public void setBoard(Board board) {
            this.board = board;
        }

        public List<String> getWorkers() {
            return workers;
        }

        public void setWorkers(List<String> workers) {
            this.workers = workers;
        }
    }

    @RequestMapping(value = "/board.json/", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createBoard(@RequestBody BoardWorkers boardWorkers, UriComponentsBuilder ucBuilder) {
        Board board = boardWorkers.getBoard();
        List<String> workersEmails = boardWorkers.getWorkers();

        LOGGER.info("Creating Board : {}", board);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!this.bossService.checkBoardsCount(boss)) {
            LOGGER.error("Unable to create Board {}. Maximal board count reached. Update subscription.", board);
            return new ResponseEntity(new CustomErrorType("Unable to create Board " + board
                     + ". Maximal board count reached. Update subscription."), HttpStatus.CONFLICT);
        }

        board.setCreator(boss);

        if(workersEmails == null) {
            LOGGER.error("Unable to create Board {}. Not all fields filled.", board);
            return new ResponseEntity(new CustomErrorType("Unable to create Board " + board
                    + ". Not all fields filled."), HttpStatus.CONFLICT);
        }

        for(String email : workersEmails) {
            Worker worker = this.workerService.findByEmail(email);

            if(worker == null) {
                LOGGER.error("Unable to create Board {}. Worker with email {} doesn't exist.", board, email);
                return new ResponseEntity(new CustomErrorType("Unable to create Board " + board
                        + ". Worker with email " + email + " doesn't exist."), HttpStatus.CONFLICT);
            }

            board.addWorker(worker);
            worker.addBoard(board);
        }

        this.boardService.saveBoard(board);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/boards/{id}.json").buildAndExpand(board.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    // ------------------- Update a Board ------------------------------------------------

    @RequestMapping(value = "/board/{id}.json", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Integer id, @RequestBody BoardWorkers boardWorkers) {
        Board board = boardWorkers.getBoard();
        List<String> workersEmails = boardWorkers.getWorkers();

        LOGGER.info("Updating Board with id {}", id);

        if(!this.boardService.boardExists(id)) {
            LOGGER.error("Unable to update. Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to update. Board with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Board currentBoard = this.boardService.findBoard(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getBoards().contains(currentBoard)) {
            LOGGER.error("Unable to update. Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to update. Board with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        if(board.getName() != null) {
            currentBoard.setName(board.getName());
        }

        if(board.getDescription() != null) {
            currentBoard.setDescription(board.getDescription());
        }

        if(workersEmails != null) {
            for(Worker w : currentBoard.getWorkers()) {
                w.getBoards().remove(currentBoard);
            }

            currentBoard.setWorkers(new HashSet<>());

            for(String email : workersEmails) {
                Worker worker = this.workerService.findByEmail(email);

                if(worker == null) {
                    LOGGER.error("Unable to update Board {}. Worker with email {} doesn't exist.", board, email);
                    return new ResponseEntity(new CustomErrorType("Unable to update Board " + board
                            + ". Worker with email " + email + " doesn't exist."), HttpStatus.CONFLICT);
                }

                currentBoard.addWorker(worker);
                worker.addBoard(currentBoard);
            }
        }

        this.boardService.saveBoard(currentBoard);
        return new ResponseEntity<Board>(currentBoard, HttpStatus.OK);
    }

    // ------------------- Delete a Board-----------------------------------------

    @RequestMapping(value = "/board/{id}.json", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteBoard(@PathVariable("id") Integer id) {
        LOGGER.info("Fetching & Deleting Board with id {}", id);

        if(!this.boardService.boardExists(id)) {
            LOGGER.error("Unable to delete. Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Board with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        Board board = this.boardService.findBoard(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getBoards().contains(board)) {
            LOGGER.error("Unable to delete. Board with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Board with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        for(Worker w : board.getWorkers()) {
            w.getBoards().remove(board);
        }

        for(Task t : board.getTasks()) {
            this.taskService.deleteTask(t);
        }

        this.boardService.deleteBoard(board);

        return new ResponseEntity<Board>(HttpStatus.NO_CONTENT);
    }
}
