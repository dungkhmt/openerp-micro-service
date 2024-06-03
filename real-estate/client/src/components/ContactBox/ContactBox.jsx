import "./ContactBox.css"
import {Avatar, Button, CopyButton} from "@mantine/core";
import {Link} from "react-router-dom";

const ContactBox = ({account, isOwner}) => {
    return (
        <div className="contact-box-container flexColCenter">
            <Avatar src={account?.avatar} size={"xl"}/>
            <div
                style={{
                    fontSize: "16px",
                    color: "#2C2C2C"
                }}
            >
                {account?.name}
            </div>
            <div
                style={{
                    fontSize: "12px",
                    color: "#505050"
                }}
            >
                <Link to={"/manager-post/" + account.accountId}>
                    Xem {account?.totalPostSell} tin bán khác
                </Link>
            </div>
            {
                account.phone !== null && (
                    <CopyButton value={account?.phone}>
                        {({ copied, copy }) => (
                            <Button fullWidth color={copied ? 'teal' : 'blue'} onClick={copy}>
                                {copied ? 'Đã sao chép' : account?.phone}
                            </Button>
                        )}
                    </CopyButton>
                )
            }
            <Button fullWidth>
                <a href={"mailto:" + account?.email}>
                    Gửi emai
                </a>
            </Button>

            {!isOwner && (
                <Button fullWidth>
                    Gửi tin nhắn
                </Button>
            )}

        </div>
    )
}

export default ContactBox;