package recipes.table.data.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import recipes.table.data.model.Recipe;
import recipes.table.data.repository.RecipeRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    public Page<Recipe> searchForRecipesByTitle(String searchQuery, Pageable pageable) {

        return recipeRepository.findByTitleContainingCaseInsensitiveAndIsDeletedIsFalse(searchQuery, pageable);
    }

    public Recipe createNewRecipe(Recipe recipe) {
        recipe.setAuthorId(String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        return recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipesByIds(List<String> recipeIds) {
        return recipeRepository.findByIdInAndIsDeletedIsFalse(recipeIds);
    }

    public Page<Recipe> searchForRecipesByAuthorId(String authorId, Pageable pageable) {
        return recipeRepository.findByAuthorIdAndIsDeletedIsFalse(authorId, pageable);
    }

    public Optional<Recipe> getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId);
    }

    public void deleteRecipeById(Recipe recipe) {
        recipe.setIsDeleted(true);
        recipeRepository.save(recipe);
    }
}
