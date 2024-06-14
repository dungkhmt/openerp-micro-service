package com.real_estate.post.models;

import com.real_estate.post.utils.TypePost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SavePostEntity {
    Long saveId;
    Long postId;
    Long accountId;
    TypePost typePost;
    Long createdAt;
}
