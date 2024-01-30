package recipes.table.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Pagination {
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;
}
