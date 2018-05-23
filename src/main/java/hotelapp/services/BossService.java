package hotelapp.services;

import hotelapp.models.Board;
import hotelapp.models.Boss;

import java.util.List;

public interface BossService {
    Boss findByEmail(String email);

    List<Boss> getAllBosses();

    boolean checkBoardsCount(Boss boss);

    boolean checkTasksInBoardCount(Boss boss, Board board);

    boolean checkWorkerAccountsCount(Boss boss);

    void saveBoss(Boss boss);
}
