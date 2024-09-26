const math = require("mathjs");

function calculateWeights(matrix) {
  let columnSums = matrix[0].map((col, i) =>
    matrix.reduce((sum, row) => sum + row[i], 0)
  );
  let normalizedMatrix = matrix.map((row) =>
    row.map((value, i) => value / columnSums[i])
  );
  let rowAverages = normalizedMatrix.map((row) => math.mean(row));
  let sumOfAverages = math.sum(rowAverages);
  let weights = rowAverages.map((value) => value / sumOfAverages);
  return weights;
}

let criteriaMatrix = [
  [1, 5, 9],
  [1/5, 1, 4],
  [1/9, 1/4, 1],
];


function calculateWeightToApply(criteriaMatrix) {
  let weight = calculateWeights(criteriaMatrix);
  let actualWeight = [];
  let minWeight = Math.min(weight[0], weight[1], weight[2])
  let divider = weight[0] + weight[1] + weight[2] + 0.4 + 0.05;
  actualWeight.push(weight[0] / divider);
  actualWeight.push(weight[1] / divider);
  actualWeight.push(weight[2] / divider);
  actualWeight.push(0.2 / divider);
  actualWeight.push(0.2 / divider);
  actualWeight.push(0.05 / divider);
  return actualWeight;
}

module.exports = calculateWeightToApply