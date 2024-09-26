import { forwardRef } from "react";
import PropTypes from "prop-types";
import ReactApexcharts from "react-apexcharts";
import { DashboardCard } from "../../../../components/card/DashboardCard";
import { Box, CardContent, useTheme } from "@mui/material";
import { useSelector } from "react-redux";
import { useMemo } from "react";

const statusColors = {
  active: "#FFCA64",
  inactive: "#9C9FA4",
  closed: "#FF6166",
  open: "#6AD01F",
  resolved: "#9E69FD",
  inprogress: "#1986C2",
};

// ! Hard code status id here
const statuses = {
  ASSIGNMENT_ACTIVE: {
    label: "Đang kích hoạt",
    color: statusColors.active,
  },
  ASSIGNMENT_INACTIVE: {
    label: "Không hoạt động",
    color: statusColors.inactive,
  },
  TASK_CLOSED: {
    label: "Đóng",
    color: statusColors.closed,
  },
  TASK_INPROGRESS: {
    label: "Đang xử lý",
    color: statusColors.inprogress,
  },
  TASK_OPEN: {
    label: "Mở",
    color: statusColors.open,
  },
  TASK_RESOLVED: {
    label: "Đã xử lý",
    color: statusColors.resolved,
  },
};

const WorkloadByStatus = forwardRef(function WorkloadByStatus(
  { style, className, onMouseDown, onMouseUp, onTouchEnd, children, ...props },
  ref
) {
  const theme = useTheme();
  const { workloadByStatus: data } = useSelector((state) => state.statistic);

  const series = data.load.map((i) => i.count ?? 0);
  const allZero = series.every((val) => val === 0);

  /**
   * @type {import("apexcharts").ApexOptions}
   */
  const options = useMemo(
    () => ({
      stroke: { width: 0 },
      labels: allZero
        ? ["Không có dữ liệu"]
        : data.load.map((i) => statuses[i.status]?.label),
      colors: allZero
        ? ["#dfdfdf"]
        : data.load.map((i) => statuses[i.status]?.color),
      dataLabels: {
        enabled: !allZero,
        formatter: (val) => `${parseInt(val, 10)}%`,
      },
      legend: {
        position: "bottom",
        markers: { offsetX: -3 },
        labels: { colors: theme.palette.text.secondary },
        itemMargin: {
          vertical: 3,
          horizontal: 10,
        },
      },
      plotOptions: {
        pie: {
          donut: {
            labels: {
              show: true,
              name: {
                fontSize: "1rem",
              },
              value: {
                fontSize: "1rem",
                color: theme.palette.text.secondary,
                formatter: (val) => `${parseInt(val, 10)}`,
              },
              total: {
                show: true,
                fontSize: "1rem",
                label: "Đã xử lý",
                formatter: () =>
                  allZero
                    ? `0%`
                    : `${Math.floor(
                        ((data.load.find((i) => i.status === "TASK_RESOLVED")
                          ?.count || 0) /
                          data.load.reduce((acc, cur) => acc + cur.count, 0)) *
                          100
                      )}%`,
                color: theme.palette.text.primary,
              },
            },
          },
        },
      },
      responsive: [
        {
          breakpoint: 992,
          options: {
            chart: {
              height: 380,
            },
            legend: {
              position: "bottom",
            },
          },
        },
        {
          breakpoint: 576,
          options: {
            chart: {
              height: 320,
            },
            plotOptions: {
              pie: {
                donut: {
                  labels: {
                    show: true,
                    name: {
                      fontSize: "1rem",
                    },
                    value: {
                      fontSize: "1rem",
                    },
                    total: {
                      fontSize: "1rem",
                    },
                  },
                },
              },
            },
          },
        },
      ],
    }),
    [data.load, theme]
  );

  return (
    <DashboardCard
      ref={ref}
      {...props}
      style={style}
      className={className}
      onMouseDown={onMouseDown}
      onMouseUp={onMouseUp}
      onTouchEnd={onTouchEnd}
      title="Khối lượng công việc theo trạng thái"
    >
      <CardContent>
        <Box sx={{ display: "flex", justifyContent: "center", width: "100%" }}>
          <ReactApexcharts
            type="donut"
            options={options}
            series={allZero ? [1] : series}
            height={300}
          />
        </Box>
      </CardContent>
      {children}
    </DashboardCard>
  );
});

WorkloadByStatus.propTypes = {
  children: PropTypes.node,
  className: PropTypes.string,
  style: PropTypes.object,
  onMouseDown: PropTypes.func,
  onMouseUp: PropTypes.func,
  onTouchEnd: PropTypes.func,
};

export { WorkloadByStatus };
