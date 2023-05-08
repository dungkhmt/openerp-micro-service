package com.hust.baseweb.applications.education.classmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelInputAddStudentToClassViaExcelUpload {
    private UUID classId;
}
