import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {request} from "../../../api";
import {useTranslation} from "react-i18next";
import {toFormattedDateTime} from "../../../utils/dateutils";
import {Box, Chip, IconButton} from "@mui/material";
import {GetApp} from "@material-ui/icons";
import {API_URL} from "../../../config/config";
import {getColorLevel} from "./lib";
import {StandardTable} from "erp-hust/lib/StandardTable";

function ListProblem() {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalPages, setTotalPage] = useState(0);
  const pageSizes = [20, 50, 100];
  const [contestProblems, setContestProblems] = useState([]);
  const [problems, setProblems] = useState([]);

  const {t} = useTranslation("education/programmingcontest/listproblem");

  const onSingleDownload = (problem) => {
    const form = document.createElement("form");

    form.setAttribute("method", "post");
    form.setAttribute("target", "_blank");
    form.setAttribute(
      "action",
      `${API_URL}/export-problem/${problem.problemId}`
    );

    document.body.appendChild(form);
    form.submit();
    form.parentNode.removeChild(form);
  };

  const columns = [
    {
      title: "ID",
      field: "problemId",
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
          {rowData["problemId"]}
        </Link>
      ),
    },
    {title: "Name", field: "problemName"},
    {title: "Created By", field: "userId"},
    {title: "Created At", field: "createdAt"},
    {
      title: "Level",
      field: "levelId",
      render: (rowData) => (
        <span style={{color: getColorLevel(`${rowData.levelId}`)}}>{`${rowData.levelId}`}</span>
      )
    },
    {
      title: "Tags",
      render: (rowData) => (
        <Box>
          {rowData?.tags.length > 0 && rowData.tags.map(tag =>
            <Chip
              size="small"
              label={tag.name}
              sx={{marginRight: "6px", marginBottom: "6px", border: "1px solid lightgray", fontStyle: "italic"}}
            />)}
        </Box>
      ),
    },
    {
      title: "Export",
      render: (rowData) => {
        return (
          <IconButton variant="contained" color="primary" onClick={() => onSingleDownload(rowData)}>
            <GetApp/>
          </IconButton>
        );
      },
    },
  ];

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const handlePageSizeChange = (event) => {
    setPageSize(event.target.value);
    setPage(1);
    // getProblemContestList();
  };

  function getProblems() {
    request("get", "/get-all-contest-problems", (res) => {
      const data = res.data.map((problem) => ({
        problemId: problem.problemId,
        problemName: problem.problemName,
        userId: problem.userId,
        createdAt: toFormattedDateTime(problem.createdAt),
        levelId: problem.levelId,
        tags: problem.tags,
      }));
      //setProblems(res.data);
      setProblems(data);
    }).then();
  }

  async function getProblemContestList() {
    request(
      "get",
      "/get-contest-problem-paging?size=" + pageSize + "&page=" + (page - 1),
      (res) => {
        setTotalPage(res.data.totalPages);
        setContestProblems(res.data.content);
      }
    ).then();
  }

  useEffect(() => {
    getProblems();
    getProblemContestList().then();
  }, [page, pageSize]);

  return (
    <div>
      <StandardTable
        title="Problems"
        columns={columns}
        data={problems}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
      {/*
      <div>
        <TableContainer component={Paper}>
          <Table sx={{ minWidth: 750 }} aria-label="customized table">
            <TableHead>
              <TableRow>
                <StyledTableCell>{t("index")}</StyledTableCell>
                <StyledTableCell align="left">{t("title")}</StyledTableCell>
                <StyledTableCell align="left">
                  {t("created by User")}
                </StyledTableCell>
                <StyledTableCell align="left">
                  {t("created date")}
                </StyledTableCell>
                <StyledTableCell align="left">
                  {t("difficulty")}
                </StyledTableCell>
                <StyledTableCell align="left">
                  {t("addTestCase")}
                </StyledTableCell>
                <StyledTableCell align="left">{t("edit")}</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {contestProblems.map((problem, index) => (
                <StyledTableRow>
                  <StyledTableCell component="th" scope="row">
                    {index + 1}
                  </StyledTableCell>

                  <StyledTableCell align="left">
                    <Link
                      to={
                        "/programming-contest/manager-view-problem-detail/" +
                        problem.problemId
                      }
                      style={{
                        textDecoration: "none",
                        color: "#000000",
                        hover: { color: "#00D8FF", textPrimary: "#00D8FF" },
                      }}
                    >
                      {problem.problemName}
                    </Link>
                  </StyledTableCell>

                  <StyledTableCell align="left">
                    <span>{`${problem.userId}`}</span>
                  </StyledTableCell>
                  <StyledTableCell align="left">
                    <span>{`${toFormattedDateTime(problem.createdAt)}`}</span>
                  </StyledTableCell>

                  <StyledTableCell align="left">
                    <span
                      style={{ color: getColorLevel(`${problem.levelId}`) }}
                    >{`${problem.levelId}`}</span>
                  </StyledTableCell>

                  <StyledTableCell align="left">
                    <Link
                      to={
                        "/programming-contest/problem-detail-create-test-case/" +
                        problem.problemId
                      }
                      style={{ textDecoration: "none", color: "black" }}
                    >
                      <Button variant="contained" color="light">
                        ADD
                      </Button>
                    </Link>
                  </StyledTableCell>
                  <StyledTableCell align="left">
                    <Link
                      to={
                        "/programming-contest/edit-problem/" + problem.problemId
                      }
                      style={{
                        textDecoration: "none",
                        color: "black",
                        cursor: "",
                      }}
                    >
                      <Button variant="contained" color="light">
                        Edit
                      </Button>
                    </Link>
                  </StyledTableCell>
                </StyledTableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </div>
      <br />
      <Grid container spacing={12}>
        <Grid item xs={6}>
          <TextField
            variant={"outlined"}
            autoFocus
            size={"small"}
            required
            select
            id="pageSize"
            value={pageSize}
            onChange={handlePageSizeChange}
          >
            {pageSizes.map((item) => (
              <MenuItem key={item} value={item}>
                {item}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        <Grid item>
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
        </Grid>
      </Grid>
      */}
    </div>
  );
}

export default ListProblem;
