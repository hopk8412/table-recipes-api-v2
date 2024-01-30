package recipes.table.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import recipes.table.data.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    @Query("{'title': { $regex: ?0, $options: 'i' }}")
    Page<Recipe> findByTitleContainingCaseInsensitiveAndIsDeletedIsFalse(String title, Pageable pageable);

    Page<Recipe> findByAuthorIdAndIsDeletedIsFalse(String authorId, Pageable pageable);

    List<Recipe> findByIdInAndIsDeletedIsFalse(List<String> recipeIds);
}
