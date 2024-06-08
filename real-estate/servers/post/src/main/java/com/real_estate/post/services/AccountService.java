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
import java.util.Random;

@Service
public class AccountService {
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz123456789";
    private static final int PASSWORD_LENGTH = 8;
    private static final Random random = new Random();

    @Autowired
    @Qualifier("accountImpl")
    AccountDao accountDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    GmailSenderService gmailSenderService;

    String avatar_default = "https://imgs.search.brave.com/HYWzW_u62rwqcCiSeS0ZXFOm48DScY8Z70UIusbvOyg/rs:fit:860:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzAzLzMyLzU5LzY1/LzM2MF9GXzMzMjU5/NjUzNV9sQWRMaGY2/S3piVzZQV1hCV2VJ/RlRvdlRpaTFkcmti/VC5qcGc";

    public void register(CreateAccountRequestDto dto) {
        Long now = System.currentTimeMillis();
        AccountEntity entity = new AccountEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setAvatar(avatar_default);
        entity.setTotalPostSell(0);
        entity.setTotalPostBuy(0);
        entity.setRole(new HashSet<>(Collections.singletonList("USER")));
        entity.setIsActive(false);
        entity.setProvider(AuthProvider.local);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        try {
            entity = accountDao.save(entity);
            String token = tokenProvider.createToken(entity.getAccountId());
            gmailSenderService.sendEmail(token, entity);
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
                .totalPostSell(entity.getTotalPostSell())
                .totalPostBuy(entity.getTotalPostBuy())
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

    public void resetPassword(String email) {
        String newPass = generatePass();
        int countRecord = accountDao.updatePassByEmail(passwordEncoder.encode(newPass), email);
        if (countRecord == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email chưa có tài khoản");
        }
        gmailSenderService.sendPass(email, newPass);
    }

    public String generatePass() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

    public void activeAccount(Long accountId) {
        int countRecord = accountDao.updateActive(accountId);
        if (countRecord == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token không hợp lệ");
        }
    }
}
