import { Line } from "react-chartjs-2";
//import { Line } from "react-Chart.js-2";

import React from "react";
//import "./styles.css";

function DashBoard(){
    const data = {
        labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"],
        datasets: [
          {
            label: "First dataset",
            data: [33, 53, 85, 41, 44, 65],
            //fill: true,
            //backgroundColor: "rgba(75,192,192,0.2)",
            //borderColor: "rgba(75,192,192,1)"
          },
          {
            label: "Second dataset",
            data: [33, 25, 35, 51, 54, 76],
            //fill: false,
            //borderColor: "#742774"
          }
        ]
      };
    return (
        <div>
          <Line data={data} />
        </div>
      );
}

export default DashBoard;