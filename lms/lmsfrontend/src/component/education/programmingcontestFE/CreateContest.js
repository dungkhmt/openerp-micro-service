import React, {useEffect, useState} from "react";
import PropTypes from "prop-types";
import {alpha} from "@mui/material/styles";
import Box from "@mui/material/Box";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableSortLabel from "@mui/material/TableSortLabel";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Paper from "@mui/material/Paper";
import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import DeleteIcon from "@mui/icons-material/Delete";
import FilterListIcon from "@mui/icons-material/FilterList";
import {visuallyHidden} from "@mui/utils";
import {request} from "../../../api";
import Pagination from "@material-ui/lab/Pagination";
import DateFnsUtils from "@date-io/date-fns";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {Button, Card, CardActions} from "@material-ui/core";
import CardContent from "@material-ui/core/CardContent";
import {makeStyles} from "@material-ui/core/styles";
import {getColorLevel, sleep} from "./lib";
import {SubmitSuccess} from "./SubmitSuccess";
import {useHistory} from "react-router-dom";
import {MenuItem} from "@material-ui/core/";
import DateTimePicker from "@mui/lab/DateTimePicker";
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import AdapterDateFns from "@mui/lab/AdapterDateFns";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4), "& .MuiTextField-root": {
      margin: theme.spacing(1), width: "30%", minWidth: 120,
    },
  }, formControl: {
    margin: theme.spacing(1), minWidth: 120, maxWidth: 300,
  },
}));

const headCells = [{
  id: "problemName", numeric: false, disablePadding: true, label: "Title",
}, {
  id: "levelOrder", numeric: true, disablePadding: false, label: "Difficulty",
},];

function EnhancedTableHead(props) {
  const {
    onSelectAllClick, order, orderBy, numSelected, rowCount, onRequestSort,
  } = props;
  const createSortHandler = (property) => (event) => {
    onRequestSort(event, property);
  };

  return (<TableHead>
      <TableRow>
        <TableCell padding="checkbox">
          <Checkbox
            color="primary"
            indeterminate={numSelected > 0 && numSelected < rowCount}
            checked={rowCount > 0 && numSelected === rowCount}
            onChange={onSelectAllClick}
            inputProps={{
              "aria-label": "select all desserts",
            }}
          />
        </TableCell>
        {headCells.map((headCell) => (<TableCell
            key={headCell.id}
            align={headCell.numeric ? "right" : "left"}
            padding={headCell.disablePadding ? "none" : "normal"}
            sortDirection={orderBy === headCell.id ? order : false}
          >
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : "asc"}
              onClick={createSortHandler(headCell.id)}
            >
              {headCell.label}
              {orderBy === headCell.id ? (<Box component="span" sx={visuallyHidden}>
                  {order === "desc" ? "sorted descending" : "sorted ascending"}
                </Box>) : null}
            </TableSortLabel>
          </TableCell>))}
      </TableRow>
    </TableHead>);
}

EnhancedTableHead.propTypes = {
  numSelected: PropTypes.number.isRequired,
  onRequestSort: PropTypes.func.isRequired,
  onSelectAllClick: PropTypes.func.isRequired,
  order: PropTypes.oneOf(["asc", "desc"]).isRequired,
  orderBy: PropTypes.string.isRequired,
  rowCount: PropTypes.number.isRequired,
};

const EnhancedTableToolbar = (props) => {
  const {numSelected} = props;

  return (<Toolbar
      sx={{
        pl: {sm: 2}, pr: {xs: 1, sm: 1}, ...(numSelected > 0 && {
          bgcolor: (theme) => alpha(theme.palette.primary.main, theme.palette.action.activatedOpacity),
        }),
      }}
    >
      {numSelected > 0 ? (<Typography
          sx={{flex: "1 1 100%"}}
          color="inherit"
          variant="subtitle1"
          component="div"
        >
          {numSelected} selected
        </Typography>) : (<Typography
          sx={{flex: "1 1 100%"}}
          variant="h6"
          id="tableTitle"
          component="div"
        >
          Choose Problem
        </Typography>)}

      {numSelected > 0 ? (<Tooltip title="Delete">
          <IconButton>
            <DeleteIcon/>
          </IconButton>
        </Tooltip>) : (<Tooltip title="Filter list">
          <IconButton>
            <FilterListIcon/>
          </IconButton>
        </Tooltip>)}
    </Toolbar>);
};

EnhancedTableToolbar.propTypes = {
  numSelected: PropTypes.number.isRequired,
};
export default function CreateContest(props) {
  const history = useHistory();

  const SYNCHRONOUS_JUDGE_MODE = "SYNCHRONOUS_JUDGE_MODE";
  const ASYNCHRONOUS_JUDGE_MODE_QUEUE = "ASYNCHRONOUS_JUDGE_MODE_QUEUE";

  const [contestName, setContestName] = useState("");
  const [contestId, setContestId] = useState("");
  const [contestTime, setContestTime] = useState(Number(0));
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [contestProblems, setContestProblems] = useState([]);
  const [pageSize, setPageSize] = useState(10);
  const [problemSelected, setProblemSelected] = useState([]);
  const [showSubmitSuccess, setShowSubmitSuccess] = useState(false);
  const isSelected = (name) => problemSelected.indexOf(name) !== -1;
  const [isPublic, setIsPublic] = useState(false);
  const [startDate, setStartDate] = React.useState(new Date());
  const [maxNumberSubmissions, setMaxNumberSubmissions] = useState(10);
  const [countDown, setCountDown] = useState(Number(0));
  const [maxSourceCodeLength, setMaxSourceCodeLength] = useState(50000);
  const [minTimeBetweenTwoSubmissions, setMinTimeBetweenTwoSubmissions] = useState(0);
  const [judgeMode, setJudgeMode] = useState(ASYNCHRONOUS_JUDGE_MODE_QUEUE);

  const classes = useStyles();

  const handleClick = (event, name) => {
    const selectedIndex = problemSelected.indexOf(name);
    let newSelected = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(problemSelected, name);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(problemSelected.slice(1));
    } else if (selectedIndex === problemSelected.length - 1) {
      newSelected = newSelected.concat(problemSelected.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(problemSelected.slice(0, selectedIndex), problemSelected.slice(selectedIndex + 1));
    }
    console.log("newSelected ", newSelected);
    setProblemSelected(newSelected);
  };
  const handlePageChange = (event, value) => {
    setPage(value);
  };

  function handleSubmit() {
    let body = {
      contestId: contestId,
      contestName: contestName,
      contestTime: contestTime,
      problemIds: problemSelected,
      isPublic: isPublic,
      maxNumberSubmissions: maxNumberSubmissions,
      startedAt: startDate,
      countDownTime: countDown,
      maxSourceCodeLength: maxSourceCodeLength,
      minTimeBetweenTwoSubmissions: minTimeBetweenTwoSubmissions,
      judgeMode: judgeMode,
    };
    request("post", "/create-contest", (res) => {
      // console.log("problem list", res.data);
      setShowSubmitSuccess(true);
      sleep(1000).then((r) => {
        history.push("/programming-contest/teacher-list-contest-manager");
      });
    }, {}, body).then();
  }

  useEffect(() => {
    request("get", "/get-contest-problem-paging?size=" + pageSize + "&page=" + (page - 1), (res) => {
      setTotalPages(res.data.totalPages);
      setContestProblems(res.data.content);
    }).then();
  }, [page]);

  return (<div>
      <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <Card>
          <CardContent>
            <Typography variant="h5" component="h2">
              Create Contest
            </Typography>
            <br/>
            <form className={classes.root} noValidate autoComplete="off">
              <TextField
                autoFocus
                required
                id="contestId"
                label="Contest Id"
                placeholder="Contest Id"
                onChange={(event) => {
                  setContestId(event.target.value);
                }}
              />

              <TextField
                autoFocus
                required
                id="contestName"
                label="Contest Name"
                placeholder="Contest Name"
                onChange={(event) => {
                  setContestName(event.target.value);
                }}
              />

              <TextField
                autoFocus
                type="number"
                required
                id="timeLimit"
                label="Time Limit"
                placeholder="Time Limit"
                onChange={(event) => {
                  setContestTime(Number(event.target.value));
                }}
              />

              <TextField
                required
                id="Count Down"
                label="Count Down"
                placeholder="Count Down"
                onChange={(event) => {
                  setCountDown(Number(event.target.value));
                }}
              />

              <TextField
                type="number"
                id="Max Number Submissions"
                label="Max number of Submissions"
                placeholder="Max number of Submissions"
                onChange={(event) => {
                  setMaxNumberSubmissions(Number(event.target.value));
                }}
                value={maxNumberSubmissions}
              />
              <TextField
                type="number"
                id="Max Source Code Length"
                label="Source Length Limit (characters)"
                placeholder="Max Source Code Length"
                onChange={(event) => {
                  setMaxSourceCodeLength(event.target.value);
                }}
                value={maxSourceCodeLength}
              />
              <TextField
                type="number"
                id="Submission Interval"
                label="Submission Interval (s)"
                placeholder="Minimum Time Between Submissions"
                onChange={(event) => {
                  setMinTimeBetweenTwoSubmissions(Number(event.target.value));
                }}
                value={minTimeBetweenTwoSubmissions}
              />

              <TextField
                select
                id="Judge Mode"
                label="Judge Mode"
                onChange={(event) => {
                  setJudgeMode(event.target.value);
                }}
                value={judgeMode}
              >
                <MenuItem key={SYNCHRONOUS_JUDGE_MODE} value={SYNCHRONOUS_JUDGE_MODE}>
                  {"Synchronous"}
                </MenuItem>
                <MenuItem key={ASYNCHRONOUS_JUDGE_MODE_QUEUE} value={ASYNCHRONOUS_JUDGE_MODE_QUEUE}>
                  {"Asynchronous - QUEUE"}
                </MenuItem>
              </TextField>

              <TextField
                select
                id="Public Contest"
                label="Public Contest"
                placeholder="Public Contest"
                onChange={(event) => {
                  setIsPublic(event.target.value);
                }}
                value={isPublic}
              >
                <MenuItem key={"true"} value={true}>
                  {"true"}
                </MenuItem>
                <MenuItem key={"false"} value={false}>
                  {"false"}
                </MenuItem>
              </TextField>

              <LocalizationProvider dateAdapter={AdapterDateFns}>
                <DateTimePicker
                  label="Date&Time picker"
                  value={startDate}
                  onChange={(value) => {
                    setStartDate(value);
                  }}
                  renderInput={(params) => <TextField {...params} />}
                />
              </LocalizationProvider>
            </form>

            <Box sx={{width: "100%"}}>
              <Paper sx={{width: "100%", mb: 2}}>
                <EnhancedTableToolbar numSelected={problemSelected.length}/>
                <TableContainer>
                  <Table
                    sx={{minWidth: 750}}
                    aria-labelledby="tableTitle"
                    size={"medium"}
                  >
                    <EnhancedTableHead
                      numSelected={problemSelected.length}
                    />
                    <TableBody>
                      {contestProblems.map((p, index) => {
                        const isItemSelected = isSelected(p.problemId);
                        const labelId = `enhanced-table-checkbox-${index}`;
                        return (<TableRow
                            hover
                            onClick={(event) => handleClick(event, p.problemId)}
                            role="checkbox"
                            aria-checked={isItemSelected}
                            tabIndex={-1}
                            key={p.problemId}
                            selected={isItemSelected}
                          >
                            <TableCell padding="checkbox">
                              <Checkbox
                                color="primary"
                                checked={isItemSelected}
                                inputProps={{
                                  "aria-labelledby": labelId,
                                }}
                              />
                            </TableCell>
                            <TableCell
                              component="th"
                              id={labelId}
                              scope="row"
                              padding="none"
                            >
                              {p.problemName}
                            </TableCell>
                            <TableCell align="right">
                              <span
                                style={{color: getColorLevel(`${p.levelId}`)}}
                              >{`${p.levelId}`}</span>
                            </TableCell>
                          </TableRow>);
                      })}
                    </TableBody>
                  </Table>
                  <TableRow>
                    <TableCell align={"right"}>
                      <Pagination
                        className="my-3"
                        count={totalPages}
                        page={page}
                        siblingCount={1}
                        boundaryCount={1}
                        variant="outlined"
                        shape="rounded"
                        onChange={handlePageChange}
                      />
                    </TableCell>
                  </TableRow>
                </TableContainer>
              </Paper>
            </Box>
          </CardContent>
          <CardActions>
            <Button
              variant="contained"
              color="light"
              style={{marginLeft: "45px"}}
              onClick={handleSubmit}
            >
              Save
            </Button>
            <SubmitSuccess
              showSubmitSuccess={showSubmitSuccess}
              content={"You have saved contest"}
            />
          </CardActions>
        </Card>
      </MuiPickersUtilsProvider>
    </div>);
}
