import "./ContactBox.css";
import { Avatar, Button, CopyButton, Dialog } from "@mantine/core";
import { Link } from "react-router-dom";
import { useDisclosure } from "@mantine/hooks";
import ConversationDetail from "../ConversationDetail/ConversationDetail";
import React, { useContext } from "react";
import { ConversationContext } from "../../context/ConversationContext";
import { toast } from "react-toastify";
import { useSelector } from "react-redux";

const ContactBox = ({ author, isOwner }) => {
  const [opened, { toggle, close }] = useDisclosure(false);
  const { findConversationWith } = useContext(ConversationContext);
  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);
  return (
    <div className="contact-box-container flexColCenter">
      <Avatar src={author?.avatar} size={"xl"} />
      <div
        style={{
          fontSize: "16px",
          color: "#2C2C2C",
        }}
      >
        {author?.name}
      </div>
      <div
        style={{
          fontSize: "12px",
          color: "#505050",
        }}
      >
        <Link to={"/manager-post/" + author.accountId}>
          Xem {author?.totalPostSell} tin bán khác
        </Link>
      </div>
      {author.phone !== null && (
        <CopyButton value={author?.phone}>
          {({ copied, copy }) => (
            <Button fullWidth color={copied ? "teal" : "blue"} onClick={copy}>
              {copied ? "Đã sao chép" : author?.phone}
            </Button>
          )}
        </CopyButton>
      )}
      <Button fullWidth>
        <a href={`mailto:${author?.email}`}>Gửi emai</a>
      </Button>

      {!isOwner && (
        <Button
          fullWidth
          onClick={() => {
            if (isLoggedIn) {
              toggle();
            } else {
              toast.info("Đăng nhập để liên lạc");
            }
          }}
        >
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
        <ConversationDetail conversationSelect={findConversationWith(author)} />
      </Dialog>
    </div>
  );
};

export default ContactBox;
