package com.real_estate.post.models.postgresql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
        name = "conversation"
)
public class ConversationPostgresEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long conversationId;

    @Column(name = "member_ids")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "conversation_postgres_entity_member_ids", joinColumns = @JoinColumn(name = "conversation_id"))
    private Set<Long> memberIds;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;
}
