package hotelapp.services;

import hotelapp.models.Board;
import hotelapp.models.Boss;
import hotelapp.repositories.BossRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BossServiceImpl implements BossService {
    private BossRepository bossRepository;

    @Autowired
    public BossServiceImpl(BossRepository bossRepository) {
        this.bossRepository = bossRepository;
    }

    public Boss findByEmail(String email) {
        return bossRepository.findByEmail(email);
    }

    public List<Boss> getAllBosses() {
        return bossRepository.findAll();
    }

    public boolean checkBoardsCount(Boss boss) {
        return boss.getBoards().size() < 10;
    }

    public boolean checkTasksInBoardCount(Board board) {
        return board.getTasks().size() < 10;
    }

    public boolean checkWorkerAccountsCount(Boss boss) {
        if(boss.getSubscription() == 0) {
            return boss.getWorkerAccounts() < 3;
        } else if(boss.getSubscription() == 10) {
            return boss.getWorkerAccounts() < 5;
        } else if(boss.getSubscription() == 20) {
            return boss.getWorkerAccounts() < 10;
        }

        return false;
    }

    public void saveBoss(Boss boss) {
        bossRepository.saveAndFlush(boss);
    }
}
