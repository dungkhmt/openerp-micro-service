package com.real_estate.post.dtos.response;

import com.real_estate.post.models.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponseDto {
    Long accountId;
    String name;
    String phone;
    String email;
    String avatar;
    Integer totalPostSell;
    Integer totalPostBuy;

    public AccountResponseDto(AccountEntity entity) {
        this.accountId = entity.getAccountId();
        this.name = entity.getName();
        this.avatar = entity.getAvatar();
        this.phone = entity.getPhone();
        this.email = entity.getEmail();
        this.totalPostBuy = entity.getTotalPostBuy();
        this.totalPostSell = entity.getTotalPostSell();
    }
}
