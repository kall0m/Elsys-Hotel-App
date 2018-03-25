package hotelapp.services;

import hotelapp.models.User;

import java.util.List;

public interface UserService {
    boolean authenticate(String username, String password);

    User findByEmail(String email);

    List<User> getAllUsers();

    void saveUser(User user);
}
