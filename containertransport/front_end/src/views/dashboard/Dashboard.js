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
    const series = [{
        name: 'New',
        // marker: {
        //     symbol: 'square'
        // },
        data: [10, 15, 50, 40, 35, 60, 25, 46,
            //     {
            //     y: 46,
            //     marker: {
            //         symbol: 'url(https://www.highcharts.com/samples/graphics/sun.png)'
            //     },
            //     accessibility: {
            //         description: 'Sunny symbol, this is the warmest point in the chart.'
            //     }
            // },
            22, 17, 12, 76]

    }, {
        name: 'Processing',
        // marker: {
        //     symbol: 'diamond'
        // },
        data: [15,
            //     {
            //     y: 15,
            //     marker: {
            //         symbol: 'url(https://www.highcharts.com/samples/graphics/snow.png)'
            //     },
            //     accessibility: {
            //         description: 'Snowy symbol, this is the coldest point in the chart.'
            //     }
            // }, 
            26, 63, 59, 105, 135, 145, 44, 85, 57, 47, 2.6]
    }, {
        name: 'Done',
        // marker: {
        //     symbol: 'diamond'
        // },
        data: [25,
            //     {
            //     y: 25,
            //     marker: {
            //         symbol: 'url(https://www.highcharts.com/samples/graphics/snow.png)'
            //     },
            //     accessibility: {
            //         description: 'Snowy symbol, this is the coldest point in the chart.'
            //     }
            // }
            76, 33, 59, 95, 35, 105, 120, 100, 81, 47, 21]
    }];
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
                                InputLabelProps={{shrink: false}}
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
                        <LineChart categories={categories} series={series} />
                    </Box>
                </Box>
                <Box className="pie-chart">
                    <Box className="title">
                        <Typography>Usage rate of each container type</Typography>
                    </Box>
                    <Box className="p-chart">
                        <PieChart />
                    </Box>
                </Box>
            </Container>
        </Box>
    )
}
export default Dashboard;