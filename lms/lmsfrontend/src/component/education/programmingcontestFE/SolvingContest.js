import * as React from "react";
import {useEffect, useState} from "react";
import {request} from "./Request";
import {useHistory, useParams} from "react-router-dom";
import Box from "@mui/material/Box";
import PropTypes from "prop-types";
import {Button, Grid, Tab, TableHead, Tabs} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import TableRow from "@material-ui/core/TableRow";
import {getColorLevel, StyledTableCell, StyledTableRow} from "./lib";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import {TabPanelHorizontal} from "./TabPanel";
import {Timer} from "./Timer";
import {ContestProblemComponent} from "./ContestProblemComponent";
import {successNoti} from "../../../utils/notification";
import {WaitScreen} from "./WaitScreen";
import LockScreen from "./LockScreen";

TabPanelHorizontal.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

function a11yProps(index) {
  return {
    id: `vertical-tab-${index}`,
    "aria-controls": `vertical-tabpanel-${index}`,
  };
}

export default function SolvingContest() {
  const { contestId } = useParams();
  const [value, setValue] = React.useState(0);
  const [contestName, setContestName] = useState();
  const [contestTime, setContestTime] = useState();
  const [problems, setProblems] = useState([]);
  const [submitted, setSubmitted] = useState([]);
  const history = useHistory();
  const [wait, setWait] = useState(true);
  const [unauthorized, setUnauthorized] = useState(false);
  const [isPublic, setIsPublic] = useState(true);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  function contestTimeOut() {
    //submitContest();
    history.push("/programming-contest/student-list-contest-registered");
  }
  function submitContest() {
    let contents = [];
    // eslint-disable-next-line array-callback-return
    problems.map((problem) => {
      let body = {
        contestId: contestId,
        problemId: problem.problemId,
        source: localStorage.getItem(
          contestId + "-" + problem.problemId + "-source"
        ),
        language: localStorage.getItem(
          contestId + "-" + problem.problemId + "-language"
        ),
      };
      contents.push(body);
    });
    console.log("contents", contents);

    let body = {
      contents: contents,
    };
    console.log("body ", body);
    request(
      "post",
      "/contest-submit-all",
      (res) => {
        console.log("ok");
      },
      {},
      body
    ).then(() => {
      problems.map((problem) => {
        localStorage.removeItem(
          contestId + "-" + problem.problemId + "-source"
        );
        localStorage.removeItem(
          contestId + "-" + problem.problemId + "-language"
        );
      });
      localStorage.removeItem(
        "startTime-" + contestTime.toString() + "-" + contestId
      );
      successNoti("You are submit contest");
      //history.push("/programming-contest/student-list-contest-registered");
    });
  }

  function handleClickSubmitSolution(index) {
    history.push(
      "/programming-contest/submit-solution-output/" +
        contestId +
        "/" +
        problems[index].problemId
    );
  }

  useEffect(() => {
    request(
      "get",
      "/get-contest-detail-solving/" + contestId,
      (res) => {
        console.log("res contest", res);
        setUnauthorized(res.data.unauthorized);
        setContestTime(res.data.contestTime);
        setProblems(res.data.list);
        setContestName(res.data.contestName);
        setIsPublic(res.data.isPublic);
        console.log("res ", res.data);
        let arr = problems.map(() => false);
        setSubmitted(arr);
        for (let i = 0; i < res.data.list.length; i++) {
          let idSource =
            contestId + "-" + res.data.list[i].problemId + "-source";
          let tmpSource = localStorage.getItem(idSource);
          let idLanguage =
            contestId + "-" + res.data.list[i].problemId + "-language";
          let tmpLanguage = localStorage.getItem(idLanguage);
          if (tmpSource == null) {
            localStorage.setItem(idSource, "");
          }
          if (tmpLanguage == null) {
            localStorage.setItem(idLanguage, "CPP");
          }
        }
      },
      {}
    ).then(() => {
      setWait(false);
    });
  }, []);

  if (wait) {
    return <WaitScreen />;
  } else {
    if (unauthorized) {
      return <LockScreen />;
    } else {
      return (
        <div>
          <Box
            sx={{
              flexGrow: 1,
              bgcolor: "background.paper",
              display: "flex",
              height: window.innerHeight - 100,
            }}
          >
            <Box>
              <Box
                sx={{
                  flexGrow: 1,
                  bgcolor: "#000000",
                  display: "flex",
                  height: 80,
                }}
              >
                <Grid container alignItems="center" style={{ padding: "10px" }}>
                  {/*<b><span style={{color:"#FFFFFF"}}>{`${timer}`}</span></b>*/}
                  {contestTime !== undefined && !isPublic ? (
                    <Timer
                      id={contestId}
                      time={contestTime}
                      timeOutHandler={contestTimeOut}
                    />
                  ) : (
                    <b>
                      <span style={{ color: "#FFFFFF" }}>{`00:00:00`}</span>
                    </b>
                  )}
                </Grid>
              </Box>
              <Tabs
                orientation="vertical"
                variant="scrollable"
                value={value}
                onChange={handleChange}
                indicatorColor={"primary"}
                aria-label="Vertical tabs example"
                sx={{ borderRight: 1, borderColor: "divider", width: 80 }}
              >
                <Tab label="ALL" {...a11yProps(0)} style={{ minWidth: 80 }} />
                {problems.map((problem, index) => {
                  return (
                    <Tab
                      label={index + 1}
                      {...a11yProps(problem.problemId)}
                      style={{ minWidth: 80 }}
                    />
                  );
                })}
              </Tabs>
            </Box>

            <Box
              sx={{
                width: window.innerWidth,
                marginLeft: 1,
                bgcolor: "background.paper",
              }}
            >
              <TabPanelHorizontal value={value} index={0}>
                <TableContainer component={Paper}>
                  <Table
                    sx={{ minWidth: window.innerWidth - 500 }}
                    aria-label="customized table"
                  >
                    <TableHead>
                      <TableRow>
                        <StyledTableCell>Question</StyledTableCell>
                        <StyledTableCell align="center">Level</StyledTableCell>
                        <StyledTableCell align="right">Action</StyledTableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {problems.map((problem, index) => (
                        <StyledTableRow>
                          <StyledTableCell component="th" scope="row">
                            {problem.problemName}
                          </StyledTableCell>
                          <StyledTableCell
                            component="th"
                            scope="row"
                            align="center"
                          >
                            <span
                              style={{
                                color: getColorLevel(`${problem.levelId}`),
                              }}
                            >{`${problem.levelId}`}</span>
                          </StyledTableCell>
                          <StyledTableCell align="right">
                            <Button
                              variant="contained"
                              color="light"
                              // style={{marginLeft:"90px"}}
                              onClick={() => {
                                setValue(index + 1);
                              }}
                              // style={{position}}
                              style={{ marginLeft: "20px" }}
                            >
                              {submitted[index] ? "Modify" : "Solve"}
                            </Button>

                            <Button
                              variant="contained"
                              color="light"
                              // style={{marginLeft:"90px"}}
                              onClick={() => {
                                handleClickSubmitSolution(index);
                              }}
                              // style={{position}}
                              style={{ marginLeft: "20px" }}
                            >
                              {submitted[index] ? "Modify" : "SubmitSolution"}
                            </Button>
                          </StyledTableCell>
                        </StyledTableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
                <br />
                <Button
                  variant="contained"
                  color="light"
                  // style={{marginLeft:"90px"}}
                  onClick={() => {
                    submitContest();
                  }}
                  // style={{position}}
                  style={{ marginLeft: "90%" }}
                >
                  Submit
                </Button>
              </TabPanelHorizontal>

              {problems.map((problem, index) => {
                return (
                  <TabPanelHorizontal value={value} index={index + 1}>
                    <ContestProblemComponent
                      contestId={contestId}
                      index={index}
                      problemDescription={problem.problemDescription}
                      problemId={problem.problemId}
                      problemName={problem.problemName}
                      submitted={submitted}
                    />
                  </TabPanelHorizontal>
                );
              })}
            </Box>
          </Box>
        </div>
      );
    }
  }
}
