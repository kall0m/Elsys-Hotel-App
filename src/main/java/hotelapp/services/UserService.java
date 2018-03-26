package hotelapp.services;

import hotelapp.models.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);

    List<User> getAllUsers();

    void saveUser(User user);
}
