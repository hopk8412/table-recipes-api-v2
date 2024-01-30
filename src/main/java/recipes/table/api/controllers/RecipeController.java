package recipes.table.api.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import recipes.table.api.model.Pagination;
import recipes.table.api.model.RecipeApiResponse;
import recipes.table.api.model.RecipeSearchRequest;
import recipes.table.data.model.Recipe;
import recipes.table.data.service.RecipeService;
import recipes.table.util.RecipeUtil;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;

    private final RecipeUtil recipeUtil;

    @PostMapping
    public ResponseEntity<RecipeApiResponse> postRecipe(@RequestBody @Validated recipes.table.api.model.Recipe recipe) {
        log.debug("Creating new recipe");

        recipes.table.api.model.Recipe newRecipe = recipeUtil.dataToApi(recipeService.createNewRecipe(recipeUtil.apiToData(recipe)));
        RecipeApiResponse recipeApiResponse = new RecipeApiResponse();
        recipeApiResponse.setPagination(null).setData(Collections.singletonList(newRecipe)).setErrors(null).setMessage("Successfully created new recipe!");
        return new ResponseEntity<>(recipeApiResponse, HttpStatus.CREATED);
    }
    @PostMapping("/search")
    public ResponseEntity<RecipeApiResponse> postRecipeSearch(@RequestBody @Validated RecipeSearchRequest recipeSearchRequest) {
        log.debug("Fetching recipes");

        int page = (recipeSearchRequest.getPage() != null) ? recipeSearchRequest.getPage() : 0;
        int size = (recipeSearchRequest.getSize() != null) ? recipeSearchRequest.getSize() : 10;

        RecipeApiResponse recipeApiResponse = new RecipeApiResponse();

        try {
            Page<Recipe> recipePage = recipeService.searchForRecipesByTitle(page, size, recipeSearchRequest.getSearchQuery());

            recipeApiResponse
                    .setData(recipeUtil.convertListToApi(recipePage.getContent()))
                    .setErrors(null)
                    .setMessage("Successfully retrieved recipes!")
                    .setPagination(new Pagination(recipePage.getNumber(), recipePage.getTotalElements(), recipePage.getTotalPages()));
            return new ResponseEntity<>(recipeApiResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Something bad happened...", e);
            recipeApiResponse.getErrors().add(e.getMessage());
            recipeApiResponse.setMessage("Something bad happened...");
            return new ResponseEntity<>(recipeApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<RecipeApiResponse> getMyRecipes(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        RecipeApiResponse recipeApiResponse = new RecipeApiResponse();
        try {
            String userId = String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            log.debug("Getting recipes for user with ID {}", userId);
            Page<Recipe> recipePage = recipeService.searchForRecipesByAuthorId(page, size, userId);

            recipeApiResponse
                    .setErrors(null)
                    .setPagination(new Pagination(recipePage.getNumber(), recipePage.getTotalElements(), recipePage.getTotalPages()))
                    .setData(recipeUtil.convertListToApi(recipePage.getContent()))
                    .setMessage("Successfully fetched my recipes!");

            return new ResponseEntity<>(recipeApiResponse, HttpStatus.OK);

        } catch (Exception e) {
            recipeApiResponse
                    .setData(null)
                    .setPagination(null)
                    .setMessage("Something went wrong!")
                    .setErrors(Collections.singletonList(e.getMessage()));;
            log.error("Something went wrong...", e);
            return new ResponseEntity<>(recipeApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeApiResponse> getRecipeById(@PathVariable String recipeId) {
        RecipeApiResponse recipeApiResponse = new RecipeApiResponse();
        try {
            Optional<Recipe> recipe = recipeService.getRecipeById(recipeId);
            if (recipe.isPresent()) {
                recipeApiResponse
                        .setPagination(null)
                        .setMessage("Successfully retrieved recipe!")
                        .setData(Collections.singletonList(recipeUtil.dataToApi(recipe.get())))
                        .setErrors(null);
                return new ResponseEntity<>(recipeApiResponse, HttpStatus.OK);
            } else {
                recipeApiResponse
                        .setPagination(null)
                        .setMessage("No recipe with this ID could be found!")
                        .setData(Collections.emptyList())
                        .setErrors(null);
                return new ResponseEntity<>(recipeApiResponse, HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            log.error("Something went wrong...", e);
            recipeApiResponse
                    .setMessage("Something went wrong!")
                    .setData(null)
                    .setErrors(Collections.singletonList(e.getMessage()))
                    .setPagination(null);
            return new ResponseEntity<>(recipeApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<RecipeApiResponse> deleteRecipeById(@PathVariable String recipeId) {
        // Fetch the recipe by ID, then compare recipe authorId to the current user's ID - if no match do not delete
        log.debug("Fetching recipe to determine if allowed to delete...");
        RecipeApiResponse recipeApiResponse = new RecipeApiResponse();
        recipeApiResponse.setPagination(null).setData(null);
        Optional<Recipe> recipe = recipeService.getRecipeById(recipeId);

        try {

            if (recipe.isPresent()) {
                // Now check if authorId matches user's ID
                //TODO: update this logic - first grab the user's ID and fetch this user's recipes, then do the check against authorId
                //this guarantees that a recipe authored by another user will never be able to reach the forbidden response below, only stating no recipe found
                //which while incorrect, makes more sense to add this extra layer of security for DELETE requests where the recipe ID could be substituted...
                if (recipe.get().getAuthorId().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
                    // Author is the current user, allow deletion
                    recipeService.deleteRecipeById(recipe.get());
                    recipeApiResponse.setErrors(null).setMessage("Successfully deleted recipe!");
                    return new ResponseEntity<>(recipeApiResponse, HttpStatus.NO_CONTENT);
                } else {
                    recipeApiResponse.setErrors(null).setMessage("You are not allowed to delete this recipe!");
                    return new ResponseEntity<>(recipeApiResponse, HttpStatus.FORBIDDEN);
                }
            } else {
                recipeApiResponse.setErrors(null).setMessage("No recipe found with provided ID!");
                return new ResponseEntity<>(recipeApiResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Something went wrong...", e);
            recipeApiResponse
                    .setErrors(Collections.singletonList(e.getMessage()))
                    .setMessage("Something went wrong!");
            return new ResponseEntity<>(recipeApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
