import React, { useContext, useRef, useState } from "react";
import "./Chatlist.css";
import {
  Avatar,
  Group,
  ScrollArea,
  UnstyledButton,
  Text,
  Box,
} from "@mantine/core";
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
      <ScrollArea
        scrollbars="y"
        w={"100%"}
        style={
          {
            // backgroundColor: "red",
          }
        }
      >
        <div
          style={{
            width: "80%",
            // backgroundColor: "yellow",
            // overflow: "hidden",
          }}
        >
          {filteredConversations.map((conversation, index) => (
            <UnstyledButton
              key={index}
              className="conversation"
              onClick={() => setConversationSelect(conversation)}
            >
              <Group
                preventGrowOverflow={false}
                wrap="nowrap"
                style={{
                  width: "80%",
                }}
              >
                <Avatar src={conversation.other.avatar} radius="xl" size="lg" />
                <div style={{ width: "50%" }}>
                  <Text size="sm" style={{ fontWeight: "500" }}>
                    {conversation.other.name}
                  </Text>
                  <Box w={"80%"}>
                    <Text size="xs" truncate="end">
                      {transferLastMessage(conversation.messages[0])}
                    </Text>
                  </Box>
                </div>
              </Group>
            </UnstyledButton>
            // <Group
            //   // className="item"
            //   key={index}
            //   onClick={() => setConversationSelect(conversation)}
            //   style={{
            //     width: "100%",
            //     // backgroundColor: "#5183fe",
            //   }}
            // >
            //   <Avatar src={conversation.other.avatar} size="lg" />
            //   <div className="texts">
            //     <span>{conversation.other.name}</span>
            //     <p>{transferLastMessage(conversation?.messages[0])}</p>
            //   </div>
            // </Group>
          ))}
        </div>
      </ScrollArea>
    </div>
  );
};

export default Chatlist;
