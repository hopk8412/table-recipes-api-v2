package recipes.table.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recipe {
    private String id;
    private List<String> ingredients;
    private List<String> instructions;
    private String authorId;
    private String title;
    private List<String> imageLinks;
}
