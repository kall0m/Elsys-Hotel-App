package hotelapp.controllers;

import hotelapp.bindingModels.TaskBindingModel;
import hotelapp.models.*;
import hotelapp.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskController {
    @Autowired
    TaskService taskService;
    @Autowired
    TypeService typeService;
    @Autowired
    BossService bossService;
    @Autowired
    WorkerService workerService;

    @GetMapping("/tasks")
    @PreAuthorize("isAuthenticated()")
    public String tasks(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boss boss = this.bossService.findByEmail(principal.getUsername());
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        List<Task> tasks = new ArrayList<>();

        if(boss != null) {
            tasks = new ArrayList<>(boss.getTasks());
        } else if(worker != null) {
            tasks = new ArrayList<>(worker.getBoss().getTasks());
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("view", "home/tasks");

        return "base-layout";
    }

    @GetMapping("/task/create")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String create(Model model, RedirectAttributes redir) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        model.addAttribute("types", boss.getTypes());
        model.addAttribute("view", "task/create");

        return "base-layout";
    }

    @PostMapping("/task/create")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String createProcess(TaskBindingModel taskBindingModel, RedirectAttributes redir) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        Type type = this.typeService.findTypeByName(boss, taskBindingModel.getTypeName());

        if(type == null) {
            redir.addFlashAttribute("message", NotificationMessages.TYPE_DOESNT_EXIST);
            return "redirect:/task/create";
        }

        Task task = new Task(
                taskBindingModel.getDescription()
        );

        task.setAssignor(boss);
        task.setType(type);

        this.taskService.saveTask(task);

        redir.addFlashAttribute("message", NotificationMessages.TASK_SUCCESSFULLY_CREATED);

        return "redirect:/tasks/" + task.getId();
    }

    @GetMapping("/tasks/{id}")
    @PreAuthorize("isAuthenticated()")
    public String details(@PathVariable Integer id, Model model, RedirectAttributes redir) {
        if(!this.taskService.taskExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getTasks().contains(task)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        model.addAttribute("task", task);
        model.addAttribute("view", "task/details");

        return "base-layout";
    }

    @GetMapping("/tasks/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String update(@PathVariable Integer id, Model model, RedirectAttributes redir) {
        if(!this.taskService.taskExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getTasks().contains(task)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        model.addAttribute("task", task);
        model.addAttribute("types", boss.getTypes());
        model.addAttribute("view", "task/edit");

        return "base-layout";
    }

    @PostMapping("/tasks/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String updateProcess(TaskBindingModel taskBindingModel, @PathVariable Integer id, RedirectAttributes redir) {
        if(!this.taskService.taskExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        Task currentTask = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getTasks().contains(currentTask)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        if(taskBindingModel.getDescription() != null) {
            currentTask.setDescription(taskBindingModel.getDescription());
        }

        Type type = this.typeService.findTypeByName(boss, taskBindingModel.getTypeName());

        if(type == null) {
            redir.addFlashAttribute("message", NotificationMessages.TYPE_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        currentTask.setType(type);

        this.taskService.saveTask(currentTask);

        redir.addFlashAttribute("message", NotificationMessages.CHANGES_SAVED);

        return "redirect:/tasks/" + currentTask.getId();
    }

    @GetMapping("/tasks/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String delete(@PathVariable Integer id, Model model, RedirectAttributes redir) {
        if(!this.taskService.taskExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getTasks().contains(task)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        model.addAttribute("task", task);
        model.addAttribute("types", boss.getTypes());
        model.addAttribute("view", "task/delete");

        return "base-layout";
    }

    @PostMapping("/tasks/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String deleteProcess(@PathVariable Integer id, RedirectAttributes redir) {
        if(!this.taskService.taskExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        Task task = this.taskService.findTask(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getTasks().contains(task)) {
            redir.addFlashAttribute("message", NotificationMessages.TASK_DOESNT_EXIST);
            return "redirect:/tasks";
        }

        this.taskService.deleteTask(task);

        redir.addFlashAttribute("message", NotificationMessages.TASK_SUCCESSFULLY_DELETED);

        return "redirect:/tasks";
    }
}
