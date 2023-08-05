import { Box, Container, FormControl, InputLabel, MenuItem, Select, Typography } from "@mui/material";
import LineChart from "components/chart/LineChart";
import React, { useEffect, useState } from "react";
import './styles.scss';
import PieChart from "components/chart/PieChart";
import { getOrderByMonth, getRateTrailer, getRateTruck, getRateUseContainer } from "api/DashboardAPI";

const Dashboard = () => {
  const categories = [
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
  ];

  // const seriesTypeContainer = [
  //   {
  //     name: 'Type Container',
  //     colorByPoint: true,
  //     data: [
  //       {
  //         name: '20ft',
  //         y: 61.04,
  //         drilldown: '20ft'
  //       },
  //       {
  //         name: '40ft',
  //         y: 38.96,
  //         drilldown: '40ft'
  //       },
  //     ]
  //   }
  // ];

  const year = (new Date()).getFullYear();
  const [yearSelect, setYearSelect] = useState(year);
  const [years, setYears] = useState(Array.from(new Array(10), (val, index) => year - index));
  const [seriesTruck, setSeriesTruck] = useState([]);
  const [seriesTrailer, setSeriesTrailer] = useState([]);
  const [seriesOrder, setSeriesOrder] = useState([]);
  const [seriesTypeContainer, setSeriesTypeContainer] = useState([]);

  useEffect(() => {
    getRateTruck().then((res) => {
      let dataSeri = {
        name: 'Truck',
        colorByPoint: true,
        data: res?.data.data.rateUseTruck
      }
      setSeriesTruck([dataSeri])
    });
    getRateTrailer().then((res) => {
      let dataSeri = {
        name: 'Truck',
        colorByPoint: true,
        data: res?.data.data.rateUseTrailer
      }
      setSeriesTrailer([dataSeri])
    });
    getOrderByMonth(yearSelect).then((res) => {
      let dataList = res?.data.data.orderByMonth;
      let result = [];
      let dataNew = {
        name: "New",
        data: dataList?.New
      }
      result.push(dataNew);
      let dataDone = {
        name: "Done",
        data: dataList?.Done
      }
      console.log("result", dataList)
      result.push(dataDone);
      setSeriesOrder(result);
    });

    getRateUseContainer().then((res) => {
      let dataSeri = {
        name: 'Type Container',
        colorByPoint: true,
        data: res?.data.data.rateUseTypeContainer
      }
      setSeriesTypeContainer([dataSeri]);
    })
    }, [yearSelect])
    console.log("years", yearSelect)
    return (
      <Box className="fullScreen">
        <Container maxWidth='100vw' className="container">
          <Box className="line-chart">
            <Box className="title">
              <Typography>Number of orders by month</Typography>
            </Box>
            <Box className="year">
              <Box className="text">
                <Typography>Năm:</Typography>
              </Box>
              <FormControl className="select">
                <Select
                  value={yearSelect}
                  onChange={(e) => setYearSelect(e.target.value)}
                  InputLabelProps={{ shrink: false }}
                  size="small"
                >
                  {years ? (
                    years.map((item, key) => {
                      return (
                        <MenuItem key={key} value={item}>{item}</MenuItem>
                      );
                    })
                  ) : null}
                </Select>
              </FormControl>
            </Box>

            <Box className="chart">
              <LineChart categories={categories} series={seriesOrder} />
            </Box>
          </Box>
          <Box className="pie-chart">
            <Box className="container-type">
              <Box className="title">
                <Typography>Usage rate of each container type</Typography>
              </Box>
              <Box className="p-chart">
                <PieChart series={seriesTypeContainer} />
              </Box>
            </Box>
            <Box className="truck">
              <Box className="title">
                <Typography>Truck usage rate</Typography>
              </Box>
              <Box className="p-chart">
                <PieChart series={seriesTruck} />
              </Box>
            </Box>
            <Box className="trailer">
              <Box className="title">
                <Typography>Trailer usage rate</Typography>
              </Box>
              <Box className="p-chart">
                <PieChart series={seriesTrailer} />
              </Box>
            </Box>
          </Box>

        </Container>
      </Box>
    )
  }
export default Dashboard;