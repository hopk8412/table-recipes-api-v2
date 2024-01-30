package recipes.table.data.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @MongoId
    private String id;
    private List<String> favoriteRecipes;
}
