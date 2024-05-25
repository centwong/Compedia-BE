package cent.wong.compedia.entity;

import cent.wong.entity.Pagination;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Setter
@Getter
public class PaginationRes<T> implements Serializable {

    private List<T> list;

    private Pagination pg;
}
