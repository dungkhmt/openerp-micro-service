package openerp.openerpresourceserver.trainingprogcourse.enity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class TrainingProgPrerequisiteId implements Serializable {

    private String courseId;
    private String prerequisiteCourseId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingProgPrerequisiteId that = (TrainingProgPrerequisiteId) o;
        return Objects.equals(courseId, that.courseId) &&
                Objects.equals(prerequisiteCourseId, that.prerequisiteCourseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, prerequisiteCourseId);
    }
}