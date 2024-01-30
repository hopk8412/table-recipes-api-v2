package recipes.table.util;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import recipes.table.api.model.Recipe;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RecipeUtil {
    private final ModelMapper modelMapper;

    public Recipe dataToApi(recipes.table.data.model.Recipe recipe) {
        return modelMapper.map(recipe, Recipe.class);
    }
    public recipes.table.data.model.Recipe apiToData(Recipe recipe) {
        return modelMapper.map(recipe, recipes.table.data.model.Recipe.class);
    }

    public List<Recipe> convertListToApi(List<recipes.table.data.model.Recipe> recipeList) {
        return recipeList.stream().map(this::dataToApi).collect(Collectors.toList());
    }
}
