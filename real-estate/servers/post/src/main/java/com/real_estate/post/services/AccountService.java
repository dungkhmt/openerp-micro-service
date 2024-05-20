package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.dtos.request.CreateAccountRequestDto;
import com.real_estate.post.dtos.request.UpdateAccountRequestDto;
import com.real_estate.post.dtos.request.UpdatePasswordRequestDto;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.security.TokenProvider;
import com.real_estate.post.utils.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    @Qualifier("accountImpl")
    AccountDao accountDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenProvider tokenProvider;

    String avatar_default = "https://imgs.search.brave.com/HYWzW_u62rwqcCiSeS0ZXFOm48DScY8Z70UIusbvOyg/rs:fit:860:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzAzLzMyLzU5LzY1/LzM2MF9GXzMzMjU5/NjUzNV9sQWRMaGY2/S3piVzZQV1hCV2VJ/RlRvdlRpaTFkcmti/VC5qcGc";

    public void register(CreateAccountRequestDto dto) {
        Long now = System.currentTimeMillis();
        AccountEntity entity = new AccountEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setAvatar(avatar_default);
        entity.setRole(new HashSet<>(Collections.singletonList("USER")));
        entity.setIsActive(true);
        entity.setProvider(AuthProvider.local);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        try {
            entity = accountDao.save(entity);
        }
        catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã được dùng");
        }
    }

    public AccountResponseDto getAccountBy(Long accountId) {
        Optional<AccountEntity> entityOptional = accountDao.findById(accountId);
        if (entityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể tìm được tài khoản");
        }
        AccountEntity entity = entityOptional.get();
        AccountResponseDto dto = new AccountResponseDto().builder()
                .accountId(entity.getAccountId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .avatar(entity.getAvatar())
                .build();

        return dto;
    }

    public void updateInfo(UpdateAccountRequestDto requestDto, Long accountId) {
        int countRecord = accountDao.updateAccount(requestDto.getAvatar(), requestDto.getPhone(), requestDto.getName(), accountId);
        if (countRecord == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại đã được sử dụng");
        }
    }

    public void updatePassword(UpdatePasswordRequestDto requestDto, Long accountId) {
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());
        System.out.println(newPassword);
        Optional<AccountEntity> entity = accountDao.findById(accountId);
        if (passwordEncoder.matches(requestDto.getOldPassword(), entity.get().getPassword())) {
            accountDao.updatePassword(newPassword, accountId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không trùng khớp");
        }
    }
}
