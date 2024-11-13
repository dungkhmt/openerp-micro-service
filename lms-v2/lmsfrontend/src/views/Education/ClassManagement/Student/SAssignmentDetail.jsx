import {Avatar, Box, Button, Card, CardContent, CardHeader, Chip, Divider, Grid, Typography,} from "@material-ui/core";
import {makeStyles, MuiThemeProvider} from "@material-ui/core/styles";
import parse from "html-react-parser";
import {DropzoneArea} from "material-ui-dropzone";
import React, {Fragment, useEffect, useState} from "react";
import {CountdownCircleTimer} from "react-countdown-circle-timer";
import {BiDetail} from "react-icons/bi";
import {FcUpload} from "react-icons/fc";
//import { useSelector } from "react-redux";
import {useParams,} from "react-router";
import {request} from "../../../../api";
import NegativeButton from "../../../../component/education/classmanagement/NegativeButton";
import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
import displayTime from "../../../../utils/DateTimeUtils";
import {errorNoti, successNoti} from "../../../../utils/notification";

const useStyles = makeStyles((theme) => ({
  divider: {
    width: "91.67%",
    marginTop: 16,
    marginBottom: 16,
  },
  rootDivider: {
    backgroundColor: "black",
  },
  card: {
    marginTop: theme.spacing(2),
    minHeight: 600,
  },
  remainingTime: {
    display: "flex",
    justifyContent: "space-around",
    textAlign: "center",
    fontSize: 16,
  },
  grid: {
    paddingLeft: 56,
  },
  tooltip: {
    maxWidth: 100,
    textAlign: "center",
  },
  countdown: {
    paddingBottom: "1rem",
  },
  exercise: {
    fontSize: "1rem",
  },
  submit: {
    paddingLeft: 72,
  },
  note: {
    width: "100%",
    marginTop: 16,
    marginBottom: 16,
  },
  cancleBtn: {
    width: 112,
    marginTop: 16,
    marginRight: 16,
    fontWeight: "normal",
  },
  submitBtn: {
    width: 112,
    marginTop: 16,
    fontWeight: "normal",
  },
  editBtn: {
    marginLeft: 16,
    fontWeight: "normal",
  },
  assignDetail: {
    display: "flex",
    // whiteSpace: "pre-wrap",
  },
}));

// const editorStyle = {
//   toolbar: {
//     background: "#90caf9",
//   },
//   editor: {
//     border: "1px solid black",
//     minHeight: "300px",
//   },
// };

const minuteSeconds = 60;
const hourSeconds = 3600;
const daySeconds = 86400;

const children = ({ remainingTime }) => {
  if (remainingTime !== 0) {
    const days = Math.floor(remainingTime / daySeconds);
    const hours = Math.floor((remainingTime % daySeconds) / hourSeconds);
    const minutes = Math.floor((remainingTime % hourSeconds) / minuteSeconds);
    const seconds = remainingTime % minuteSeconds;

    return `${days < 10 ? "0" + days : days} : ${
      hours < 10 ? "0" + hours : hours
    } : ${minutes < 10 ? "0" + minutes : minutes} : ${
      seconds < 10 ? "0" + seconds : seconds
    }`;
  } else {
    return "Hết thời gian";
  }
};

// const Alert = (props) => {
//   return <MuiAlert elevation={6} variant="filled" {...props} />;
// };

function SAssignmentDetail() {
  const classes = useStyles();
  const params = useParams();
  //const history = useHistory();
  //const token = useSelector((state) => state.auth.token);

  // Countdown.
  const [remainingTime, setRemainingTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [key, setKey] = useState("initial-countdown");

  // Assignment detail.
  const [hideSubject, setHideSubject] = useState(true);
  const [assignDetail, setAssignDetail] = useState({});

  // Submit and edit submission.
  const [file, setFile] = useState();
  const [isUpdating, setIsUpdating] = useState(false);

  // // Snackbar.
  // const [state, setState] = React.useState({
  //   open: false,
  //   vertical: "top",
  //   horizontal: "center",
  // });

  // const { vertical, horizontal, open } = state;
  // const [message, setMessage] = useState("");

  // Functions.
  const getAssignDetail = () => {
    request(
      // token,
      // history,
      "get",
      `/edu/assignment/${params.assignmentId}/student`,
      (res) => {
        let assignDetail = res.data.assignmentDetail;
        let startTime = new Date(assignDetail.openTime);
        let endTime = new Date(assignDetail.closeTime);

        setRemainingTime(
          endTime.getTime() < Date.now()
            ? 0
            : (endTime.getTime() - Date.now()) / 1000
        );

        setDuration((endTime.getTime() - startTime.getTime()) / 1000);

        setKey("update-params");

        setAssignDetail({
          name: assignDetail.name,
          subject: assignDetail.subject,
          startTime: startTime,
          endTime: endTime,
          submitedFileName: res.data.submitedFileName,
        });
      }
    );
  };

  // const handleClose = (event, reason) => {
  //   if (reason === "clickaway") {
  //     return;
  //   }

  //   setState({ ...state, open: false });
  // };

  const onClickCancleBtn = () => {
    setIsUpdating(false);
  };

  const onClickSubmitBtn = () => {
    if (assignDetail.endTime.getTime() < new Date().getTime()) {
      errorNoti("Đã quá hạn nộp bài.");
      setRemainingTime(0);
    } else {
      const data = new FormData();
      data.append("file", file);

      request(
        // token,
        // history,
        "post",
        `/edu/assignment/${params.assignmentId}/submission`,
        (res) => {
          // setMessage(isUpdating ? "Chỉnh sửa thành công" : "Nộp bài thành công");
          // setState({ ...state, open: true });

          successNoti(
            isUpdating ? "Chỉnh sửa thành công." : "Nộp bài thành công."
          );

          setIsUpdating(false);

          setAssignDetail({
            ...assignDetail,
            submitedFileName: file.name,
          });
        },
        {
          400: (e) => {
            if ("not exist" === e.response.data?.error) {
              errorNoti("Bài tập này đã bị xoá trước đó.");
            } else if ("deadline exceeded" === e.response.data?.error) {
              errorNoti("Đã quá hạn nộp bài.");
              setRemainingTime(0);
            } else {
              errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
            }
          },
          500: (e) => {
            errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
          },
        },
        data
      );
    }
  };

  useEffect(() => {
    getAssignDetail();
  }, []);

  return (
    <MuiThemeProvider>
      <Card className={classes.card}>
        <CardHeader
          avatar={
            <Avatar style={{ background: "#ff7043" }}>
              <BiDetail size={32} />
            </Avatar>
          }
          title={<Typography variant="h5">Thông tin bài tập</Typography>}
        />
        <CardContent>
          <Grid container alignItems="flex-start" className={classes.grid}>
            <Grid item md={3} className={classes.countdown}>
              <CountdownCircleTimer
                key={key}
                isLinearGradient={true}
                isPlaying={true}
                size={150}
                strokeWidth={9}
                duration={duration}
                initialRemainingTime={remainingTime}
                children={children}
                colors={[
                  ["#64dd17", 0.5],
                  ["#1565c0", 0.5],
                ]}
              />
            </Grid>
            <Grid item md={9}>
              <Grid container>
                <Grid item md={3} sm={3} xs={3}>
                  <Typography>Tên bài tập</Typography>
                  <Typography>Ngày giao</Typography>
                  <Typography>Hạn nộp</Typography>
                </Grid>
                <Grid item md={8} sm={8} xs={8} direction="column">
                  <div className={classes.assignDetail}>
                    <b>:&nbsp;</b>
                    {assignDetail.name ? (
                      <Typography>{assignDetail.name}</Typography>
                    ) : null}
                  </div>

                  {assignDetail.startTime ? (
                    <div className={classes.assignDetail}>
                      <b>:</b>&nbsp;{displayTime(assignDetail.startTime)}
                    </div>
                  ) : (
                    <b>:</b>
                  )}

                  {assignDetail.endTime ? (
                    <div className={classes.assignDetail}>
                      <b>:</b>&nbsp;{displayTime(assignDetail.endTime)}
                    </div>
                  ) : (
                    <b>
                      <br />:
                    </b>
                  )}
                </Grid>
                {assignDetail.submitedFileName ? (
                  <Grid item md={12}>
                    <div className={classes.divider}>
                      <Divider
                        variant="fullWidth"
                        classes={{ root: classes.rootDivider }}
                      />
                    </div>
                    <Box display="flex" alignItems="center" fullWidth>
                      <Grid item md={3} sm={3} xs={3}>
                        <Typography>Tệp đã tải lên</Typography>
                      </Grid>
                      <Grid item md={8} sm={8} xs={8}>
                        <Box display="flex" fullWidth alignItems="center">
                          <Typography>
                            <b>:&nbsp;</b>
                          </Typography>
                          <Chip
                            variant="outlined"
                            clickable
                            color="primary"
                            label={assignDetail.submitedFileName}
                          />
                          {remainingTime > 0 && isUpdating === false ? (
                            <PositiveButton
                              label="Chỉnh sửa"
                              className={classes.editBtn}
                              onClick={() => setIsUpdating(true)}
                            />
                          ) : null}
                        </Box>
                      </Grid>
                    </Box>
                  </Grid>
                ) : null}
              </Grid>
            </Grid>
            <Grid item md={11} className={classes.exercise}>
              <Box display="flex" fullWidth>
                <Button
                  style={{
                    textTransform: "none",
                    fontSize: "bold",
                    color: "blue",
                    marginLeft: 29,
                    paddingBottom: 10,
                  }}
                  onClick={() => setHideSubject(!hideSubject)}
                >
                  {hideSubject ? "Hiện đề bài" : "Ẩn đề bài"}
                </Button>
              </Box>
              {hideSubject ? null : parse(assignDetail.subject)}
            </Grid>
          </Grid>
        </CardContent>
        {remainingTime > 0 &&
        (assignDetail.submitedFileName == null || isUpdating === true) ? (
          <Fragment>
            <CardHeader
              avatar={
                <Avatar style={{ background: "#e7f3ff" }}>
                  <FcUpload size={32} />
                </Avatar>
              }
              title={<Typography variant="h5">Nộp bài tập</Typography>}
            />
            <CardContent className={classes.submit}>
              <DropzoneArea
                filesLimit={1}
                maxFileSize={10485760}
                showPreviews={true}
                showPreviewsInDropzone={false}
                showFileNamesInPreview={true}
                useChipsForPreview={true}
                dropzoneText="Kéo và thả tệp vào đây hoặc nhấn để chọn tệp"
                previewText="Xem trước:"
                previewChipProps={{ variant: "outlined", color: "primary" }}
                getFileAddedMessage={(fileName) =>
                  `Tệp ${fileName} tải lên thành công`
                }
                showAlerts={["error"]}
                alertSnackbarProps={{
                  anchorOrigin: { vertical: "bottom", horizontal: "right" },
                }}
                getFileLimitExceedMessage={(filesLimit) =>
                  `Vượt quá số lượng tệp tối đa được cho phép. Chỉ được phép tải lên tối đa ${filesLimit} tệp.`
                }
                getDropRejectMessage={(
                  rejectedFile,
                  acceptedFiles,
                  maxFileSize
                ) => {
                  var message = "Tệp ".concat(
                    rejectedFile.name,
                    " bị từ chối. "
                  );

                  // if (!acceptedFiles.includes(rejectedFile.type)) {
                  //   message += "Định dạng tệp không hỗ trợ. ";
                  //   return message;
                  // }

                  if (rejectedFile.size > maxFileSize) {
                    message +=
                      "Kích thước tệp quá lớn. Kích thước giới hạn là " +
                      maxFileSize / 1048576 +
                      " megabytes. ";
                    return message;
                  }
                }}
                onChange={(files) => setFile(files[0])}
              />
              {isUpdating ? (
                <NegativeButton
                  label="Huỷ"
                  className={classes.cancleBtn}
                  onClick={onClickCancleBtn}
                />
              ) : null}
              <PositiveButton
                label={isUpdating ? "Cập nhật" : "Nộp bài"}
                className={classes.submitBtn}
                onClick={onClickSubmitBtn}
              />
            </CardContent>
          </Fragment>
        ) : null}
      </Card>
      {/* <Snackbar
        autoHideDuration={3000}
        anchorOrigin={{ vertical, horizontal }}
        open={open}
        onClose={handleClose}
        key={vertical + horizontal}
      >
        <Alert onClose={handleClose} severity="success">
          {message}
        </Alert>
      </Snackbar> */}
    </MuiThemeProvider>
  );
}

export default SAssignmentDetail;
