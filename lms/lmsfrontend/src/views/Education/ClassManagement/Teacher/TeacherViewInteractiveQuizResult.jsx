import { Card, CardContent } from "@material-ui/core";
//import EditIcon from "@material-ui/icons/Edit";
import { makeStyles, MuiThemeProvider } from "@material-ui/core/styles";
import { useEffect, useState } from "react";
import { request } from "../../../../api";
import StandardTable from "component/table/StandardTable";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
}));

export default function TeacherViewInteractiveQuizResult(props) {
  const classes = useStyles();

  const columns = [
    {
      field: "userId",
      title: "User",
    },
    {
      field: "score",
      title: "Điểm",
    },
  ];

  const [results, setResults] = useState([]);

  function getResultOfInteractiveQuiz() {
    request(
      // token, history,
      "get",
      `/get-result-of-interactive-quiz/` + props.interactiveQuizId,
      (res) => {
        //console.log("getClassesOfUser, lst = ", lst);
        setResults(res.data);
      }
    );
  }

  useEffect(() => {
    getResultOfInteractiveQuiz();
  }, []);

  return (
    <MuiThemeProvider>
      <Card className={classes.card}>
        <CardContent>
          <StandardTable
            title="Kết quả"
            columns={columns}
            data={results}
            hideCommandBar
            options={{
              selection: false,
              search: true,
              sorting: true,
            }}
            //   onRowClick={navigateToClassDetailPage}
          />
        </CardContent>
      </Card>
    </MuiThemeProvider>
  );
}
