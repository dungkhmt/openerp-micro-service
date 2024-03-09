import { Box, Icon, Typography } from "@mui/material";
import { toast } from "react-hot-toast";
import { GiInfo } from "react-icons/gi";
import { IconContext } from "react-icons/lib/cjs";
import { MdCancel, MdWarning } from "react-icons/md";
import TertiaryButton from "../../components/button/TertiaryButton";

// Snackbar

export const processingNoti = (job, message) =>
  toast.promise(job(), {
    loading: message.loading ?? "Đang xử lý...",
    success: message.success ?? "Thành công!",
    error: message.error ?? "Rất tiếc! Đã có lỗi xảy ra.",
  });

export const wifiOffNotify = () =>
  toast.custom(
    () => (
      <Box display="flex" alignItems="center">
        <Icon style={{ margin: 6 }}>wifi_off_rounded</Icon>
        <Typography component="span" style={{ padding: 6, flexGrow: 1 }}>
          Bạn đang offline.
        </Typography>
        <TertiaryButton
          disableRipple
          onClick={() => window.location.reload(false)}
          style={{ fontSize: "1rem", color: "#42a5f5" }}
        >
          Làm mới
        </TertiaryButton>
      </Box>
    ),
    {}
  );

const ContentContainer = (props) => (
  <Box display="flex" alignItems="center">
    {/* eslint-disable-next-line react/prop-types */}
    {props.children}
  </Box>
);

export const errorNoti = (message) => {
  const content = (
    <ContentContainer>
      <IconContext.Provider>
        <MdCancel size={20} style={{ marginRight: "5px" }} />
      </IconContext.Provider>
      {message}
    </ContentContainer>
  );

  return toast.error(content);
};

export const successNoti = (message) => toast.success(message);

export const warningNoti = (message) =>
  toast(message, {
    icon: (
      <IconContext.Provider>
        <MdWarning size={20} style={{ marginRight: "5px" }} />
      </IconContext.Provider>
    ),
  });

export const infoNoti = (message) =>
  toast(message, {
    icon: (
      <IconContext.Provider>
        <GiInfo size={20} style={{ marginRight: "5px" }} />
      </IconContext.Provider>
    ),
  });
