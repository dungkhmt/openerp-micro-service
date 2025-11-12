package com.real_estate.post.dtos.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountPostByProvinceResponseDto {
    String provinceId;
    String nameProvince;
    Long totalPost;
}
