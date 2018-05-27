package hotelapp.services;

import hotelapp.models.Boss;

import java.util.List;

public interface BossService {
    Boss findByEmail(String email);

    Boss findByConfirmationToken(String confirmationToken);

    Boss findByForgotPasswordToken(String confirmationToken);

    List<Boss> getAllBosses();

    boolean checkWorkerAccountsCount(Boss boss);

    boolean checkTypesCount(Boss boss);

    void saveBoss(Boss boss);
}
