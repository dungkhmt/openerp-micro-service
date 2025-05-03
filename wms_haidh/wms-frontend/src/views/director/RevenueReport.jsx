import React, { useState, useEffect } from 'react';
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import { request } from "../../api";
import { formatPrice } from '../../utils/utils';
const RevenueReport = () => {
  const [data, setData] = useState([]);

  // Sample data
  useEffect(() => {
    request("get", `/reports/monthly-revenue`, (res) => {
      setData(res.data);
    });
  }, []);


  return (
    <div style={{ width: '100%', height: 440, display: 'flex', flexDirection: 'column' }}>
      <ResponsiveContainer width="100%" height={400}>
        <AreaChart
          data={data}
          margin={{ top: 10, right: 30, left: 50, bottom: 0 }}
        >
          <defs>
            <linearGradient id="revenueColor" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#8884d8" stopOpacity={0.8} />
              <stop offset="95%" stopColor="#8884d8" stopOpacity={0} />
            </linearGradient>
            <linearGradient id="profitColor" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#82ca9d" stopOpacity={0.8} />
              <stop offset="95%" stopColor="#82ca9d" stopOpacity={0} />
            </linearGradient>
          </defs>
          <XAxis dataKey="date" />
          <YAxis
            tickFormatter={(value) => value.toLocaleString("vi-VN")}
          />
          <CartesianGrid strokeDasharray="3 3" />
          <Tooltip formatter={(value) => formatPrice(value)} />
          <Legend />
          <Area type="monotone" dataKey="revenue" stroke="#8884d8" fillOpacity={1} fill="url(#revenueColor)" name="Revenue" />
          <Area type="monotone" dataKey="profit" stroke="#82ca9d" fillOpacity={1} fill="url(#profitColor)" name="Profit" />
        </AreaChart>
      </ResponsiveContainer>
      <h2 className="text-xl font-semibold text-center mt-3">
        Monthly Revenue and Profit Overview
      </h2>

    </div>
  );
};

export default RevenueReport;
