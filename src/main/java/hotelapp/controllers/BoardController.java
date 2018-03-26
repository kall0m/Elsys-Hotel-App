package hotelapp.controllers;

import hotelapp.bindingModels.BoardBindingModel;
import hotelapp.models.Board;
import hotelapp.models.Boss;
import hotelapp.services.BoardService;
import hotelapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserService userService;

    @GetMapping("/boards")
    public String boards(Model model) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss user = (Boss) this.userService.findByEmail(principal.getUsername());

        List<Board> boards = new ArrayList<>(user.getBoards());

        model.addAttribute("boards", boards);
        model.addAttribute("view", "board/boss-boards");

        return "base-layout";
    }

    @GetMapping("/boards/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {
        model.addAttribute("view", "board/create");

        return "base-layout";
    }

    @PostMapping("/boards/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(BoardBindingModel boardBindingModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/user/{id}/boards/create";
        }

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boss user = (Boss) this.userService.findByEmail(principal.getUsername());

        Board board = new Board(
                boardBindingModel.getName(),
                boardBindingModel.getDescription(),
                user
        );

        this.boardService.saveBoard(board);

        return "redirect:/user/{id}/boards";
    }
}
