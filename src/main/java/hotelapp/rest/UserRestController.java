package hotelapp.rest;

import hotelapp.bindingModels.RestLogin;
import hotelapp.models.Boss;
import hotelapp.models.User;
import hotelapp.models.Worker;
import hotelapp.security.JwtAuthenticationResponse;
import hotelapp.security.JwtTokenProvider;
import hotelapp.services.BossService;
import hotelapp.services.UserService;
import hotelapp.services.WorkerService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserRestController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private BossService bossService;
    @Autowired
    private WorkerService workerService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody RestLogin restLogin) {
        User user = this.userService.findByEmail(restLogin.getEmail());

        if(user != null) {
            if(!tokenProvider.validateToken(user.getJwtToken())) {
                user.setJwtToken(null);
            }

            if(user.getJwtToken() != null) {
                return new ResponseEntity(new CustomErrorType("User already logged in!"),
                        HttpStatus.OK);
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        restLogin.getEmail(),
                        restLogin.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        user.setJwtToken(jwt);

        this.userService.saveUser(user);

        Boss boss = this.bossService.findByEmail(restLogin.getEmail());
        Worker worker = this.workerService.findByEmail(restLogin.getEmail());

        if(boss != null) {
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, 0));
        } else if(worker != null) {
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, worker, 1));
        }

        return null;
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.findByEmail(principal.getUsername());

        if(user == null) {
            return ResponseEntity.ok("User is not logged in!");
        }

        user.setJwtToken(null);

        SecurityContextHolder.clearContext();

        this.userService.saveUser(user);

        return ResponseEntity.ok("User successfully logged out!");
    }
}
