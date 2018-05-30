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
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
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
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String create(Model model, RedirectAttributes redir) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!this.bossService.checkWorkerAccountsCount(boss)) {
            redir.addFlashAttribute("message", NotificationMessages.MAXIMAL_ACCS_COUNT_LIMIT_REACHED);
            return "redirect:/workers";
        }

        model.addAttribute("types", boss.getTypes());
        model.addAttribute("view", "worker/create");

        return "base-layout";
    }

    @PostMapping("worker/create")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String createProcess(RedirectAttributes redir, WorkerBindingModel workerBindingModel) {
        List<String> emails = new ArrayList<>();
        List<String> passwords = new ArrayList<>();

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!this.bossService.checkWorkerAccountsCount(boss)) {
            redir.addFlashAttribute("message", NotificationMessages.MAXIMAL_ACCS_COUNT_LIMIT_REACHED);
            return "redirect:/workers";
        }

        Type type = this.typeService.findTypeByName(boss, workerBindingModel.getTypeName());

        if(type == null) {
            redir.addFlashAttribute("message", NotificationMessages.TYPE_DOESNT_EXIST);
            return "redirect:/worker/create";
        }

        List<Worker> workers = new ArrayList<>();

        for(int i = 0; i < workerBindingModel.getCount(); i++) {
            if(!this.bossService.checkWorkerAccountsCount(boss)) {
                break;
            }

            emails.add(new String(getRandomString(8)));

            while(this.workerService.findByEmail(emails.get(i)) != null) {
                emails.remove(i);
                emails.add(new String(getRandomString(8)));
            }

            passwords.add(new String(getRandomString(8)));

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            Worker worker = new Worker(
                    emails.get(i),
                    null,
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

    @GetMapping("/workers/{id}")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String details(@PathVariable Integer id, Model model, RedirectAttributes redir) {
        if(!this.workerService.workerExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.WORKER_DOESNT_EXIST);
            return "redirect:/workers";
        }

        Worker worker = this.workerService.findWorker(id);

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!boss.getWorkers().contains(worker)) {
            redir.addFlashAttribute("message", NotificationMessages.WORKER_DOESNT_EXIST);
            return "redirect:/workers";
        }

        model.addAttribute("worker", worker);
        model.addAttribute("view", "worker/details");

        return "base-layout";
    }

    @GetMapping("/worker/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
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
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
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
