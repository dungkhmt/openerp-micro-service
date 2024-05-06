package com.real_estate.post.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionEntity {
	Long transactionId;
	Long accountId;
	Long amount;
	String type;
	String status;
	Long createdAt;
	Long updatedAt;
}
