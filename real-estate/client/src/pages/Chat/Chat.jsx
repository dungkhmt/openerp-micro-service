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
      {Object.keys(conversationSelect).length > 0 && (
        <ConversationDetail conversationSelect={conversationSelect} />
      )}
    </div>
  );
};

export default Chat;
