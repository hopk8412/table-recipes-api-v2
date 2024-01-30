package recipes.table.api.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recipes.table.api.model.UserApiResponse;
import recipes.table.data.model.User;
import recipes.table.data.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<UserApiResponse> getUserFavoritedRecipes(@PathVariable String userId) {
        UserApiResponse userApiResponse = new UserApiResponse();
        // Find user by ID
        Optional<User> user = userService.fetchUserById(userId);

       // Return paginated list of favorites

        return null;
    }

    @PostMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<UserApiResponse> favoriteRecipe(@PathVariable String userId, @PathVariable String recipeId) {
        // Query for the given user ID

        // Append given recipeId to user's favorites list
        return null;
    }

    @DeleteMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<UserApiResponse> unfavoriteRecipe(@PathVariable String userId, @PathVariable String recipeId) {
        // Query for the given user ID

        // Remove given recipeId from user's favorites list
        return null;
    }
}
