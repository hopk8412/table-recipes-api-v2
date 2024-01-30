package recipes.table.api.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recipes.table.api.model.Pagination;
import recipes.table.api.model.Recipe;
import recipes.table.api.model.User;
import recipes.table.api.model.UserApiResponse;
import recipes.table.data.service.RecipeService;
import recipes.table.data.service.UserService;
import recipes.table.util.RecipeUtil;
import recipes.table.util.UserUtil;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final RecipeService recipeService;

    private final RecipeUtil recipeUtil;

    private final UserUtil userUtil;

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<UserApiResponse> getUserFavoritedRecipes(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        UserApiResponse userApiResponse = new UserApiResponse();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<String> favoriteRecipeIds = userService.getFavoritedRecipeIds(userId, pageable);
            // Note: only fetching recipes that have not been deleted - this takes care of an edge case where an author (or DBA)
            // has deleted a recipe from the recipes collection (the deleted recipe's ID would still be present in the user's list of favorites...)
            List<Recipe> favoriteRecipes = recipeUtil.convertListToApi(recipeService.getRecipesByIds(favoriteRecipeIds.getContent()));
            userApiResponse
                    .setMessage("Successfully retrieved favorite recipes!")
                    .setPagination(new Pagination(favoriteRecipeIds.getNumber(), favoriteRecipeIds.getTotalElements(), favoriteRecipeIds.getTotalPages())
            )
                    .setData(favoriteRecipes)
                    .setErrors(null);
            return new ResponseEntity<>(userApiResponse, HttpStatus.OK);
        } catch (Exception e) {
            userApiResponse
                    .setMessage("Error retrieving user's favorite recipes!")
                    .setData(Collections.emptyList())
                    .setPagination(null)
                    .setErrors(Collections.singletonList(e.getMessage()));
            return new ResponseEntity<>(userApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<UserApiResponse> favoriteRecipe(@PathVariable String userId, @PathVariable String recipeId) {
        UserApiResponse userApiResponse = new UserApiResponse();
        try {
            // Service method call to fetch provided user by ID and add provided recipeId to favorites list...
            User user = userUtil.dataToApi(userService.addRecipeToUsersFavorites(userId, recipeId));
            Recipe recipe = recipeUtil.dataToApi(recipeService.getRecipeById(recipeId).orElse(null));
            if (recipe != null) {
                userApiResponse
                        .setMessage("Successfully added recipe to users favorites")
                        .setErrors(null)
                        .setPagination(new Pagination().setTotalItems(1L))
                        .setData(Collections.singletonList(recipe));
                return new ResponseEntity<>(userApiResponse, HttpStatus.CREATED);
            } else {
                // If we're here, somehow a user attempted to favorite a recipe that doesn't exist... shouldn't be possible!
                userApiResponse
                        .setMessage("An issue occurred!")
                        .setErrors(Collections.singletonList("User attempted to favorite a recipe that does not exist..."))
                        .setPagination(null)
                        .setData(null);
                return new ResponseEntity<>(userApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            userApiResponse
                    .setData(null)
                    .setMessage("Error adding recipe to user's favorites list!")
                    .setPagination(null)
                    .setErrors(Collections.singletonList(e.getMessage()));
            return new ResponseEntity<>(userApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<UserApiResponse> unfavoriteRecipe(@PathVariable String userId, @PathVariable String recipeId) {
        UserApiResponse userApiResponse = new UserApiResponse();
        try {
            // Service method call to fetch the given userId and remove the given recipeId from their favorites list
            userService.removeRecipeFromUsersFavorites(userId, recipeId);
            userApiResponse
                    .setErrors(null)
                    .setMessage("Successfully deleted recipe from favorites!")
                    .setData(null)
                    .setPagination(null);
            return new ResponseEntity<>(userApiResponse, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            userApiResponse
                    .setMessage("Error removing recipe from user's favorites!")
                    .setPagination(null)
                    .setData(null)
                    .setErrors(Collections.singletonList(e.getMessage()));
            return new ResponseEntity<>(userApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
