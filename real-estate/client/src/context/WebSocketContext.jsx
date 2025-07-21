import React, { createContext, useContext, useEffect, useState } from "react";
import { AccountContext } from "./AccountContext";
import Stomp from "stompjs";
import { ConversationContext } from "./ConversationContext";
import { TransportActionEnum } from "../utils/dataType";
import SockJS from "sockjs-client";
import { toast } from "react-toastify";

const WebSocketContext = createContext(undefined);

const WebsocketContextProvider = ({ children }) => {
  const [ws, setWsClient] = useState(undefined);
  const [messages, setMessages] = useState([]);
  const [isWsConnected, setWsConnected] = useState(true);
  const [areAllMessagesFetched, setAllMessagesFetched] = useState(true);
  const { account } = useContext(AccountContext);
  const { addMessage } = useContext(ConversationContext);

  useEffect(() => {
    if (
      account !== null &&
      account !== undefined &&
      Object.keys(account).length > 0
    ) {
      initWs(account);
    } else {
      disconnect();
    }
  }, [account]);

  async function initWs(account) {
    if (!account) {
      return;
    }
    const Sock = new SockJS("http://localhost:2805/ws");
    const wsObj = Stomp.over(Sock);
    setWsClient(wsObj);
    wsObj.connect({}, function () {
      setWsConnected(true);
      wsObj.subscribe(`/queue/topic/${account.accountId}/user`, (res) => {
        const data = JSON.parse(res.body);
        // console.log(data);
        switch (data.action) {
          case TransportActionEnum.NOTIFICATION_MESSAGE:
            const message = data.object;
            addMessage(message);
            toast.info("Có tin nhắn mới");
            break;
          default:
            break;
        }
      });
    });
  }

  const disconnect = () => {
    if (ws) {
      ws.disconnect();
    }
  };

  return (
    <WebSocketContext.Provider
      value={{
        ws,
        areAllMessagesFetched,
        messages,
        isWsConnected,
        setAllMessagesFetched,
        setWsClient,
        setMessages,
        setWsConnected,
        disconnect,
      }}
    >
      {children}
    </WebSocketContext.Provider>
  );
};

export { WebSocketContext, WebsocketContextProvider };
