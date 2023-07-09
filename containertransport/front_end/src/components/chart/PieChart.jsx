import { blue, deepOrange, indigo, orange } from "@mui/material/colors";
import Highcharts from "highcharts";
import variablePie from "highcharts/modules/variable-pie.js";
import { useEffect, useState } from "react";

const { Box, CircularProgress } = require("@mui/material");
const { HighchartsReact } = require("highcharts-react-official");
variablePie(Highcharts);

const colors = [
  indigo[900],
  deepOrange[500],
  deepOrange[200],
  blue[200],
  orange[100],
  indigo[600],
  deepOrange[300],
  deepOrange[700],
  blue[700],
  orange[800],
];
const PieChart = ({ series: data }) => {
  const [options, setOptions] = useState({
    chart: {
      type: "variablepie",
    },
    title: {
      text: null,
    },
    credits: {
      enabled: false,
    },
    legend: {
      layout: "vertical",
      align: "right",
      verticalAlign: "middle",
      marginTop: "10px",
    },
    accessibility: {
      announceNewData: {
        enabled: true
      },
      point: {
        valueSuffix: '%'
      }
    },
    tooltip: {
      headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
      pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
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
      variablepie: {
        allowPointSelect: true,
        cursor: "pointer",
        dataLabels: {
          enabled: false,
        },
        showInLegend: true,
        borderRadius: 5,
      },
    },
    series: [
      {
        name: 'Browsers',
        colorByPoint: true,
        data: [
          {
            name: '20fit',
            y: 61.04,
            drilldown: 'Chrome'
          },
          {
            name: '40fit',
            y: 38.96,
            drilldown: 'a'
          },
        ]
      }
    ],
  });
  useEffect(() => {
    setOptions({
      ...options,
      series: [
        {
          name: 'Type Container',
          colorByPoint: true,
          data:
            [
              {
                name: '20fit',
                y: 61.04,
                drilldown: 'Chrome'
              },
              {
                name: '40fit',
                y: 38.96,
                drilldown: 'a'
              },
            ]


        }
      ],
    });
  }, [data]);
  return (
    <Box>
      <HighchartsReact highcharts={Highcharts} options={options} />
    </Box>
  );
};
export default PieChart;
