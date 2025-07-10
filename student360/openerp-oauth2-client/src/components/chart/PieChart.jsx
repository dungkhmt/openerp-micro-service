import React from "react";
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";
import { useTheme } from "@emotion/react";
import { Stack, Typography } from "@mui/material";
import Legend from "./Legend";
import ChartTitle from "./ChartTitle";

const PieCharts = ({
  data,
  title,
  mainTitle,
  totalValue,
  innerRadius = 102,
  outerRadius = 132,
}) => {
  const theme = useTheme();

  if (!data) {
    return <div>Dữ liệu không hợp lệ</div>;
  }

  let transformedData = [];

  if (Array.isArray(data)) {
    transformedData = data.map((item) => ({
      name: item[0],
      value: item[1],
    }));
  } else {
    transformedData = Object.entries(data).map(([name, value]) => ({
      name,
      value,
    }));
  }

  const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042"];

  return (
    <Stack
      sx={{ position: "relative", flexShrink: 2 }}
      alignItems={"center"}
      backgroundColor={theme.palette.grey[100]}
      padding={(2, 2, 0, 2)}
      boxSizing={"border-box"}
      gap={1}
      borderRadius={1}
    >
      <Stack justifyContent={"flex-start"}>
        <ChartTitle>{title}</ChartTitle>
      </Stack>
      <Stack
        sx={{
          position: "absolute",
          transform: "translate(-50%, -50%)",
          top: "52%",
          left: "50%",
          alignItems: "center",
          gap: 1,
        }}
      >
        <Typography sx={{ typography: "contentMRegular", color: "grey.900" }}>
          {mainTitle}
        </Typography>

        <Stack
          alignItems={"center"}
          direction={"row"}
          justifyContent={"center"}
        >
          {totalValue && (
            <Typography
              sx={{
                mr: 0.5,
                typography: "h2",
                color: "grey.900",
              }}
            >
              {totalValue}
            </Typography>
          )}
        </Stack>
        <Stack alignItems={"center"} gap={0.5}>
          <Typography variant="content2xsRegular">submission</Typography>
        </Stack>
      </Stack>

      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            data={transformedData}
            cx="50%"
            cy="50%"
            innerRadius={innerRadius}
            outerRadius={outerRadius}
            dataKey="value"
            cursor={"pointer"}
          >
            {/* Tạo các cell cho biểu đồ tròn */}
            {transformedData.map((entry, index) => (
              <Cell
                key={`cell-${index}`}
                fill={COLORS[index % COLORS.length]}
              />
            ))}
          </Pie>
          <Tooltip />
        </PieChart>
      </ResponsiveContainer>
      <Stack direction="row" gap={2}>
        {transformedData.map((entry, index) => (
          <Legend
            key={`cell-${index}`}
            title={entry.name}
            value={entry.value}
            color={COLORS[index % COLORS.length]}
          />
        ))}
      </Stack>
    </Stack>
  );
};

export default PieCharts;
