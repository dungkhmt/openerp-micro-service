package com.real_estate.post.dtos.request;

import com.real_estate.post.utils.TypePost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLikeRequestDto {
    Long postId;
    TypePost typePost;
}
