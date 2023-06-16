import { Box, CircularProgress } from "@mui/material";
import Highcharts from "highcharts";
import { HighchartsReact } from "highcharts-react-official";
import { useEffect, useState } from "react";

const colors = ["#F58E6A", "#FFCC7E", "#293977", "#FFDBCC", "#F9FBFC"];

const LineChart = ({ categories, series, loading }) => {
  const [options, setOptions] = useState({
    chart: {
      type: "spline",
      scrollablePlotArea: {
        opacity: 1,
        minWidth: categories.length * 50,
        scrollPositionX: 0,
      },
    },
    credits: {
      enabled: false,
    },
    title: {
      text: null,
    },
    yAxis: {
      title: {
        text: null,
      },
    },
    plotOptions: {
      series: {
        states: {
          inactive: {
            opacity: 1,
          },
          hover: {
            halo: {
              opacity: 0,
            },
          },
        },
      },
    },
    xAxis: {
      categories,
    },
    series: series.map((el, id) => ({
      ...el,
      color: colors[id],
    })),
    legend: {
      enabled: series.length === 1 ? false : true,
    },
  });

  useEffect(() => {
    setOptions({
      ...options,
      xAxis: {
        categories,
      },
      series: series.map((el, id) => ({
        ...el,
        color: colors[id],
      })),
    });
  }, [categories, series]);
  return (
    <Box>
      {loading ? (
        <CircularProgress />
      ) : (
        <HighchartsReact highcharts={Highcharts} options={options} />
      )}
    </Box>
  );
};

export default LineChart;
