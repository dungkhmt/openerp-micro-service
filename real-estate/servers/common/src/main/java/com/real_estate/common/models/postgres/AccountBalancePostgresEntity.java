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
	name = "account-balance"
)
public class AccountBalancePostgresEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	Long accountId;

	@Column(name = "balance")
	@ColumnDefault("0")
	Long balance;

	@Column(name = "created_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;
}
