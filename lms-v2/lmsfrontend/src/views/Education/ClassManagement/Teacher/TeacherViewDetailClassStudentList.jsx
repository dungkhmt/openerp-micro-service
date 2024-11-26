import React, {useEffect, useRef, useState} from "react";
import {Avatar, Card, CardContent, CardHeader, Paper, Typography,} from "@material-ui/core";
import {Link} from "react-router-dom";
//import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";

import MaterialTable from "material-table";
import {request} from "../../../../api";
import {FcConferenceCall} from "react-icons/fc";
//import { useParams } from "react-router";
import {drawerWidth} from "../../../../assets/jss/material-dashboard-react";
import changePageSize, {localization, tableIcons,} from "../../../../utils/MaterialTableUtils";
import NegativeButton from "../../../../component/education/classmanagement/NegativeButton";
import StudentListOfClass from "../../../../component/education/classmanagement/teacher/StudentListOfClass";
//import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";

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
  negativeBtn: {
    minWidth: 112,
    marginLeft: 10,
    marginRight: 10,
  },
}));

export default function TeacherViewDetailClassStudentList(props) {
  const classes = useStyles();
  //const params = useParams();
  const classId = props.classId;
  //const history = useHistory();
  const [students, setStudents] = useState([]);
  //const [stuWillBeDeleted, setStuWillBeDeleted] = useState();
  //const [fetchedStudents, setFetchedStudents] = useState(false);
  const studentTableRef = useRef(null);
  //const [openDelStuDialog, setOpenDelStuDialog] = useState(false);

  console.log("classId = ", classId);

  const registCols = [
    {
      field: "id",
      title: "User Login",
      render: (rowData) => (
        <Link to={"/edu/student/learning/detail/" + rowData["id"]}>
          {rowData["id"]}
        </Link>
      ),
      //...headerProperties,
    },
    {
      field: "name",
      title: "Họ và tên",
    },
    {
      field: "email",
      title: "Email",
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
      render: (rowData) => (
        <NegativeButton
          label="Loại khỏi lớp"
          className={classes.negativeBtn}
          onClick={() => onClickRemoveBtn(rowData)}
        />
      ),
    },
  ];

  const onClickRemoveBtn = (rowData) => {
    //setOpenDelStuDialog(true);
    //setStuWillBeDeleted({ id: rowData.id, name: rowData.name });
  };

  const getStudents = (type) => {
    request(
      // token,
      // history,
      "get",
      `/edu/class/${classId}/students`,
      (res) => {
        setStudents(res.data);
        //setFetchedStudents(true);
        changePageSize(res.data.length, studentTableRef);
      }
    );
  };

  useEffect(() => {
    //getClassDetail();
    //getAssigns();
    //getStudentAssignment();
    //getStudents("register");
    getStudents();
  }, []);

  return (
    <div>
      <Card>
        <CardContent>
          <StudentListOfClass classId={classId}/>
        </CardContent>
      </Card>

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
              Toolbar: () => null,
              Container: (props) => <Paper {...props} elevation={0} />,
            }}
            options={{
              filtering: true,
              sorting: false,
              search: false,
              pageSize: 10,
              debounceInterval: 500,
              toolbarButtonAlignment: "left",
            }}
          />
        </CardContent>
        {/* </Collapse> */}
      </Card>
    </div>
  );
}
