package com.hust.baseweb.applications.education.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class DownloadSummissionsIM {

    @NotNull(message = "Được yêu cầu")
    @Size(min = 1, message = "Phải chứa ít nhất một phần tử")
    private List<String> studentIds;
}
