package hotelapp.services;

import hotelapp.models.Boss;

import java.util.List;

public interface BossService {
    Boss findByEmail(String email);

    List<Boss> getAllBosses();

    void saveBoss(Boss boss);
}
