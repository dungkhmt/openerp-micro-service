import {Card, CardContent} from "@material-ui/core";
//import EditIcon from "@material-ui/icons/Edit";
import {makeStyles, MuiThemeProvider} from "@material-ui/core/styles";
import {useEffect, useState} from "react";
import {useHistory} from "react-router";
import {request} from "../../../../api";
import CurrentTeacherClassList from "../../../../component/education/class/CurrentTeacherClassList";
import CurrentUserClassList from "../../../../component/education/class/CurrentUserClassList";
import withScreenSecurity from "../../../../component/withScreenSecurity";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
}));

function TClassList() {
  const classes = useStyles();
  const history = useHistory();

  const columns = [
    {
      field: "classCode",
      title: "Mã lớp",
    },
    {
      field: "courseId",
      title: "Mã môn",
    },
    {
      field: "courseName",
      title: "Tên môn",
    },
    {
      field: "createdByUserLoginId",
      title: "Người tạo",
    },

    {
      field: "semester",
      title: "Học kỳ",
    },

    {
      field: "statusId",
      title: "Trạng thái",
    },
  ];

  const [classesOfUser, setClassesOfUser] = useState([]);

  function getClassesOfUser() {
    request(
      // token, history,
      "get",
      "/edu/class/get-classes-of-user/null",
      (res) => {
        //let lst = [];
        //res.data.map((e) => {
        //  lst.push(e);s
        //});
        console.log("getClassesOfUser, res.data = ", res.data);
        //console.log("getClassesOfUser, lst = ", lst);
        setClassesOfUser(res.data);
      }
    );
  }

  useEffect(() => {
    getClassesOfUser();
  }, []);

  return (
    <MuiThemeProvider>
      <Card className={classes.card}>
        <CardContent>
          <CurrentTeacherClassList/>
        </CardContent>

        <CardContent>
          <CurrentUserClassList />
        </CardContent>
      </Card>
    </MuiThemeProvider>
  );
}

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
export default withScreenSecurity(TClassList, screenName, true);
