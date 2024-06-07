package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.AccountDao;
import com.real_estate.post.daos.interfaces.ConversationDao;
import com.real_estate.post.daos.interfaces.MessageDao;
import com.real_estate.post.dtos.request.FirstMessageRequestDto;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.dtos.response.ConversationResponseDto;
import com.real_estate.post.models.ConversationEntity;
import com.real_estate.post.models.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ConversationService {
    @Autowired
    @Qualifier("conversationImpl")
    ConversationDao conversationDao;

    @Autowired
    @Qualifier("messageImpl")
    MessageDao messageDao;

    @Autowired
    @Qualifier("accountImpl")
    AccountDao accountDao;

    @Autowired
    AccountService accountService;

    @Transactional
    public ConversationResponseDto createConversationAndMessage(FirstMessageRequestDto dto, Long myId) {
        Long now = System.currentTimeMillis();
        ConversationEntity conversationEntity = new ConversationEntity().builder()
                .memberIds(new HashSet<>(Arrays.asList(myId, dto.getReceiverId())))
                .createdAt(now)
                .updatedAt(now)
                .build();
        conversationEntity = conversationDao.saveConversation(conversationEntity);

        MessageEntity messageEntity = new MessageEntity().builder()
                .conversationId(conversationEntity.getConversationId())
                .senderId(myId)
                .content(dto.getContent())
                .messageType(dto.getMessageType())
                .createdAt(now)
                .updatedAt(now)
                .build();

        messageEntity = messageDao.saveMessage(messageEntity);
        AccountResponseDto other = accountService.getAccountBy(dto.getReceiverId());

        return new ConversationResponseDto().builder()
                .conversationId(conversationEntity.getConversationId())
                .isLastMessage(true)
                .lastTimeMessage(now)
                .other(other)
                .messages(new ArrayList<>(Arrays.asList(messageEntity)))
                .build();
    }

    public ConversationResponseDto getConversationByAllMemberId(Long myId, Long otherId) {
        List<ConversationEntity> conversationEntities = conversationDao.findByMember(myId, otherId);
        ConversationEntity conversationEntity = conversationEntities.size() > 0 ? conversationEntities.get(0) : null;
        if (conversationEntity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn và người này chưa có cuộc trò chuyện");
        } else {
            AccountResponseDto other = accountService.getAccountBy(otherId);
            List<MessageEntity> messageEntities = messageDao.findLast20By(conversationEntity.getConversationId(), -1L);
            return new ConversationResponseDto().builder()
                    .conversationId(conversationEntity.getConversationId())
                    .isLastMessage(messageEntities.size() < 20)
                    .lastTimeMessage(messageEntities.get(0).getCreatedAt())
                    .other(other)
                    .messages(messageEntities)
                    .build();
        }
    }

    public List<ConversationResponseDto> getAll(Long myId) {
        List<ConversationEntity> conversationEntities = conversationDao.findByMember(myId, -1L);
        List<ConversationResponseDto> result = new ArrayList<ConversationResponseDto>();
        for (ConversationEntity conversationEntity : conversationEntities) {
            ConversationResponseDto dto = getOne(conversationEntity.getConversationId(), myId);
            if (dto != null) {
                result.add(dto);
            }
        }
        return result;
    }

    public ConversationResponseDto getOne(Long conversationId, Long myId) {
        Optional<ConversationEntity> conversationEntityOptional = conversationDao.findById(conversationId);
        if (conversationEntityOptional.isPresent()) {
            ConversationEntity conversationEntity = conversationEntityOptional.get();
            for (Long otherId : conversationEntity.getMemberIds()) {
                if (otherId != myId) {
                    AccountResponseDto other = accountService.getAccountBy(otherId);
                    List<MessageEntity> messageEntities = messageDao.findLast20By(conversationEntity.getConversationId(), -1L);
                    return new ConversationResponseDto().builder()
                            .conversationId(conversationEntity.getConversationId())
                            .isLastMessage(messageEntities.size() < 20)
                            .lastTimeMessage(messageEntities.get(0).getCreatedAt())
                            .other(other)
                            .messages(messageEntities)
                            .build();
                }
            }
        }
        return null;
    }
}
