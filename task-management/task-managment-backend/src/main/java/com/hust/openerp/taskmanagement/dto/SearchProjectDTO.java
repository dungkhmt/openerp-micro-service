package com.hust.openerp.taskmanagement.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SearchProjectDTO {
    private UUID id;
    private String name;
}
