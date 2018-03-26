package hotelapp.controllers;

import hotelapp.models.Board;
import hotelapp.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/user/{id}/boards")
    public String boards(Model model) {
        List<Board> boards = this.boardService.getAllBoards();

        model.addAttribute("boards", boards);
        model.addAttribute("view", "board/boards");

        return "base-layout";
    }
}
