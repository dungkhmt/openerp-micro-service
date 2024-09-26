import { Box, CircularProgress } from "@mui/material";
import Highcharts from "highcharts";
import { HighchartsReact } from "highcharts-react-official";
import _ from "lodash";
import { useEffect, useState } from "react";
const raw = Array.from(
  Array(12),
  (el, i) => `${_.padStart(String(i + 1), 2, "0")}/12`
);

const AreaChart = ({ color, data, loading }) => {
  const [options, setOptions] = useState({
    chart: {
      type: "area",
      width: 400,
      height: 400,
    },
    title: {
      text: null,
    },
    legend: {
      enabled: false,
    },
    subtitle: {
      text: null,
    },
    xAxis: {
      tickmarkPlacement: "on",
      title: {
        enabled: false,
      },
      startOnTick: true,
      visible: false,
      categories: raw,
    },
    yAxis: {
      visible: false,
      title: {
        text: null,
      },
      //   labels: {
      //     formatter: function () {
      //       return this.value / 1000 + "k";
      //     },
      //   },
    },
    credits: {
      enabled: false,
    },
    plotOptions: {
      area: {
        marker: {
          enabled: false,
          symbol: "circle",
          radius: 2,
          states: {
            hover: {
              enabled: true,
            },
          },
        },
        fillColor: {
          linearGradient: {
            x1: 0,
            y1: 0,
            x2: 0,
            y2: 1,
          },
          stops: [
            [0, Highcharts.color(color).setOpacity(0.3).get("rgba")],
            [1, Highcharts.color(color).setOpacity(0).get("rgba")],
          ],
        },
      },
      lineWidth: 6,
    },
    series: [
      {
        name: "Sales",
        data: data,
        color,
      },
    ],
  });
  useEffect(() => {
    setOptions({
      ...options,
      chart: {
        type: "area",
        width: 400,
        height: 400,
      },
      series: [
        {
          name: "Sales",
          data: data,
          color,
        },
      ],
    });
  }, [data, options]);
  return (
    <Box>
      {loading ? (
        <CircularProgress />
      ) : (
        <div>
          <HighchartsReact highcharts={Highcharts} options={options} />
        </div>
      )}
    </Box>
  );
};

export default AreaChart;
