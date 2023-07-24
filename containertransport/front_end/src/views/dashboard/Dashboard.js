import { Box, Container, FormControl, InputLabel, MenuItem, Select, Typography } from "@mui/material";
import LineChart from "components/chart/LineChart";
import React, { useEffect, useState } from "react";
import './styles.scss';
import PieChart from "components/chart/PieChart";

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
    const seriesOrder = [{
        name: 'New',
        data: [10, 15, 50, 40, 35, 60, 25, 46, 22, 17, 12, 76]

    },{
        name: 'Done',
        data: [25, 76, 33, 59, 95, 35, 105, 120, 100, 81, 47, 21]
    }];

    const seriesTypeContainer = [
        {
          name: 'Type Container',
          colorByPoint: true,
          data: [
            {
              name: '20ft',
              y: 61.04,
              drilldown: '20ft'
            },
            {
              name: '40ft',
              y: 38.96,
              drilldown: '40ft'
            },
          ]
        }
      ];
      const seriesTruck = [
        {
          name: 'Truck',
          colorByPoint: true,
          data: [
            {
              name: 'Using',
              y: 51.04,
              drilldown: 'Using'
            },
            {
              name: 'Not used',
              y: 48.96,
              drilldown: 'Not used'
            },
          ]
        }
      ];
      const seriesTrailer = [
        {
          name: 'Trailer',
          colorByPoint: true,
          data: [
            {
              name: 'Using',
              y: 41.04,
              drilldown: 'Using'
            },
            {
              name: 'Not used',
              y: 58.96,
              drilldown: 'Not used'
            },
          ]
        }
      ];
    const year = (new Date()).getFullYear();
    const [yearSelect, setYearSelect] = useState(year);
    const [years, setYears] = useState(Array.from(new Array(10), (val, index) => year - index));
    console.log("years", years)
    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
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
                            <PieChart series={seriesTypeContainer}/>
                        </Box>
                    </Box>
                    <Box className="truck">
                        <Box className="title">
                            <Typography>Truck usage rate</Typography>
                        </Box>
                        <Box className="p-chart">
                            <PieChart series={seriesTruck}/>
                        </Box>
                    </Box>
                    <Box className="trailer">
                        <Box className="title">
                            <Typography>Trailer usage rate</Typography>
                        </Box>
                        <Box className="p-chart">
                            <PieChart series={seriesTrailer}/>
                        </Box>
                    </Box>
                </Box>

            </Container>
        </Box>
    )
}
export default Dashboard;