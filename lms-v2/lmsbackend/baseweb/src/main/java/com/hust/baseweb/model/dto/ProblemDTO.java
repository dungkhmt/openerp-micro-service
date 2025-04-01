package com.hust.baseweb.model.dto;

import com.hust.baseweb.applications.programmingcontest.entity.TagEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProblemDTO {

    String problemId;

    String problemName;

    String userId;

    String levelId;

    Date createdAt;

    int appearances;

    List<TagEntity> tags;

    String statusId;

}
