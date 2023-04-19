import React, {useEffect, useState} from "react";
import {Avatar, Button, Table, TextField,} from "@material-ui/core";
import PersonIcon from "@material-ui/icons/Person";
import CommentItem from "./comment/CommentItem.jsx";
import {request} from "../../../../api";
import {errorNoti, successNoti} from "utils/notification/index.js";

export default function CommentsOnQuiz(props) {
  const { questionId, open, setOpen } = props;
  const [comment, setComment] = useState("");
  const [comments, setComments] = useState([]);
  const [commentFlag, setCommentFlag] = useState(false);
  const [loginUser, setLoginUser] = useState(null);

  function handleChangeComment(e) {
    setComment(e.target.value);
  }
  function getCommentList() {
    request("get", "/get-list-comments-on-quiz/" + questionId, (res) => {
      console.log("getCommentList, res = ", res);
      setComments(res.data);
    });
  }
  function handlePostComment() {
    if (comment.trim() === "") {
      errorNoti("Nội dung không được để trống");
    } else {
      request(
        "post",
        "/post-comment-on-quiz",
        (res) => {
          getCommentList();
          successNoti("Đăng bình luận thành công", true);
          setComment("");
        },
        {},
        {
          questionId: questionId,
          comment: comment,
        }
      );
    }
  }

  //get user login
  useEffect(() => {
    request(
      "get",
      "/my-account/",
      (res) => {
        let data = res.data;

        setLoginUser({
          name: data.name,
          userName: data.user,
          partyId: data.partyId,
        });
      },
      { 401: () => {} }
    );
  }, []);

  useEffect(() => {
    getCommentList();
    //}, [commentFlag]);
  }, [commentFlag]);
  return (
    <>
      <div>
        <div
          style={{
            padding: "30px 0px 30px 0px",
            display: "flex",
          }}
        >
          <Avatar
            style={{ height: "40px", width: "40px", marginRight: "20px" }}
          >
            <PersonIcon />
          </Avatar>
          <TextField
            value={comment}
            onChange={handleChangeComment}
            style={{ flexGrow: 1, marginRight: "20px" }}
            placeholder="Bình luận về quiz này"
          />
          <Button
            variant="contained"
            color="secondary"
            onClick={handlePostComment}
          >
            Bình luận
          </Button>
        </div>
        <Table>
          {comments.map((item, index) => (
            // <tr>
            //   <td>
            //     {item.fullNameOfCreator}&nbsp;(
            //     {toFormattedDateTime(item.createdStamp)}
            //     ): &nbsp;&nbsp; {item.commentText}
            //   </td>
            // </tr>
            <CommentItem
              comment={item}
              setCommentFlag={setCommentFlag}
              commentFlag={commentFlag}
              loginUser={loginUser}
            />
          ))}
        </Table>
      </div>
    </>
  );
}
