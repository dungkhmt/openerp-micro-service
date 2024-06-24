import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { useState } from 'react';
import { request } from 'api';
import { useEffect } from 'react';


export default function BasicTable() {
  const [data, setData] = useState([]);
  const [users, setUsers] = useState([]);
  const [count, setCount] = useState([]);
  const [logs, setLogs] = useState([]);
  const [rowData, setRowData] = useState([]);

  const getTopUsers = async() => {
    await request("get", "/asset/top-admin-users", (res) => {
      setData(res.data);
    });
  };

  const queryUsers = async() => {
    setLogs([]);
    for(let i = 0; i < users.length; i++){
      const user = users[i];
      await request("get", `asset/get-by-admin/${user}`, (res) => {
        setLogs((prev) => [...prev, res.data]);
      });
    }
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

  const buildData = () => {
    const arr = [];
    setRowData([]);
    for(let i = 0; i < logs.length; i++){
      const log = logs[i];
      arr.push({id: i, name: log[0].admin_id, all: count[i], available: 0, inuse: 0, repairing: 0, deprecated: 0});
      for(let j = 0; j < log.length; j++){
        if(log[j].status_id == 1){
          arr[i]["available"] += 1;
        }
        if(log[j].status_id == 2){
          arr[i]["inuse"] += 1;
        }
        if(log[j].status_id == 3){
          arr[i]["repairing"] += 1;
        }
        if(log[j].status_id == 4){
          arr[i]["deprecated"] += 1;
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

  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Top Admin Users</TableCell>
            <TableCell align="right">Asset Management</TableCell>
            <TableCell align="right">Asset Available</TableCell>
            <TableCell align="right">Asset In Use</TableCell>
            <TableCell align="right">Asset Repairing</TableCell>
            <TableCell align="right">Asset Deprecated</TableCell>
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
              <TableCell align="right">{row.available}</TableCell>
              <TableCell align="right">{row.inuse}</TableCell>
              <TableCell align="right">{row.repairing}</TableCell>
              <TableCell align="right">{row.deprecated}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
