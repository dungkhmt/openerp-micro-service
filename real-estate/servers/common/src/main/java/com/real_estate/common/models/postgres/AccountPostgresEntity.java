package com.real_estate.common.models.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
	name = "account",
	indexes = {
		@Index(name = "phone_id", columnList = "phone")
	}
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

	@Column(name = "avatar")
	String avatar;

	@Column(name = "role")
	@ColumnDefault(value = "USER")
	String role;

	@Column(name = "repotation", nullable = false)
	@ColumnDefault(value = "100")
	Integer reputation;

	@Column(name = "is_active")
	@ColumnDefault("false")
	Boolean isActive;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;
}
