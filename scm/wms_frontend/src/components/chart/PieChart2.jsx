import Highcharts from "highcharts";
import HC_more from "highcharts/highcharts-more";
import { useRef, useState } from "react";

import highcharts3d from "highcharts/highcharts-3d";
import drilldown from "highcharts/modules/drilldown.js";

import { Box, CircularProgress } from "@mui/material";
import { HighchartsReact } from "highcharts-react-official";
let series = [
  {
    name: "Browsers",
    colorByPoint: true,
    data: [
      {
        name: "Chrome",
        y: 62.74,
        drilldown: "Chrome",
      },
      {
        name: "FirefoxGuest",
        y: 29.57,
        drilldown: "FirefoxGuest",
      },
      {
        name: "Internet Explorer",
        y: 7.23,
        drilldown: "Internet Explorer",
      },
      {
        name: "Safari",
        y: 5.58,
        drilldown: "Safari",
      },
      {
        name: "Edge",
        y: 4.02,
        drilldown: "Edge",
      },
      {
        name: "Opera",
        y: 1.92,
        drilldown: "Opera",
      },
      {
        name: "Other",
        y: 7.62,
        drilldown: null,
      },
    ],
  },
];

HC_more(Highcharts);
highcharts3d(Highcharts);
drilldown(Highcharts);

function PieChart2({ series, loading }) {
  let chart = null;

  var offset = 180;
  const [angle, setAngle] = useState(offset);

  var sample_options = {
    credits: { enabled: false },
    chart: {
      type: "pie",
      backgroundColor: "rgba(0,0,0,0)",
      //backgroundColor: 'black',
      margin: [0, 0, 0, 15],
      spacingTop: 0,
      spacingBottom: 0,
      spacingLeft: 0,
      spacingRight: 0,
      options3d: {
        enabled: true,
        alpha: 65,
        beta: 0,
      },
    },
    title: { text: undefined },
    accessibility: {
      point: {
        valueSuffix: "%",
      },
    },
    tooltip: {
      pointFormat: "{series.name}: <b>{point.percentage:.1f}%</b>",
    },
    plotOptions: {
      pie: {
        // allowPointSelect: true,
        // todo: don't think I'm seeing this shadow here
        shadow: false,
        startAngle: angle,
        cursor: "pointer",
        depth: 55,
        size: "120%",
      },
      series: {
        allowPointSelect: true,
        dataLabels: {
          enabled: false,
          format: "{point.name}",
        },
        point: {
          events: {
            select: function () {
              console.log("chart event", chart);
            },
          },
        },
      },
    },
    series: series,
  };

  const elementRef = useRef();

  return (
    <div>
      <Box>
        {loading ? (
          <CircularProgress />
        ) : (
          <HighchartsReact
            ref={elementRef}
            highcharts={Highcharts}
            allowChartUpdate={true}
            options={sample_options}
          />
        )}
      </Box>
    </div>
  );
}
export default PieChart2;
// https://codesandbox.io/s/mrfirouz-3d-pie-chart-0gm4h?file=/src/BrowserData.js:0-6569
