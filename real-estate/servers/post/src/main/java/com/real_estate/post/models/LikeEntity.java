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
public class LikeEntity {
    Long likeId;
    Long postId;
    Long likerId;
    TypePost typePost;
    Long createdAt;
}
