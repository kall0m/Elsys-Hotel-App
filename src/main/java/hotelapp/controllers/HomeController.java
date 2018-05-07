package hotelapp.controllers;

import hotelapp.bindingModels.GenerateWorkerAccountBindingModel;
import hotelapp.models.Role;
import hotelapp.models.Worker;
import hotelapp.services.RoleService;
import hotelapp.services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class HomeController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private WorkerService workerService;

    @GetMapping("/")
    public String index(Model model)
    {
        model.addAttribute("view", "home/index");
        return "base-layout";
    }

    @GetMapping("/about")
    public String aboutUs(Model model)
    {
        model.addAttribute("view", "home/about");
        return "base-layout";
    }

    @GetMapping("/worker/generate-account")
    public String generateWorkerAccount(Model model) {
        model.addAttribute("view", "home/generate-worker-account");
        return "base-layout";
    }

    @PostMapping("worker/generate-account")
    public String generateWorkerAccountProcess(RedirectAttributes redir, GenerateWorkerAccountBindingModel generateWorkerAccountBindingModel) {
        List<String> emails = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> passwords = new ArrayList<>();

        for(int i = 0; i < generateWorkerAccountBindingModel.getCount(); i++) {
            byte[] array = new byte[7]; // length is bounded by 7

            new Random().nextBytes(array);
            emails.add(new String(array, Charset.forName("ASCII")));

            new Random().nextBytes(array);
            names.add(new String(array, Charset.forName("ASCII")));

            new Random().nextBytes(array);
            passwords.add("123");

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            Worker worker = new Worker(
                    emails.get(i),
                    names.get(i),
                    bCryptPasswordEncoder.encode(passwords.get(i))
            );

            Role workerRole = this.roleService.findRole("ROLE_USER");

            worker.addRole(workerRole);

            this.workerService.saveWorker(worker);
        }

        redir.addFlashAttribute("emails", emails);
        redir.addFlashAttribute("names", names);
        redir.addFlashAttribute("passwords", passwords);

        return "redirect:/generated-accounts";
    }

    @GetMapping("/generated-accounts")
    public String generatedWorkerAccounts(Model model) {
        model.addAttribute("view", "home/generated-accounts");
        return "base-layout";
    }
}


