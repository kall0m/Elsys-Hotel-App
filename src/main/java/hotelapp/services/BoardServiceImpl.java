package hotelapp.services;

import hotelapp.models.Board;
import hotelapp.repositories.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {
    private BoardRepository boardRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public boolean boardExists(Integer id) {
        return boardRepository.exists(id);
    }

    public Board findBoard(Integer id) {
        return boardRepository.findOne(id);
    }

    public void deleteBoard(Board board) {
        boardRepository.delete(board);
    }

    public void saveBoard(Board board) {


        boardRepository.saveAndFlush(board);
    }
}
