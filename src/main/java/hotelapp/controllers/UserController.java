package hotelapp.controllers;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import hotelapp.bindingModels.ForgotPasswordBindingModel;
import hotelapp.bindingModels.SetNewPasswordBindingModel;
import hotelapp.bindingModels.UserBindingModel;
import hotelapp.models.Boss;
import hotelapp.models.Role;
import hotelapp.models.User;
import hotelapp.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BossService bossService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("view", "user/register");

        return "base-layout";
    }

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel, HttpServletRequest request, RedirectAttributes redir){
        User userExists = this.userService.findByEmail(userBindingModel.getEmail());

        if (userExists != null) {
            redir.addFlashAttribute("message", NotificationMessages.USER_ALREADY_EXISTS);

            return "redirect:/register";
        }

        if(!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
            redir.addFlashAttribute("message", NotificationMessages.PASSWORDS_DONT_MATCH);

            return "redirect:/register";
        }

        Zxcvbn passwordCheck = new Zxcvbn();

        Strength strength = passwordCheck.measure(userBindingModel.getPassword());

        if (strength.getScore() < 2) {
            redir.addFlashAttribute("message", NotificationMessages.PASSWORD_TOO_WEAK);

            return "redirect:/register";
        }

        Boss boss = new Boss(
                userBindingModel.getEmail(),
                userBindingModel.getFullName(),
                bCryptPasswordEncoder.encode(userBindingModel.getPassword()),
                userBindingModel.getSubscription()
        );

        Role userRole = this.roleService.findRole("ROLE_USER");

        boss.addRole(userRole);

        // Disable user until they click on confirmation link in email
        boss.setEnabled(false);

        // Generate random 36-character string token for confirmation link
        boss.setConfirmationToken(UUID.randomUUID().toString());

        this.bossService.saveBoss(boss);

        String appUrl = request.getScheme() + "://" + request.getServerName();

        SimpleMailMessage registrationEmail = this.emailService.createEmail(
                boss.getEmail(),
                EmailDrafts.USER_REGISTRATION_SUBJECT,
                EmailDrafts.USER_REGISTRATION_CONTENT(appUrl, boss.getConfirmationToken()),
                EmailDrafts.APP_EMAIL
        );

        try {
            emailService.sendEmail(registrationEmail);
        }catch( Exception e ){
            // catch error
            logger.info("Error Sending Email: " + e.getMessage());
        }

        redir.addFlashAttribute("message", NotificationMessages.USER_CONFIRMATION_EMAIL_SENT(boss.getEmail()));

        return "redirect:/";
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token, RedirectAttributes redir) {
        Boss boss = this.bossService.findByConfirmationToken(token);

        if (boss == null) { // No token found in DB
            redir.addFlashAttribute("message", NotificationMessages.WRONG_CONFIRMATION_LINK);
            return "redirect:/";
        } else { // Token found
            boss.setEnabled(true);
            this.bossService.saveBoss(boss);
        }

        redir.addFlashAttribute("message", NotificationMessages.USER_EMAIL_SUCCESSFULLY_CONFIRMED);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout, Model model) {
        if(error != null) {
            model.addAttribute("message", NotificationMessages.WRONG_EMAIL_OR_PASSWORD);
        }

        if(logout != null) {
            model.addAttribute("message", NotificationMessages.USER_SUCCESSFULLY_LOGGED_OUT);
        }

        model.addAttribute("view", "user/login");

        return "base-layout";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";
    }

    @GetMapping("/user/forgot_password")
    public String forgotPassword(Model model) {
        model.addAttribute("view", "user/forgot-password");

        return "base-layout";
    }

    @PostMapping("/user/forgot_password")
    public String forgotPasswordProcess(ForgotPasswordBindingModel forgotPasswordBindingModel, RedirectAttributes redir, HttpServletRequest request) {
        Boss boss = this.bossService.findByEmail(forgotPasswordBindingModel.getEmail());

        if (boss == null) {
            redir.addFlashAttribute("message", NotificationMessages.USER_DOESNT_EXISTS(forgotPasswordBindingModel.getEmail()));

            return "redirect:/register";
        }

        // Generate random 36-character string token for forgot password link
        boss.setForgotPasswordToken(UUID.randomUUID().toString());

        this.bossService.saveBoss(boss);

        String appUrl = request.getScheme() + "://" + request.getServerName();

        SimpleMailMessage forgotPasswordEmail = this.emailService.createEmail(
                boss.getEmail(),
                EmailDrafts.USER_FORGOT_PASSWORD_SUBJECT,
                EmailDrafts.USER_FORGOT_PASSWORD_CONTENT(appUrl, boss.getForgotPasswordToken()),
                EmailDrafts.APP_EMAIL
        );

        try {
            emailService.sendEmail(forgotPasswordEmail);
        }catch( Exception e ){
            // catch error
            logger.info("Error Sending Email: " + e.getMessage());
        }

        redir.addFlashAttribute("message", NotificationMessages.USER_FORGOT_PASSWORD_CONFIRMATION_EMAIL_SENT(boss.getEmail()));

        return "redirect:/";
    }

    @GetMapping("/user/set_new_password")
    public String setNewPassword(@RequestParam("token") String token, Model model, RedirectAttributes redir) {
        Boss boss = this.bossService.findByForgotPasswordToken(token);

        if(boss == null) {
            redir.addFlashAttribute("message", NotificationMessages.USER_DOESNT_HAVE_FORGOT_PASSWORD_CONFIRMATION_TOKEN);

            return "redirect:/user/forgot_password";
        }

        model.addAttribute("user", boss);
        model.addAttribute("view", "user/set-new-password");

        return "base-layout";
    }

    @PostMapping("/user/set_new_password")
    public String setNewPasswordProcess(@RequestParam("token") String token, SetNewPasswordBindingModel setNewPasswordBindingModel, RedirectAttributes redir) {
        Boss boss = this.bossService.findByForgotPasswordToken(token);

        if (boss == null) { // No token found in DB
            redir.addFlashAttribute("message", NotificationMessages.USER_DOESNT_HAVE_FORGOT_PASSWORD_CONFIRMATION_TOKEN);

            return "redirect:/user/forgot_password";
        } else { // Token found
            if(!setNewPasswordBindingModel.getNewPassword().equals(setNewPasswordBindingModel.getConfirmPassword())) {
                redir.addFlashAttribute("message", NotificationMessages.PASSWORDS_DONT_MATCH);

                return "redirect:/user/set_new_password?token=" + token;
            }

            Zxcvbn passwordCheck = new Zxcvbn();

            Strength strength = passwordCheck.measure(setNewPasswordBindingModel.getNewPassword());

            if (strength.getScore() < 2) {
                redir.addFlashAttribute("message", NotificationMessages.PASSWORD_TOO_WEAK);

                return "redirect:/user/set_new_password?token=" + token;
            }

            // Set the reset token to null so it cannot be used again
            boss.setForgotPasswordToken(null);

            boss.setPassword(bCryptPasswordEncoder.encode(setNewPasswordBindingModel.getNewPassword()));

            this.bossService.saveBoss(boss);
        }

        redir.addFlashAttribute("message", NotificationMessages.CHANGES_SAVED);

        return "redirect:/login";
    }
}
