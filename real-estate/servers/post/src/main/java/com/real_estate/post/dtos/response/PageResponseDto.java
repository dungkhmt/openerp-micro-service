package com.real_estate.post.dtos.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageResponseDto<T> {
    private Integer code;
    private List<T> data;
    private com.real_estate.post.dtos.response.MetaDataResponseDto<T> metadata;

    public PageResponseDto(Integer code, Page<T> page) {
        this.code = code;
        this.data = page.getContent();
        this.metadata = new MetaDataResponseDto<T>(page);
    }
}