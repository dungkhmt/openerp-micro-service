import PropTypes from "prop-types";
import { Pie } from "react-chartjs-2";

function PieChart({ labels, datasets }) {
  return (
    <Pie
      options={{
        width: "1200",
        height: "1200",
      }}
      data={{
        labels: labels,
        datasets: datasets,
      }}
    />
  );
}

PieChart.propTypes = {
  labels: PropTypes.arrayOf(PropTypes.string).isRequired,
  datasets: PropTypes.arrayOf(PropTypes.object).isRequired,
};

export default PieChart;
