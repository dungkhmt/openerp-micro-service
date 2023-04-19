import {Pie} from "react-chartjs-2";
import React from "react";

function PieChart({labels, datasets}) {
  return (
    <Pie
      options={{
        width: "1200",
        height: "1200"
      }}
      data={{
        labels: labels,
        datasets: datasets
      }}
    />
  );
}

export default PieChart;
