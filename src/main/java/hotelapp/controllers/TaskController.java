package hotelapp.controllers;

import hotelapp.bindingModels.TaskBindingModel;
import hotelapp.models.Board;
import hotelapp.models.Boss;
import hotelapp.models.Task;
import hotelapp.models.Worker;
import hotelapp.services.BoardService;
import hotelapp.services.BossService;
import hotelapp.services.TaskService;
import hotelapp.services.WorkerService;
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
public class TaskController {
    @Autowired
    TaskService taskService;
    @Autowired
    BoardService boardService;
    @Autowired
    BossService bossService;
    @Autowired
    WorkerService workerService;

    @GetMapping("/boards/{id}/tasks/create")
    @PreAuthorize("isAuthenticated()")
    public String create(@PathVariable Integer id, Model model) {
        if(!this.boardService.boardExists(id)) {
            return "redirect:/boards";
        }

        Board board = this.boardService.findBoard(id);

        List<Worker> workers = new ArrayList<>(board.getWorkers());

        model.addAttribute("board", board);
        model.addAttribute("workers", workers);
        model.addAttribute("view", "task/create");

        return "base-layout";
    }

    @PostMapping("/boards/{id}/tasks/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(@PathVariable Integer id, TaskBindingModel taskBindingModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/board/{id}/tasks/create";
        }

        if(!this.boardService.boardExists(id)) {
            return "redirect:/boards";
        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(boss == null) {
            return "redirect:/board/{id}/tasks/create";
        }

        Board board = this.boardService.findBoard(id);

        Task task = new Task(
                taskBindingModel.getDescription()
        );

        task.setAssignor(boss);
        task.setBoard(board);
        task.setWorker(workerService.findByEmail(taskBindingModel.getWorker()));

        this.taskService.saveTask(task);

        return "redirect:/boards";
    }
}
