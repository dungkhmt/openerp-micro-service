package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.*;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageWrapper<T> {
    private PageInfo pageInfo;
    private List<T> pageContent;

    public <K> PageWrapper<K> convert(Function<? super T, K> converter){
        var content = pageContent.stream()
                .map(converter)
                .toList();
        return PageWrapper.<K>builder()
                .pageInfo(pageInfo)
                .pageContent(content)
                .build();
    }
}
