package hotelapp.services;

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

    public boolean checkWorkerAccountsCount(Boss boss) {
        if(boss.getSubscription() == SubscriptionService.FREE_SUB) {
            return boss.getWorkers().size() < SubscriptionService.FREE_ACCS_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.FIRST_SUB) {
            return boss.getWorkers().size() < SubscriptionService.FIRST_ACCS_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.SECOND_SUB) {
            return boss.getWorkers().size() < SubscriptionService.SECOND_ACCS_COUNT;
        }

        return false;
    }

    public boolean checkTypesCount(Boss boss) {
        if(boss.getSubscription() == SubscriptionService.FREE_SUB) {
            return boss.getTypes().size() < SubscriptionService.FREE_TYPES_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.FIRST_SUB) {
            return boss.getTypes().size() < SubscriptionService.FIRST_TYPES_COUNT;
        } else if(boss.getSubscription() == SubscriptionService.SECOND_SUB) {
            return boss.getTypes().size() < SubscriptionService.SECOND_TYPES_COUNT;
        }

        return false;
    }

    public void saveBoss(Boss boss) {
        bossRepository.saveAndFlush(boss);
    }
}
