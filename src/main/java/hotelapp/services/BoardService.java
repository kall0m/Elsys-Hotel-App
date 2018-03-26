package hotelapp.services;

import hotelapp.models.Board;

import java.util.List;

public interface BoardService {
    List<Board> getAllBoards();

    boolean boardExists(Integer id);

    Board findBoard(Integer id);

    void deleteBoard(Board board);

    void saveBoard(Board board);
}
