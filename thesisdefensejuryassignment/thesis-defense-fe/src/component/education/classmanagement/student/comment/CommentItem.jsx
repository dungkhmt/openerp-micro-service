import {
  Avatar,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  Input,
  Menu,
  MenuItem,
} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import {useEffect, useState} from "react";
import displayTime from "utils/DateTimeUtils";
import {errorNoti, successNoti} from "utils/notification";
import {request} from "../../../../../api";
import ReplyCommentItem from "./ReplyCommentItem";

const useStyles = makeStyles((theme) => ({
  commentItem: {
    display: "flex",
    flexDirection: "row",
    alignItems: "flex-start",
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },
  commentContent: {
    marginLeft: theme.spacing(1),
    flexGrow: 1,
    display: "flex",
  },
  commentActionBtn: {
    color: "#bbb",
    fontSize: "10px",
  },
  listComment: {
    marginLeft: theme.spacing(6),
  },
  displayMenu: {
    visibility: true,
  },
  hideMenu: {
    visibility: false,
  },
}));

export default function CommentItem({
  comment,
  commentFlag,
  setCommentFlag,
  userId,
}) {
  const [isEditting, setIsEditting] = useState(false);
  const [commentTextEdit, setCommentTextEdit] = useState(comment.commentText);
  const [openModal, setOpenModal] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [isShowReplyComment, setIsShowReplyComment] = useState(false);
  const [listReplyComment, setListReplyComment] = useState([]);
  const [isShowInputReply, setIsShowInputReply] = useState(false);
  const [replyCommentText, setReplyCommentText] = useState("");
  const [flag, setFlag] = useState(false);
  const classes = useStyles();
  const [menuAnchorEl, setMenuAnchorEl] = useState(null);
  const open = Boolean(menuAnchorEl);
  const [isDisplayMenu, setIsDisplayMenu] = useState(false);

  const handleClose = () => {
    setMenuAnchorEl(null);
  };
  const handleClick = (event) => {
    setMenuAnchorEl(event.currentTarget);
  };

  useEffect(() => {
    let getListReply = async () => {
      request(
        "get",
        `/get-list-reply-comments-on-quiz/${comment.commentId}`,
        (res) => {
          setListReplyComment(res.data);
        }
      );
      /*
			let res = await authGet(
				dispatch,
				token,
				`/get-list-reply-comments-on-quiz/${comment.commentId}`
			);

			setListReplyComment(res);
			*/
    };

    getListReply();
  }, [flag]);

  const onHandleUpdateComment = () => {
    editComment();
    setIsEditting(false);
  };

  const handleClickOpenModal = () => {
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    //close menu edit/delete
    setAnchorEl(null);
    setOpenModal(false);
  };

  const onConfirmDeleteComment = () => {
    deleteComment();
    setAnchorEl(null);
    setOpenModal(false);
  };

  //function with call api
  //get list reply of comment
  const onGetListReplyComment = async (commentId) => {
    if (isShowReplyComment === false) {
      request("get", `/get-list-reply-comments-on-quiz/${commentId}`, (res) => {
        setListReplyComment(res.data);
      });

      /*
		let res = await authGet(
        dispatch,
        token,
        `/get-list-reply-comments-on-quiz/${commentId}`
      );
      setListReplyComment(res);
		console.log(listReplyComment);
		*/
    }
    setIsShowReplyComment(!isShowReplyComment);
  };

  //edit comment
  const editComment = async () => {
    let body = {
      commentText: commentTextEdit,
    };

    /*
    let edittedComment = await authPut(
      dispatch,
      token,
      `/edit-comment-on-quiz/${comment.commentId}`,
      body
    );
		*/
    request(
      "put",
      `/edit-comment-on-quiz/${comment.commentId}`,
      (res) => {
        if (200 === res.status) {
          successNoti("Sửa bình luận thành công", true);
        }
      },
      () => {
        errorNoti("Sửa bình luận thất bại", true);
      },
      body
    );

    setCommentFlag(!commentFlag);
  };

  //delete comment
  const deleteComment = async () => {
    // let edittedComment = await authDelete(
    //   dispatch,
    //   token,
    //   `/delete-comment-on-quiz/${comment.commentId}`,
    //   {}
    // );

    // setCommentFlag(!commentFlag);
    request(
      "delete",
      `/delete-comment-on-quiz/${comment.commentId}`,
      (res) => {
        successNoti("Xóa bình luận thành công", true);
      },
      () => {
        errorNoti("Xóa bình luận thất bại", true);
      }
    );

    setCommentFlag(!commentFlag);
  };

  //post reply comment
  const createComment = async () => {
    let body = {
      comment: replyCommentText.trim(),
      questionId: comment.questionId,
      replyToCommentId: comment.commentId,
    };

    if (replyCommentText.trim() !== "") {
      // /*
      // let commentPost = await authPost(
      //   dispatch,
      //   token,
      //   "/post-comment-on-quiz",
      //   body
      // );
      // */
      request(
        "post",
        "/post-comment-on-quiz",
        (res) => {
          if (200 === res.status) {
            onGetListReplyComment(comment.commentId);
            successNoti("Đăng bình luận thành công", true);
          }
        },
        {},
        body
      );
      setReplyCommentText("");
    } else {
      errorNoti("Bình luận không thể để trống");
    }
  };

  //format date
  const formatDate = (originalDate) => {
    let date = new Date(originalDate);

    return displayTime(date);
  };

  return (
    <div>
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
          <Button onClick={handleCloseModal} color="primary">
            Hủy bỏ
          </Button>
          <Button onClick={() => onConfirmDeleteComment()} color="secondary">
            Xóa
          </Button>
        </DialogActions>
      </Dialog>
      <div className={classes.commentItem}>
        <Avatar>
          {comment.fullNameOfCreator.split(" ").pop().charAt(0).toUpperCase()}
        </Avatar>
        <div
          className={classes.commentContent}
          onMouseEnter={() => setIsDisplayMenu(true)}
          onMouseLeave={() => setIsDisplayMenu(false)}
        >
          <div style={{ flexGrow: 1 }}>
            <div>
              <b>
                {`${comment.fullNameOfCreator} (${comment.createdByUserLoginId})`}
              </b>
              &nbsp;
              <span style={{ marginLeft: "5px" }}>
                {comment ? formatDate(comment.createdStamp) : null}
              </span>
            </div>
            <div>
              {!isEditting ? (
                comment.commentText
              ) : (
                <div>
                  <Input
                    value={commentTextEdit}
                    onChange={(event) => setCommentTextEdit(event.target.value)}
                    type="text"
                  />
                  <Button
                    className={classes.commentActionBtn}
                    onClick={() => setIsEditting(false)}
                  >
                    Hủy
                  </Button>
                  <Button onClick={() => onHandleUpdateComment()}>
                    Cập nhật
                  </Button>
                </div>
              )}
            </div>
            <div>
              <Button
                onClick={() => setIsShowInputReply(!isShowInputReply)}
                className={classes.commentActionBtn}
              >
                Phản hồi
              </Button>
              <Button
                onClick={() => onGetListReplyComment(comment.commentId)}
                className={classes.commentActionBtn}
              >
                {isShowReplyComment ? (
                  <span>&#x25B2; Ẩn phản hồi</span>
                ) : (
                  <span>&#x25BC; Xem các phản hổi</span>
                )}
              </Button>
              {/* {userId === comment.createdByUserLoginId && (
              <>
                <Button
                  className={classes.commentActionBtn}
                  onClick={() => setIsEditting(!isEditting)}
                >
                  Chỉnh sửa
                </Button>
                <Button
                  className={classes.commentActionBtn}
                  onClick={handleClickOpenModal}
                >
                  Xóa
                </Button>
              </>
            )} */}
            </div>
          </div>
          <div style={{ height: "70px", minWidth: "70px" }}>
            {isDisplayMenu && userId === comment.createdByUserLoginId && (
              <IconButton
                aria-label="more"
                aria-controls="long-menu"
                aria-haspopup="true"
                onClick={handleClick}
                // className={isDisplayMenu ? classes.displayMenu : classes.hideMenu}
                style={{ height: "70px", width: "70px" }}
              >
                <MoreVertIcon />
              </IconButton>
            )}
            <Menu
              id="long-menu"
              anchorEl={menuAnchorEl}
              open={open}
              onClose={handleClose}
              keepMounted
            >
              <MenuItem
                onClick={() => {
                  setIsEditting(!isEditting);
                  handleClose();
                }}
              >
                Chỉnh sửa
              </MenuItem>
              <MenuItem
                onClick={() => {
                  handleClickOpenModal();
                  handleClose();
                }}
              >
                Xoá
              </MenuItem>
            </Menu>
          </div>
        </div>
      </div>
      <div className={classes.listComment}>
        {isShowReplyComment && (
          <div>
            {listReplyComment.length > 0 &&
              listReplyComment.map((comment) => (
                <ReplyCommentItem
                  comment={comment}
                  flag={flag}
                  setFlag={setFlag}
                  userId={userId}
                />
              ))}
          </div>
        )}
        {isShowInputReply && (
          <div>
            <Input
              value={replyCommentText}
              onChange={(event) => setReplyCommentText(event.target.value)}
            />
            <Button onClick={createComment}>Phản hồi</Button>
          </div>
        )}
      </div>
    </div>
  );
}
