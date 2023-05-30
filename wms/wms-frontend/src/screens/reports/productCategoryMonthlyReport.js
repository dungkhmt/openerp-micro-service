import React, { useEffect, useState } from 'react';
import CanvasJSReact from '@canvasjs/react-charts';
import { request } from 'api';
import { API_PATH } from 'screens/apiPaths';
import { Box, Grid, MenuItem, Select } from '@mui/material';
import { convertToVNDFormat } from 'screens/utils/utils';

var CanvasJS = CanvasJSReact.CanvasJS;
var CanvasJSChart = CanvasJSReact.CanvasJSChart;

const ProductCategoryMonthlyReport = () => {
  const [points, setPoints] = useState([]);
  const [months, setMonths] = useState([]);
  const [selectedMonth, setSelectedMonth] = useState(null);
  const [selectedPoints, setSelectedPoints] = useState([]);

  useEffect(() => {
    request(
      'get',
      API_PATH.PRODUCT_CATEGORY_MONTHLY_REPORT,
      (res) => {
        var resPoints = res?.data?.points;
        for (const month in resPoints) {
          for (var i = 0; i < resPoints[month].length; i++) {
            resPoints[month][i].profit = convertToVNDFormat(resPoints[month][i].profit);
          }
        }
        console.log("Res points => ", resPoints);
        setPoints(resPoints);
        setMonths(res?.data?.months);
        if (res?.data?.months?.length > 0) {
          setSelectedMonth(res?.data?.months[0]);
        }
      }
    )
  }, []);

  useEffect(() => {
    for (const month in points) {
      console.log("Points => ", points, "; month => ", month);
      if (month == selectedMonth) {
        setSelectedPoints(points[month]);
        break;
      }
    }
  }, [selectedMonth]);

  const options = {
    theme: "white",
    animationEnabled: true,
    exportFileName: "New Year Resolutions",
    exportEnabled: true,
    title:{
      text: "Thống kê lợi nhuận theo nhóm hàng hóa"
    },
    data: [{
      type: "pie",
      showInLegend: true,
      legendText: "{label}",
      toolTipContent: "{label}: <strong>{y}%</strong>\nLợi nhuận: <strong>{profit}</strong>",
      indexLabel: "{y}%",
      indexLabelPlacement: "inside",
      dataPoints: selectedPoints
    }]
  }

  return <div>
    <Grid container
      spacing={2}
      direction="column"
      alignItems="center"
      justifyContent="center">
      <Box mt={5}>
        <Select
          value={selectedMonth}
          defaultValue={selectedMonth}
          onChange={(e) => setSelectedMonth(e.target.value)}>
          {
            months.length > 0 &&
            months.map(month => <MenuItem value={month}>{month}</MenuItem>)
          }
        </Select>
      </Box>
    </Grid>
    <CanvasJSChart options = {options} />
  </div>
}

export default ProductCategoryMonthlyReport;