import {Grid, TableHead} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import TableContainer from "@material-ui/core/TableContainer";
import TableRow from "@material-ui/core/TableRow";
import {Box} from "@mui/material";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TablePagination from "@mui/material/TablePagination";
import Typography from "@mui/material/Typography";
import {useEffect, useState} from "react";
import XLSX from "xlsx";
import {StyledTableCell, StyledTableRow} from "./lib";
import {useParams} from "react-router-dom";
import {API_URL} from "../../../config/config";

export default function ContestManagerRankingPublic() {
  const { contestId } = useParams();
  const [ranking, setRanking] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [getPointForRankingType, setGetPointForRankingType] =
    useState("HIGHEST");

  const downloadHandler = (event) => {
    if (ranking.length === 0) {
      return;
    }

    var wbcols = [];

    wbcols.push({ wpx: 80 });
    wbcols.push({ wpx: 120 });
    let numOfProblem = ranking[0].mapProblemsToPoints.length;
    for (let i = 0; i < numOfProblem; i++) {
      wbcols.push({ wpx: 50 });
    }
    wbcols.push({ wpx: 50 });

    let datas = [];

    for (let i = 0; i < ranking.length; i++) {
      let data = {};
      data["Username"] = ranking[i].userId;
      data["Fullname"] = ranking[i].fullname;
      for (let j = 0; j < numOfProblem; j++) {
        const problem = ranking[i].mapProblemsToPoints[j].problemId;
        const problemPoint = ranking[i].mapProblemsToPoints[j].point;
        data[problem] = problemPoint;
      }
      data["TOTAL"] = ranking[i].totalPoint;

      datas[i] = data;
    }

    var sheet = XLSX.utils.json_to_sheet(datas);
    var wb = XLSX.utils.book_new();
    sheet["!cols"] = wbcols;

    XLSX.utils.book_append_sheet(wb, sheet, "ranking");
    XLSX.writeFile(
      wb,
      contestId + "-RANKING-" + getPointForRankingType + ".xlsx"
    );
  };

  const handlePageRankingSizeChange = (event) => {
    setRowsPerPage(parseInt(event.target.value));
    setPage(0);
  };

  function getRanking() {
    fetch(API_URL + "/public/ranking-programming-contest/" + contestId)
      .then((response) => response.json())
      .then((data) => {
        console.log("fetch ", data);
        setRanking(data.sort((a, b) => b.totalPoint - a.totalPoint));
      });

    /*
    request(
      "get",
      "/get-ranking-contest-new/" +
        contestId +
        "?getPointForRankingType=" +
        getPointForRankingType,
      (res) => {
        setRanking(res.data.sort((a, b) => b.totalPoint - a.totalPoint));
      }
    ).then();
      */
  }

  useEffect(() => {
    getRanking();
  }, []);

  useEffect(() => {
    getRanking();
  }, [getPointForRankingType]);

  return (
    <Box>
      <Box
        sx={{
          width: "900px",
          marginBottom: "20px",
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
        }}
      >
        <Typography variant="h5">Contest Ranking</Typography>
      </Box>

      <Box>
        <TableContainer component={Paper}>
          <Table
            sx={{ minWidth: window.innerWidth - 500 }}
            aria-label="customized table"
          >
            <TableHead>
              <TableRow>
                <StyledTableCell align="center"></StyledTableCell>
                <StyledTableCell align="center">Username</StyledTableCell>
                <StyledTableCell align="center">Fullname</StyledTableCell>
                <StyledTableCell align="center">
                  <b>TOTAL</b>
                </StyledTableCell>

                {ranking.length > 0 &&
                  ranking[0].mapProblemsToPoints.map((problem) => {
                    return (
                      <StyledTableCell
                        sx={{ color: "yellow !important" }}
                        align="center"
                      >
                        {problem.problemId}
                      </StyledTableCell>
                    );
                  })}
              </TableRow>
            </TableHead>
            <TableBody>
              {ranking.length > 0 &&
                ranking.map(
                  (element, index) =>
                    index >= page * rowsPerPage &&
                    index < (page + 1) * rowsPerPage && (
                      <StyledTableRow>
                        <StyledTableCell>
                          <b>{index + 1}</b>
                        </StyledTableCell>

                        <StyledTableCell align="center">
                          <b>{element.userId}</b>
                        </StyledTableCell>
                        <StyledTableCell align="center">
                          <b>{element.fullname}</b>
                        </StyledTableCell>

                        <StyledTableCell align="center">
                          <b>{element.totalPoint}</b>
                        </StyledTableCell>
                        {element.mapProblemsToPoints.map((problem) => {
                          return (
                            <StyledTableCell align="center">
                              {problem.point}
                            </StyledTableCell>
                          );
                        })}
                      </StyledTableRow>
                    )
                )}
            </TableBody>
          </Table>
        </TableContainer>
        <Grid container spacing={12}>
          <Grid item>
            <TablePagination
              shape="rounded"
              count={ranking.length}
              page={page}
              rowsPerPage={rowsPerPage}
              onPageChange={(event, value) => {
                setPage(value);
              }}
              onRowsPerPageChange={handlePageRankingSizeChange}
            />
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
}
