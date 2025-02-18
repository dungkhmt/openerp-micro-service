// import * as React from "react";
// import Box from "@mui/material/Box";
// import {ScrollBox} from "react-scroll-box";
// import Typography from "@mui/material/Typography";
// import {Table, TableBody, TableHead} from "@material-ui/core";
// import TableRow from "@material-ui/core/TableRow";
// import {Link} from "react-router-dom";
// import {styled} from '@mui/material/styles';
// import TableCell, {tableCellClasses} from '@mui/material/TableCell';
// import TableContainer from "@material-ui/core/TableContainer";
// import Paper from "@material-ui/core/Paper";
//
//
// const StyledTableCell = styled(TableCell)(({theme}) => ({
//   [`&.${tableCellClasses.head}`]: {
//     backgroundColor: theme.palette.common.black,
//     color: theme.palette.common.white,
//   },
//   [`&.${tableCellClasses.body}`]: {
//     fontSize: 14,
//   },
// }));
//
// const StyledTableRow = styled(TableRow)(({theme}) => ({
//   '&:nth-of-type(odd)': {
//     backgroundColor: theme.palette.action.hover,
//   },
//   // hide last border
//   '&:last-child td, &:last-child th': {
//     border: 0,
//   },
// }));
//
// export function ProblemSubmission(props) {
//   const submitted = props.submitted;
//   const problemSubmission = props.problemSubmission;
//
//   const getColor = (status) => {
//     switch (status) {
//       case 'Accepted':
//         return 'green';
//       default:
//         return 'red';
//     }
//   }
//
//   if (!submitted) {
//
//     return (
//       <div>
//         <br/><br/><br/>
//         <Box sx={{display: 'flex', p: 1}}>
//
//           <ScrollBox style={{width: '100%', overflow: "auto", height: "148px"}}>
//             <Typography variant={"h4"} style={{marginLeft: "20%"}} color={"#d6d6d6"}>
//               You don't have any submissions yet.
//             </Typography>
//           </ScrollBox>
//         </Box>
//
//       </div>
//     );
//   } else {
//     return (
//       <div>
//
//         <TableContainer component={Paper}>
//           <Table sx={{minWidth: 100}} aria-label="customized table">
//             <TableHead>
//               <TableRow>
//                 <StyledTableCell>Time Submitted</StyledTableCell>
//                 <StyledTableCell align="right">Status</StyledTableCell>
//                 <StyledTableCell align="right">Run time</StyledTableCell>
//                 <StyledTableCell align="right">Memory</StyledTableCell>
//                 <StyledTableCell align="right">Language</StyledTableCell>
//               </TableRow>
//             </TableHead>
//             <TableBody>
//               {problemSubmission.map((p) => (
//                 <StyledTableRow>
//                   <StyledTableCell component="th" scope="row">
//                     {p.timeSubmitted}
//                   </StyledTableCell>
//                   <StyledTableCell align="right">
//                     <Link to={"/programming-contest/problem-submission-detail/" + p.problemSubmissionId}
//                           style={{textDecoration: 'none', color: "black", cursor: ""}}>
//                       <span style={{color: getColor(`${p.status}`)}}>{`${p.status}`}</span>
//                     </Link>
//
//                   </StyledTableCell>
//                   <StyledTableCell align="right">{p.runTime}</StyledTableCell>
//                   <StyledTableCell align="right">{p.memory}</StyledTableCell>
//                   <StyledTableCell align="right">{p.language}</StyledTableCell>
//                 </StyledTableRow>
//               ))}
//             </TableBody>
//           </Table>
//         </TableContainer>
//
//       </div>
//     )
//   }
// }
