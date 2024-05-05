import { LoadingButton } from "@mui/lab";
import { Box, Button, DialogActions } from "@mui/material";

const ModalAction = (props) => {
  const {
    children,
    textOk,
    textClose,
    onClose,
    onOk,
    isLoading,
    isNotShowCloseButton,
    isNotShowActionButton,
    disabledOk,
    okButtonType, // primary | secondary | success | error
    renderActions: Component,
    ...remainProps
  } = props;

  if (!onOk && !onClose && !Component) {
    return <> </>;
  }

  return (
    <DialogActions
      {...remainProps}
      style={{ paddingTop: "28px", paddingRight: "4px" }}
    >
      {Component ? (
        <Component />
      ) : (
        <Box display="flex">
          {onClose && !isNotShowCloseButton ? (
            <Button
              variant="outlined"
              color="primary"
              size="small"
              onClick={() => onClose()}
            >
              {textClose ? textClose : "common:cancel"}
            </Button>
          ) : null}
          {onOk && !isNotShowActionButton ? (
            <LoadingButton
              variant="contained"
              color={okButtonType ? okButtonType : "primary"}
              size="small"
              style={{ marginLeft: "16px" }}
              onClick={() => onOk()}
              loading={isLoading}
              disabled={disabledOk}
            >
              {textOk ? textOk : "OK"}
            </LoadingButton>
          ) : null}
        </Box>
      )}
    </DialogActions>
  );
};

export default ModalAction;
