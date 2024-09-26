import {
  Avatar,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Link,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Paper,
  Typography,
} from "@material-ui/core";
import AppBar from "@material-ui/core/AppBar";
import Box from "@material-ui/core/Box";
// import withAsynchScreenSecurity from "../../../../component/education/classmanagement/withAsynchScreenSecurity";
import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
import Tab from "@material-ui/core/Tab";
import Tabs from "@material-ui/core/Tabs";
import MaterialTable from "material-table";
import PropTypes from "prop-types";
import React, {useEffect, useRef, useState} from "react";
import ReactExport from "react-data-export";
import {BiDetail} from "react-icons/bi";
import {FcApproval, FcClock, FcConferenceCall, FcExpired, FcMindMap,} from "react-icons/fc";
//import { useSelector } from "react-redux";
import {useHistory, useParams} from "react-router";
import {request} from "../../../../api";
import {drawerWidth} from "../../../../assets/jss/material-dashboard-react";
import CustomizedDialogs from "../../../../component/dialog/CustomizedDialogs";
import AssignList from "../../../../component/education/classmanagement/AssignList";
import NegativeButton from "../../../../component/education/classmanagement/NegativeButton";
import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
import {StyledBadge} from "../../../../component/education/classmanagement/StyledBadge";
import TeacherViewLogUserCourseChapterMaterialList
  from "../../../../component/education/course/TeacherViewLogUserCourseChapterMaterialList";
import TeacherViewLogUserQuizList from "../../../../component/education/course/TeacherViewLogUserQuizList";
//import displayTime from "../../../../utils/DateTimeUtils";
import changePageSize, {localization, tableIcons,} from "../../../../utils/MaterialTableUtils";
import {errorNoti} from "../../../../utils/notification";

const ExcelFile = ReactExport.ExcelFile;
const ExcelSheet = ReactExport.ExcelFile.ExcelSheet;

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`scrollable-auto-tabpanel-${index}`}
      aria-labelledby={`scrollable-auto-tab-${index}`}
      {...other}
    >
      {value === index && <Box p={2}>{children}</Box>}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

function a11yProps(index) {
  return {
    id: `scrollable-auto-tab-${index}`,
    "aria-controls": `scrollable-auto-tabpanel-${index}`,
  };
}

const useStyles = makeStyles((theme) => ({
  root: {
    // flexGrow: 1,
    margin: "auto",
    width: `calc(100vw - ${drawerWidth + theme.spacing(4) * 2 + 1}px)`,
    backgroundColor: theme.palette.background.paper,
  },
  card: {
    marginTop: theme.spacing(2),
  },
  grid: {
    paddingLeft: 56,
  },
  negativeBtn: {
    minWidth: 112,
    marginLeft: 10,
    marginRight: 10,
  },
  positiveBtn: {
    minWidth: 112,
  },
  dialogRemoveBtn: {
    fontWeight: "normal",
  },
  listItem: {
    height: 48,
    borderRadius: 6,
    marginBottom: 6,
    backgroundColor: "#f5f5f5",
    "&:hover": {
      backgroundColor: "#e0e0e0",
    },
  },
  open: { transform: "rotate(-180deg)", transition: "0.3s" },
  close: { transition: "0.3s" },
  item: {
    paddingLeft: 32,
  },
  tabs: { padding: theme.spacing(2) },
  tabSelected: {
    background: "rgba(254,243,199,1)",
    color: "rgba(180,83,9,1) !important",
  },
  tabRoot: {
    margin: "0px 0.5rem",
    borderRadius: "0.375rem",
    textTransform: "none",
  },
}));

function TClassDetail() {
  const classes = useStyles();
  const params = useParams();
  const history = useHistory();
  //const token = useSelector((state) => state.auth.token);

  const tabs = [
    "Thông tin chung",
    "DS SV",
    "SV đăng ký",
    "Bài tập",
    "DS nộp bài tập",
    "Lịch sử học",
    "Lịch sử làm quiz",
  ];

  // Class.
  const [classDetail, setClassDetail] = useState({});
  const [fetchedClassDetail, setFetchedClassDetail] = useState(false);
  // Student.
  const [students, setStudents] = useState([]);
  const [stuWillBeDeleted, setStuWillBeDeleted] = useState();
  const [fetchedStudents, setFetchedStudents] = useState(false);

  // Regist.
  const [registStudents, setRegistStudents] = useState([]);
  const [selectedRegists, setSelectedRegists] = useState([]);

  // Assignment.
  const [assignSets, setAssignSets] = useState([
    { title: "Đã giao", data: [] },
    { title: "Chưa giao", data: [] },
    { title: "Đã xoá", data: [] },
  ]);
  // const [deletedAssignId, setDeletedAssignId] = useState();
  // Student Assignment
  const [assignmentList, setAssignmentList] = useState([]);
  const [studentAssignmentList, setStudentAssignmentList] = useState([]);
  const [fetchedStudentAssignment, setFetchedStudentAssignment] =
    useState(false);
  // Dialog.
  const [openDelStuDialog, setOpenDelStuDialog] = useState(false);

  // Tables.
  const [openClassStuCard, setOpenClassStuCard] = useState(false);
  const [openRegistCard, setOpenRegistCard] = useState(false);
  const [openStuAssignCard, setOpenStuAssignCard] = useState(false);

  // Tables's ref.
  const studentTableRef = useRef(null);
  const registTableRef = useRef(null);
  //const assignTableRef = useRef(null);
  const studentAssignTableRef = useRef(null);

  const headerProperties = {
    headerStyle: {
      textAlign: "center",
    },
    cellStyle: {
      textAlign: "center",
      fontSize: "1rem",
    },
  };

  /*
  // Column.
  const assignCols = [
    {
      field: "name",
      title: "Tên bài tập",
      ...headerProperties,
    },
    {
      field: "closeTime",
      title: "Hạn nộp",
      ...headerProperties,
      render: (rowData) => {
        return displayTime(new Date(rowData.closeTime));
      },
    },
  ];
*/
  const registCols = [
    {
      field: "name",
      title: "Họ và tên",
      ...headerProperties,
    },
    {
      field: "email",
      title: "Email",
      ...headerProperties,
      render: (rowData) => (
        <Link href={`mailto:${rowData.email}`}>{rowData.email}</Link>
      ),
    },
  ];

  const stuCols = [
    ...registCols,
    {
      field: "",
      title: "",
      ...headerProperties,
      render: (rowData) => (
        <NegativeButton
          label="Loại khỏi lớp"
          className={classes.negativeBtn}
          onClick={() => onClickRemoveBtn(rowData)}
        />
      ),
    },
  ];

  const stuAssignCols = [
    {
      field: "studentName",
      title: "Họ và tên sinh viên",
      ...headerProperties,
    },
  ].concat(
    !fetchedStudentAssignment
      ? []
      : !studentAssignmentList.length
      ? []
      : studentAssignmentList[0].assignmentList.map((assignment, index) => {
          return {
            field: "assignmentList[" + index + "].assignmentStatus",
            title: assignment.assignmentName,
            ...headerProperties,
          };
        }),
    [
      {
        field: "totalSubmitedAssignment",
        //field: "totalSubmitedAssignment",
        title: "Tổng số bài nộp",
        ...headerProperties,
      },
    ]
  );

  const TableBorderStyle = "medium";
  const TableHeaderStyle = {
    style: {
      font: { sz: "14", bold: true },
      alignment: { vertical: "center", horizontal: "center" },
      border: {
        top: { style: TableBorderStyle },
        bottom: { style: TableBorderStyle },
        left: { style: TableBorderStyle },
        right: { style: TableBorderStyle },
      },
    },
  };

  const TableCellStyle = {
    style: {
      font: { sz: "14" },
      alignment: { vertical: "center", horizontal: "center" },
      border: {
        top: { style: TableBorderStyle },
        bottom: { style: TableBorderStyle },
        left: { style: TableBorderStyle },
        right: { style: TableBorderStyle },
      },
    },
  };

  const DataSet = [
    {
      columns: [
        {
          title: "Họ và tên sinh viên",
          ...TableHeaderStyle,
          width: { wch: "Họ và tên sinh viên".length },
        },
      ].concat(
        !fetchedStudentAssignment
          ? []
          : !studentAssignmentList.length
          ? []
          : studentAssignmentList[0].assignmentList.map((assignment) => {
              return {
                title: assignment.assignmentName,
                ...TableHeaderStyle,
                width: { wch: assignment.assignmentName.length + 3 },
              };
            }),
        [
          {
            title: "Tổng số bài nộp",
            ...TableHeaderStyle,
            width: { wch: "Tổng số bài nộp".length },
          },
        ]
      ),
      data: !fetchedStudentAssignment
        ? []
        : studentAssignmentList.map((data) => {
            return [{ value: data.studentName, ...TableCellStyle }].concat(
              data.assignmentList.map((data2) => {
                return { value: data2.assignmentStatus, ...TableCellStyle };
              }),
              [{ value: data.totalSubmitedAssignment, ...TableCellStyle }]
            );
          }),
    },
  ];

  // Functions.
  const getClassDetail = () => {
    request(
      // token, history,
      "get",
      `/edu/class/${params.id}`,
      (res) => {
        setClassDetail(res.data);
        setFetchedClassDetail(!fetchedClassDetail);
      }
    );
  };

  const getStudents = (type) => {
    if (type === "register") {
      request(
        // token,
        // history,
        "get",
        `/edu/class/${params.id}/registered-students`,
        (res) => {
          setRegistStudents(res.data);
          // console.log("registered students = " + res.data);
          changePageSize(res.data.length, registTableRef);
        }
      );
    } else {
      request(
        // token,
        // history,
        "get",
        `/edu/class/${params.id}/students`,
        (res) => {
          setStudents(res.data);
          setFetchedStudents(true);
          changePageSize(res.data.length, studentTableRef);
        }
      );
    }
  };

  const getAssigns = () => {
    request(
      // token,
      // history,
      "get",
      `/edu/class/${params.id}/assignments/teacher`,
      (res) => {
        // changePageSize(res.data.length, assignTableRef);
        let wait4Opening = [];
        let opened = [];
        let deleted = [];
        let current = new Date();

        setAssignmentList(res.data);

        res.data.forEach((assign) => {
          if (assign.deleted) {
            deleted.push(assign);
          } else {
            let open = new Date(assign.openTime);

            if (current.getTime() < open.getTime()) {
              wait4Opening.push(assign);
            } else {
              let close = new Date(assign.closeTime);

              if (close.getTime() < current.getTime()) {
                opened.push({ ...assign, opening: false });
              } else {
                opened.push({ ...assign, opening: true });
              }
            }
          }
        });

        setAssignSets([
          { ...assignSets[0], data: opened },
          { ...assignSets[1], data: wait4Opening },
          { ...assignSets[2], data: deleted },
        ]);
      }
    );
  };

  // Functions.
  const getStudentAssignment = () => {
    request(
      // token,
      // history,
      "get",
      `/edu/class/${params.id}/all-student-assignments/teacher`,
      (res) => {
        setStudentAssignmentList(res.data);
        setFetchedStudentAssignment(true);
      }
    );
  };

  // const onClickStuCard = () => {
  //   setOpenClassStuCard(!openClassStuCard);

  //   if (fetchedStudents == false) {
  //     getStudents("class");
  //   }
  // };

  // Delete student.
  const onClickRemoveBtn = (rowData) => {
    setOpenDelStuDialog(true);
    setStuWillBeDeleted({ id: rowData.id, name: rowData.name });
  };

  const onDeleteStudent = () => {
    setOpenDelStuDialog(false);
    let id = stuWillBeDeleted.id;

    request(
      // token,
      // history,
      "put",
      "/edu/class/registration-status",
      (res) => {
        if (res.data[id].status === 200) {
          // Remove student in student list.
          setStudents(students.filter((student) => student.id !== id));
        } else {
          // The student may have been removed previously.
          errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
        }
      },
      {},
      {
        classId: params.id,
        studentIds: [id],
        status: "REMOVED",
      }
    );
  };

  // Aprrove or deny registrations.
  const onSelectionChange = (rows) => {
    setSelectedRegists(rows.map((row) => row.id));
  };

  const onUpdateStatus = (type) => {
    request(
      // token,
      // history,
      "put",
      "/edu/class/registration-status",
      (res) => {
        let data = res.data;
        let tmp = [];
        let result;

        // In case it is necessary to update the student list.
        if (type === "APPROVED" && fetchedStudents) {
          let newStudents = [];

          for (let i = 0; i < registStudents.length; i++) {
            result = data[registStudents[i].id];

            if (result === undefined || result.status !== 200) {
              // Not selected or status update failed.
              tmp.push(registStudents[i]);
            } else {
              // Successfully update.
              newStudents.push({
                name: registStudents[i].name,
                id: registStudents[i].id,
                email: registStudents[i].email,
              });
            }
          }

          setStudents([...students, ...newStudents]);
        } else {
          for (let i = 0; i < registStudents.length; i++) {
            result = data[registStudents[i].id];

            if (result === undefined || result.status !== 200) {
              // Not selected or status update failed.
              tmp.push(registStudents[i]);
            }
          }
        }

        setRegistStudents(tmp);
      },
      {},
      {
        classId: params.id,
        studentIds: selectedRegists,
        status: type,
      }
    );
  };

  // Assignments.
  const onClickAssign = (id) => {
    history.push(`/edu/teacher/class/${params.id}/assignment/${id}`);
  };

  const handleClose = () => {
    setOpenDelStuDialog(false);
  };

  // const onClickStuAssignCard = () => {
  //   setOpenStuAssignCard(!openStuAssignCard);

  //   if (fetchedStudentAssignment === false) {
  //     getStudentAssignment();
  //   }

  //   if (fetchedClassDetail === false) {
  //     getClassDetail();
  //   }
  // };

  const [value, setValue] = React.useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  useEffect(() => {
    getClassDetail();
    getAssigns();
    getStudentAssignment();
    getStudents("register");
    getStudents();
  }, []);

  return (
    <div className={classes.root}>
      {/* <Card className={classes.card}> */}
      <AppBar position="static" color="inherit" elevation={0}>
        <Tabs
          className={classes.tabs}
          value={value}
          onChange={handleChange}
          variant="scrollable"
          scrollButtons="on"
          aria-label="scrollable auto tabs example"
          TabIndicatorProps={{
            style: {
              display: "none",
            },
          }}
        >
          {tabs.map((label, index) => (
            <Tab
              disableRipple
              classes={{ root: classes.tabRoot, selected: classes.tabSelected }}
              label={label}
              {...a11yProps(index)}
            />
          ))}
        </Tabs>
      </AppBar>

      <TabPanel value={value} index={0}>
        <CardHeader
          avatar={
            <Avatar style={{ background: "#ff7043" }}>
              <BiDetail size={32} />
            </Avatar>
          }
          title={<Typography variant="h5">Thông tin lớp</Typography>}
        />
        <CardContent>
          <Grid container className={classes.grid}>
            <Grid item md={3} sm={3} xs={3} container direction="column">
              <Typography>Mã lớp</Typography>
              <Typography>Mã học phần</Typography>
              <Typography>Tên học phần</Typography>
              <Typography>Loại lớp</Typography>
            </Grid>
            <Grid item md={8} sm={8} xs={8} container direction="column">
              <Typography>
                <b>:</b> {classDetail.code}
              </Typography>
              <Typography>
                <b>:</b> {classDetail.courseId}
              </Typography>
              <Typography>
                <b>:</b> {classDetail.name}
              </Typography>
              <Typography>
                <b>:</b> {classDetail.classType}
              </Typography>
            </Grid>
          </Grid>
        </CardContent>
      </TabPanel>

      <TabPanel value={value} index={1}>
        <Card className={classes.card} elevation={0}>
          {/* <CardActionArea disableRipple onClick={onClickStuCard}> */}
          <CardHeader
            avatar={
              <Avatar style={{ background: "white" }}>
                {/*#ffeb3b <PeopleAltRoundedIcon /> */}
                <FcConferenceCall size={40} />
              </Avatar>
            }
            title={<Typography variant="h5">Danh sách sinh viên</Typography>}
            // action={
            //   <div>
            //     <IconButton aria-label="show more">
            //       <FcExpand
            //         size={24}
            //         className={clsx(
            //           !openClassStuCard && classes.close,
            //           openClassStuCard && classes.open
            //         )}
            //       />
            //     </IconButton>
            //   </div>
            // }
          />
          {/* </CardActionArea>
            <Collapse in={openClassStuCard} timeout="auto"> */}
          <CardContent>
            <MaterialTable
              title=""
              columns={stuCols}
              icons={tableIcons}
              tableRef={studentTableRef}
              localization={localization}
              data={students}
              components={{
                Container: (props) => <Paper {...props} elevation={0} />,
              }}
              options={{
                filtering: true,
                sorting: false,
                search: false,
                pageSize: 10,
                debounceInterval: 500,
                headerStyle: {
                  backgroundColor: "#673ab7",
                  fontWeight: "bold",
                  fontSize: "1rem",
                  color: "white",
                },
                filterCellStyle: { textAlign: "center" },
                cellStyle: { fontSize: "1rem", textAlign: "center" },
                toolbarButtonAlignment: "left",
              }}
            />
          </CardContent>
          {/* </Collapse> */}
        </Card>
      </TabPanel>
      <TabPanel value={value} index={2}>
        <Card className={classes.card} elevation={0}>
          {/* <CardActionArea
              disableRipple
              onClick={() => setOpenRegistCard(!openRegistCard)}
            > */}
          <CardHeader
            avatar={
              <Avatar style={{ background: "white" }}>
                <FcApproval size={40} />
              </Avatar>
            }
            title={
              <StyledBadge badgeContent={registStudents.length} color="error">
                Phê duyệt sinh viên đăng ký
              </StyledBadge>
            }
            titleTypographyProps={{
              variant: "h5",
            }}
            // action={
            //   <div>
            //     <IconButton aria-label="show more">
            //       <FcExpand
            //         size={24}
            //         className={clsx(
            //           !openRegistCard && classes.close,
            //           openRegistCard && classes.open
            //         )}
            //       />
            //     </IconButton>
            //   </div>
            // }
          />
          {/* </CardActionArea> */}
          {/* <Collapse in={openRegistCard} timeout="auto"> */}
          <CardContent>
            <MaterialTable
              title=""
              columns={registCols}
              tableRef={registTableRef}
              data={registStudents}
              localization={localization}
              components={{
                Container: (props) => <Paper {...props} elevation={0} />,
                Action: (props) => {
                  if (props.action.icon === "refuse") {
                    return (
                      <NegativeButton
                        label="Từ chối"
                        className={classes.negativeBtn}
                        onClick={(event) =>
                          props.action.onClick(event, props.data)
                        }
                      />
                    );
                  }
                  if (props.action.icon === "approve") {
                    return (
                      <PositiveButton
                        label="Phê duyệt"
                        className={classes.positiveBtn}
                        onClick={(event) =>
                          props.action.onClick(event, props.data)
                        }
                      />
                    );
                  }
                },
              }}
              options={{
                search: false,
                pageSize: 10,
                selection: true,
                debounceInterval: 500,
                headerStyle: {
                  backgroundColor: "#673ab7",
                  fontWeight: "bold",
                  fontSize: "1rem",
                  color: "white",
                },
                sorting: false,
                cellStyle: { fontSize: "1rem" },
                toolbarButtonAlignment: "left",
                showTextRowsSelected: false,
              }}
              actions={[
                {
                  icon: "approve",
                  position: "toolbarOnSelect",
                  onClick: () => onUpdateStatus("APPROVED"),
                },
                {
                  icon: "refuse",
                  position: "toolbarOnSelect",
                  onClick: () => onUpdateStatus("REFUSED"),
                },
              ]}
              onSelectionChange={(rows) => onSelectionChange(rows)}
            />
          </CardContent>
          {/* </Collapse> */}
        </Card>
      </TabPanel>
      {/* </Card> */}

      <TabPanel value={value} index={3}>
        <Card className={classes.card}>
          <CardHeader
            avatar={
              <Avatar style={{ background: "white" }}>
                <FcMindMap size={40} />
              </Avatar>
            }
            title={<Typography variant="h5">Bài tập</Typography>}
            action={
              <PositiveButton
                label="Tạo mới"
                className={classes.positiveBtn}
                onClick={() => {
                  history.push(
                    `/edu/teacher/class/${params.id}/assignment/create`
                  );
                }}
              />
            }
          />
          <Grid container md={12} justify="center">
            <Grid item md={10}>
              <CardContent className={classes.assignList}>
                {/* <MaterialTable
            title=""
            columns={assignCols}
            tableRef={assignTableRef}
            localization={localization}
            data={assign}
            components={{
              Container: (props) => <Paper {...props} elevation={0} />,
              Action: (props) => {
                if (props.action.icon === "create") {
                  return (
                    <PositiveButton
                      label="Tạo mới"
                      className={classes.positiveBtn}
                      onClick={(event) =>
                        props.action.onClick(event, props.data)
                      }
                    />
                  );
                }
              },
            }}
            options={{
              search: false,
              pageSize: 10,
              actionsColumnIndex: -1,
              debounceInterval: 500,
              headerStyle: {
                backgroundColor: "#673ab7",
                fontWeight: "bold",
                fontSize: "1rem",
                color: "white",
                paddingLeft: 5,
                paddingRight: 5,
              },
              sorting: false,
              cellStyle: {
                fontSize: "1rem",
                whiteSpace: "normal",
                paddingLeft: 5,
                wordBreak: "break-word",
              },
              toolbarButtonAlignment: "left",
            }}
            actions={[
              {
                icon: "create",
                position: "toolbar",
                onClick: () => {
                  history.push(
                    `/edu/teacher/class/${params.id}/assignment/create`
                  );
                },
              },
            ]}
            onRowClick={(event, rowData) => {
              console.log(rowData);
              history.push(
                `/edu/teacher/class/${params.id}/assignment/${rowData.id}`
              );
            }}
          /> */}
                <List>
                  {assignSets.map((assignList) => (
                    <AssignList title={assignList.title}>
                      {assignList.data.map((assign) => (
                        <ListItem
                          button
                          disableRipple
                          className={classes.listItem}
                          onClick={() => onClickAssign(assign.id)}
                        >
                          <ListItemText primary={assign.name} />
                          <ListItemIcon>
                            {assign.opening ? (
                              <FcClock size={24} />
                            ) : assign.opening == false ? (
                              <FcExpired size={24} />
                            ) : null}
                          </ListItemIcon>
                        </ListItem>
                      ))}
                    </AssignList>
                  ))}
                </List>
              </CardContent>
            </Grid>
          </Grid>
        </Card>
      </TabPanel>

      <TabPanel value={value} index={4}>
        <Card className={classes.card}>
          {/* <CardActionArea disableRipple onClick={onClickStuAssignCard}> */}
          <CardHeader
            avatar={
              <Avatar style={{ background: "white" }}>
                {/*#ffeb3b <PeopleAltRoundedIcon /> */}
                <FcConferenceCall size={40} />
              </Avatar>
            }
            title={<Typography variant="h5">Danh sách nộp bài tập</Typography>}
            // action={
            //   <div>
            //     <IconButton aria-label="show more">
            //       <FcExpand
            //         size={24}
            //         className={clsx(
            //           !openStuAssignCard && classes.close,
            //           openStuAssignCard && classes.open
            //         )}
            //       />
            //     </IconButton>
            //   </div>
            // }
          />
          {/* </CardActionArea>
          <Collapse in={openStuAssignCard} timeout="auto"> */}
          <CardContent>
            {studentAssignmentList.length !== 0 ? (
              <ExcelFile
                filename={"Danh sách nộp bài tập lớp " + classDetail.code}
                element={
                  <Button
                    variant="contained"
                    color="secondary"
                    style={{ marginLeft: "0px" }}
                  >
                    Xuất Excel
                  </Button>
                }
              >
                <ExcelSheet
                  dataSet={DataSet}
                  name={"Danh sách nộp bài tập lớp " + classDetail.code}
                />
              </ExcelFile>
            ) : null}
            <MaterialTable
              title=""
              columns={stuAssignCols}
              icons={tableIcons}
              tableRef={studentAssignTableRef}
              localization={localization}
              data={studentAssignmentList}
              components={{
                Container: (props) => <Paper {...props} elevation={0} />,
              }}
              options={{
                fixedColumns: {
                  left: 1,
                  right: 1,
                },
                draggable: false,
                filtering: true,
                sorting: true,
                search: false,
                pageSize: 10,
                debounceInterval: 500,
                headerStyle: {
                  backgroundColor: "#673ab7",
                  fontWeight: "bold",
                  fontSize: "1rem",
                  color: "white",
                },
                filterCellStyle: { textAlign: "center" },
                cellStyle: { fontSize: "1rem", textAlign: "center" },
                toolbarButtonAlignment: "left",
                // exportButton: true,
                // exportFileName: "Danh sách nộp bài tập lớp " + classDetail.code,
                // exportDelimiter: ",",
              }}
            />
          </CardContent>
          {/* </Collapse> */}
        </Card>
      </TabPanel>

      <TabPanel value={value} index={5}>
        <TeacherViewLogUserCourseChapterMaterialList classId={params.id} />
      </TabPanel>
      <TabPanel value={value} index={6}>
        <TeacherViewLogUserQuizList classId={params.id} />
      </TabPanel>

      {/* Dialogs */}
      <CustomizedDialogs
        open={openDelStuDialog}
        handleClose={handleClose}
        title="Loại sinh viên?"
        content={
          <Typography gutterBottom>
            Loại sinh viên <b>{stuWillBeDeleted?.name}</b> khỏi lớp.
            <br />
            <b>
              Cảnh báo: Bạn không thể hủy hành động này sau khi đã thực hiện.
            </b>
          </Typography>
        }
        actions={
          <PositiveButton
            label="Loại khỏi lớp"
            className={classes.dialogRemoveBtn}
            onClick={onDeleteStudent}
          />
        }
      />
    </div>
  );
}

export default TClassDetail;
// export default withAsynchScreenSecurity(TClassDetail, "SCR_TCLASSDETAIL");
