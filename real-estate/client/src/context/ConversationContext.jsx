import React, { createContext, useContext, useEffect, useState } from "react";
import ConversationRequest from "../services/ConversationRequest";
import { AccountContext } from "./AccountContext";

const ConversationContext = createContext(undefined);

const ConversationContextProvider = ({ children }) => {
  const { account } = useContext(AccountContext);
  const [conversations, setConversations] = useState([]);

  const findConversationWith = (other) => {
    // console.log("other", other);
    // console.log(conversations);
    const conversation = conversations?.find(
      (conv) => conv.other.accountId === other?.accountId,
    );
    // console.log(conversation);
    if (conversation === null || conversation === undefined) {
      return {
        conversationId: null,
        isLastMessage: true,
        lastTimeMessage: null,
        other: other,
        messages: [],
      };
    } else {
      return conversation;
    }
  };

  const getConversations = () => {
    const conversationRequest = new ConversationRequest();
    conversationRequest.getConversations().then((response) => {
      if (response.code === 200) {
        setConversations(response.data);
      }
    });
  };
  const addMessage = (message) => {
    setConversations((prevConversations) => {
      // Tìm conversation có chứa message mới
      const existingConversation = prevConversations?.find(
        (conv) => conv.conversationId === message.conversationId,
      );

      if (existingConversation) {
        // Nếu tìm thấy conversation, cập nhật lại messages
        const updatedConversations = prevConversations?.filter(
          (conv) => conv.conversationId !== message.conversationId,
        );
        return [
          {
            ...existingConversation,
            messages: [message, ...existingConversation.messages], // Thêm message mới vào đầu danh sách
            lastTimeMessage: message.createdAt, // Cập nhật lastTimeMessage
          },
          ...updatedConversations, // Chuyển conversation lên đầu danh sách
        ];
      } else {
        getConversations();
      }
    });
  };

  const addConversation = (conversation) => {
    setConversations([conversation, ...conversations]);
  };

  useEffect(() => {
    if (Object.keys(account).length > 0) {
      getConversations();
    }
  }, [account]);
  return (
    <ConversationContext.Provider
      value={{
        conversations,
        addMessage,
        findConversationWith,
        addConversation,
        setConversations,
      }}
    >
      {children}
    </ConversationContext.Provider>
  );
};

export { ConversationContext, ConversationContextProvider };
