import { Card, CardContent } from "@material-ui/core";
//import EditIcon from "@material-ui/icons/Edit";
import { makeStyles, MuiThemeProvider } from "@material-ui/core/styles";
import { useEffect, useState } from "react";
import { request } from "../../../../api";
import StandardTable from "component/table/StandardTable";
import { toFormattedDateTime } from "utils/dateutils";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
}));

export default function TeacherViewInteractiveQuizSubmission(props) {
  const classes = useStyles();

  const columns = [
    {
      field: "questionId",
      title: "questionId",
    },
    {
      field: "userId",
      title: "User",
    },
    {
      field: "answerList",
      title: "Câu trả lời",
    },
    {
      field: "isCorrect",
      title: "Kết quả",
    },
    {
      field: "createAt",
      title: "Thời gian nộp",
    },
  ];

  const [results, setResults] = useState([]);

  function getResultOfInteractiveQuiz() {
    request(
      // token, history,
      "get",
      `/get-interactive-quiz-submission/` + props.interactiveQuizId,
      (res) => {
        //console.log("getClassesOfUser, lst = ", lst);
        const data = res.data;
        const content = data.map((submission) => ({
          ...submission,
          createAt: toFormattedDateTime(submission.createdStamp),
        }));
        setResults(content);
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
            title="Kết quả nộp bài"
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
