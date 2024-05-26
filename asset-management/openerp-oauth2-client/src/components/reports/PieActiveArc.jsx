import * as React from 'react';
import { PieChart } from '@mui/x-charts/PieChart';
import { useState } from 'react';
import { request } from 'api';
import { useEffect } from 'react';

const fakeData = [
  { id: 4, value: 10, label: 'AVAILABLE' },
  { id: 1, value: 15, label: 'IN USE' },
  { id: 2, value: 20, label: 'REPAIRING' },
  { id: 3, value: 25, label: 'DEPRECATED' },
];

// sau api: sap xep theo thoi gian
export default function PieActiveArc() {
  const [requestLog, setRequestLog] = useState([]);
  const [data, setData] = useState([]);

  const getAllRequestLogs = async() => {
    await request("get", "/request-log/get-by-user", (res) => {
      setRequestLog(res.data);
    });
  };

  const getAllRequests = async() => {
    await request("get", "/request/get-by-user", (res) => {
      setData(res.data);
    });
  };

  const arr = [
    { id: 0, value: 0, label: 'PENDING' },
    { id: 1, value: 0, label: 'APPROVED' },
    { id: 2, value: 0, label: 'REJECTED' },
    { id: 3, value: 0, label: 'DONE' }
  ];
  const buildData = () => {
    for(let i = 0; i < data.length; i++){
      const item = data[i];
      if(item.status === 0){
        arr[0].value += 1;
      } else if(item.status === 1){
        arr[1].value += 1;
      } else if(item.status === 2){
        arr[2].value += 1;
      } else if(item.status === 3){
        arr[3].value += 1;
      }
    }
  };

  useEffect(() => {
    getAllRequestLogs();
    getAllRequests();
    console.log("logs", requestLog);
    console.log("data", data);

    buildData();
  }, []);

  return (
    <div>
    <PieChart
      series={[
        {
          data: fakeData,
          highlightScope: { faded: 'global', highlighted: 'item' },
          faded: { innerRadius: 25, additionalRadius: -30, color: 'gray' },
        },
      ]}
      height={350}
    />
    <h3 style={{textAlign: "center"}}>Request By Status</h3>
    </div>
  );
}
