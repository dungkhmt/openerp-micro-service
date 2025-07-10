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
import ChartTooltip from "./ChartTooltip";
import ChartSkeleton from "./ChartSkeleton";

const StyledBox = styled(Box)(({ theme }) => ({
  backgroundColor: theme.palette.grey[100],
  padding: theme.spacing(2),
  borderRadius: theme.spacing(1),
  flexGrow: 1,
}));

const DoubleLineChart = ({
  data,
  title,
  subtitle,
  xAxisName,
  yAxis1Name,
  yAxis2Name,
}) => {
  const theme = useTheme();

  let chartData = [];

  if (!data) {
    return <ChartSkeleton />;
  }

  if (Array.isArray(data)) {
    chartData = data.map((item) => ({
      [xAxisName]: item[0] + "h",
      [yAxis1Name]: item[1],
      [yAxis2Name]: item[2],
    }));
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
  const lineColor1 = theme.palette.primary.main;
  const lineColor2 = theme.palette.secondary.main;

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
              <Tooltip content={<ChartTooltip />} />

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
                dataKey={yAxis1Name}
                type="monotone"
                dot={false}
                strokeWidth={3}
                stroke={lineColor1}
                yAxisId="left"
                cursor="pointer"
              />

              <Line
                dataKey={yAxis2Name}
                type="monotone"
                dot={false}
                strokeWidth={3}
                stroke={lineColor2}
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

export default DoubleLineChart;
