package hotelapp.services;

import hotelapp.models.Boss;
import hotelapp.repositories.BossRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
