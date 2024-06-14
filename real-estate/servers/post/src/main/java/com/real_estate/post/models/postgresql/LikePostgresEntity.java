package com.real_estate.post.models.postgresql;

import com.real_estate.post.utils.TypePost;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
        name = "like_post"
)
public class LikePostgresEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    Long likeId;

    @Column(name = "post_id")
    Long postId;

    @Column(name = "liker_id")
    Long likerId;

    @Column(name = "type_post")
    @Enumerated(EnumType.STRING)
    TypePost typePost;

    @Column(name = "created_at")
    Long createdAt;
}
