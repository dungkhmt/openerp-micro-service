import React, {useEffect, useRef, useState} from "react";
import {Avatar, Card, CardContent, CardHeader, Paper, Typography,} from "@material-ui/core";
import MaterialTable from "material-table";
//import { BiDetail } from "react-icons/bi";
import {FcMindMap,} from "react-icons/fc";
import {makeStyles} from "@material-ui/core/styles";
import {drawerWidth} from "../../../../assets/jss/material-dashboard-react";
//import NegativeButton from "../../../../component/education/classmanagement/NegativeButton";
import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
import {useHistory} from "react-router";
import {request} from "../../../../api";
import changePageSize, {localization, tableIcons,} from "../../../../utils/MaterialTableUtils";
import displayTime from "../../../../utils/DateTimeUtils";
import AssignmentListOfClass from "../../../../component/education/classmanagement/teacher/AssignmentListOfClass";

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

export default function TeacherViewDetailClassExercises(props) {
  const classes = useStyles();
  //const params = useParams();
  const history = useHistory();
  const classId = props.classId;
  const [data, setData] = useState([]);
  const tableRef = useRef(null);

  const cols = [
    {
      field: "name",
      title: "Tên bài tập",
    },
    {
      field: "openTime",
      title: "Thời gian bắt đầu",
      filtering: false,
      render: (rowData) => {
        let date = new Date(rowData.openTime);

        return displayTime(date);
      },
    },
    {
      field: "closeTime",
      title: "Thời gian kết thúc",
      filtering: false,
      render: (rowData) => {
        let date = new Date(rowData.closeTime);

        return displayTime(date);
      },
    },
    {
      field: "status",
      title: "Trạng thái",
      lookup: {
        CLOSED: "Đã hết hạn",
        STARTED: "Đã giao",
        DELETED: "Đã xóa",
        "NOT STARTED": "Chưa giao",
      },
    },
  ];
  // const [deletedAssignId, setDeletedAssignId] = useState();
  // Student Assignment

  const addStatusProperty = (assignments) => {
    let current = new Date();
    assignments.forEach((assign) => {
      if (assign.deleted) {
        assign.status = "DELETED";
      } else {
        let open = new Date(assign.openTime);

        if (current.getTime() < open.getTime()) {
          assign.status = "NOT STARTED";
        } else {
          let close = new Date(assign.closeTime);

          if (close.getTime() < current.getTime()) {
            assign.status = "CLOSED";
          } else {
            assign.status = "STARTED";
          }
        }
      }
    });

    console.log(assignments);
    return assignments;
  };

  const getAssigns = () => {
    request(
      // token,
      // history,
      "get",
      `/edu/class/${classId}/assignments/teacher`,
      (res) => {
        changePageSize(res.data.length, tableRef);
        let assignmentData = addStatusProperty(res.data);
        setData(assignmentData);
      }
    );
  };

  useEffect(() => {
    //getClassDetail();
    getAssigns();
    //getStudentAssignment();
    //getStudents("register");
    //getStudents();
  }, []);

  return (
    <div>
      <Card>
        <CardContent>
          <AssignmentListOfClass classId={classId}
                                 userRole="teacher"
                                 enableCreateAssignment />
        </CardContent>
      </Card>

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
                history.push(`/edu/teacher/class/${classId}/assignment/create`);
              }}
            />
          }
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
              Toolbar: () => null,
              Container: (props) => <Paper {...props} elevation={0} />,
            }}
            options={{
              filtering: true,
              search: false,
              pageSize: 10,
              debounceInterval: 500,
            }}
            onRowClick={(event, rowData) => {
              // console.log(rowData);
              history.push({
                //pathname: `/edu/teacher/class/${rowData.id}`,
                pathname: `/edu/teacher/class/${classId}/assignment/${rowData.id}`,
                state: {},
              });
            }}
          />
        </CardContent>
      </Card>
    </div>
  );
}
