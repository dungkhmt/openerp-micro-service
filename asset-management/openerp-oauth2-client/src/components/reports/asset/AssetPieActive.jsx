import * as React from "react";
import { PieChart } from "@mui/x-charts/PieChart";
import { useState } from "react";
import { request } from "api";
import { useEffect } from "react";

const fakeData = [
  { id: 4, value: 10, label: "AVAILABLE" },
  { id: 1, value: 15, label: "IN USE" },
  { id: 2, value: 20, label: "REPAIRING" },
  { id: 3, value: 25, label: "DEPRECATED" },
];

// sau api: sap xep theo thoi gian
export default function PieActiveArc() {
  const [requestLog, setRequestLog] = useState([]);
  const [data, setData] = useState([]);
  const [log, setLog] = useState([
    { id: 0, value: 0, label: "AVAILABLE" },
    { id: 1, value: 0, label: "IN USE" },
    { id: 2, value: 0, label: "REPAIRING" },
    { id: 3, value: 0, label: "DEPRECATED" },
  ]);

  const getAllRequestLogs = async () => {
    await request("get", "/request-log/get-by-user", (res) => {
      setRequestLog(res.data);
    });
  };

  const getAllRequests = async () => {
    await request("get", "/asset/get-all", (res) => {
      setData(res.data);
    });
  };

  const arr = [
    { id: 0, value: 0, label: "AVAILABLE" },
    { id: 1, value: 0, label: "IN USE" },
    { id: 2, value: 0, label: "REPAIRING" },
    { id: 3, value: 0, label: "DEPRECATED" },
  ];
  
  const buildData = () => {
    for (let i = 0; i < data?.length; i++) {
      const item = data[i];
      if (item.status_id === 1) {
        arr[0].value += 1;
      } else if (item.status_id === 2) {
        arr[1].value += 1;
      } else if (item.status_id === 3) {
        arr[2].value += 1;
      } else if (item.status_id === 4) {
        arr[3].value += 1;
      }
    }

    setLog(arr);
  };

  useEffect(() => {
    getAllRequestLogs();
    getAllRequests();
  }, []);

  useEffect(() => {
    buildData();
  }, [data]);

  return (
    <div>
      <PieChart
        series={[
          {
            data: log,
            highlightScope: { faded: "global", highlighted: "item" },
            faded: { innerRadius: 25, additionalRadius: -30, color: "gray" },
          },
        ]}
        height={350}
      />
      <h3 style={{ textAlign: "center" }}>Request By Status</h3>
    </div>
  );
}
