import * as React from "react";
import {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {request} from "../../../api";
import Typography from "@mui/material/Typography";
import {Autocomplete, Button, Grid, IconButton, LinearProgress, TextField,FormControlLabel,Checkbox} from "@mui/material";
import StandardTable from "component/table/StandardTable";
import Box from "@mui/material/Box";
import {errorNoti, successNoti} from "../../../utils/notification";
import ModalAddProblemToContest from "./ModalAddProblemToContest";
import EditIcon from "@mui/icons-material/Edit";
import ModalUpdateProblemInfoInContest from "./ModalUpdateProblemInfoInContest";
import DeleteIcon from "@mui/icons-material/Delete";
import {getColorLevel} from "./lib";
import {getSubmissionModeFromConstant} from "./Constant";
import {LoadingButton} from "@mui/lab";
export function ContestManagerManageProblem(props) {
  const contestId = props.contestId;
  const [allProblems, setAllProblems] = useState([]);
  const [contestProblems, setContestProblems] = useState([]);
  const [loading, setLoading] = useState(true);

  const [searchProblemValue, setSearchProblemValue] = useState("");
  const [chosenProblem, setChosenProblem] = useState(null);
  const [editingProblem, setEditingProblem] = useState(null);

  const [openModalAddProblem, setOpenModalAddProblem] = useState(false);
  const [openModalUpdateProblem, setOpenModalUpdateProblem] = useState(false);

  const [isImportFromExistingContest, setIsImportFromExistingContest] = useState(false);
  const [importFromContestId, setImportFromContestId] = useState(null);

  const columns = [
    {
      title: "Problem",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/manager-view-problem-detail/" +
              rowData["problemId"],
          }}
          style={{
            textDecoration: "none",
            color: "blue",
            cursor: "",
          }}
        >
          {rowData["problemName"]}
        </Link>
      ),
    },
    {title: "Created By", field: "createdByUserId"},
    {title: "Problem Name in Contest", field: "problemRename"},
    {title: "Problem Code in Contest", field: "problemRecode"},
    {
      title: "Level",
      render: (problem) => (
        <span style={{color: getColorLevel(`${problem.levelId}`)}}>
          {`${problem.levelId}`}
        </span>
      )
    },
    {
      title: "Submission Mode",
      render: (problem) => (
        <span>
          {getSubmissionModeFromConstant(problem?.submissionMode)}
        </span>
      )
    },
    {
      title: "Edit",
      render: (problem) => (
        <IconButton
          onClick={() => {
            setEditingProblem(problem)
            setOpenModalUpdateProblem(true);
          }}
          variant="contained"
          color="success"
        >
          <EditIcon/>
        </IconButton>
      ),
    },
    {
      title: "Remove",
      render: (problem) => (
        <IconButton
          variant="contained"
          color="error"
          onClick={() => {
            request(
              "delete",
              "/contest-problem?contestId=" + contestId + "&problemId=" + problem.problemId,
              () => {
                successNoti("Problem removed from contest", 5000);
                getAllProblemsInContest();
              },
              {}
            ).then();
          }}
        >
          <DeleteIcon/>
        </IconButton>
      ),
    }
  ];

  const handleImportProblems = () => {
    setLoading(true);
    let body = {
      contestId: contestId,
      fromContestId: importFromContestId
    };

    request(
      "post",
      "/import-problems-from-a-contest",
      (res) => {
        successNoti("Import problems successfully")
        sleep(1000).then(() => {
          //history.push("/programming-contest/contest-manager/" + res.data.contestId);
          getAllProblemsInContest()
        });
      },
      {
        //onError: (err) => {
        //  errorNoti(err?.response?.data?.message, 5000)
        //}
      },
      body
    )
      .then()
      .finally(() => setLoading(false));
  }
  const getAllProblems = () => {
    request("get", "/problems/general-info", (res) => {
      setAllProblems(res.data || []);
    }).then();
  }

  const getAllProblemsInContest = () => {
    request("get", "/contests/" + contestId, (res) => {
      setContestProblems(res.data.list || []);
    }).then(() => setLoading(false));
  }

  useEffect(() => {
    getAllProblems();
    getAllProblemsInContest()
  }, [])

  function addNewProblem(newProblem) {
    if (newProblem?.addNew) return;

    if (contestProblems.filter(problem => problem.problemId === newProblem.problemId).length > 0) {
      errorNoti("Problem is already in contest", 3000);
      return;
    }
    setChosenProblem(newProblem);
    setOpenModalAddProblem(true);
  }
  const isValidContestId = () => {
    return new RegExp(/[%^/\\|.?;[\]]/g).test(contestId);
  };
  const handleAddProblemToContestSuccess = () => {
    successNoti("Problem saved to contest successfully", 5000);
    getAllProblemsInContest();
  }
  const handleCloseModal = () => {
    setOpenModalAddProblem(false);
    setOpenModalUpdateProblem(false);
  }

  return (
    <Box sx={{margin: "14px 0"}}>
      {loading && <LinearProgress/>}
      <Box display="flex" justifyContent="space-between" sx={{marginBottom: "16px"}}>
        <Autocomplete
          id="problem-select"
          onChange={(event, value) => addNewProblem(value)}
          options={[{addNew: "true"}, ...allProblems]}
          sx={{width: "100%"}}
          autoHighlight
          getOptionLabel={(option) => option.problemName || ""}
          inputValue={searchProblemValue}
          onInputChange={(event, newInputValue, reason) => {
            if (reason === "reset") setSearchProblemValue("");
            else setSearchProblemValue(newInputValue);
          }}
          renderOption={(props, option) => {
            if (option.addNew === "true") {
              return (
                <Box {...props} key={option.label}>
                  <Button
                    variant="outlined"
                    sx={{
                      width: "100%",
                    }}
                    onClick={() => window.open("/programming-contest/create-problem")}
                  >
                    <Typography>{"Create new Problem"}</Typography>
                  </Button>
                </Box>
              );
            } else {
              return (
                <Box {...props} key={option.problemId}>
                  <Grid container>
                    <Grid item xs={5}>
                      {option.problemName}
                    </Grid>
                    <Grid item xs={5} sx={{display: "flex"}}>
                      <Typography>
                        {" "}
                        {"ID"}: {option.problemId}{" "}
                      </Typography>
                    </Grid>
                    <Grid item xs={2} sx={{display: "flex"}}>
                      <Typography sx={{color: getColorLevel(option.levelId)}}>
                        {option.levelId}
                      </Typography>
                    </Grid>
                  </Grid>
                </Box>
              )
            }
          }}
          renderInput={(params) => {
            return <TextField {...params} autoFocus placeholder="Search problem to add to contest"/>;
          }}
        />
      </Box>
      <Box sx={{marginTop: "12px"}}>
      <FormControlLabel
          label="Import From Existing Contest"
          control={
            <Checkbox
              checked={isImportFromExistingContest}
              onChange={() => setIsImportFromExistingContest(!isImportFromExistingContest)}
            />}
        />
     
      <Box display="flex" justifyContent="space-between" sx={{marginBottom: "16px"}}>
      <TextField
                fullWidth
                autoFocus
                required
                value={importFromContestId}
                id="importFromContestId"
                label="Contest Id"
                onChange={(event) => {
                  setImportFromContestId(event.target.value);
                }}
                error={isValidContestId()}
                helperText={
                  isValidContestId()
                    ? "Contest ID must not contain special characters including %^/\\|.?;[]"
                    : ""
                }
              />
        <LoadingButton
          loading={loading}
          variant="contained"
          //style={{marginTop: "36px"}}
          onClick={handleImportProblems}
          disabled={isValidContestId() || loading}
        >
          Import
        </LoadingButton>
      </Box>      
      
      </Box>
      <StandardTable
        title={"Problems in Contest"}
        columns={columns}
        data={contestProblems}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: true,
          sorting: true,
        }}
      />

      <ModalAddProblemToContest
        contestId={contestId}
        chosenProblem={chosenProblem}
        isOpen={openModalAddProblem}
        handleSuccess={handleAddProblemToContestSuccess}
        handleClose={handleCloseModal}
      />

      <ModalUpdateProblemInfoInContest
        contestId={contestId}
        editingProblem={editingProblem}
        isOpen={openModalUpdateProblem}
        handleSuccess={handleAddProblemToContestSuccess}
        handleClose={handleCloseModal}
      />
    </Box>
  );
}
