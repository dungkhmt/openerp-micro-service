package com.real_estate.post.models;

import com.real_estate.post.utils.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountEntity {
  Long accountId;
  String name;
  String password;
  String phone;
  String email;
  String avatar;
  Integer totalPostSell;
  Integer totalPostBuy;
  Set<String> role;
  AuthProvider provider;
  String providerId;
  Boolean isActive;
  Long createdAt;
  Long updatedAt;
}
