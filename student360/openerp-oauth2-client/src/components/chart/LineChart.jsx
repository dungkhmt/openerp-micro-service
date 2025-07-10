/* eslint-disable no-unused-vars */
import { Stack, useTheme, styled, Box, Typography } from "@mui/material";

import React from "react";
import {
  CartesianGrid,
  LineChart,
  Line,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import ChartTitle from "./ChartTitle";
import ChartSkeleton from "./ChartSkeleton";
import CustomTooltip from "./ChartTooltip";

const StyledBox = styled(Box)(({ theme }) => ({
  backgroundColor: theme.palette.grey[100],
  padding: theme.spacing(2),
  borderRadius: theme.spacing(1),
  flexGrow: 1,
}));

const LineChartCoponent = ({ data, title, subtitle, xAxisName, yAxisName }) => {
  const theme = useTheme();

  let chartData = [];

  const monthOrder = {
    "Tháng 9": 0,
    "Tháng 10": 1,
    "Tháng 11": 2,
    "Tháng 12": 3,
    "Tháng 1": 4,
    "Tháng 2": 5,
    "Tháng 3": 6,
  };

  if (!data) {
    return <ChartSkeleton />;
  }
  if (data && Array.isArray(data)) {
    chartData = data
      .map((item) => ({
        [xAxisName]:
          item.submissionMonth !== undefined
            ? "Tháng " + item.submissionMonth
            : item[0],
        [yAxisName]:
          item.numberOfSubmissions !== undefined
            ? item.numberOfSubmissions
            : item[1],
      }))
      .sort((a, b) => {
        const aMonth = a[xAxisName];
        const bMonth = b[xAxisName];

        if (
          monthOrder[aMonth] === undefined ||
          monthOrder[bMonth] === undefined
        ) {
          return aMonth.localeCompare(bMonth);
        }

        return monthOrder[aMonth] - monthOrder[bMonth];
      });
  } else {
    chartData = Object.entries(data)
      .map(([key, value]) => ({
        [xAxisName]: key,
        [yAxisName]: value,
      }))
      .sort((a, b) => new Date(a[xAxisName]) - new Date(b[xAxisName]));
  }

  // Tick Style
  const xAxisTickStyle = {
    fontSize: 12,
    fontWeight: 500,
    lineHeight: "16px",
    fill: `${theme.palette.grey[700]}`,
  };

  const yAxisLeftTickStyle = {
    fontSize: 12,
    fontWeight: "500",
    lineHeight: "16px",
    fill: `${theme.palette.grey[700]}`,
    textAnchor: "start",
    dx: -45,
  };

  const strokeColor = theme.palette.grey[400];
  const lineColor = theme.palette.chart[10];

  return (
    <StyledBox>
      <Stack direction="row" justifyContent="space-between">
        <ChartTitle>{title}</ChartTitle>
        <Typography>{subtitle}</Typography>
      </Stack>
      <Stack height="400px">
        <Stack width="100%" height="100%" mt={3}>
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData} margin={{ left: 0, right: 30 }}>
              <Tooltip content={<CustomTooltip />} />

              <CartesianGrid
                vertical={false}
                stroke={strokeColor}
                strokeDasharray={3}
              />

              <XAxis
                dataKey={xAxisName}
                axisLine={false}
                tickLine={false}
                tick={xAxisTickStyle}
                textAnchor="middle"
              />

              <XAxis dataKey={xAxisName} xAxisId={1} hide />

              <YAxis
                yAxisId="left"
                axisLine={false}
                tickLine={false}
                tick={yAxisLeftTickStyle}
              />

              <Line
                dataKey={yAxisName}
                type="monotone"
                dot={false}
                strokeWidth={3}
                stroke={lineColor}
                yAxisId="left"
                cursor="pointer"
              />
            </LineChart>
          </ResponsiveContainer>
        </Stack>
      </Stack>
    </StyledBox>
  );
};

export default LineChartCoponent;
