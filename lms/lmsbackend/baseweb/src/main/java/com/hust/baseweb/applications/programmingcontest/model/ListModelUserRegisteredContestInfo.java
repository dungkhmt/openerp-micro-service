package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class ListModelUserRegisteredContestInfo {
    Page<ModelUserRegisteredClassInfo> contents;
}
