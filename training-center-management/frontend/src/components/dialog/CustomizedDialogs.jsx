import CloseIcon from "@mui/icons-material/Close";
import { grey } from "@mui/material/colors";
import Dialog from "@mui/material/Dialog";
import MuiDialogActions from "@mui/material/DialogActions";
import MuiDialogContent from "@mui/material/DialogContent";
import MuiDialogTitle from "@mui/material/DialogTitle";
import IconButton from "@mui/material/IconButton";
import { styled } from "@mui/material/styles";

const dialogTitleStyles = {
  root: (theme) => ({
    margin: 0,
    height: theme.spacing(8),
    padding: theme.spacing(2),
  }),
  closeButton: (theme) => ({
    width: 40,
    height: 40,
    position: "absolute",
    top: theme.spacing(1.5),
    right: theme.spacing(2),
    color: "rgba(0, 0, 0, 0.5)",
    background: grey[300],
    "&:hover": {
      background: grey[400],
    },
  }),
};

const DialogTitle = (props) => {
  const { children, onClose, sx, centerTitle, ...other } = props;

  return (
    <MuiDialogTitle
      sx={(theme) => ({
        ...dialogTitleStyles.root(theme),
        ...(sx ? sx(theme) : {}),
      })}
      style={{ textAlign: centerTitle ? "center" : "left" }}
      {...other}
    >
      {children}
      {onClose && (
        <IconButton
          aria-label="close"
          sx={dialogTitleStyles.closeButton}
          onClick={onClose}
        >
          <CloseIcon style={{ fontSize: 28 }} />
        </IconButton>
      )}
    </MuiDialogTitle>
  );
};

const DialogContent = styled(MuiDialogContent)(({ theme }) => ({
  padding: theme.spacing(1) + " !important",
}));

const styles = {
  dialogActionsRoot: (theme) => ({
    margin: 0,
    height: theme.spacing(8),
    padding: theme.spacing(1),
  }),
  paper: {
    borderRadius: 2,
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
  },
  topDivider: {
    borderTop: "1px solid rgba(0, 0, 0, 0.12)",
  },
  bottomDivider: {
    borderBottom: "1px solid rgba(0, 0, 0, 0.12)",
  },
};

const DialogActions = ({ sx, children }) => {
  return (
    <MuiDialogActions
      sx={(theme) => ({
        ...styles.dialogActionsRoot(theme),
        ...(sx ? sx(theme) : {}),
      })}
    >
      {children}
    </MuiDialogActions>
  );
};

export default function CustomizedDialogs(props) {
  const {
    open,
    title,
    styles: customStyles,
    centerTitle,
    contentTopDivider,
    contentBottomDivider,
    handleClose,
    actions,
    content,
  } = props;

  return (
    <div>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        PaperProps={{ sx: styles.paper }}
      >
        <DialogTitle
          id="customized-dialog-title"
          onClose={handleClose}
          centerTitle={centerTitle}
          sx={customStyles?.title}
        >
          <strong>{title}</strong>
        </DialogTitle>
        <DialogContent
          sx={(theme) => ({
            ...(contentTopDivider ? styles.topDivider : {}),
            ...(contentBottomDivider ? styles.bottomDivider : {}),
            ...(customStyles?.content ? customStyles.content(theme) : {}),
          })}
        >
          {content}
        </DialogContent>
        {actions && (
          <DialogActions sx={customStyles?.actions}>{actions}</DialogActions>
        )}
      </Dialog>
    </div>
  );
}
