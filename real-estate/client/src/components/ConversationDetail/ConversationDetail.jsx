import React, { useContext, useEffect, useRef, useState } from "react";
import { MessageType, TransportActionEnum } from "../../utils/dataType";
import ConversationRequest from "../../services/ConversationRequest";
import { WebSocketContext } from "../../context/WebSocketContext";
import { AccountContext } from "../../context/AccountContext";
import EmojiPicker from "emoji-picker-react";
import "./ConversationDetail.css";
import { ImBin } from "react-icons/im";
import {
  ActionIcon,
  rem,
  ScrollArea,
  Text,
  Code,
  SimpleGrid,
  Button,
} from "@mantine/core";
import { LuSendHorizonal } from "react-icons/lu";
import { ConversationContext } from "../../context/ConversationContext";
import { uploadImage } from "../../utils/common";
import { CiFaceSmile } from "react-icons/ci";
import { IoClose, IoImageOutline } from "react-icons/io5";
import MessageRequest from "../../services/MessageRequest";

const ConversationDetail = ({ conversationSelect }) => {
  const { addMessage, addConversation } = useContext(ConversationContext);
  const { account } = useContext(AccountContext);
  const { ws } = useContext(WebSocketContext);
  const [currentConversation, setCurrentConversation] =
    useState(conversationSelect);
  const [content, setContent] = useState("");
  const [messageType, setMessageType] = useState(MessageType.TEXT);
  const [openEmoji, setOpenEmoji] = useState(false);
  const [img, setImg] = useState({
    file: null,
    url: "",
  });

  const isFirstLoad = useRef(true);

  const [scrollPosition, onScrollPositionChange] = useState({ x: 0, y: 0 });

  const viewport = useRef(null);

  const [loadingSend, setLoadingSend] = useState(false);

  const getMoreMessage = () => {
    if (currentConversation?.isLastMessage === false) {
      const lastMessageId =
        currentConversation.messages[currentConversation.messages.length - 1]
          .messageId;
      const messageRequest = new MessageRequest();
      messageRequest
        .getMoreMessage({
          conversationId: conversationSelect.conversationId,
          lastMessageId: lastMessageId,
        })
        .then((response) => {
          if (response.code === 200) {
            setCurrentConversation({
              ...currentConversation,
              messages: [...currentConversation.messages, ...response.data],
              isLastMessage: response.data.length < 20,
            });
          }
        });
    }
  };

  const scrollToBottom = () => {
    if (viewport.current) {
      viewport.current.scrollTo({
        top: viewport.current.scrollHeight,
        behavior: "smooth",
      });
    }
  };

  const handleEmoji = (e) => {
    setContent((prev) => prev + e.emoji);
    setOpenEmoji(false);
  };

  const handleImg = (e) => {
    if (e.target.files[0]) {
      setImg({
        file: e.target.files[0],
        url: URL.createObjectURL(e.target.files[0]),
      });
      setMessageType(MessageType.IMAGE);
    }
  };

  const handleSendMessage = async (event) => {
    event.preventDefault();
    let image;
    setLoadingSend(true);
    if (messageType === MessageType.IMAGE) {
      image = await uploadImage(img.file);
    }

    if (currentConversation?.conversationId > 0) {
      const now = Date.now();
      const lastMessage = {
        senderId: account.accountId,
        action: TransportActionEnum.NOTIFICATION_MESSAGE,
        conversationId: currentConversation.conversationId,
        content: messageType === MessageType.TEXT ? content : image,
        messageType,
        createdAt: now,
        updatedAt: now,
        messageId: 0,
      };
      addMessage(lastMessage);

      const newCurrentConversation = {
        ...currentConversation,
        messages: [lastMessage, ...currentConversation.messages],
        lastTimeMessage: now,
      };

      setCurrentConversation(newCurrentConversation);
      ws.send(
        "/app/message",
        {},
        JSON.stringify({
          senderId: account.accountId,
          receiverId: currentConversation.other.accountId,
          action: TransportActionEnum.NOTIFICATION_MESSAGE,
          conversationId: currentConversation.conversationId,
          content: messageType === MessageType.TEXT ? content : image,
          messageType,
          messageId: 0, // truong nay dung de sua xoa
        }),
      );
    } else {
      const conversationRequest = new ConversationRequest();
      conversationRequest
        .createConversation({
          receiverId: currentConversation.other.accountId,
          content,
          messageType,
        })
        .then((response) => {
          if (response.code === 200) {
            // console.log(response.data);
            setCurrentConversation(response.data);
            addConversation(response.data);
          }
        });
    }

    setContent("");
    setMessageType(MessageType.TEXT);
    setImg({
      file: null,
      url: "",
    });
    setLoadingSend(false);
  };

  useEffect(() => {
    setCurrentConversation(conversationSelect);
    isFirstLoad.current = true;
  }, [conversationSelect]);

  useEffect(() => {
    if (isFirstLoad.current) {
      setTimeout(scrollToBottom, 100);
      isFirstLoad.current = false;
    } else {
      if (scrollPosition.y === 0) {
        getMoreMessage();
      }
    }
  }, [scrollPosition]);
  return (
    <div className="conversationDetailContainer">
      <div className="top">
        <div className="other">
          <img
            src={currentConversation?.other?.avatar || "../avatar.png"}
            alt=""
          />
          <div className="texts">
            <span>{currentConversation?.other?.name}</span>
          </div>
        </div>
      </div>

      <ScrollArea
        h={"100%"}
        w={"100%"}
        viewportRef={viewport}
        onScrollPositionChange={onScrollPositionChange}
      >
        <SimpleGrid
          cols={1}
          spacing="xs"
          verticalSpacing="lg"
          style={{
            width: "100%",
            margin: "10px 0",
          }}
          // className="center"
        >
          {currentConversation?.messages
            ?.slice()
            .reverse()
            .map((message, index, arr) => {
              const isFirstMessage = index === 0;
              return (
                <div
                  ref={isFirstMessage ? viewport : null}
                  style={{
                    // backgroundColor: "red",
                    width: "100%",
                  }}
                  // className={
                  //   message.senderId === account?.accountId
                  //     ? "message own"
                  //     : "message"
                  // }
                  key={index}
                >
                  <div
                    // className="texts"
                    className={
                      message.senderId === account?.accountId
                        ? "message-item flexColEnd"
                        : "other flexColStart "
                    }
                    title={
                      new Date(message.createdAt).getHours() +
                      ":" +
                      new Date(message.createdAt).getMinutes()
                    }
                  >
                    {message.messageType === MessageType.TEXT ? (
                      <p>{message.content}</p>
                    ) : (
                      <img
                        style={{
                          ObjectFit: "contain",
                          width: "70%",
                          height: "300px",
                          borderRadius: "10px",
                        }}
                        src={message.content}
                        alt=""
                      />
                    )}
                    {/*<span*/}
                    {/*  style={{*/}
                    {/*    color: "white",*/}
                    {/*  }}*/}
                    {/*>*/}
                    {/*  {format(message.createdAt)}*/}
                    {/*</span>*/}
                  </div>
                </div>
              );
            })}
        </SimpleGrid>
      </ScrollArea>
      <form onSubmit={handleSendMessage}>
        {img.url ? (
          <div className="bottom">
            <div
              style={{
                width: "60px",
                height: "60px",
                display: "block",
                position: "relative",
              }}
            >
              <img
                src={img.url}
                alt=""
                className="imageUpload"
                style={{
                  position: "absolute",
                  width: "100%",
                  height: "100%",
                  objectFit: "cover",
                  borderRadius: "10px",
                  zIndex: 0,
                }}
              />
              <ActionIcon
                size={20}
                radius="xl"
                variant="filled"
                onClick={() => setImg({ file: null, url: "" })}
                className="deleteImage"
              >
                <IoClose
                  style={{ width: rem(18), height: rem(18) }}
                  stroke={1.5}
                />
              </ActionIcon>
            </div>
            <Button
              className="sendButton"
              disabled={img.url === ""}
              // onClick={handleSendMessage}
              type="submit"
              loading={loadingSend}
            >
              <LuSendHorizonal className="icon" />
            </Button>
          </div>
        ) : (
          <div className="bottom">
            <div className="icons">
              <label htmlFor="file">
                <IoImageOutline
                  style={{
                    cursor: "pointer",
                    color: "white",
                  }}
                  className="icon"
                />
              </label>
              <input
                type="file"
                id="file"
                style={{ display: "none" }}
                onChange={handleImg}
              />
            </div>
            <input
              type="text"
              placeholder={"Nhập tin nhắn"}
              value={content}
              onChange={(e) => setContent(e.target.value)}
            />
            <div className="emoji">
              <CiFaceSmile
                style={{
                  cursor: "pointer",
                  color: "white",
                }}
                className="icon"
                onClick={() => setOpenEmoji((prev) => !prev)}
              />
              <div className="picker">
                <EmojiPicker
                  width={"300px"}
                  open={openEmoji}
                  onEmojiClick={handleEmoji}
                />
              </div>
            </div>
            <Button
              className="sendButton flexCenter"
              disabled={content === ""}
              // onClick={handleSendMessage}
              type="submit"
              loading={loadingSend}
            >
              <LuSendHorizonal className="icon" />
            </Button>
          </div>
        )}
      </form>
    </div>
  );
};

export default ConversationDetail;
