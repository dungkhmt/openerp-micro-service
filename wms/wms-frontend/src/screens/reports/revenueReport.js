import React, { useEffect, useState } from 'react';
import CanvasJSReact from '@canvasjs/react-charts';
import { request } from 'api';
import { API_PATH } from 'screens/apiPaths';

var CanvasJS = CanvasJSReact.CanvasJS;
var CanvasJSChart = CanvasJSReact.CanvasJSChart;

const RevuenueReport = () => {

  const [profit, setProfit] = useState([]);
  const [revenue, setRevenue] = useState([]);

  useEffect(() => {
    request(
      'get',
      API_PATH.REVENUE_PROFIT_REPORT,
      (res) => {
        setProfit(res?.data?.profit);
        setRevenue(res?.data?.revenue);
      }
    )
  }, []);

  const options = {
    theme: "light2",
    title: {
      text: "Biểu đồ Doanh thu - Lợi nhuận"
    },
    toolTip: {
      shared: true
    },
    data: [
    {
      type: "area",
      name: "Doanh thu",
      showInLegend: true,
      xValueFormatString: "MMM YYYY",
      yValueFormatString: "#,##0.##",
      dataPoints: revenue.map(revenue => ({ x: new Date(revenue.x), y: revenue.y }))
    },
    {
      type: "area",
      name: "Lợi nhuận",
      showInLegend: true,
      xValueFormatString: "MMM YYYY",
      yValueFormatString: "#,##0.##",
      dataPoints: profit.map(profit => ({ x: new Date(profit.x), y: profit.y }))
    }
    ]
  }

  return <div>
    <CanvasJSChart options = {options}
    /* onRef={ref => this.chart = ref} */
  /></div>
}

export default RevuenueReport;