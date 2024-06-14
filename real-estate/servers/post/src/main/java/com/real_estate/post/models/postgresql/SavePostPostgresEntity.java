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
        name = "save_post"
)
public class SavePostPostgresEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "save_id")
    Long saveId;

    @Column(name = "post_id")
    Long postId;

    @Column(name = "account_id")
    Long accountId;

    @Column(name = "type_post")
    @Enumerated(EnumType.STRING)
    TypePost typePost;

    @Column(name = "created_at")
    Long createdAt;
}
