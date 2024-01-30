package recipes.table.data.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import recipes.table.data.model.Recipe;
import recipes.table.data.repository.RecipeRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    public Page<Recipe> searchForRecipesByTitle(Integer page, Integer size, String searchQuery) {
        Pageable paging = PageRequest.of(page, size);

        return recipeRepository.findByTitleContainingCaseInsensitive(searchQuery, paging);
    }

    public Recipe createNewRecipe(Recipe recipe) {
        recipe.setAuthorId(String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        return recipeRepository.save(recipe);
    }

    public Page<Recipe> searchForRecipesByAuthorId(Integer page, Integer size, String authorId) {
        Pageable paging = PageRequest.of(page, size);

        return recipeRepository.findByAuthorId(authorId, paging);
    }

    public Optional<Recipe> getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId);
    }

    public void deleteRecipeById(Recipe recipe) {
        recipeRepository.delete(recipe);
    }
}
