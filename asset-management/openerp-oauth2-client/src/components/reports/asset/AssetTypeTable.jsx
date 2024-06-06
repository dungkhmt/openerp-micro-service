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


export default function AssetTypeTable() {
	const [types, setTypes] = useState([]);
  const [logs, setLogs] = useState([]);
	const [assets, setAssets] = useState([]);
  const [rowData, setRowData] = useState([]);

	const getTopTypes = async() => {
		await request("get", "/asset-type/get-top-types", (res) => {
			setTypes(res.data);
		});
	};

	const queryTypes = async() => {
		setLogs([]);
		for(let i = 0; i < types.length; i++){
			await request("get", `/asset-type/get/${types[i]}`, (res) => {
				setLogs((prev) => [...prev, res.data]);
			});
		}
	};

	// const queryAssets = async() => {
	// 	setAssets([]);
	// 	for(let i = 0; i < logs.length; i++){
	// 		const log = logs[i];
	// 		await request("get", `asset/get-by-type/${log.id}`, (res) => {
	// 			setAssets((prev) => [...prev, res.data]);
	// 		});
	// 	}
	// };

  const buildData = () => {
    const arr = [];
    setRowData([]);
		for(let i = 0; i < logs.length; i++){
			const log = logs[i];
			arr.push({id: log.id, name: log.name, all: log.num_assets, available: 0, inuse: 0, repairing: 0, deprecated: 0});
		}
    // for(let i = 0; i < assets.length; i++){
		// 	const asset = assets[i];
		// 	const type = arr.find(item => item.id === asset.type_id);
		// 	if(asset.status_id == 1){
		// 		type["available"] += 1;
		// 	}
		// 	if(asset.status_id == 2){
		// 		type["inuse"] += 1;
		// 	}
		// 	if(asset.status_id == 3){
		// 		type["repairing"] += 1;
		// 	}
		// 	if(asset.status_id == 4){
		// 		type["deprecated"] += 1;
		// 	}
    // }
    setRowData(arr);
  };

  useEffect(() => {
		getTopTypes();
  }, []);

  useEffect(() => {
		queryTypes();
  }, [types]);

  // useEffect(() => {
  //   queryAssets();
  // }, [logs]);

	useEffect(() => {
		buildData();
	}, [logs]);

  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 300 }} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Top Asset Types</TableCell>
            <TableCell align="right">Management</TableCell>
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
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
