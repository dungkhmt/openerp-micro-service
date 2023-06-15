import { Box, CircularProgress } from "@mui/material";
import Highcharts from "highcharts";
import { HighchartsReact } from "highcharts-react-official";
import HighchartsStreamgraph from "highcharts/modules/streamgraph";
import { useState } from "react";
import { getRandomIntInclusive } from "../../layout/notification/Notification";
HighchartsStreamgraph(Highcharts);

const colors = ["#F58E6A", "#FFCC7E", "#293977", "#F9FBFC", "#FFDBCC"];

const categories = [
  "01/12",
  "02/12",
  "03/12",
  "04/12",
  "05/12",
  "06/12",
  "07/12",
];

const Streamgraph = ({ categories, series: data, loading }) => {
  const [options, setOptions] = useState({
    chart: {
      type: "streamgraph",
      zoomType: "x",
    },
    colors,
    credits: {
      enabled: false,
    },
    title: {
      text: null,
    },
    xAxis: {
      categories,
    },
    yAxis: {
      visible: false,
      startOnTick: false,
      endOnTick: false,
    },

    legend: {
      enabled: false,
    },
    series: [
      {
        name: "Bến tre",
        data: categories.map((el) => getRandomIntInclusive(10000, 100000)),
      },
      {
        name: "Đồng Tháp",
        data: categories.map((el) => getRandomIntInclusive(10000, 100000)),
      },
      {
        name: "Bạc Liêu",
        data: categories.map((el) => getRandomIntInclusive(10000, 100000)),
      },
      {
        name: "Sóc Trăng",
        data: categories.map((el) => getRandomIntInclusive(10000, 100000)),
      },
      {
        name: "Cà Mau",
        data: categories.map((el) => getRandomIntInclusive(10000, 100000)),
      },
    ],
  });
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

export default Streamgraph;
