import { Box, CircularProgress } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { HighchartsReact } from "highcharts-react-official";
import Highcharts from "highcharts/highmaps";
import { useEffect, useState } from "react";
const MapChart = ({ data: addedData, loading }) => {
  const { data: topology, isLoading } = useQuery(["get-map"], () =>
    fetch("https://code.highcharts.com/mapdata/countries/vn/vn-all.topo.json", {
      method: "get",
    }).then((res) => res.json())
  );
  const [options, setOptions] = useState({
    chart: {
      map: topology,
    },

    title: {
      text: null,
    },
    credits: {
      enabled: false,
    },
    subtitle: {
      text: null,
    },

    mapNavigation: {
      enabled: true,
      buttonOptions: {
        verticalAlign: "bottom",
      },
    },

    colorAxis: {
      min: 0,
    },
    series: [
      {
        data: addedData.map((el) => [el[0], el[1]]),
        name: "Random data",
        states: {
          hover: {
            color: "#293977",
          },
        },
        dataLabels: {
          enabled: true,
          format: "{point.name}",
        },
        mapData: topology,
      },
    ],
  });
  useEffect(() => {
    setOptions({
      ...options,
      chart: {
        map: topology,
      },
      series: [
        {
          data: addedData.map((el) => [el[0], el[1]]),
          name: "Random data",
          states: {
            hover: {
              color: "#293977",
            },
          },
          dataLabels: {
            enabled: true,
            format: "{point.name}",
          },
          mapData: topology,
        },
      ],
    });
  }, [topology, isLoading, addedData]);
  return (
    <Box>
      {loading ? (
        <CircularProgress />
      ) : (
        <div>
          <HighchartsReact
            constructorType={"mapChart"}
            highcharts={Highcharts}
            options={options}
          />
        </div>
      )}
    </Box>
  );
};

export default MapChart;
