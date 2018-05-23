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

    public Boss findByConfirmationToken(String confirmationToken) {
        return this.bossRepository.findByConfirmationToken(confirmationToken);
    }

    public Boss findByForgotPasswordToken(String forgotPasswordToken) {
        return this.bossRepository.findByForgotPasswordToken(forgotPasswordToken);
    }

    public List<Boss> getAllBosses() {
        return bossRepository.findAll();
    }

    public boolean checkBoardsCount(Boss boss) {
        if(boss.getSubscription() == SubscriptionService.FREE_SUB) {
            return boss.getBoards().size() < SubscriptionService.FREE_BOARDS_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.FIRST_SUB) {
            return boss.getBoards().size() < SubscriptionService.FIRST_BOARDS_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.SECOND_SUB) {
            return boss.getBoards().size() < SubscriptionService.SECOND_BOARDS_COUNT;
        }

        return false;
    }

    public boolean checkTasksInBoardCount(Boss boss, Board board) {
        if(boss.getSubscription() == SubscriptionService.FREE_SUB) {
            return boss.getBoards().size() < SubscriptionService.FREE_TASKS_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.FIRST_SUB) {
            return boss.getBoards().size() < SubscriptionService.FIRST_TASKS_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.SECOND_SUB) {
            return boss.getBoards().size() < SubscriptionService.SECOND_TASKS_COUNT;
        }

        return false;
    }

    public boolean checkWorkerAccountsCount(Boss boss) {
        if(boss.getSubscription() == SubscriptionService.FREE_SUB) {
            return boss.getWorkerAccounts() < SubscriptionService.FREE_ACCS_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.FIRST_SUB) {
            return boss.getWorkerAccounts() < SubscriptionService.FIRST_ACCS_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.SECOND_SUB) {
            return boss.getWorkerAccounts() < SubscriptionService.SECOND_ACCS_COUNT;
        }

        return false;
    }

    public void saveBoss(Boss boss) {
        bossRepository.saveAndFlush(boss);
    }
}
