import {
  Avatar,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Input,
  Menu,
  MenuItem,
} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {request} from "api";
import {useEffect, useState} from "react";
import displayTime from "utils/DateTimeUtils";
import {errorNoti, successNoti} from "utils/notification";

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
    flexDirection: "row",
    alignItems: "flex-start",
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },
  inputComment: {
    display: "flex",
    flexDirection: "row",
    alignItems: "center",
  },
  customTextField: {
    flexGrow: 1,
    "& input::placeholder": {
      fontSize: "12px",
    },
  },
  growItem: {
    flexGrow: 1,
    marginLeft: theme.spacing(1),
  },
  btnComment: {
    fontSize: "12px",
    color: "#1976d2",
  },
}));

export default function ReplyCommentItem({
  comment,
  deleteComment,
  editComment,
  setFlag,
  flag,
  userId,
}) {
  const [valueCommentMessage, setValueCommentMessage] = useState(
    comment.commentMessage
  );
  const [isEdittingComment, setIsEdittingComment] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const open = Boolean(anchorEl);
  const classes = useStyles();

  useEffect(() => {
    setValueCommentMessage(comment.commentMessage);
  }, [comment]);
  const handleClickOpenModal = () => {
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    //close menu edit/delete
    setAnchorEl(null);
    setOpenModal(false);
  };

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleDeleteComment = () => {
    // deleteComment(comment.commentId);
    request(
      "delete",
      `/edu/class/comment/${comment.commentId}`,
      (res) => {
        setFlag(!flag);
        successNoti("Xóa bình luận thành công", true);
      },
      () => {
        errorNoti("Xóa bình luận thất bại", true);
      }
    );
    setOpenModal(false);
    setAnchorEl(null);
  };

  const handleShowInputComment = () => {
    setAnchorEl(null);
    setIsEdittingComment(true);
  };

  let body = {};

  const handleSaveEditComment = (cmtContent) => {
    // editComment(comment.commentId, cmtContent);
    if (
      cmtContent === undefined ||
      cmtContent === null ||
      cmtContent.trim() === ""
    ) {
      errorNoti("Nội dung bình luận không được để trống", true);
    } else {
      let body = {
        commentMessage: cmtContent,
        createdStamp: comment.createdStamp,
      };
      request(
        // token,
        // history,
        "put",
        `/edu/class/comment/${comment.commentId}`,
        (res) => {
          if (200 === res.status) {
            setFlag(!flag);
            setIsEdittingComment(false);
            successNoti("Sửa bình luận thành công", true);
          }
        },
        () => {
          errorNoti("Sửa bình luận thất bại", true);
        },
        body
      );
    }
  };

  //format date
  const formatDate = (originalDate) => {
    let date = new Date(originalDate);

    return displayTime(date);
  };

  return (
    <div className={classes.root}>
      {/**Modal confirm delete comment */}
      <Dialog
        open={openModal}
        onClose={handleCloseModal}
        aria-labelledby="draggable-dialog-title"
      >
        <DialogTitle style={{ cursor: "move" }} id="draggable-dialog-title">
          Xóa bình luận
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            Khi bạn xóa bình luận, các bình luận trả lời cũng mất theo. <br />
            Bạn có chắc muốn xóa?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button autoFocus onClick={handleCloseModal} color="primary">
            Hủy bỏ
          </Button>
          <Button onClick={() => handleDeleteComment()} color="secondary">
            Xóa
          </Button>
        </DialogActions>
      </Dialog>
      <Avatar>
        {comment.fullNameOfCreator.split(" ").pop().charAt(0).toUpperCase()}
      </Avatar>
      <div>
        <b>{comment.fullNameOfCreator}</b>
        &nbsp;
        <span style={{ marginLeft: "5px" }}>
          {comment ? formatDate(comment.createdStamp) : null}
        </span>
        <div className={classes.growItem}>
          {isEdittingComment ? (
            <div>
              <Input
                value={valueCommentMessage}
                onChange={(event) => setValueCommentMessage(event.target.value)}
                type="text"
                fullWidth={true}
              />
              <Button onClick={() => setIsEdittingComment(false)}>Hủy</Button>
              <Button
                onClick={() => handleSaveEditComment(valueCommentMessage)}
              >
                Lưu
              </Button>
            </div>
          ) : (
            <div>{comment.commentMessage}</div>
          )}
        </div>
        <div>
          {userId === comment.postedByUserLoginId && (
            <Button
              aria-label="more"
              id="long-button"
              aria-controls="long-menu"
              aria-expanded={open ? "true" : undefined}
              aria-haspopup="true"
              onClick={(event) => handleClick(event)}
              style={{ color: "#bbb", fontSize: "10px" }}
            >
              Khác
            </Button>
          )}
        </div>
      </div>
      <Menu
        id="long-menu"
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
      >
        <MenuItem onClick={() => handleShowInputComment()}>Chỉnh sửa</MenuItem>
        <MenuItem onClick={handleClickOpenModal}>Xoá</MenuItem>
      </Menu>
    </div>
  );
}
