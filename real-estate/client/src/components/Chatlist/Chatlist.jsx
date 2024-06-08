import React, { useContext, useRef, useState } from "react";
import "./Chatlist.css";
import { ScrollArea } from "@mantine/core";
import { AccountContext } from "../../context/AccountContext";

const Chatlist = ({ setConversationSelect, conversations }) => {
  const [input, setInput] = useState("");
  const { account } = useContext(AccountContext);

  const filteredConversations = conversations.filter((c) =>
    c.other.name.toLowerCase().includes(input.toLowerCase()),
  );

  const transferLastMessage = (lastMessage) => {
    if (lastMessage.messageType === "TEXT") {
      if (lastMessage.senderId === account.accountId) {
        return "Bạn: " + lastMessage.content;
      } else {
        return lastMessage.content;
      }
    } else {
      if (lastMessage.senderId === account.accountId) {
        return "Bạn đã gửi một ảnh";
      } else {
        return "Đã gửi một ảnh";
      }
    }
  };
  return (
    <div className="conversationList">
      <div className="search">
        <div className="searchBar">
          <img src="./search.png" alt="" />
          <input
            type="text"
            placeholder="Search"
            onChange={(e) => setInput(e.target.value)}
          />
        </div>
      </div>
      {filteredConversations.map((coversation, index) => (
        <div
          className="item"
          key={index}
          onClick={() => setConversationSelect(coversation)}
          style={{
            backgroundColor: "#5183fe",
          }}
        >
          <img src={coversation.other.avatar || "./avatar.png"} alt="" />
          <div className="texts">
            <span>{coversation.other.name}</span>
            <p>{transferLastMessage(coversation?.messages[0])}</p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default Chatlist;
