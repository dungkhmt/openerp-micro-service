import {makeStyles} from "@material-ui/core/styles";
import {
  Avatar,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Menu,
  MenuItem,
  TextField,
} from "@material-ui/core";
import {useEffect, useState} from "react";
import displayTime from "utils/DateTimeUtils";
import {errorNoti, successNoti} from "utils/notification";
import {request} from "../../../../api";
import ReplyCommentItem from "./ReplyCommentItem";

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

export default function CommentItem({
  comment,
  chapterMaterialId,
  deleteComment,
  editComment,
  userId,
}) {
  const [valueCommentMessage, setValueCommentMessage] = useState(
    comment.commentMessage
  );
  const [replyCommentMessage, setReplyCommentMessage] = useState("");
  const [isEdittingComment, setIsEdittingComment] = useState(false);
  const [isShowReplyInput, setIsShowReplyInput] = useState(false);
  const [showReplyList, setShowReplyList] = useState(false);
  const [listReplyComment, setListReplyComment] = useState([]);
  const [flag, setFlag] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const open = Boolean(anchorEl);
  const classes = useStyles();

  useEffect(() => {
    setValueCommentMessage(comment.commentMessage);
    // onGetListReplyComment(comment.commentId);
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
    deleteComment(comment.commentId, () =>
      onGetListReplyComment(comment.commentId)
    );
    setOpenModal(false);
    setAnchorEl(null);
  };

  const handleShowInputComment = () => {
    setAnchorEl(null);
    setIsEdittingComment(true);
  };

  const handleSaveEditComment = (cmtContent) => {
    editComment(comment.commentId, cmtContent, comment.createdStamp, () =>
      onGetListReplyComment(comment.commentId)
    );
    setIsEdittingComment(false);

    setFlag(!flag);
  };

  //post reply comment
  const createReplyComment = async () => {
    if (
      replyCommentMessage === "undefined" ||
      replyCommentMessage === null ||
      replyCommentMessage.trim() === ""
    ) {
      errorNoti("Bình luận không được để trống", true);
    } else {
      let body = {
        commentMessage: replyCommentMessage,
        eduCourseMaterialId: chapterMaterialId,
        replyToCommentId: comment.commentId,
      };

      // let commentPost = await authPost(
      //   dispatch,
      //   token,
      //   "/edu/class/comment",
      //   body
      // );
      request(
        "post",
        "/edu/class/comment",
        (res) => {
          if (200 === res.status) {
            setFlag(!flag);
            setReplyCommentMessage("");
            successNoti("Đăng bình luận thành công", true);
          }
        },
        () => {
          errorNoti("Đăng bình luận thất bại", true);
        },
        body
      );

      // if flag change, rerender listcomment
      // setFlag(!flag);
      // onGetListReplyComment(comment.commentId);
      // setReplyCommentMessage("");
    }
  };

  //get list reply of comment
  const onGetListReplyComment = async (commentId) => {
    // if (showReplyList === false) {

    request("get", `/edu/class/reply-comment/${commentId}`, (res) => {
      setListReplyComment(res.data);
      console.log(listReplyComment);
    });

    // }
    // setShowReplyList(!showReplyList);
  };

  //format date
  const formatDate = (originalDate) => {
    let date = new Date(originalDate);

    return displayTime(date);
  };

  useEffect(() => {
    onGetListReplyComment(comment.commentId);
  }, [flag]);

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
        <div>
          <b>{`${comment.fullNameOfCreator} (${comment.postedByUserLoginId})`}</b>
          &nbsp;
          <span style={{ marginLeft: "5px" }}>
            {comment ? formatDate(comment.createdStamp) : null}
          </span>
        </div>
        <div className={classes.growItem}>
          {isEdittingComment ? (
            <div>
              <TextField
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
          <Button
            onClick={() => setIsShowReplyInput(!isShowReplyInput)}
            style={{ color: "#bbb", fontSize: "10px" }}
          >
            Phản hồi
          </Button>

          <Button
            onClick={() => {
              setShowReplyList(!showReplyList);
              onGetListReplyComment(comment.commentId);
            }}
            style={{ color: "#1976d2", fontSize: "10px" }}
          >
            {showReplyList ? (
              <span>&#x25B2; Ẩn phản hồi</span>
            ) : (
              <span>&#x25BC; Xem các phản hổi</span>
            )}
          </Button>

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
        <div className={classes.listComment}>
          {showReplyList && (
            <div>
              {listReplyComment.length > 0 &&
                listReplyComment.map((comment) => (
                  <ReplyCommentItem
                    comment={comment}
                    editComment={editComment}
                    deleteComment={deleteComment}
                    chapterMaterialId={chapterMaterialId}
                    flag={flag}
                    setFlag={setFlag}
                    userId={userId}
                  />
                ))}
            </div>
          )}
          {isShowReplyInput && (
            <div>
              <TextField
                value={replyCommentMessage}
                onChange={(event) => setReplyCommentMessage(event.target.value)}
                style={{ fontSize: "10px" }}
                className={classes.customTextField}
                placeholder="Phản hồi bình luận"
              />
              <Button
                onClick={createReplyComment}
                style={{ fontSize: "10px", color: "#1976d2" }}
              >
                Phản hồi
              </Button>
            </div>
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
