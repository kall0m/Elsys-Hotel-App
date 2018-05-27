package hotelapp.rest;

import hotelapp.models.Boss;
import hotelapp.models.Worker;
import hotelapp.services.BossService;
import hotelapp.services.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WorkerRestController {
    public static final Logger LOGGER = LoggerFactory.getLogger(TaskRestController.class);

    @Autowired
    private BossService bossService;
    @Autowired
    private WorkerService workerService;

    // -------------------Retrieve All Workers---------------------------------------------

    @RequestMapping(value = "/workers", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Worker>> listAllWorkers() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boss boss = this.bossService.findByEmail(principal.getUsername());
        Worker worker = this.workerService.findByEmail(principal.getUsername());

        List<Worker> workers = new ArrayList<>();

        if(boss != null) {
            workers = new ArrayList<>(boss.getWorkers());
        } else if(worker != null) {
            workers = new ArrayList<>(worker.getBoss().getWorkers());
        }

        if (workers.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Worker>>(workers, HttpStatus.OK);
    }
}
