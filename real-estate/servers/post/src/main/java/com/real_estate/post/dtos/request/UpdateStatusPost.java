package com.real_estate.post.dtos.request;

import com.real_estate.post.utils.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStatusPost {
    Long postId;
    PostStatus status;
}
