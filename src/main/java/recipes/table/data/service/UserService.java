package recipes.table.data.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import recipes.table.data.model.Recipe;
import recipes.table.data.model.User;
import recipes.table.data.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> fetchUserById(String userId) {
        return userRepository.findById(userId);
    }
}
