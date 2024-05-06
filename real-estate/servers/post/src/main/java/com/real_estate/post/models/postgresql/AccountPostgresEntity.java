package com.real_estate.post.models.postgresql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.security.AuthProvider;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
	name = "account"
//	indexes = {
//		@Index(name = "phone_id", columnList = "phone")
//	}
)
public class AccountPostgresEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	Long accountId;

	@Column(name = "name")
	String name;

	@Column(name = "password")
	String password;

	@Column(name = "phone", unique = true)
	String phone;

	@Column(name = "email", unique = true)
	String email;

	@Column(name = "avatar")
	String avatar;

	@Column(name = "role")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "account_postgres_entity_role", joinColumns = @JoinColumn(name = "account_id"))
	Set<String> role;

//	@Column(name = "repotation", nullable = false)
//	@ColumnDefault(value = "100")
//	Integer reputation;

	@NotNull
	private String provider;

	private String providerId;

	@Column(name = "is_active")
	@ColumnDefault("false")
	Boolean isActive;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;
}
