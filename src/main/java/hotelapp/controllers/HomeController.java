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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class HomeController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private WorkerService workerService;

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
            emails.add(new String(getRandomString(8)));

            names.add(new String(getRandomString(8)));

            passwords.add(new String(getRandomString(8)));

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


