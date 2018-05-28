package hotelapp.controllers;

import hotelapp.bindingModels.TypeBindingModel;
import hotelapp.models.Boss;
import hotelapp.models.Type;
import hotelapp.models.Worker;
import hotelapp.services.BossService;
import hotelapp.services.NotificationMessages;
import hotelapp.services.TypeService;
import hotelapp.services.WorkerService;
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
public class TypeController {
    @Autowired
    private BossService bossService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private TypeService typeService;

    @GetMapping("/types")
    @PreAuthorize("isAuthenticated()")
    public String types(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boss boss = this.bossService.findByEmail(principal.getUsername());
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        List<Type> types = new ArrayList<>();

        if(boss != null) {
            types = new ArrayList<>(boss.getTypes());
        } else if(worker != null) {
            types = new ArrayList<>(worker.getBoss().getTypes());
        }

        model.addAttribute("types", types);
        model.addAttribute("view", "home/types");

        return "base-layout";
    }

    @GetMapping("/type/create")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String create(Model model, RedirectAttributes redir) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!this.bossService.checkTypesCount(boss)) {
            redir.addFlashAttribute("message", NotificationMessages.MAXIMAL_TYPES_COUNT_LIMIT_REACHED);
            return "redirect:/types";
        }

        model.addAttribute("view", "type/create");

        return "base-layout";
    }

    @PostMapping("/type/create")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String createProcess(TypeBindingModel typeBindingModel, RedirectAttributes redir) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss boss = this.bossService.findByEmail(principal.getUsername());

        if(!this.bossService.checkTypesCount(boss)) {
            redir.addFlashAttribute("message", NotificationMessages.MAXIMAL_TYPES_COUNT_LIMIT_REACHED);
            return "redirect:/types";
        }

        Type type = new Type(
                typeBindingModel.getName()
        );

        type.setBoss(boss);

        this.typeService.saveType(type);

        redir.addFlashAttribute("message", NotificationMessages.TYPE_SUCCESSFULLY_CREATED);

        return "redirect:/types";
    }

    @GetMapping("/type/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String delete(Model model, @PathVariable Integer id, RedirectAttributes redir) {
        if(!this.typeService.typeExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.TYPE_DOESNT_EXIST);
            return "redirect:/types";
        }

        Type type = this.typeService.findType(id);

        model.addAttribute("type", type);
        model.addAttribute("view", "type/delete");

        return "base-layout";
    }

    @PostMapping("/type/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_BOSS')")
    public String deleteProcess(@PathVariable Integer id, RedirectAttributes redir) {
        if(!this.typeService.typeExists(id)) {
            redir.addFlashAttribute("message", NotificationMessages.TYPE_DOESNT_EXIST);
            return "redirect:/types";
        }

        Type type = this.typeService.findType(id);

        this.typeService.deleteType(type);

        redir.addFlashAttribute("message", NotificationMessages.TYPE_SUCCESSFULLY_DELETED);

        return "redirect:/types";
    }
}
