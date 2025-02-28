package openerp.openerpresourceserver.generaltimetabling.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable

public class ClassGroupId implements Serializable {
    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassGroupId that = (ClassGroupId) o;
        return groupId.equals(that.groupId) && classId.equals(that.classId);
    }

    @Override
    public int hashCode() {
        return groupId.hashCode() + classId.hashCode();
    }
}
