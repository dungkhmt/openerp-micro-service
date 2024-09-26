package com.example.api.controllers.admin.dto;

import com.example.shared.enumeration.BusStatus;
import lombok.Data;

@Data
public class BusManipulateParam {
    private Boolean isToSchool;
    private String date;
    private String numberPlate;
    private BusStatus status;
}
