import {
  Avatar,
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Paper,
  Typography,
} from "@material-ui/core";
import { green } from "@material-ui/core/colors";
import { MuiThemeProvider, makeStyles } from "@material-ui/core/styles";
import { GetApp, PeopleAltRounded } from "@material-ui/icons";
import parse from "html-react-parser";
import MaterialTable from "material-table";
import { Fragment, useEffect, useRef, useState } from "react";
import { CountdownCircleTimer } from "react-countdown-circle-timer";
import { BiDetail } from "react-icons/bi";
import { FcDownload } from "react-icons/fc";
import { useHistory, useParams } from "react-router";
import { request } from "../../../../api";
import CustomizedDialogs from "../../../../component/dialog/CustomizedDialogs";
import NegativeButton from "../../../../component/education/classmanagement/NegativeButton";
import NegativeDialogButton from "../../../../component/education/classmanagement/NegativeDialogButton";
import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
import displayTime from "../../../../utils/DateTimeUtils";
import changePageSize, {
  localization,
  tableIcons,
} from "../../../../utils/MaterialTableUtils";
import { errorNoti } from "../../../../utils/notification";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
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
  editBtn: {
    // fontWeight: "normal",
  },
  divider: {
    width: "91.67%",
    marginTop: 16,
    marginBottom: 16,
  },
  rootDivider: {
    backgroundColor: "black",
  },
  buttonProgress: {
    color: green[500],
    position: "absolute",
    top: "50%",
    left: "50%",
    marginTop: -12,
    marginLeft: -12,
  },
  downloadBtn: {
    minWidth: 176,
    borderRadius: 6,
    textTransform: "none",
    fontSize: "1rem",
  },
  wrapper: {
    margin: theme.spacing(1),
    position: "relative",
  },
  assignDetail: {
    display: "flex",
    // whiteSpace: "pre-wrap",
  },
  hideSubjectBtn: {
    marginLeft: 29,
    paddingBottom: 10,
  },
  deleteBtn: {
    marginRight: 10,
  },
  downloadRow: {
    display: "flex",
    alignItems: "center",
  },
  fileDownload: {
    color: "rgb(0, 0, 238)",
    "&:hover": {
      textDecoration: "underline",
      cursor: "pointer",
    },
  },
}));

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

function TAssignmentDetail() {
  const classes = useStyles();
  const params = useParams();
  const history = useHistory();
  // const token = useSelector((state) => state.auth.token);

  // Countdown.
  const [remainingTime, setRemainingTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [key, setKey] = useState("initial-countdown");

  // Assignment detail.
  const [hideSubject, setHideSubject] = useState(true);
  const [assignDetail, setAssignDetail] = useState({});

  //Submission.
  const [selectedSubmissions, setSelectedSubmission] = useState([]);
  // const [isZipping, setIsZipping] = useState(false);

  // Dialog.
  const [open, setOpen] = useState(false);

  const cols = [
    {
      field: "name",
      title: "Họ và tên",
      filtering: true,
    },
    {
      field: "submissionDate",
      title: "Ngày nộp",
      filtering: false,
      render: (rowData) => {
        let date = rowData.submissionDate;

        return displayTime(date);
      },
    },
    {
      field: "originalFileName",
      title: "File",
      filtering: false,
      render: (rowData) => {
        return (
          <span
            className={classes.downloadRow}
            onClick={() => onSingleDownload(rowData)}
            title="Tải xuống"
          >
            <GetApp />
            &nbsp;
            <span className={classes.fileDownload}>
              {rowData?.originalFileName
                ? rowData.originalFileName
                : "undefined"}
            </span>
          </span>
        );
      },
    },
  ];

  const [data, setData] = useState([]);
  const tableRef = useRef(null);

  // Functions.
  const getAssignDetail = () => {
    request("get", `/edu/assignment/${params.assignmentId}/teacher`, (res) => {
      let assignDetail = res.data.assignmentDetail;
      let startTime = new Date(assignDetail.openTime);
      let endTime = new Date(assignDetail.closeTime);
      let data = res.data.submissions;

      data = data.map((submission) => ({
        ...submission,
        submissionDate: new Date(submission.submissionDate),
      }));

      changePageSize(data.length, tableRef);
      setData(data);

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
        noSubmissions: res.data.noSubmissions,
        deleted: assignDetail.deleted,
      });
    });
  };

  const onSingleDownload = (submission) => {
    const form = document.createElement("form");

    form.setAttribute("method", "post");
    form.setAttribute("target", "_blank");

    // // TODO: consider
    // form.setAttribute(
    //   "action",
    //   `${config.url.API_URL}/edu/assignment/${params.assignmentId}/submissions?token=${token}`
    // );

    const input = document.createElement("input");

    input.setAttribute("type", "hidden");
    input.setAttribute("name", "studentIds");
    input.setAttribute("value", submission.studentId);

    form.appendChild(input);

    document.body.appendChild(form);
    form.submit();
    form.parentNode.removeChild(form);
  };

  const onDownload = () => {
    const form = document.createElement("form");

    form.setAttribute("method", "post");
    form.setAttribute("target", "_blank");

    // // TODO: consider
    // form.setAttribute(
    //   "action",
    //   `${config.url.API_URL}/edu/assignment/${params.assignmentId}/submissions?token=${token}`
    // );

    for (const submission of selectedSubmissions) {
      const input = document.createElement("input");

      input.setAttribute("type", "hidden");
      input.setAttribute("name", "studentIds");
      input.setAttribute("value", submission.studentId);

      form.appendChild(input);
    }

    document.body.appendChild(form);
    form.submit();
    form.parentNode.removeChild(form);

    // request(
    //   "post",
    //   `/edu/assignment/${params.assignmentId}/submissions`,
    //   (res) => {
    //     setIsZipping(false);
    //     window.location.href = `${config.url.API_URL}/edu/assignment/${params.assignmentId}/download-file/${res.data}`;
    //   },
    //   { onError: () => setIsZipping(false) },
    //   {
    //     studentIds: studentIds,
    //   }
    // );
  };

  // Delete assignment.
  const onDeleteAssign = () => {
    setOpen(false);

    request(
      "delete",
      `/edu/assignment/${params.assignmentId}`,
      () => {
        history.goBack();
      },
      {
        rest: (e) => {
          errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
        },
        // 400: (e) => {
        //   if ("not allowed" == e.response.data?.error) {
        //     errorNoti("Không thể xoá bài tập vì đã có sinh viên nộp bài.");
        //   } else {
        //     errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
        //   }
        // },
        // 404: (e) => {
        //   if ("not exist" == e.response.data?.error) {
        //     history.goBack();
        //   } else {
        //     errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
        //   }
        // },
      }
    );
  };

  // Dialog.
  const handleClose = () => {
    setOpen(false);
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
          action={
            assignDetail.deleted ? null : (
              <Fragment>
                <NegativeButton
                  label="Xoá"
                  className={classes.deleteBtn}
                  onClick={() => setOpen(true)}
                />
                <PositiveButton
                  label="Chỉnh sửa"
                  // startIcon={<EditIcon />}
                  className={classes.editBtn}
                  onClick={() =>
                    history.push(
                      `/edu/teacher/class/${params.classId}/assignment/${params.assignmentId}/edit`
                    )
                  }
                />
              </Fragment>
            )
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
              <Grid container md={12}>
                <Grid item md={3} sm={3} xs={3} direction="column">
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
              </Grid>
              <Grid item md={12}>
                <div className={classes.divider}>
                  <Divider
                    variant="fullWidth"
                    classes={{ root: classes.rootDivider }}
                  />
                </div>
                <Box display="flex" width="100%">
                  <Grid item md={3} sm={3} xs={3}>
                    <Typography>Sinh viên đã nộp bài</Typography>
                  </Grid>
                  <Grid item md={8} sm={8} xs={8}>
                    {assignDetail.noSubmissions === undefined ? (
                      <b>:</b>
                    ) : (
                      <Typography>
                        <b>:</b> {assignDetail.noSubmissions}
                      </Typography>
                    )}
                  </Grid>
                </Box>
              </Grid>
            </Grid>
            <Grid item md={11} className={classes.exercise}>
              <Box display="flex" fullWidth>
                <NegativeDialogButton
                  label={hideSubject ? "Hiện đề bài" : "Ẩn đề bài"}
                  className={classes.hideSubjectBtn}
                  onClick={() => setHideSubject(!hideSubject)}
                />
              </Box>
              {hideSubject ? null : parse(assignDetail.subject)}
            </Grid>
          </Grid>
        </CardContent>
      </Card>
      <Card className={classes.card}>
        <CardHeader
          avatar={
            <Avatar style={{ background: "#f9a825" }}>
              <PeopleAltRounded />
            </Avatar>
          }
          title={<Typography variant="h5">Danh sách nộp bài</Typography>}
        />

        <CardContent>
          <MaterialTable
            title=""
            columns={cols}
            icons={tableIcons}
            tableRef={tableRef}
            localization={localization}
            data={data}
            components={{
              Container: (props) => <Paper {...props} elevation={0} />,
              Action: (props) => {
                if (props.action.icon === "download") {
                  return (
                    <div className={classes.wrapper}>
                      <Button
                        // disabled={isZipping}
                        variant="outlined"
                        color="primary"
                        startIcon={<FcDownload size={24} />} // isZipping ? null : <FcDownload size={24} />
                        onClick={(event) =>
                          props.action.onClick(event, props.data)
                        }
                      >
                        Tải xuống
                        {/* {isZipping ? "Đang nén các tệp" : "Tải xuống"} */}
                      </Button>
                      {/* {isZipping && (
                        <CircularProgress
                          size={24}
                          className={classes.buttonProgress}
                        />
                      )}*/}
                    </div>
                  );
                }
              },
            }}
            options={{
              search: false,
              filtering: true,
              pageSize: 10,
              selection: true,
              debounceInterval: 500,
              toolbarButtonAlignment: "left",
              showTextRowsSelected: false,
            }}
            actions={[
              {
                icon: "download",
                position: "toolbarOnSelect",
                onClick: () => onDownload(),
              },
            ]}
            onSelectionChange={(rows) => {
              setSelectedSubmission(rows);
            }}
          />
        </CardContent>
      </Card>

      {/* Dialog */}
      <CustomizedDialogs
        open={open}
        handleClose={handleClose}
        title="Xoá bài tập?"
        content={
          <Typography gutterBottom>
            Giảng viên vẫn có thể xem nhưng <b>không</b> thể chỉnh sửa thông tin
            bài tập này sau khi đã xoá.
            <br />
            <b>
              Cảnh báo: Bạn không thể hủy hành động này sau khi đã thực hiện.
            </b>
          </Typography>
        }
        actions={
          <PositiveButton
            label="Xoá"
            className={classes.dialogDeleteBtn}
            onClick={onDeleteAssign}
          />
        }
      />
    </MuiThemeProvider>
  );
}

export default TAssignmentDetail;
