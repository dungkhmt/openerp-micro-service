import { forwardRef } from "react";
import PropTypes from "prop-types";
import ReactApexcharts from "react-apexcharts";
import { DashboardCard } from "../../../../components/card/DashboardCard";
import { Box, CardContent, useTheme } from "@mui/material";

const statusColors = {
  active: "#FFCA64",
  inactive: "#9C9FA4",
  closed: "#FF6166",
  open: "#6AD01F",
  inprogress: "#9E69FD",
  resolved: "#1986C2",
};

const WorkloadByStatus = forwardRef(function WorkloadByStatus(
  { style, className, onMouseDown, onMouseUp, onTouchEnd, children, ...props },
  ref
) {
  const theme = useTheme();

  const options = {
    stroke: { width: 0 },
    labels: [
      "Đang kích hoạt",
      "Không hoạt động",
      "Đóng",
      "Đang xử lý",
      "Mở",
      "Đã xử lý",
    ],
    colors: [
      statusColors.active,
      statusColors.inactive,
      statusColors.closed,
      statusColors.inprogress,
      statusColors.open,
      statusColors.resolved,
    ],
    dataLabels: {
      enabled: true,
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
              formatter: () => "21%",
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
      title="Khối lượng công việc theo trạng thái"
    >
      <CardContent>
        <Box sx={{ display: "flex", justifyContent: "center", width: "100%" }}>
          <ReactApexcharts
            type="donut"
            options={options}
            series={[10, 16, 50, 50, 50, 50]}
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
