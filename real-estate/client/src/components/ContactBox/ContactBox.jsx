import "./ContactBox.css";
import { Avatar, Button, CopyButton, Dialog } from "@mantine/core";
import { Link } from "react-router-dom";
import { useDisclosure } from "@mantine/hooks";
import ConversationDetail from "../ConversationDetail/ConversationDetail";
import React, { useContext } from "react";
import { ConversationContext } from "../../context/ConversationContext";

const ContactBox = ({ account, isOwner }) => {
  const [opened, { toggle, close }] = useDisclosure(false);
  const { findConversationWith } = useContext(ConversationContext);

  return (
    <div className="contact-box-container flexColCenter">
      <Avatar src={account?.avatar} size={"xl"} />
      <div
        style={{
          fontSize: "16px",
          color: "#2C2C2C",
        }}
      >
        {account?.name}
      </div>
      <div
        style={{
          fontSize: "12px",
          color: "#505050",
        }}
      >
        <Link to={"/manager-post/" + account.accountId}>
          Xem {account?.totalPostSell} tin bán khác
        </Link>
      </div>
      {account.phone !== null && (
        <CopyButton value={account?.phone}>
          {({ copied, copy }) => (
            <Button fullWidth color={copied ? "teal" : "blue"} onClick={copy}>
              {copied ? "Đã sao chép" : account?.phone}
            </Button>
          )}
        </CopyButton>
      )}
      <Button fullWidth>
        <a href={`mailto:${account?.email}`}>Gửi emai</a>
      </Button>

      {!isOwner && (
        <Button fullWidth onClick={toggle}>
          Gửi tin nhắn
        </Button>
      )}

      <Dialog
        className="custom-dialog"
        opened={opened}
        withCloseButton
        onClose={close}
        // size="lg"
        spaci
        radius="md"
        zIndex={1001}
        style={{
          width: "450px",
          height: "500px",
          backgroundColor: "rgba(17, 25, 40, 1)",
          padding: "0 !important",
        }}
      >
        <ConversationDetail
          conversationSelect={findConversationWith(account)}
        />
      </Dialog>
    </div>
  );
};

export default ContactBox;
