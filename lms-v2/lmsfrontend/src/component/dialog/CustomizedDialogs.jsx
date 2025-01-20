import { grey } from "@material-ui/core/colors";
import Dialog from "@material-ui/core/Dialog";
import MuiDialogActions from "@material-ui/core/DialogActions";
import MuiDialogContent from "@material-ui/core/DialogContent";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import IconButton from "@material-ui/core/IconButton";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import CloseIcon from "@material-ui/icons/Close";
import clsx from "clsx";

const dialogTitleStyles = (theme) => ({
  root: {
    margin: 0,
    height: theme.spacing(8),
    padding: theme.spacing(2),
  },
  closeButton: {
    width: 40,
    height: 40,
    position: "absolute",
    top: theme.spacing(1) * 1.5,
    right: theme.spacing(2),
    color: "rgba(0, 0, 0, 0.5)",
    background: grey[300],
    "&:hover": {
      background: grey[400],
    },
  },
});

const DialogTitle = withStyles(dialogTitleStyles)((props) => {
  const { children, classes, onClose, style, centerTitle, ...other } = props;

  return (
    <MuiDialogTitle
      className={classes.root}
      style={{ textAlign: centerTitle ? "center" : "left", ...style }}
      {...other}
    >
      {/* <Box
        display="flex"
        width="100%"
        justifyContent={justifyTitle ? justifyTitle : "center"}
        alignItems="center"
      > */}
      {/* <Typography variant="h6"> */}
      {children}
      {/* </Typography> */}
      {/* </Box> */}
      {onClose && (
        <IconButton
          aria-label="close"
          className={classes.closeButton}
          onClick={onClose}
        >
          <CloseIcon style={{ fontSize: 28 }} />
        </IconButton>
      )}
    </MuiDialogTitle>
  );
});

const DialogContent = withStyles((theme) => ({
  root: {
    padding: theme.spacing(2),
  },
}))(MuiDialogContent);

const useStyles = makeStyles((theme) => ({
  dialogActionsRoot: {
    margin: 0,
    height: theme.spacing(8),
    padding: theme.spacing(1),
    justifyContent: 'center',
    gap: theme.spacing(0.5),
  },
  paper: {
    borderRadius: 8,
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
  },
  topDivider: {
    borderTop: "1px solid rgba(0, 0, 0, 0.12)",
  },
  bottomDivider: {
    borderBottom: "1px solid rgba(0, 0, 0, 0.12)",
  },
}));

const DialogActions = ({ className, children }) => {
  const classes = useStyles();

  return (
    <MuiDialogActions className={clsx(classes.dialogActionsRoot, className)}>
      {children}
    </MuiDialogActions>
  );
};

export default function CustomizedDialogs(props) {
  const classes = useStyles();
  const {
    open,
    title,
    classNames,
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
        classes={{ paper: clsx(classes.paper, classNames?.paper) }}
      >
        <DialogTitle
          id="customized-dialog-title"
          onClose={handleClose}
          style={classNames?.title}
          centerTitle={centerTitle}
        >
          <strong>{title}</strong>
        </DialogTitle>
        <DialogContent
          className={clsx(
            {
              [classes.topDivider]: contentTopDivider,
              [classes.bottomDivider]: contentBottomDivider,
            },
            classNames?.content
          )}
        >
          {content}
        </DialogContent>
        {actions && (
          <DialogActions className={classNames?.actions}>
            {actions}
          </DialogActions>
        )}
      </Dialog>
    </div>
  );
}
