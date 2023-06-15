import { Box, CircularProgress } from "@mui/material";
import Highcharts from "highcharts";
import { HighchartsReact } from "highcharts-react-official";
import { useEffect, useState } from "react";
import { useMeasure } from "react-use";

const colors = ["#F58E6A", "#FFCC7E", "#293977", "#F9FBFC", "#FFDBCC"];

const ColumnChart = (props) => {
  const {
    categories,
    series,
    legend = false,
    grouping = true,
    onClickColumn,
    loading,
  } = props;
  const [ref, { x, y, width, height, top, right, bottom, left }] = useMeasure();
  const [options, setOptions] = useState({
    chart: {
      type: "column",
      height: 300,
      scrollablePlotArea: {
        //opacity: 1,
        minWidth: 1415,
        scrollPositionX: 1,
      },
    },
    title: {
      text: null,
    },
    subtitle: {
      text: null,
    },
    credits: {
      enabled: false,
    },
    legend: {
      enabled: legend,
    },
    xAxis: {
      categories: [],
      tickmarkPlacement: "on",
      title: {
        enabled: false,
      },
      startOnTick: true,
      labels: {
        rotation: 0,
      },
    },
    yAxis: {
      title: {
        text: null,
      },
      tickAmount: 6,
    },
    // tooltip: {
    //   headerFormat: "<div>",
    //   backgroundColor: "#333333",
    //   borderColor: "#333333",
    //   footerFormat: "</div>",
    //   useHTML: true,
    //   formatter: function () {
    //     const str = `<p style="color:{series.color};padding:4px 2px; color: #ffffff; font-size: 16px; font-weight: 600">${this.y}</p>`;
    //     return str;
    //   },
    // },
    plotOptions: {
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
      column: {
        pointWidth: 30,
        grouping,
        //shadow: false,
        borderWidth: 0,
      },
    },
    series: [],
    // rules: [{
    //   condition: {
    //     maxWidth:
    //   }
    // }]
  });

  useEffect(() => {
    setOptions({
      ...options,
      chart: {
        ...options.chart,
        scrollablePlotArea: {
          minWidth: categories?.length * 40,
          scrollPositionX: 1,
        },
      },
      xAxis: {
        ...options.xAxis,
        categories,
      },
      series: series.map((el, id) => ({
        color: colors[id],
        ...el,
      })),
      plotOptions: {
        ...options.plotOptions,
        series: {
          ...options.plotOptions.series,
          point: {
            events: {
              // click: function () {
              //   onClickColumn ? onClickColumn(this) : undefined;
              // },
            },
          },
        },
      },
    });
  }, [categories, series]);
  return (
    <Box ref={ref} sx={{ width: "100%", position: "relative" }}>
      {loading ? (
        <Box
          sx={{
            // height: "100%",
            // width: "100%",
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            zIndex: 1,
          }}
          //className="bg-stone-600 flex flex-row items-center justify-between "
        >
          <CircularProgress />
        </Box>
      ) : null}

      <Box sx={{ maxWidth: width }}>
        <HighchartsReact highcharts={Highcharts} options={options} />
      </Box>
    </Box>
  );
};

export default ColumnChart;
