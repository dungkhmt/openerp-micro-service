import DateFnsUtils from "@date-io/date-fns";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  MenuItem,
  TextField,
  Typography,
} from "@material-ui/core/";
import {makeStyles} from "@material-ui/core/styles";
import {Add, KeyboardArrowLeft, KeyboardArrowRight} from "@material-ui/icons";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import {Link} from "react-router-dom";
import {request} from "../../../api";
import {errorNoti, successNoti} from "../../../utils/notification";
import Player from "../../../utils/Player";
import Loading from "../../common/Loading";
import withScreenSecurity from "../../withScreenSecurity";

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
    flexDirection: "row",
    alignItems: "flex-start",
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },
  infoItem: {
    display: "flex",
    alignItems: "center",
  },
}));

function TeacherCourseChapterMaterialDetail() {
  const params = useParams();
  const chapterMaterialId = params.chapterMaterialId;

  const [chapterMaterial, setChapterMaterial] = useState(null);
  const [sourceId, setSourceId] = useState(null);
  const [listImage, setListImage] = useState([]);
  const [displayImage, setDisplayImage] = useState(0);
  const [materialType, setMaterialType] = useState(null);
  const [materialTypeList, setMaterialTypeList] = useState([]);
  const [selectedInputFile, setSelectedInputFile] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isEditting, setIsEditting] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);
  const [flag, setFlag] = useState(false);

  const classes = useStyles();

  async function getImages(slideId) {
    request(
      "post",
      "/get-slide",
      (res) => {
        setListImage(res.data);
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
    request(
      "get",
      "/edu/class/get-course-chapter-material-detail/" + chapterMaterialId,
      (res) => {
        res = res.data;
        setChapterMaterial(res);
        console.log("getCourseChapterMaterialDetail ", res);
        if (res.sourceId !== null) {
          setSourceId(res.sourceId);
        } else if (res.slideId !== null) {
          getImages(res.slideId);
        }
      }
    );

    //let res = authGet(dispatch, token, '/edu/class/get-course-chapter-material-detail/' + chapterMaterialId);
  }

  async function deleteSlideOrVideo() {
    request(
      "delete",
      "/edu/class/delete-course-chapter-material-detail-slide-video/" +
        chapterMaterialId,
      (res) => {
        successNoti("Xóa thành công", true);
        setFlag(!flag);
      },
      {
        onError: (e) => {
          errorNoti("Xóa thất bại", true);
          setFlag(!flag);
        },
      }
    );
  }

  async function getCourseChapterMaterialTypeList() {
    request(
      "get",
      "/edu/class/get-course-chapter-material-type-list",
      (res) => {
        const lst = res.data;
        setMaterialTypeList(lst);
        console.log("types = ", lst);
      }
    );
  }

  function onInputFileChange(event) {
    setSelectedInputFile(event.target.files[0]);
  }

  async function handleSubmit() {
    //console.log('handle submit');
    setIsLoading(true);
    let body = {
      eduCourseMaterialId: chapterMaterialId,
      eduCourseMaterialName: chapterMaterial.eduCourseMaterialName,
      eduCourseMaterialType: materialType,
      slideId: chapterMaterial.slideId,
      sourceId: chapterMaterial.sourceId,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("files", selectedInputFile);

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/edu/class/update-chapter-material-of-course",
      (res) => {
        res = res.data;
        console.log("res = ", res);
        if (res.error) {
          errorNoti("Thêm bài giảng thất bại", true);
        } else {
          successNoti("Thêm bài giảng thành công", true);
          setIsEditting(false);
          setFlag(!flag);
        }
        setIsLoading(false);
      },
      {},
      formData,
      config
    );

    //let chapter = await authPost(dispatch, token, '/edu/class/create-chapter-material-of-course', body);
    //history.push("/edu/course/chapter/detail/" + chapterId);
    //edu/teacher/course/chapter/detail/010a357c-eb5b-49a6-93de-ec1aef3695dd
  }

  const prevImage = () => {
    if (displayImage > 0) {
      setDisplayImage(displayImage - 1);
    }
  };

  const nextImage = () => {
    if (displayImage < listImage.length) {
      setDisplayImage(displayImage + 1);
    }
  };
  useEffect(() => {
    getCourseChapterMaterialDetail();
    getCourseChapterMaterialTypeList();
    //setSourceId(chapterMaterial.sourceId);
  }, [flag]);

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleOpenDialog = () => {
    setOpenDialog(true);
  };

  const onHandleConfirmDelete = () => {
    deleteSlideOrVideo();
    handleCloseDialog();
  };

  return (
    <>
      <Dialog
        open={openDialog}
        onClose={handleCloseDialog}
        aria-labelledby="draggable-dialog-title"
      >
        <DialogTitle style={{ cursor: "move" }} id="draggable-dialog-title">
          Xác nhận xóa
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            <div>Bạn sẽ không thể khôi phục được tài liệu nếu xóa.</div>
            <div>Bạn có chắc muốn xóa tài liệu bài giảng.</div>
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button autoFocus onClick={handleCloseDialog} color="primary">
            Hủy
          </Button>
          <Button onClick={onHandleConfirmDelete} color="primary">
            Xóa
          </Button>
        </DialogActions>
      </Dialog>
      {isEditting ? (
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
          {isLoading ? (
            <Loading />
          ) : (
            <Card>
              <CardContent>
                <Typography variant="h5" component="h2">
                  Thêm tài liệu bài giảng
                </Typography>
                <form className={classes.root} noValidate autoComplete="off">
                  <div>
                    <TextField
                      required
                      id="materialType"
                      select
                      label="Thể Loại"
                      value={materialType}
                      fullWidth
                      onChange={(event) => {
                        setMaterialType(event.target.value);
                        //console.log(problemId,event.target.value);
                      }}
                    >
                      {materialTypeList.map((item) => (
                        <MenuItem key={item} value={item}>
                          {item}
                        </MenuItem>
                      ))}
                    </TextField>

                    <label>Select Input file</label>
                    <input type="file" onChange={onInputFileChange} />
                    <br></br>
                    <br></br>
                  </div>
                </form>
              </CardContent>
              <CardActions>
                <Button
                  variant="contained"
                  color="primary"
                  style={{ marginLeft: "45px" }}
                  onClick={handleSubmit}
                >
                  Thay đổi
                </Button>
                <Button
                  variant="contained"
                  onClick={() => setIsEditting(false)}
                >
                  Hủy
                </Button>
              </CardActions>
            </Card>
          )}
        </MuiPickersUtilsProvider>
      ) : (
        <>
          <Card>
            <CardContent>
              MaterialDetail{" "}
              <Link
                to={"/edu/teacher/course/chapter/detail/" + chapterMaterialId}
              >
                {chapterMaterialId}
              </Link>
              {chapterMaterial?.eduCourseMaterialType ===
                "EDU_COURSE_MATERIAL_TYPE_VIDEO" && <Player id={sourceId} />}
              {chapterMaterial?.eduCourseMaterialType ===
                "EDU_COURSE_MATERIAL_TYPE_SLIDE" && (
                <>
                  {listImage && (
                    <>
                      <div style={{ textAlign: "center", padding: "20px" }}>
                        <img
                          src={`data:image/png;base64,${listImage[displayImage]}`}
                          alt="img"
                          width={"80%"}
                          style={{ border: "1px solid #000" }}
                        />
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
                        >{`Page ${displayImage + 1}/${listImage.length}`}</span>
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
                  <span style={{ marginBottom: "10px" }}>
                    Bài giảng không có tài liệu
                  </span>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => {
                      setIsEditting(true);
                    }}
                  >
                    <Add />
                    Thêm mới tài liệu
                  </Button>
                </div>
              )}
            </CardContent>
          </Card>
          <Card style={{ marginTop: "20px" }}>
            <CardContent>
              <Typography gutterBottom variant="h5" component="h2">
                Infomation
              </Typography>
              <div className={classes.infoItem}>
                <h4>Material chapter name:</h4>
                <span>
                  {chapterMaterial?.eduCourseChapter?.chapterName
                    ? chapterMaterial?.eduCourseChapter?.chapterName
                    : "undefined"}
                </span>
              </div>
              <div className={classes.infoItem}>
                <h4>Material chapter Id:</h4>
                <a
                  href={`/edu/teacher/course/chapter/detail/${chapterMaterial?.eduCourseChapter?.chapterId}`}
                >
                  {chapterMaterial?.eduCourseChapter?.chapterId
                    ? chapterMaterial?.eduCourseChapter?.chapterId
                    : "undefined"}
                </a>
              </div>
              <div className={classes.infoItem}>
                <h4>Course material name:</h4>
                <span>
                  {chapterMaterial?.eduCourseMaterialName
                    ? chapterMaterial?.eduCourseMaterialName
                    : "undefined"}
                </span>
              </div>
              <div className={classes.infoItem}>
                <h4>Course material type:</h4>
                <span>
                  {chapterMaterial?.eduCourseMaterialType
                    ? chapterMaterial?.eduCourseMaterialType
                    : "undefined"}
                </span>
              </div>
            </CardContent>
            <CardActions>
              <Button
                variant="contained"
                color="secondary"
                onClick={handleOpenDialog}
              >
                Xóa tài liệu
              </Button>
              <Button
                variant="contained"
                color="primary"
                onClick={() => setIsEditting(true)}
              >
                Chỉnh sửa tài liệu
              </Button>
            </CardActions>
          </Card>
        </>
      )}
    </>
  );
}

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
export default withScreenSecurity(
  TeacherCourseChapterMaterialDetail,
  screenName,
  true
);
