package hotelapp.services;

import hotelapp.models.User;
import hotelapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Override
    public boolean authenticate(String username, String password) {
        // Provide a sample password check: username == password
        return Objects.equals(username, password);
    }

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        userRepository.saveAndFlush(user);
    }
}
