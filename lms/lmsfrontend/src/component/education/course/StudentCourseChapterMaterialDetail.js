import {Button, Card, CardContent} from "@material-ui/core/";
import {makeStyles} from "@material-ui/core/styles";
import {KeyboardArrowLeft, KeyboardArrowRight, ZoomIn, ZoomOut,} from "@material-ui/icons";
import {useKeycloak} from "@react-keycloak/web";
import {number} from "prop-types";
import {useEffect, useState} from "react";
import {FullScreen, useFullScreenHandle} from "react-full-screen";
import {useParams} from "react-router";
import {Link} from "react-router-dom";
import {classState} from "state/ClassState";
import {errorNoti, successNoti} from "utils/notification";
import {request} from "../../../api";
import Player from "../../../utils/Player";
import Loading from "../../common/Loading";
import CommentItem from "./comment/CommentItem";
import InputComment from "./comment/InputComment";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    marginTop: theme.spacing(4),
  },

  noComment: {
    margin: "10px auto",
    textAlign: "center",
    fontSize: "16px",
    fontWeight: "bold",
  },
}));

function StudentCourseChapterMaterialDetail() {
  const [comment, setComment] = useState("");
  const [flag, setFlag] = useState(false);
  const [listComment, setListComment] = useState([]);
  const params = useParams();
  const chapterMaterialId = params.chapterMaterialId;

  const classId = classState.classId.get();

  const [chapterMaterial, setChapterMaterial] = useState(null);
  const [sourceId, setSourceId] = useState(null);
  const [chapterId, setChapterId] = useState(null);
  const [chapterName, setChapterName] = useState(null);
  const classes = useStyles();
  const [listImage, setListImage] = useState([]);
  const [displayImage, setDisplayImage] = useState(0);
  const [isFullScreen, setIsFullScreen] = useState(false);
  const [pageNumberValue, setPageNumberValue] = useState(1);
  const [isLoading, setIsLoading] = useState(false);

  //
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;

  //handle change page number
  const onHandleChangePageNumber = (event) => {
    setPageNumberValue(event.target.value);
  };

  //handle press enter numberpage
  const onPressEnter = (event) => {
    if (event.key === "Enter") {
      if (pageNumberValue !== "") {
        if (pageNumberValue >= 1 && pageNumberValue <= listImage.length) {
          setDisplayImage(pageNumberValue - 1);
        }
      }
    }
  };

  async function getImages(slideId) {
    setIsLoading(true);

    request(
      "post",
      "/get-slide",
      (res) => {
        console.log("listImg: ", res.data);
        setListImage(res.data);
        setIsLoading(false);
      },
      {},
      {
        // fileId: "62829f1693445a31606162b6;62829f1793445a31606162b8",
        fileId: slideId,
      }
    );

    //let res = authGet(dispatch, token, '/edu/class/get-course-chapter-material-detail/' + chapterMaterialId);
  }

  async function getCourseChapterMaterialDetail() {
    // let res = await authGet(
    //   dispatch,
    //   token,
    //   "/edu/class/get-course-chapter-material-detail/" + chapterMaterialId
    // );

    request(
      "get",
      `/edu/class/get-course-chapter-material-detail/${chapterMaterialId}/${classId}`,
      (res) => {
        res = res.data;
        setChapterMaterial(res);
        console.log("getCourseChapterMaterialDetail ", res);
        if (res.sourceId !== null) {
          setSourceId(res.sourceId);
        } else {
          getImages(res.slideId);
        }
        setChapterId(res.eduCourseChapter.chapterId);
        setChapterName(res.eduCourseChapter.chapterName);
      }
    );
  }

  const prevImage = () => {
    if (displayImage > 0) {
      setPageNumberValue(displayImage);
      setDisplayImage(displayImage - 1);
    }
  };

  const nextImage = () => {
    if (displayImage < listImage.length) {
      setPageNumberValue(displayImage + 2);
      setDisplayImage(displayImage + 1);
    }
  };

  const handleSeeFullScreen = useFullScreenHandle();

  // async function getListCommentsEduCourseMaterial() {
  //   let res = await authGet(
  //     dispatch,
  //     token,
  //     `/edu/class/comment/${chapterMaterialId}`
  //   );

  //   let cmtOnVideo = res.filter((cmt) => {
  //     return cmt.replyToCommentId === null;
  //   });
  //   let cmtReplyCmt = res.filter((cmt) => {
  //     return cmt.replyToCommentId !== null;
  //   });

  //   cmtOnVideo.map((cmtOnVid) => {
  //     cmtOnVid.listReplyComments = [];
  //     return cmtOnVid;
  //   });
  //   cmtReplyCmt.forEach((cmt) => {
  //     cmtOnVideo.map((cmtOnVid) => {
  //       if (cmtOnVid.commentId === cmt.replyToCommentId) {
  //         cmtOnVid.listReplyComments.push(cmt);
  //       }

  //       return cmtOnVid;
  //     });
  //   });
  //   setListComment(cmtOnVideo);
  //   console.log(cmtOnVideo);
  // }

  // const commentOnCourse = async () => {
  //   let body = {
  //     commentMessage: comment.commentMessage,
  //     eduCourseMaterialId: chapterMaterialId,
  //     replyToCommentId: comment.replyToCommentId,
  //   };

  //   if (comment.commentMessage !== "") {
  //     let commentPost = await authPost(
  //       dispatch,
  //       token,
  //       "/edu/class/comment",
  //       body
  //     );
  //   }

  //   // if flag change, rerender listcomment
  //   setFlag(!flag);
  //   //   console.log(commentPost);
  //   //   if(commentPost.commentId){
  //   //     if(!commentPost.replyToCommentId){
  //   //       setListComment([
  //   //         ...listComment,
  //   //         commentPost
  //   //       ])
  //   //     } else {
  //   //       let newArr = listComment;

  //   //       newArr.map(cmt => {
  //   //         if(cmt.commentId === commentPost.replyToCommentId){
  //   //           cmt.listReplyComments.push(commentPost);
  //   //         }
  //   //       })

  //   //       setListComment(newArr);
  //   //     }
  //   //   }
  //   //   console.log(listComment)
  //   // }
  // };

  // const getMessageFromInput = (message, replyToCommentId) => {
  //   setComment({
  //     ...comment,
  //     commentMessage: message,
  //     replyToCommentId,
  //   });
  // };

  async function getListMainCommentOnCourse() {
    request("get", `/edu/class/main-comment/${chapterMaterialId}`, (res) => {
      console.log(res.data);
      setListComment(res.data);
    });
  }

  const commentOnCourse = async () => {
    if (
      comment.commentMessage === undefined ||
      comment.commentMessage === null ||
      comment.commentMessage.trim() === ""
    ) {
      errorNoti("Bình luận không được để trống", true);
    } else {
      let body = {
        commentMessage: comment.commentMessage,
        eduCourseMaterialId: chapterMaterialId,
        replyToCommentId: comment.replyToCommentId,
      };

      request(
        "post",
        "/edu/class/comment",
        (res) => {
          if (200 === res.status) {
            setFlag(!flag);
            successNoti("Đăng bình luận thành công", true);
          }
        },
        () => {
          errorNoti("Đăng bình luận thất bại", true);
        },
        body
      );
    }
  };

  const deleteComment = async (cmtId, renderReplyList) => {
    request(
      "delete",
      `/edu/class/comment/${cmtId}`,
      (res) => {
        setFlag(!flag);
        successNoti("Xóa bình luận thành công", true);
        renderReplyList();
      },
      () => {
        errorNoti("Xóa bình luận thất bại", true);
      }
    );
  };

  const editComment = async (
    cmtId,
    commentMessage,
    createdStamp,
    renderReplyList
  ) => {
    if (
      commentMessage === "undefined" ||
      commentMessage === null ||
      commentMessage.trim() === ""
    ) {
      errorNoti("Bình luận không được để trống", true);
    } else {
      let body = {
        commentMessage,
        createdStamp: createdStamp,
      };

      request(
        "put",
        `/edu/class/comment/${cmtId}`,
        (res) => {
          if (200 === res.status) {
            setFlag(!flag);
            successNoti("Sửa bình luận thành công", true);
            renderReplyList();
          }
        },
        () => {
          errorNoti("Sửa bình luận thất bại", true);
        },
        body
      );

      // let edittedComment = await authPut(
      //   dispatch,
      //   token,
      //   `/edu/class/comment/${cmtId}`,
      //   body
      // );
    }
  };

  const getMessageFromInput = (message, replyToCommentId) => {
    setComment({
      ...comment,
      commentMessage: message,
      replyToCommentId,
    });
  };

  useEffect(() => {
    getCourseChapterMaterialDetail();
    //setSourceId(chapterMaterial.sourceId);
    //get user login
  }, []);

  useEffect(() => {
    // getListCommentsEduCourseMaterial();
    getListMainCommentOnCourse();
  }, [flag]);

  return (
    <>
      {/* <Card>
        <CardContent>
          Quay về chương:{" "}
          <Link to={"/edu/student/course/chapter/detail/" + chapterId}>
            {chapterName}
          </Link>
          <Player id={sourceId} />
        </CardContent>
      </Card> */}
      <Card>
        <CardContent>
          Quay về chương:{" "}
          <Link to={"/edu/teacher/course/chapter/detail/" + chapterId}>
            {chapterName}
          </Link>
          {chapterMaterial?.eduCourseMaterialType ===
            "EDU_COURSE_MATERIAL_TYPE_VIDEO" && <Player id={sourceId} />}
          {chapterMaterial?.eduCourseMaterialType ===
            "EDU_COURSE_MATERIAL_TYPE_SLIDE" && (
            <>
              {listImage && (
                <>
                  <div style={{ textAlign: "center", padding: "20px" }}>
                    {isLoading ? (
                      <Loading />
                    ) : (
                      <img
                        src={`data:image/png;base64,${listImage[displayImage]}`}
                        alt="img"
                        style={{
                          border: "1px solid #000",
                          maxWidth: "80%",
                          maxHeight: "85vh",
                        }}
                      />
                    )}
                    <FullScreen
                      onChange={(isFullScreen, handleSeeFullScreen) => {
                        setIsFullScreen(isFullScreen);
                      }}
                      handle={handleSeeFullScreen}
                      style={{ position: "relavtive" }}
                      // onChange={setIsFullScreen(!isFullScreen)}
                    >
                      {isFullScreen && (
                        <>
                          <img
                            src={`data:image/png;base64,${listImage[displayImage]}`}
                            alt="img"
                            style={{
                              border: "1px solid #000",
                              maxWidth: "100%",
                              maxHeight: "100%",
                            }}
                          />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                              alignItems: "center",
                              padding: "10px",
                              background: "rgb(33 28 28 / 50%)",
                              position: "fixed",
                              width: "350px",
                              bottom: "10px",
                              left: "50%",
                              marginLeft: "-175px",
                              borderRadius: "5px",
                              color: "white",
                            }}
                          >
                            <Button
                              onClick={prevImage}
                              disabled={displayImage === 0 ? true : false}
                            >
                              <KeyboardArrowLeft style={{ color: "#fff" }} />
                            </Button>
                            <span
                              style={{
                                width: "120px",
                                maxWidth: "120px",
                                textAlign: "center",
                              }}
                            >
                              Page{" "}
                              <input
                                type={number}
                                value={pageNumberValue}
                                onKeyUp={onPressEnter}
                                onChange={onHandleChangePageNumber}
                                style={{
                                  width: "40px",
                                  height: "25px",
                                  background: "#a09c9c96",
                                  border: "none",
                                  color: "#fff",
                                  borderRadius: "5px",
                                  marginRight: "3px",
                                }}
                              />
                              /{listImage.length}
                            </span>
                            <Button
                              onClick={nextImage}
                              disabled={
                                displayImage === listImage.length - 1
                                  ? true
                                  : false
                              }
                            >
                              <KeyboardArrowRight style={{ color: "#fff" }} />
                            </Button>
                            <Button
                              onClick={() => {
                                handleSeeFullScreen.exit();
                                setIsFullScreen(false);
                              }}
                              style={{ marginLeft: "10px" }}
                            >
                              <ZoomOut style={{ color: "#fff" }} />
                            </Button>
                          </div>
                        </>
                      )}
                    </FullScreen>
                  </div>
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "flex-end",
                      alignItems: "center",
                      padding: "20px",
                    }}
                  >
                    <Button
                      variant="contained"
                      onClick={prevImage}
                      disabled={displayImage === 0 ? true : false}
                    >
                      <KeyboardArrowLeft />
                      Prev
                    </Button>
                    <span
                      style={{
                        width: "120px",
                        maxWidth: "120px",
                        textAlign: "center",
                      }}
                    >
                      Page{" "}
                      <input
                        type={number}
                        value={pageNumberValue}
                        onKeyUp={onPressEnter}
                        onChange={onHandleChangePageNumber}
                        style={{ width: "40px", height: "25px" }}
                      />
                      /{listImage.length}
                    </span>
                    <Button
                      variant="contained"
                      onClick={nextImage}
                      disabled={
                        displayImage === listImage.length - 1 ? true : false
                      }
                    >
                      Next
                      <KeyboardArrowRight />
                    </Button>
                    <Button
                      variant="contained"
                      onClick={() => {
                        handleSeeFullScreen.enter();
                        setIsFullScreen(true);
                      }}
                      style={{ marginLeft: "10px" }}
                    >
                      Zoom
                      <ZoomIn />
                    </Button>
                  </div>
                </>
              )}
            </>
          )}
          {chapterMaterial?.eduCourseMaterialType === null && (
            <div
              style={{
                height: "300px",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                flexDirection: "column",
              }}
            >
              Bài giảng không có tài liệu
            </div>
          )}
        </CardContent>
      </Card>
      <Card className={classes.root}>
        <InputComment
          getMessageFromInput={getMessageFromInput}
          commentOnCourse={commentOnCourse}
        />
        {listComment.length === 0 && (
          <div className={classes.noComment}>
            Video này chưa có bình luận nào
          </div>
        )}
        {listComment.length >= 0 &&
          listComment.map((cmt) => (
            <CommentItem
              comment={cmt}
              chapterMaterialId={chapterMaterialId}
              getMessageFromInput={getMessageFromInput}
              commentOnCourse={commentOnCourse}
              deleteComment={deleteComment}
              editComment={editComment}
              userId={token.preferred_username}
            />
          ))}
      </Card>
    </>
  );
}

export default StudentCourseChapterMaterialDetail;
