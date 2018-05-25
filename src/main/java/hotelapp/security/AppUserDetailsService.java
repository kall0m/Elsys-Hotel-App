package hotelapp.security;

import hotelapp.security.AppUserDetails;
import hotelapp.models.Worker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import hotelapp.models.User;
import hotelapp.repositories.UserRepository;

import javax.transaction.Transactional;

@Service("appUserDetailsService")
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid User");
        } else if(user instanceof Worker) {
            throw new UsernameNotFoundException("Invalid User");
        } else {
            if(!user.isEnabled()) {
                throw new UsernameNotFoundException("Account not enabled");
            }

            return AppUserDetails.create(user);
        }
    }

    // This method is used by JwtAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Integer id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid User");
        } else if(user instanceof Worker) {
            throw new UsernameNotFoundException("Invalid User");
        } else {
            if(!user.isEnabled()) {
                throw new UsernameNotFoundException("Account not enabled");
            }

            return AppUserDetails.create(user);
        }
    }
}
