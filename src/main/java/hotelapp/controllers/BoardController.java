package hotelapp.controllers;

import hotelapp.bindingModels.BoardBindingModel;
import hotelapp.models.Board;
import hotelapp.models.Boss;
import hotelapp.models.Task;
import hotelapp.models.Worker;
import hotelapp.services.BoardService;
import hotelapp.services.BossService;
import hotelapp.services.WorkerService;
import hotelapp.validators.BossValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BossService bossService;
    @Autowired
    private WorkerService workerService;

    @GetMapping("/boards")
    @PreAuthorize("isAuthenticated()")
    public String boards(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boss boss = this.bossService.findByEmail(principal.getUsername());
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        List<Board> boards = new ArrayList<>();

        if(boss != null) {
            boards = new ArrayList<>(boss.getBoards());
        } else if(worker != null) {
            boards = new ArrayList<>(worker.getBoards());
        }

        model.addAttribute("boards", boards);
        model.addAttribute("view", "board/boss-boards");

        return "base-layout";
    }

    @GetMapping("/boards/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {
        List<Worker> workers = this.workerService.getAllWorkers();

        model.addAttribute("workers", workers);
        model.addAttribute("view", "board/create");

        return "base-layout";
    }

    @PostMapping("/boards/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(BoardBindingModel boardBindingModel, BindingResult bindingResult) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(boss == null) {
            return "redirect:/boards/create";
        }

        /*BossValidator bossValidator = new BossValidator();
        bossValidator.validate(boss, bindingResult);

        if (bindingResult.hasErrors()){
            return "redirect:/boards/create";
        }*/

        if(!this.bossService.checkBoardsCount(boss)) {
            return "redirect:/boards/create";
        }

        Board board = new Board(
                boardBindingModel.getName(),
                boardBindingModel.getDescription(),
                boss
        );

        if(boardBindingModel.getWorkers() == null) {
            return "redirect:/boards/create";
        }

        for(String w : boardBindingModel.getWorkers()) {
            Worker worker = this.workerService.findByEmail(w);

            if(worker == null) {
                return "redirect:/boards/create";
            }

            board.addWorker(worker);
            worker.addBoard(board);

            //this.workerService.saveWorker(worker);
        }

        this.boardService.saveBoard(board);

        return "redirect:/boards";
    }

    @GetMapping("/boards/{id}")
    @PreAuthorize("isAuthenticated()")
    public String details(@PathVariable Integer id, Model model) {
        if(!this.boardService.boardExists(id)) {
            return "redirect:/boards";
        }

        Board board = this.boardService.findBoard(id);

        List<Task> tasks = new ArrayList<>(board.getTasks());

        model.addAttribute("board", board);
        model.addAttribute("tasks", tasks);
        model.addAttribute("view", "board/details");

        return "base-layout";
    }
}
