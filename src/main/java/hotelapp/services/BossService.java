package hotelapp.services;

import hotelapp.models.Boss;

public interface BossService {
    Boss findByEmail(String email);
}
