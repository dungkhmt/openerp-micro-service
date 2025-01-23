import AddIcon from "@material-ui/icons/Add";
//import { Box, LinearProgress } from "@mui/material";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useHistory, useParams } from "react-router-dom";
import { request } from "../../../api";
import { toFormattedDateTime } from "../../../utils/dateutils";
import StandardTable from "../../table/StandardTable";
import { RejudgeButton } from "./RejudgeButton";
import { getStatusColor } from "./lib";
//import { Box, IconButton, Tooltip, LinearProgress } from "@mui/material";
import {useTranslation} from "react-i18next";
import RichTextEditor from "../../common/editor/RichTextEditor";
import { Box, Tooltip, LinearProgress,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,TextField,
    Typography,Button,
   } from "@mui/material";

  import PrimaryButton from "component/button/PrimaryButton";

export default function ManagerViewProblemDetailAndSubmisionsInContest(props) {
  const [submissions, setSubmissions] = useState([]);
  const [problem, setProblem] = useState(null);
  const [loading, setLoading] = useState(true);
  const { contestId, problemId} = useParams();
  const [problemDescription, setProblemDescription] = useState("");
  const [solutionCode, setSolutionCode] = useState("");
  const [newProblemId, setNewProblemId] = useState(null);  
  const [openMapNewProblemDialog, setOpenMapNewProblemDialog] = useState(false);  
  const [errorMessage, setErrorMessage] = useState("");

   const {t} = useTranslation([
      "education/programmingcontest/problem",
      "common",
      "validation",
    ]);

  const columns = [
    {
      title: "ID",
      field: "contestSubmissionId",
      cellStyle: { minWidth: "80px" },
      render: (rowData) => (
        <Link
          to={
            "/programming-contest/manager-view-contest-problem-submission-detail/" +
            rowData.contestSubmissionId
          }
        >
          {rowData.contestSubmissionId.substring(0, 6)}
        </Link>
      ),
    },
    { title: "User ID", field: "userId" },
    {
      title: "Full name",
      field: "fullname",
      cellStyle: { minWidth: "170px" },
    },

    {
      title: "Problem ID",
      field: "problemId",
      render: (rowData) => (
        <Tooltip title={rowData.problemName} placement="bottom-start" arrow>
          {rowData.problemId}
        </Tooltip>
      ),
    },
    // { title: "Problem Name", field: "problemName" },

    {
      title: "Testcases Passed",
      field: "testCasePass",
      // cellStyle: { textAlign: "center" },
    },
    { title: "Lang", field: "sourceCodeLanguage" },
    {
      title: "Status",
      field: "status",
      // cellStyle: { textAlign: "center" },
      render: (rowData) => (
        <span style={{ color: getStatusColor(`${rowData.status}`) }}>
          {`${rowData.status}`}
        </span>
      ),
    },
    // {title: "Message", field: "message"},
    { title: "Point", field: "point" },
    {
      title: "Submitted At",
      field: "createAt",
      cellStyle: {
        minWidth: "112px",
        // textAlign: "center"
      },
    },
    {
      title: "Rejudge",
      sortable: "false",
      headerStyle: { textAlign: "center" },
      cellStyle: { textAlign: "center" },
      render: (rowData) => (
        <RejudgeButton submissionId={rowData.contestSubmissionId} />
      ),
    },
    { title: "Man. Status", field: "managementStatus" },
    { title: "Violation", field: "violationForbiddenInstruction" },
    
  ];

  function getProblemDetail(){
    request("get", "/teacher-get-problem-detail-in-contest/" + contestId + "/" + problemId, (res) => {
        setLoading(false);
        console.log('getProblemDetail, res = ',res);
        setProblem(res.data);
        setProblemDescription(res.data.problemDescription);
        setSolutionCode(res.data.solutionCode);
      });
  }
  function getSubmissionsOfContestProblem(){
    request("get", "/teacher-get-submissions-of-problem-in-contest/" + contestId + "/" + problemId, (res) => {
        setLoading(false);
        setSubmissions(res.data);
      });
  } 


  const handleMapNewProblemDialogOpen = () => {
    setOpenMapNewProblemDialog(true);
  };

  const handleMapNewProblemDialogClose = () => {
    setOpenMapNewProblemDialog(false);
    setNewContestId("");
    setNewContestName("");
    setErrorMessage("");
    history.push("/programming-contest/contest-manager/"+contestId); // comment this line : not return to list-problems when click cancel
  };

  const handleProcess = () => {


    const body = {
        contestId: contestId,
        problemId: problemId,
        newProblemId: newProblemId,
    };

    request(
        "post", 
        "/map-new-problem-to-submissions-in-contest",
        (res) => { 
            handleMapNewProblemDialogClose();
            history.push("/programming-contest/contest-manager/"+newContestId);
        },
        {
            onError: (error) => {
                setErrorMessage("Failed to clone the problem. Please try again.");
                console.error("Error cloning problem:", error);
            },
            400: (error) => {
                setErrorMessage("Invalid request. Please check your input.");
            },
            404: (error) => {
                setErrorMessage("Original problem not found.");
            },
            500: (error) => {
              setErrorMessage("Original problem already exists.");
          },
        },
        body 
    );
};

const handleRejudge = () => {


  const body = {
      contestId: contestId,
      problemId: problemId,
  
  };

  request(
      "post", 
      "/submissions-of-a-problem-in-contest/rejudge",
      (res) => { 
          //handleMapNewProblemDialogClose();
          //history.push("/programming-contest/contest-manager/"+newContestId);
      },
      {
          onError: (error) => {
              setErrorMessage("Failed to rejudge submissions. Please try again.");
              console.error("Error cloning problem:", error);
          },
          400: (error) => {
              setErrorMessage("400 Invalid request. Please check your input.");
          },
          404: (error) => {
              setErrorMessage("404 not found.");
          },
          500: (error) => {
            setErrorMessage("Error 500");
        },
      },
      body 
  );
};

  useEffect(() => {
    getProblemDetail();
    getSubmissionsOfContestProblem();
  }, []);

  return (
    <Box mb={2}>
      {loading && <LinearProgress />}
      <PrimaryButton onClick={handleMapNewProblemDialogOpen}>
        Map New Problem
      </PrimaryButton>
      <PrimaryButton onClick={handleRejudge}>
        Rejudge
      </PrimaryButton>
      
      <div>
      {contestId}:{problemId}
      </div>
      <div>
        
        <Box sx={{marginTop: "24px", marginBottom: "24px"}}>
                <Typography variant="h6" sx={{marginBottom: "8px"}}>
                  {t("problemDescription")}
                </Typography>
                <RichTextEditor
                  toolbarHidden
                  content={problemDescription}
                  readOnly
                />
              </Box>
        
      </div>
      <div>
     
      </div>
      <StandardTable
              title={"Submissions"}
              columns={columns}
              data={submissions}
              hideCommandBar
              options={{
                selection: false,
                pageSize: 5,
                search: true,
                sorting: true,
              }}
            />

<Dialog open={openMapNewProblemDialog} onClose={handleMapNewProblemDialogClose}>
        <DialogTitle>{"Map new problem for submissions"}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="New Problem ID"
            type="text"
            fullWidth
            variant="outlined"
            value={newProblemId}
            onChange={(e) => setNewProblemId(e.target.value)}
           />
          
          {errorMessage && <Typography color="error">{errorMessage}</Typography>}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleMapNewProblemDialogClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleProcess} color="primary">
            Update
          </Button>
        </DialogActions>
      </Dialog>        
    </Box>
  );
}
