import React, { useState } from "react";
import ReactECharts from "echarts-for-react";
import IconButton from "@mui/material/IconButton";
import PieChartIcon from "@mui/icons-material/PieChart";
import BarChartIcon from "@mui/icons-material/BarChart";

const Chart = ({ data, title }) => {
  const [chartType, setChartType] = useState("bar");

  const options = {
    title: {
      text: title,
      textStyle: {
        fontFamily: "Arial, sans-serif",
      },
    },
    tooltip: {},
    xAxis: {
      type: "category",
      show: chartType === "bar",
      data: data?.map((item) => item.label),
    },
    yAxis: {
      type: "value",
    },
    legend: {
      show: chartType === "pie",
      orient: "vertical",
      right: 10,
      top: 50,
      data: data?.map((item) => item.label),
    },
    series: [
      {
        name: "Value",
        type: chartType,
        label: {
          show: chartType === "pie",
          formatter: `{b}\n{c}`,
        },
        data:
          chartType === "bar"
            ? data?.map((item) => item.value)
            : data?.map((item) => ({ value: item.value, name: item.label })),
      },
    ],
  };

  const switchChartType = () => {
    setChartType(chartType === "bar" ? "pie" : "bar");
  };

  return (
    <div style={{ position: "relative", width: "100%", height: "100%" }}>
      <ReactECharts option={options} />
      <div style={{ position: "absolute", top: 0, right: 0 }}>
        <IconButton onClick={switchChartType}>
          {chartType === "bar" ? <PieChartIcon /> : <BarChartIcon />}
        </IconButton>
      </div>
    </div>
  );
};

export default Chart;
