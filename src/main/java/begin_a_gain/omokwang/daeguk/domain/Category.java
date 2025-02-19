package begin_a_gain.omokwang.daeguk.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "daeguk_category")
public class Category {
    @Id
    private String category;

    private String description;
}
