import Highcharts from "highcharts";
import { HighchartsReact } from "highcharts-react-official";
import _ from "lodash";
import { useState } from "react";
import { getRandomIntInclusive } from "../../layout/notification/Notification";
const raw = Array.from(
  Array(10),
  (el, i) => `${_.padStart(String(i + 1), 2, "0")}/12`
);

const AreaChart = ({ color }) => {
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
        name: "USA",
        data: [...raw.map(() => getRandomIntInclusive(1000, 3000))],
        color,
      },
    ],
  });
  return (
    <div>
      <HighchartsReact highcharts={Highcharts} options={options} />
    </div>
  );
};

export default AreaChart;
