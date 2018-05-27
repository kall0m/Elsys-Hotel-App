package hotelapp.controllers;

import hotelapp.services.BossService;
import hotelapp.services.RoleService;
import hotelapp.services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private BossService bossService;

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
}


