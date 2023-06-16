import { blue, deepOrange, indigo, orange } from "@mui/material/colors";
import Highcharts from "highcharts";
import variablePie from "highcharts/modules/variable-pie.js";
import { useEffect, useState } from "react";

const { Box, CircularProgress } = require("@mui/material");
const { HighchartsReact } = require("highcharts-react-official");
variablePie(Highcharts);
// borderRadius(Highcharts);

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
const PieChart = ({ categories, series: data, loading }) => {
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
    tooltip: {
      // useHTML: true,
      // borderWidth: 0,
      // backgroundColor: null,
      // borderColor: null,
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
        size: 200,
        borderWidth: 4,
        minPointSize: 10,
        innerSize: "80%",
        zMin: 0,
        data: data
          ? data.map((el, id) => ({
              name: el[0],
              color: colors[id],
              y: el[1],
            }))
          : [
              {
                name: "Spain",
                y: 505992,
                z: 20,
                color: "#293977",
              },
              {
                name: "France",
                y: 551695,
                z: 20,
                color: "#F58E6A",
              },
              {
                name: "Poland",
                y: 312679,
                z: 20,
                color: "#FFCC7E",
              },
            ],
      },
    ],
  });
  useEffect(() => {
    setOptions({
      ...options,
      series: [
        {
          size: 200,
          borderWidth: 4,
          minPointSize: 10,
          innerSize: "80%",
          zMin: 0,
          data: data
            ? data.map((el, id) => ({
                name: el[0],
                z: 20,
                color: colors[id],
                y: el[1],
              }))
            : [
                {
                  name: "Spain",
                  y: 505992,
                  z: 20,
                  color: "#293977",
                },
                {
                  name: "France",
                  y: 551695,
                  z: 20,
                  color: "#F58E6A",
                },
                {
                  name: "Poland",
                  y: 312679,
                  z: 20,
                  color: "#FFCC7E",
                },
              ],
        },
      ],
    });
  }, [data]);
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
export default PieChart;
