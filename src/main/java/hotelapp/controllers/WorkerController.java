package hotelapp.controllers;

import hotelapp.bindingModels.WorkerBindingModel;
import hotelapp.models.Boss;
import hotelapp.models.Role;
import hotelapp.models.Type;
import hotelapp.models.Worker;
import hotelapp.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class WorkerController {
    @Autowired
    private BossService bossService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TypeService typeService;

    private static String getRandomString(int length) {
        StringBuilder alphanumeric = new StringBuilder();
        String alphabetCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        alphanumeric.append(alphabetCaps);
        alphanumeric.append(alphabetCaps.toLowerCase());
        alphanumeric.append("1234567890");

        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * alphanumeric.length());
            salt.append(alphanumeric.charAt(index));
        }

        return salt.toString();

    }

    @GetMapping("/workers")
    @PreAuthorize("isAuthenticated()")
    public String workers(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boss boss = this.bossService.findByEmail(principal.getUsername());
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        List<Worker> workers = new ArrayList<>();

        if(boss != null) {
            workers = new ArrayList<>(boss.getWorkers());
        } else if(worker != null) {
            workers = new ArrayList<>(worker.getBoss().getWorkers());
        }

        model.addAttribute("workers", workers);
        model.addAttribute("view", "home/workers");

        return "base-layout";
    }

    @GetMapping("/worker/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        model.addAttribute("types", boss.getTypes());
        model.addAttribute("view", "worker/create");

        return "base-layout";
    }

    @PostMapping("worker/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(RedirectAttributes redir, WorkerBindingModel workerBindingModel) {
        List<String> emails = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> passwords = new ArrayList<>();

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        Type type = this.typeService.findTypeByName(boss, workerBindingModel.getTypeName());

        if(type == null) {
            redir.addFlashAttribute("message", NotificationMessages.TYPE_DOESNT_EXIST);
            return "redirect:/worker/create";
        }

        List<Worker> workers = new ArrayList<>();

        for(int i = 0; i < workerBindingModel.getCount(); i++) {
            emails.add(new String(getRandomString(8)));

            names.add(new String(getRandomString(8)));

            passwords.add(new String(getRandomString(8)));

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            Worker worker = new Worker(
                    emails.get(i),
                    names.get(i),
                    bCryptPasswordEncoder.encode(passwords.get(i)),
                    passwords.get(i)
            );

            worker.setBoss(boss);
            worker.setEnabled(true);
            worker.setType(type);

            Role workerRole = this.roleService.findRole("ROLE_WORKER");

            worker.addRole(workerRole);

            this.workerService.saveWorker(worker);

            workers.add(worker);
        }

        redir.addFlashAttribute("workers", workers);

        redir.addFlashAttribute("message", NotificationMessages.CREATED_WORKERS(workerBindingModel.getCount()));

        return "redirect:/workers";
    }

    @GetMapping("/worker/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id, RedirectAttributes redir) {
        if(!this.workerService.workerExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.WORKER_DOESNT_EXIST);
            return "/workers";
        }

        Worker worker = this.workerService.findWorker(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        model.addAttribute("worker", worker);
        model.addAttribute("types", boss.getTypes());
        model.addAttribute("view", "worker/delete");

        return "base-layout";
    }

    @PostMapping("/worker/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id, RedirectAttributes redir) {
        if(!this.workerService.workerExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.WORKER_DOESNT_EXIST);
            return "/workers";
        }

        Worker worker = this.workerService.findWorker(id);

        this.workerService.deleteWorker(worker);

        redir.addFlashAttribute("message", NotificationMessages.WORKER_SUCCESSFULLY_DELETED);

        return "redirect:/workers";
    }
}
