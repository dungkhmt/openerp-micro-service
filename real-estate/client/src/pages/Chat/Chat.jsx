import Chatlist from "../../components/Chatlist/Chatlist";
import React, { useContext, useEffect, useState } from "react";
import "./Chat.css";
import ConversationDetail from "../../components/ConversationDetail/ConversationDetail";
import { ConversationContext } from "../../context/ConversationContext";
const Chat = () => {
  const { conversations, findConversationWith } =
    useContext(ConversationContext);
  const [conversationSelect, setConversationSelect] = useState({});
  useEffect(() => {
    if (conversationSelect?.conversationId > 0) {
      setConversationSelect((prev) => findConversationWith(prev.other));
    }
  }, [conversations]);
  return (
    <div className="chatContainer">
      <Chatlist
        setConversationSelect={setConversationSelect}
        conversations={conversations}
      />
      <div
        style={{
          width: "70%",
        }}
      >
        {Object.keys(conversationSelect).length > 0 ? (
          <ConversationDetail conversationSelect={conversationSelect} />
        ) : (
          <h2>Chọn cuộc trò chuyện để bắt đầu</h2>
        )}
      </div>
    </div>
  );
};

export default Chat;
