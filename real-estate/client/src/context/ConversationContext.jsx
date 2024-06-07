import React, { createContext, useContext, useEffect, useState } from "react";
import ConversationRequest from "../services/ConversationRequest";

const ConversationContext = createContext(undefined);

const ConversationContextProvider = ({ children }) => {
  const [conversations, setConversations] = useState([]);

  const findConversationWith = (other) => {
    // console.log("other", other);
    // console.log(conversations);
    const conversation = conversations.find(
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
      const existingConversation = prevConversations.find(
        (conv) => conv.conversationId === message.conversationId,
      );

      if (existingConversation) {
        // Nếu tìm thấy conversation, cập nhật lại messages
        const updatedConversations = prevConversations.filter(
          (conv) => conv.conversationId !== message.conversationId,
        );
        return [
          {
            ...existingConversation,
            messages: [message, ...existingConversation.messages], // Thêm message mới vào đầu danh sách
            lastTimeMessage: message.createdAt, // Cập nhật lastTimeMessage
            isLastMessage: true, // Đánh dấu là message cuối cùng
          },
          ...updatedConversations, // Chuyển conversation lên đầu danh sách
        ];
      } else {
        // Nếu chưa tìm thấy conversation, tạo mới
        getConversations();
      }
    });
  };

  useEffect(() => {
    localStorage.getItem("token") && getConversations();
  }, []);
  return (
    <ConversationContext.Provider
      value={{ conversations, addMessage, findConversationWith }}
    >
      {children}
    </ConversationContext.Provider>
  );
};

export { ConversationContext, ConversationContextProvider };
