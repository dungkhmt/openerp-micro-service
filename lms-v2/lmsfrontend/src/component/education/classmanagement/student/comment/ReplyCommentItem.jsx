import {
  Avatar,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Input,
} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {useState} from "react";
import displayTime from "utils/DateTimeUtils";
import {errorNoti, successNoti} from "utils/notification";
import {request} from "../../../../../api";

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
  },
  commentActionBtn: {
    color: "#bbb",
    fontSize: "10px",
  },
}));

export default function ReplyCommentItem({ comment, flag, setFlag, userId }) {
  const [isEditting, setIsEditting] = useState(false);
  const [commentTextEdit, setCommentTextEdit] = useState(comment.commentText);
  const [openModal, setOpenModal] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const classes = useStyles();

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
  //edit comment
  const editComment = async () => {
    let body = {
      commentText: commentTextEdit,
    };

    // let edittedComment = await authPut(
    //   dispatch,
    //   token,
    //   `/edit-comment-on-quiz/${comment.commentId}`,
    //   body
    // );
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

    setFlag(!flag);
  };

  //delete comment
  const deleteComment = async () => {
    // let edittedComment = await authDelete(
    //   dispatch,
    //   token,
    //   `/delete-comment-on-quiz/${comment.commentId}`,
    //   {}
    // );

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

    setFlag(!flag);
  };

  //format date
  const formatDate = (originalDate) => {
    let date = new Date(originalDate);

    return displayTime(date);
  };

  return (
    <div>
      <div className={classes.commentItem}>
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
        <Avatar style={{ width: "30px", height: "30px", fontSize: "14px" }}>
          {comment.fullNameOfCreator.split(" ").pop().charAt(0).toUpperCase()}
        </Avatar>
        <div className={classes.commentContent}>
          <div>
            <b>{comment.fullNameOfCreator}</b>&nbsp;
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
          {userId === comment.createdByUserLoginId && (
            <div>
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
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
