package com.real_estate.post.models.postgresql;

import com.real_estate.post.utils.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

	@Column(name = "total_post_sell")
	@ColumnDefault(value = "0")
	Integer totalPostSell = 0;

	@Column(name = "total_post_buy")
	@ColumnDefault(value = "0")
	Integer totalPostBuy = 0;

	@Column(name = "role")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "account_postgres_entity_role", joinColumns = @JoinColumn(name = "account_id"))
	Set<String> role;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider")
	AuthProvider provider;

	@Column(name = "provider_id")
	String providerId;

	@Column(name = "is_active")
	@ColumnDefault("false")
	Boolean isActive;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;
}
