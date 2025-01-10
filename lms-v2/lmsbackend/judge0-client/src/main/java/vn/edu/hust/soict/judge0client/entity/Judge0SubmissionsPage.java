package vn.edu.hust.soict.judge0client.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Judge0SubmissionsPage {

    Judge0Submission[] submissions;

    Meta meta;
}

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
class Meta {

    @JsonProperty("current_page")
    Integer currentPage;

    @JsonProperty("next_page")
    Integer nextPage;

    @JsonProperty("prev_page")
    Integer prevPage;

    @JsonProperty("total_pages")
    Integer totalPages;

    @JsonProperty("total_count")
    Integer totalCount;
}
