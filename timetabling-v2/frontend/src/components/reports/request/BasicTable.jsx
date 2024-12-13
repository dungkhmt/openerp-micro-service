import React, { useEffect, useMemo, useState } from "react";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { request } from "api";

export default function BasicTable() {
  const [data, setData] = useState([]);
  const [users, setUsers] = useState([]);
  const [count, setCount] = useState([]);
  const [logs, setLogs] = useState([]);
  const [rowData, setRowData] = useState([]);

  const getTopUsers = async() => {
    await request("get", "/request/top-users", (res) => {
      setData(res.data);
    });
  };

  const processData = () => {
    setUsers([]);
    setCount([]);
    for(let i = 0; i < data.length; i++){
      const item = data[i];
      const words = item.split(",");

      setUsers((prev) => [...prev, words[0]]);
      setCount((prev) => [...prev, words[1]]);
    }
  };

  const queryUsers = async() => {
    setLogs([]);
    for(let i = 0; i < users.length; i++){
      const user = users[i];
      await request("get", `request/get-by-user/${user}`, (res) => {
        setLogs((prev) => [...prev, res.data]);
      });
    }
  };

  const buildData = () => {
    const arr = [];
    setRowData([]);
    for(let i = 0; i < logs.length; i++){
      const log = logs[i];
      arr.push({id: i, name: log[0].user_id, all: count[i], pending: 0, approved: 0, rejected: 0, done: 0});
      for(let j = 0; j < log.length; j++){
        if(log[j].status == 0){
          arr[i]["pending"] += 1;
        }
        if(log[j].status == 1){
          arr[i]["approved"] += 1;
        }
        if(log[j].status == 2){
          arr[i]["rejected"] += 1;
        }
        if(log[j].status == 3){
          arr[i]["done"] += 1;
        }
      }
    }
    setRowData(arr);
  };

  useEffect(() => {
    getTopUsers();
  }, []);

  useEffect(() => {
    processData();
  }, [data]);

  useEffect(() => {
    queryUsers();
  }, [users]);

  useEffect(() => {
    buildData();
  }, [logs]);

  console.log("logs", logs);
  console.log("rowdata", rowData);

  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Top contributors</TableCell>
            <TableCell align="right">Requests Created</TableCell>
            <TableCell align="right">Requests Pending</TableCell>
            <TableCell align="right">Requests Approved</TableCell>
            <TableCell align="right">Requests Rejected</TableCell>
            <TableCell align="right">Requests Done</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rowData.map((row) => (
            <TableRow
              key={row.name}
              sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            >
              <TableCell component="th" scope="row">
                {row.name}
              </TableCell>
              <TableCell align="right">{row.all}</TableCell>
              <TableCell align="right">{row.pending}</TableCell>
              <TableCell align="right">{row.approved}</TableCell>
              <TableCell align="right">{row.rejected}</TableCell>
              <TableCell align="right">{row.done}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
