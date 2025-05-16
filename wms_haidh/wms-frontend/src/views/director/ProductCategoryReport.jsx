import React, { useEffect, useState } from 'react';
import {
  Box, Grid, MenuItem, Select, Typography,
  Table, TableBody, TableCell, TableHead, TableRow
} from '@mui/material';
import { PieChart, Pie, Cell, Tooltip, Legend } from 'recharts';
import { request } from "../../api";

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#A28CFE'];

const ProductCategoryReport = () => {
  const [months, setMonths] = useState([]);
  const [selectedMonth, setSelectedMonth] = useState('');
  const [selectedPoints, setSelectedPoints] = useState([]);
  const [monthlyProfit, setMonthlyProfit] = useState('');
  const [totalProfit, setTotalProfit] = useState([]);

  useEffect(() => {
    request("get", `/reports/monthly-profit`, (res) => {
      setTotalProfit(res.data);
      const monthList = res.data.map(p => p.date);
      setMonths(monthList);
      if (monthList.length > 0) {
        setSelectedMonth(monthList[0]);
      }
    });
  }, []);


  useEffect(() => {
    if (selectedMonth) {
      request("get", `/reports/monthly-profit-by-category?month=${selectedMonth}`, (res) => {
        const raw = res.data;

        const profitEntry = totalProfit.find(p => p.date === selectedMonth);
        const total = profitEntry?.profit || 0;

        const dataWithValue = raw.map(item => {
          const percentage = total > 0 ? (item.profit / total * 100) : 0;
          return {
            ...item,
            value: parseFloat(percentage.toFixed(2))
          };
        });

        let points = [];
        if (dataWithValue.length <= 4) {
          points = dataWithValue;
        } else {
          const top4 = dataWithValue.slice(0, 4);
          const others = dataWithValue.slice(4);

          const totalOtherProfit = others.reduce((sum, item) => sum + item.profit, 0);
          const otherPercentage = total > 0 ? (totalOtherProfit / total * 100) : 0;

          points = [
            ...top4,
            {
              label: "Other",
              profit: totalOtherProfit,
              value: parseFloat(otherPercentage.toFixed(2))
            }
          ];
        }

        setSelectedPoints(points);
        setMonthlyProfit(profitEntry?.profit.toLocaleString("en-US") || '0');
      });
    }
  }, [selectedMonth, totalProfit]);




  const renderTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <Box p={1} sx={{ bgcolor: 'white', border: '1px solid #ccc' }}>
          <Typography variant="body2"><strong>{data.label}</strong></Typography>
          <Typography variant="body2">Percentage: {data.value}%</Typography>
          <Typography variant="body2">
            Profit: {data.profit.toLocaleString("en-US")} VND
          </Typography>
        </Box>
      );
    }
    return null;
  };


  return (
    <Box p={4}>
      <Grid container spacing={2}>
        <Grid item xs={12} sm={3}>
          <Select
            value={selectedMonth}
            onChange={(e) => setSelectedMonth(e.target.value)}
            disabled={months.length === 0}
          >
            {months.map(month => (
              <MenuItem key={month} value={month}>
                {month}
              </MenuItem>
            ))}
          </Select>

        </Grid>

        <Grid item xs={12} sm={9}>
          <Box display="flex" justifyContent="flex-start" alignItems="center" height="100%">
            <Typography variant="h6">Total Profit: {monthlyProfit} VND</Typography>
          </Box>
        </Grid>

        <Grid item xs={12}>
          <Grid container spacing={4} justifyContent="center" alignItems="flex-start">
            <Grid item>
              <PieChart width={400} height={350}>
                <Pie
                  data={selectedPoints}
                  dataKey="value"
                  nameKey="label"
                  cx="50%"
                  cy="50%"
                  outerRadius={100}
                  label={({ percent }) => `${(percent * 100).toFixed(0)}%`}
                >
                  {selectedPoints.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip content={renderTooltip} />
                <Legend />
              </PieChart>
            </Grid>
            <Grid item sx={{ minWidth: 300 }}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Category</TableCell>
                    <TableCell>Percentage (%)</TableCell>
                    <TableCell>Profit (VND)</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {selectedPoints.map((p, i) => (
                    <TableRow key={i}>
                      <TableCell>{p.label}</TableCell>
                      <TableCell>{p.value}</TableCell>
                      <TableCell>{p.profit.toLocaleString("en-US")}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </Box>
  );
};

export default ProductCategoryReport;
