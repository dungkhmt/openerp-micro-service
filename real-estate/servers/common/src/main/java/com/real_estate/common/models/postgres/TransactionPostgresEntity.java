package com.real_estate.common.models.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
	name = "transaction"
)
public class TransactionPostgresEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	Long transactionId;

	@Column(name = "account_id")
	Long accountId;

	@Column(name = "amount")
	Long amount;

	@Column(name = "type")
	String type;

	@Column(name = "status")
	String status;

	@Column(name = "create_at")
	Long createdAt;

	@Column(name = "updated_at")
	Long updatedAt;
}
