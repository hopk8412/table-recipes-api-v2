package recipes.table.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeApiResponse {
    private String message;
    private Pagination pagination;
    private List<Recipe> data;
    private List<String> errors;
}
