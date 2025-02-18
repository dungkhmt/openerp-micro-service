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
public class Judge0Language {

    Integer id;

    String name;

    @JsonProperty("is_archived")
    Boolean isArchived;

    @JsonProperty("source_file")
    String sourceFile;

    @JsonProperty("compile_cmd")
    String compileCmd;

    @JsonProperty("run_cmd")
    String runCmd;
}
