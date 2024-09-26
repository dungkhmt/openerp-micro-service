package com.hust.baseweb.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder

public class ListPersonModel {

    Page<PersonModel> contents;
}
