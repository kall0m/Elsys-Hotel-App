package hotelapp.rest;

import hotelapp.bindingModels.RestLogin;
import hotelapp.models.User;
import hotelapp.security.JwtAuthenticationResponse;
import hotelapp.security.JwtTokenProvider;
import hotelapp.services.UserService;
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

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
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
