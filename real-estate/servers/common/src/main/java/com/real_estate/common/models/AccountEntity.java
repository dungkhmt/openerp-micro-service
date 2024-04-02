package com.real_estate.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountEntity {
  Long accountId;
  String name;
  String password;
  String phone;
  String avatar;
  String role;
  Integer reputation;
  Boolean isActive;
  Long createdAt;
  Long updatedAt;
}
