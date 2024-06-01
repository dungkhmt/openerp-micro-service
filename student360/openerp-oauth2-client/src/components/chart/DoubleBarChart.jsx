import styled from "@emotion/styled";
import { Box, Stack, useMediaQuery } from "@mui/material";
import React from "react";
import {
  Bar,
  CartesianGrid,
  Cell,
  ComposedChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import ChartTitle from "./ChartTitle";
import ChartTooltip from "./ChartTooltip";
import { useTheme } from "@emotion/react";

const StyledBox = styled(Box)(({ theme }) => ({
  backgroundColor: theme.palette.grey[100],
  padding: theme.spacing(2),
  flexGrow: 3,
  borderRadius: theme.spacing(1),
}));

const DoubleBarChart = ({ data, fieldX = "bucket", fieldY = "delta" }) => {
  const theme = useTheme();

  const isTablet = useMediaQuery(theme.breakpoints.down("md"));

  const yAxisLeftTickStyle = {
    fontSize: isTablet ? 10 : 12,
    fontWeight: "500",
    lineHeight: "16px",
    fill: `${theme.palette.grey[700]}`,
    textAnchor: "start",
    dx: isTablet ? -7 : -25,
  };

  const yAxisRightTickStyle = {
    fontSize: isTablet ? 10 : 12,
    fontWeight: "500",
    fill: `${theme.palette.grey[700]}`,
    textAnchor: "end",
    dx: isTablet ? 7 : 25,
  };

  return (
    <StyledBox>
      <Stack direction="row" justifyContent="space-between">
        <ChartTitle>Submission Count per Contest</ChartTitle>
      </Stack>
      <Stack height="350px">
        <Stack width="100%" height="100%" mt={1}>
          <ResponsiveContainer width="100%" height="100%">
            <ComposedChart
              data={data}
              margin={{
                left: -25,
                right: -25,
                bottom: 30,
              }}
            >
              <Tooltip content={<ChartTooltip />} />

              <XAxis
                dataKey="contestId"
                axisLine={false}
                tickLine={false}
                interval={0}
                angle={-24}
                textAnchor="end"
                dx={50}
                dy={isTablet ? -5 : 0}
                tick={{ fontSize: 12 }}
                padding={{ bottom: 20 }}
              />
              <XAxis dataKey="totalProblems" xAxisId={1} hide />

              <Bar
                barSize={
                  isTablet
                    ? Math.min(130 / data?.length, 45)
                    : Math.min(360 / data?.length, 50)
                }
                dataKey={fieldX}
                yAxisId="left"
                xAxisId={1}
                radius={[6, 6, 6, 6]}
                fill={`${theme.palette.grey[200]}`}
                cursor="pointer"
              />

              <Bar
                barSize={
                  isTablet
                    ? Math.min(130 / data?.length, 45)
                    : Math.min(360 / data?.length, 50)
                }
                dataKey={fieldY}
                yAxisId="left"
                radius={[6, 6, 6, 6]}
              >
                {data?.map((entry) => (
                  <Cell
                    key={entry}
                    fill={`${theme.palette.chart[11]}`}
                    cursor="pointer"
                  />
                ))}
              </Bar>
              <Tooltip content={<ChartTooltip />} />

              <CartesianGrid
                vertical={false}
                stroke={theme.palette.grey[400]}
                strokeDasharray={3}
              />
              <YAxis
                yAxisId="left"
                axisLine={false}
                tickLine={false}
                tick={yAxisLeftTickStyle}
              />

              <YAxis
                yAxisId="right"
                orientation="right"
                axisLine={false}
                tickLine={false}
                tick={yAxisRightTickStyle}
                // tickFormatter={value}
              />
            </ComposedChart>
          </ResponsiveContainer>
        </Stack>
      </Stack>
    </StyledBox>
  );
};

export default DoubleBarChart;
