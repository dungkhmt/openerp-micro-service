import {
  Box,
  CardContent,
  Grid,
  Tooltip,
  Typography,
  useTheme,
} from "@mui/material";
import PropTypes from "prop-types";
import { forwardRef } from "react";
import ReactApexcharts from "react-apexcharts";
import { useSelector } from "react-redux";
import { DashboardCard } from "../../../../components/card/DashboardCard";

const TaskCreatedStatistic = forwardRef(function TaskCreatedStatistic(
  { style, className, onMouseDown, onMouseUp, onTouchEnd, children, ...props },
  ref
) {
  const theme = useTheme();
  const { created } = useSelector((state) => state.statistic);

  const chartOptions = {
    stroke: { lineCap: "round" },
    labels: ["In progress"],
    legend: {
      show: false,
      position: "bottom",
      labels: {
        colors: theme.palette.text.secondary,
      },
      markers: {
        offsetX: -3,
      },
      itemMargin: {
        vertical: 3,
        horizontal: 10,
      },
    },
    colors: ["#6AD01F"],
    plotOptions: {
      radialBar: {
        inverseOrder: true,
        hollow: { size: "40%" },
        dataLabels: {
          show: true,
          name: {
            show: true,
            fontSize: "0.6rem",
          },
          value: {
            show: true,
            fontSize: "0.55rem",
            color: theme.palette.text.secondary,
            offsetY: 0,
          },
          total: {
            show: true,
            fontWeight: 400,
            label: "Created",
            fontSize: "0.6rem",
            color: theme.palette.text.primary,
            formatter: function (w) {
              const totalValue =
                w.globals.seriesTotals.reduce((a, b) => {
                  return a + b;
                }, 0) / w.globals.series.length;

              if (totalValue % 1 === 0) {
                return totalValue + "%";
              } else {
                return totalValue.toFixed(2) + "%";
              }
            },
          },
        },
      },
    },
    grid: {
      padding: {
        top: -20,
      },
    },
  };

  return (
    <DashboardCard
      ref={ref}
      {...props}
      style={style}
      className={className}
      onMouseDown={onMouseDown}
      onMouseUp={onMouseUp}
      onTouchEnd={onTouchEnd}
      title="Tạo mới"
    >
      <CardContent>
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <Grid container spacing={0}>
            <Grid item sx={6} md={6} lg={5} xl={4}>
              <Tooltip title="Chi tiết">
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "flex-start",
                    flexDirection: "column",
                    cursor: "pointer",
                    borderRadius: "5px",
                    padding: (theme) => theme.spacing(0),
                    "&:hover": {
                      backgroundColor: "#f7f7f7",
                    },
                    flex: 1,
                  }}
                >
                  <Typography variant="h3">{created.count}</Typography>
                  <Typography variant="subtitle2" sx={{ fontSize: "0.7rem" }}>
                    nhiệm vụ
                  </Typography>
                </Box>
              </Tooltip>
            </Grid>
            <Grid item xs={6} md={6} lg={7} xl={8} sx={{ mt: 2 }}>
              <Tooltip title={`${created.percentage}%`}>
                <div style={{ minWidth: "120px" }}>
                  <ReactApexcharts
                    type="radialBar"
                    height={120}
                    options={chartOptions}
                    series={[created.percentage]}
                  />
                </div>
              </Tooltip>
            </Grid>
          </Grid>
        </Box>
      </CardContent>
      {children}
    </DashboardCard>
  );
});

TaskCreatedStatistic.propTypes = {
  style: PropTypes.object,
  className: PropTypes.string,
  onMouseDown: PropTypes.func,
  onMouseUp: PropTypes.func,
  onTouchEnd: PropTypes.func,
  children: PropTypes.node,
};

export { TaskCreatedStatistic };
