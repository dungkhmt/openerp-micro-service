import * as React from 'react';
import { BarChart } from '@mui/x-charts/BarChart';
import { axisClasses } from '@mui/x-charts';
import { useState } from 'react';
import { useEffect } from 'react';

const chartSetting = {
  yAxis: [
    {
      label: 'rainfall (mm)',
    },
  ],
  width: 850,
  height: 450,
  sx: {
    [`.${axisClasses.left} .${axisClasses.label}`]: {
      transform: 'translate(-20px, 0)',
    },
  },
};

const valueFormatter = (value) => `${value} assets`;

export default function BarsDataset({ data }) {
  const [logs, setLogs] = useState([]);
  const arr = [
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Jan",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Fev",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Mar",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Apr",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "May",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "June",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "July",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Aug",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Sept",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Oct",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Nov",
    },
    {
      available: 0,
      inuse: 0,
      repairing: 0,
      deprecated: 0,
      month: "Dec",
    },
  ];

  const convertToMonth = (datetime) => {
    const date = new Date(datetime);
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    return month;
  };

  const getAssetStatus = (asset) => {
    if(asset.status_id == 1){
      return "available";
    }
    if(asset.status_id == 2){
      return "inuse";
    }
    if(asset.status_id == 3){
      return "repairing";
    }
    return "deprecated";
  };

  const buildData = () => {
    for(let i = 0; i < data.length; i++){
      const month = convertToMonth(data[i].since);
      const status = getAssetStatus(data[i]);
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

    setLogs(arr);
  };

  useEffect(() => {
    buildData();
  }, [data]);

  return (
    <BarChart
      dataset={logs}
      xAxis={[{ scaleType: 'band', dataKey: 'month' }]}
      series={[
        { dataKey: 'available', label: 'Available', valueFormatter },
        { dataKey: 'inuse', label: 'In Use', valueFormatter },
        { dataKey: 'repairing', label: 'Repairing', valueFormatter },
        { dataKey: 'deprecated', label: 'Deprecated', valueFormatter },
      ]}
      title='Asset Status By Month'
      {...chartSetting}
    />
  );
};
