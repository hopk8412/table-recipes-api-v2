package recipes.table.data.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import recipes.table.data.model.Recipe;
import recipes.table.data.model.User;
import recipes.table.data.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Page<String> getFavoritedRecipeIds(String userId, Pageable pageable) {
        User user = fetchUserOrNull(userId);

        if (user != null) {
            List<String> favoritedRecipes = user.getFavoriteRecipes();
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), favoritedRecipes.size());
            List<String> sublist = favoritedRecipes.subList(start, end);
            return new PageImpl<>(sublist, pageable, favoritedRecipes.size());
        } else {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    public User addRecipeToUsersFavorites(String userId, String recipeId) {
        // Fetch the user and if found add the provided recipe ID to their favorites list...
        User user = fetchUserOrNull(userId);
        if (user != null) {
            if (user.getFavoriteRecipes() != null) {
                user.getFavoriteRecipes().add(recipeId);
            } else {
                user.setFavoriteRecipes(List.of(recipeId));
            }
            userRepository.save(user);
            return user;
        } else {
            log.warn("No user found with ID {}", userId);
            return null;
        }
    }

    public void removeRecipeFromUsersFavorites(String userId, String recipeId) {
        // Fetch the user and if found remove the provided recipe ID from their favorites list...
        User user = fetchUserOrNull(userId);
        if (user != null) {
            if (user.getFavoriteRecipes() != null) {
                user.getFavoriteRecipes().remove(recipeId);
            }
            userRepository.save(user);
        }
    }

    private User fetchUserOrNull(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
