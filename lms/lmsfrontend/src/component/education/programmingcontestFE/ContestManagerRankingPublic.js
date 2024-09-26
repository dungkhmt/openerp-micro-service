// import {Grid, TableHead} from "@material-ui/core";
// import Paper from "@material-ui/core/Paper";
// import TableContainer from "@material-ui/core/TableContainer";
// import TableRow from "@material-ui/core/TableRow";
// import {Box} from "@mui/material";
// import Table from "@mui/material/Table";
// import TableBody from "@mui/material/TableBody";
// import TablePagination from "@mui/material/TablePagination";
// import Typography from "@mui/material/Typography";
// import {useEffect, useState} from "react";
// import {StyledTableCell, StyledTableRow} from "./lib";
// import {useParams} from "react-router-dom";
//
// export default function ContestManagerRankingPublic() {
//   const { contestId } = useParams();
//   const [ranking, setRanking] = useState([]);
//   const [page, setPage] = useState(0);
//   const [rowsPerPage, setRowsPerPage] = useState(10);
//
//   const handlePageRankingSizeChange = (event) => {
//     setRowsPerPage(parseInt(event.target.value));
//     setPage(0);
//   };
//
//   function getRanking() {
//     request("get",
//       "/public/ranking-programming-contest/" + contestId,
//       res => {
//         setRanking(res.data.sort((a, b) => b.totalPoint - a.totalPoint));
//       })
//   }
//
//   useEffect(() => {
//     getRanking();
//   }, []);
//
//   return (
//     <Box>
//       <Box
//         sx={{
//           width: "900px",
//           marginBottom: "20px",
//           display: "flex",
//           flexDirection: "row",
//           justifyContent: "space-between",
//         }}
//       >
//         <Typography variant="h5">Contest Ranking</Typography>
//       </Box>
//
//       <Box>
//         <TableContainer component={Paper}>
//           <Table
//             sx={{ minWidth: window.innerWidth - 500 }}
//             aria-label="customized table"
//           >
//             <TableHead>
//               <TableRow>
//                 <StyledTableCell align="center"></StyledTableCell>
//                 <StyledTableCell align="center">Username</StyledTableCell>
//                 <StyledTableCell align="center">Fullname</StyledTableCell>
//                 <StyledTableCell align="center">
//                   <b>TOTAL</b>
//                 </StyledTableCell>
//
//                 {ranking.length > 0 &&
//                   ranking[0].mapProblemsToPoints.map((problem) => {
//                     return (
//                       <StyledTableCell
//                         sx={{ color: "yellow !important" }}
//                         align="center"
//                       >
//                         {problem.problemId}
//                       </StyledTableCell>
//                     );
//                   })}
//               </TableRow>
//             </TableHead>
//             <TableBody>
//               {ranking.length > 0 &&
//                 ranking.map(
//                   (element, index) =>
//                     index >= page * rowsPerPage &&
//                     index < (page + 1) * rowsPerPage && (
//                       <StyledTableRow>
//                         <StyledTableCell>
//                           <b>{index + 1}</b>
//                         </StyledTableCell>
//
//                         <StyledTableCell align="center">
//                           <b>{element.userId}</b>
//                         </StyledTableCell>
//                         <StyledTableCell align="center">
//                           <b>{element.fullname}</b>
//                         </StyledTableCell>
//
//                         <StyledTableCell align="center">
//                           <b>{element.totalPoint}</b>
//                         </StyledTableCell>
//                         {element.mapProblemsToPoints.map((problem) => {
//                           return (
//                             <StyledTableCell align="center">
//                               {problem.point}
//                             </StyledTableCell>
//                           );
//                         })}
//                       </StyledTableRow>
//                     )
//                 )}
//             </TableBody>
//           </Table>
//         </TableContainer>
//         <Grid container spacing={12}>
//           <Grid item>
//             <TablePagination
//               shape="rounded"
//               count={ranking.length}
//               page={page}
//               rowsPerPage={rowsPerPage}
//               onPageChange={(event, value) => {
//                 setPage(value);
//               }}
//               onRowsPerPageChange={handlePageRankingSizeChange}
//             />
//           </Grid>
//         </Grid>
//       </Box>
//     </Box>
//   );
// }
