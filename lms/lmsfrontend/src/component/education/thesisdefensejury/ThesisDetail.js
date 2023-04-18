import * as React from "react";
import {useEffect, useState} from "react";
import {useHistory, useParams} from "react-router-dom";
import {request} from "../../../api";
import Typography from "@mui/material/Typography";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@mui/material/Table";
import {Button, TableHead} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow";
import {StyledTableCell, StyledTableRow} from "../programmingcontestFE/lib";
import TableBody from "@mui/material/TableBody";

export default function ThesisDetail(props) {
  const params = useParams();
  const history = useHistory();
  const [name, setName] = useState();
  const [thesis, setThesis] = useState([]);

  async function getAllThesis() {
    request(
      // token,
      // history,
      "GET",
      `/thesis/${params.id}`,
      (res) => {
        console.log(res.data)
        setThesis([res.data]);
        setName(res.data.name)
      }
    );
  }

  useEffect(() => {
    getAllThesis()
  }, []);

  const handleEdit = () => {
    history.push({
      pathname: `/thesis/edit/${thesis[0].id}`,
      state: {
        thesisID: params.id
      }
    });

  };

  return (
    <div>
      <Typography variant="h4" component="h2">
        Thesis: {name}
      </Typography>

      <TableContainer component={Paper}>
        <Table
          sx={{minWidth: window.innerWidth - 500}}
          aria-label="customized table"
        >
          <TableHead>
            <TableRow>
              <StyledTableCell>Mô tả</StyledTableCell>
              <StyledTableCell>Tên người tạo</StyledTableCell>
              <StyledTableCell>Tên người hướng dẫn</StyledTableCell>
              <StyledTableCell>Tên HĐ</StyledTableCell>
              <StyledTableCell>Tên chương trình đào tạo</StyledTableCell>
              {/* <StyledTableCell>Keyword</StyledTableCell> */}
              <StyledTableCell align="center">Ngày tạo</StyledTableCell>
              <StyledTableCell align="center"></StyledTableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {thesis.map((ele, index) => (
              <StyledTableRow>
                <StyledTableCell>
                  <b>{ele.thesis_abstract}</b>
                </StyledTableCell>
                <StyledTableCell component="th" scope="row" align="center">
                  <b>{ele.student_name} </b>
                </StyledTableCell>
                <StyledTableCell component="th" scope="row" align="center">
                  <b>{ele.supervisor_name} </b>
                </StyledTableCell>
                <StyledTableCell component="th" scope="row" align="center">
                  <b>{ele.defense_jury_name} </b>
                </StyledTableCell>
                <StyledTableCell component="th" scope="row" align="center">
                  <b>{ele.program_name} </b>
                </StyledTableCell>
                {/* <StyledTableCell component="th" scope="row" align="center">
                    <b>{ele.keyword} </b>
                </StyledTableCell> */}
                <StyledTableCell component="th" scope="row" align="center">
                  <b>{ele.createdTime} </b>
                </StyledTableCell>
                <StyledTableCell component="th" scope="row" align="center">
                  <Button color="primary" onClick={handleEdit}>
                    Edit
                  </Button>
                </StyledTableCell>
              </StyledTableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}
