import Highcharts from "highcharts";
import { useEffect, useState } from "react";
const { Box, CircularProgress } = require("@mui/material");
const { HighchartsReact } = require("highcharts-react-official");
const BarChart = ({ categories, series, loading }) => {
  const [options, setOptions] = useState({
    chart: {
      type: "bar",
    },
    title: {
      text: null,
      // text: "Title",
    },
    subtitle: {
      // text: "Subtitle",
    },
    xAxis: {
      categories: [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
      ],
      title: {
        text: "Month",
      },
    },
    yAxis: {
      series,
      title: {
        text: "Monthly figures",
      },
    },
    plotOptions: {
      bar: {
        // dataLabels: {
        //   enabled: true,
        // },
      },
      series: {
        allowPointSelect: true,
        states: {
          inactive: {
            opacity: 1,
          },
          hover: {
            brightness: -0.15,
          },
        },
        point: {},
      },
    },
    credits: {
      enabled: false,
    },
    series,
  });

  useEffect(() => {
    setOptions({
      ...options,
      series,
      xAxis: {
        ...options.xAxis,
      },
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
export default BarChart;
