import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import {Box, IconButton, Modal, Typography} from "@mui/material";
import React from "react";
import ModalAction from "./ModalAction";

const HustModal = (props) => {
  const {
    children,
    onClose,
    onOk,
    textOk,
    textClose,
    title,
    isLoading,
    showCloseBtnTitle = true,
    maxWidthPaper = 640,
    minWidthPaper,
    classRoot,
    isNotShowCloseButton,
    isNotShowActionButton,
    isNotShowFooter,
    disabledOk,
    ...remainProps
  } = props;

  return (
    <Modal {...remainProps} onClose={onClose} className={`${classRoot}`}>
      <Box
        sx={{
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          bgcolor: "background.paper",
          border: "2px solid gray",
          width: maxWidthPaper,
          maxWidth: maxWidthPaper,
          maxHeight: "500px",
          overflowY: "auto",
          boxShadow: 24,
          p: "16px 28px 20px 28px",
          borderRadius: 3,
          outline: 0,
          //   "& > :not(style)": { m: 1 },
        }}
      >
        <Box
          display="flex"
          flexDirection="row"
          justifyContent="space-between"
          mb={2}
        >
          <Typography align="center" variant="h5">
            {title || ""}
          </Typography>
          {showCloseBtnTitle ? (
            <IconButton onClick={onClose}>
              <CloseRoundedIcon />
            </IconButton>
          ) : (
            <></>
          )}
        </Box>
        {children}
        {!isNotShowFooter && (
          <ModalAction
            onClose={onClose}
            onOk={onOk}
            textOk={textOk}
            textClose={textClose}
            isLoading={isLoading}
            disabledOk={disabledOk}
            isNotShowCloseButton={isNotShowCloseButton}
            isNotShowActionButton={isNotShowActionButton}
          />
        )}
      </Box>
    </Modal>
  );
};

export default React.memo(HustModal);
