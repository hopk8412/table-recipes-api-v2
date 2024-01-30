package recipes.table.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Document(collection = "recipes")
public class Recipe {
    @MongoId
    private String id;
    private List<String> ingredients;
    private List<String> instructions;
    private String authorId;
    private String title;
    private List<String> imageLinks;
}
