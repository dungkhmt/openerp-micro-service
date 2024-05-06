package com.real_estate.post.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountBalanceEntity {
	Long accountId;
	Long balance;
	Long createdAt;
	Long updatedAt;
}
