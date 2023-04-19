import React, {useEffect, useState} from "react";
import {connect} from "react-redux";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import {makeStyles} from "@material-ui/core/styles";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import Button from "@material-ui/core/Button";
import {API_URL} from "../../config/config";
import {useHistory} from "react-router-dom";

const useStyles = makeStyles({
  table: {
    minWidth: 650,
  },
});

function ListUserLogins(props) {
  const classes = useStyles();
  const [data, setData] = useState([]);
  const history = useHistory(); // HOOK moc cau

  const viewDetailUserLogin = (username) => {
    //alert('viewDetailUserLogin');
    history.push("/userlogin/detail/" + username); // truyen tham so
  };
  useEffect(() => {
    console.log("ListUserLogin , useEffect");
    console.log(props.isUpdate);
    const headers = new Headers();

    //headers.append("Accept", "application/json");

    headers.append("X-Auth-Token", props.token);
    fetch(`${API_URL}/get-list-user-logins`, {
      method: "GET",
      headers: headers,
    })
      .then((res) => res.json())
      .then(
        (res) => {
          setData(res);
        },
        (error) => {
          setData([]);
        }
      );
  }, [props.isUpdate]);

  return (
    <TableContainer component={Paper}>
      <Table className={classes.table} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell>Party</TableCell>
            <TableCell align="right">UserName</TableCell>
            <TableCell align="right">PartyId</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {data.map((row) => (
            <TableRow key={row.userName}>
              <TableCell component="th" scope="row">
                {row.partyId}
              </TableCell>
              <TableCell align="right">
                <a href="/tracklocations/list">{row.userName}</a>
              </TableCell>
              <TableCell align="right">{row.partyId}</TableCell>
              <TableCell align="right">
                <Button
                  variant="contained"
                  color="secondary"
                  onClick={() => viewDetailUserLogin(row.userName)}
                >
                  Chi tiết
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

const mapStateToProps = (state) => ({
  token: state.auth.token,
});

const mapDispatchToProps = (dispatch) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(ListUserLogins);
