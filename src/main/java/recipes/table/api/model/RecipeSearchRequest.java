package recipes.table.api.model;

import lombok.Data;

@Data
public class RecipeSearchRequest {
    private String searchQuery;
    private Integer page;
    private Integer size;
}
