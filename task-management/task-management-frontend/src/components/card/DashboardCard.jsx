import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Card,
  CardContent,
  Dialog,
  DialogContent,
  DialogTitle,
  Divider,
  Fade,
  IconButton,
  Tooltip,
  Typography,
  styled,
} from "@mui/material";
import { isFunction } from "@mui/x-data-grid/internals";
import PropTypes from "prop-types";
import { forwardRef, useState } from "react";

const StyledCard = styled(Card)(({ theme }) => ({
  boxShadow: theme.shadows[1],
  border: `1px solid ${theme.palette.grey[200]}`,
  borderRadius: 7,
  "& .title": {
    position: "relative",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: theme.spacing(2, 1, 1, 4),
    gap: theme.spacing(2),

    "& .title_text": {
      flex: 1,
      position: "relative",
      fontWeight: 600,
      fontSize: "0.9675rem",
      color: theme.palette.text.secondary,

      "&:hover": {
        border: `1.75px dashed ${theme.palette.grey[200]}`,
      },
    },
    "& .btn-drag": {
      display: "none",
      position: "absolute",
      left: 0,
      top: 8,
      padding: theme.spacing(0, 1),
      minWidth: "auto",
      color: theme.palette.secondary.main,
      cursor: "grab",
    },

    "& .title_action": {
      display: "flex",
      alignItems: "center",
      gap: theme.spacing(1),

      "& button": {
        padding: theme.spacing(1),
        minWidth: "auto",
        height: "auto",
        color: theme.palette.secondary.main,
        border: `1px solid ${theme.palette.grey[200]}`,
        borderRadius: 3,
      },

      "& .default_action": {
        display: "none",
      },
    },
  },
  "&:hover": {
    boxShadow: theme.shadows[2],
    ".title_action .default_action": {
      display: "inherit",
    },
    ".btn-drag": {
      display: "inline",
    },
  },
}));

const Transition = forwardRef(function Transition(props, ref) {
  return <Fade in ref={ref} {...props} timeout={550} />;
});

// eslint-disable-next-line react/display-name
const DashboardCard = forwardRef(function DashboardCard(
  {
    style,
    className,
    onMouseDown,
    onMouseUp,
    onTouchEnd,
    children,
    title,
    isDraggable = true,
    isExpandable = true,
    isRefreshable = true,
    onDialogClose,
    onRefresh,
    action,
    ...props
  },
  ref
) {
  const [open, setOpen] = useState(false);

  const handleClose = () => {
    if (isFunction(onDialogClose)) {
      onDialogClose();
    }

    setOpen(false);
  };

  const Action = action;

  return (
    <StyledCard style={style} className={className} {...props} ref={ref}>
      <CardContent className="title">
        <Tooltip title={title}>
          <Typography noWrap variant="body1" className="title_text">
            {title}
          </Typography>
        </Tooltip>
        {isDraggable && (
          <Button
            title="Kéo để di chuyển"
            onMouseDown={onMouseDown}
            onMouseUp={onMouseUp}
            onTouchEnd={onTouchEnd}
            className="btn-drag"
          >
            <Icon
              icon="material-symbols-light:drag-indicator"
              width={14}
              height={14}
            />
          </Button>
        )}
        <Box className="title_action">
          {/* action */}
          {Action && <Action />}
          {/* expand */}
          {isExpandable && (
            <Button title="Mở rộng" className="default_action">
              <Icon icon="lucide:expand" onClick={() => setOpen(true)} />
            </Button>
          )}
          {/* refresh */}
          {isRefreshable && (
            <Button
              title="Làm mới"
              onClick={onRefresh}
              className="default_action"
            >
              <Icon icon="eva:refresh-fill" />
            </Button>
          )}
        </Box>
      </CardContent>
      {open && (
        <Dialog
          open={open}
          keepMounted
          onClose={handleClose}
          TransitionComponent={Transition}
          aria-labelledby="dialog-expand"
          aria-describedby="dialog-expand-description"
          maxWidth="xl"
        >
          <DialogTitle
            id="dialog-expand"
            sx={{ padding: (theme) => theme.spacing(2, 4) }}
          >
            {title}
          </DialogTitle>
          <Divider />
          <Box sx={{ position: "absolute", top: 6, right: 6 }}>
            {Action && <Action />}
            <IconButton onClick={handleClose}>
              <Icon icon="typcn:times-outline" />
            </IconButton>
          </Box>
          <DialogContent sx={{ padding: 0 }}>
            <Box sx={{ width: "80vw", height: "70vh" }}>{children}</Box>
          </DialogContent>

          <Divider />
        </Dialog>
      )}
      {children}
    </StyledCard>
  );
});

DashboardCard.propTypes = {
  children: PropTypes.node,
  className: PropTypes.string,
  onMouseDown: PropTypes.func,
  onMouseUp: PropTypes.func,
  onTouchEnd: PropTypes.func,
  style: PropTypes.object,
  title: PropTypes.string.isRequired,
  isDraggable: PropTypes.bool,
  isExpandable: PropTypes.bool,
  isRefreshable: PropTypes.bool,
  onDialogClose: PropTypes.func,
  onRefresh: PropTypes.func,
  action: PropTypes.elementType,
};

export { DashboardCard };
