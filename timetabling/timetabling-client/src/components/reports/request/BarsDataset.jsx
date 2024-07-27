import React from "react";
import { BarChart } from "@mui/x-charts/BarChart";
import { axisClasses } from "@mui/x-charts";
import { useEffect } from "react";
import { useState } from "react";

const chartSetting = {
  yAxis: [
    {
      label: "rainfall (mm)",
    },
  ],
  width: 850,
  height: 450,
  sx: {
    [`.${axisClasses.left} .${axisClasses.label}`]: {
      transform: "translate(-20px, 0)",
    },
  },
};

const valueFormatter = (value) => `${value} requests`;

export default function BarsDataset({ data }) {
  const [log, setLog] = useState([]);
  const arr = [
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Jan",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Fev",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Mar",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Apr",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "May",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "June",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "July",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Aug",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Sept",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Oct",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Nov",
    },
    {
      pending: 0,
      approved: 0,
      rejected: 0,
      done: 0,
      month: "Dec",
    },
  ];

  const convertToMonth = (datetime) => {
    const date = new Date(datetime);
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    return month;
  };

  const getRequestStatus = (req) => {
    if(req.status == 0){
      return "pending";
    }
    if(req.status == 1){
      return "approved";
    }
    if(req.status == 2){
      return "rejected";
    }
    return "done";
  };

  const buildData = () => {
    for (let i = 0; i < data.length; i++) {
      const month = convertToMonth(data[i].since);
      const status = getRequestStatus(data[i]);
      if(month === "01"){
        arr[0][status] += 1;
      }
      if(month === "02"){
        arr[1][status] += 1;
      }
      if(month === "03"){
        arr[2][status] += 1;
      }
      if(month === "04"){
        arr[3][status] += 1;
      }
      if(month === "05"){
        arr[4][status] += 1;
      }
      if(month === "06"){
        arr[5][status] += 1;
      }
      if(month === "07"){
        arr[6][status] += 1;
      }
      if(month === "08"){
        arr[7][status] += 1;
      }
      if(month === "09"){
        arr[8][status] += 1;
      }
      if(month === "10"){
        arr[9][status] += 1;
      }
      if(month === "11"){
        arr[10][status] += 1;
      }
      if(month === "12"){
        arr[11][status] += 1;
      }
    }

    setLog(arr);
  };

  useEffect(() => {
    buildData();
  }, [data]);

  return (
    <BarChart
      dataset={log}
      xAxis={[{ scaleType: "band", dataKey: "month" }]}
      series={[
        { dataKey: "pending", label: "Pending", valueFormatter },
        { dataKey: "approved", label: "Approved", valueFormatter },
        { dataKey: "rejected", label: "Rejected", valueFormatter },
        { dataKey: "done", label: "Done", valueFormatter },
      ]}
      title="Request Status By Month"
      {...chartSetting}
    />
  );
}
