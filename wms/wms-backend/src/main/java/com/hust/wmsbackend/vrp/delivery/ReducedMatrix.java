package com.hust.wmsbackend.vrp.delivery;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Slf4j
public class ReducedMatrix implements Cloneable {
    private double reducedCost;
    private List<Double[]> matrix;

    public void setInfinityValue(int row, int col) {
        try {
            Double[] rowMatrix = matrix.get(row);
            rowMatrix[col] = Double.MAX_VALUE;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Can not update matrix %s with (row:%s, col:%s)", matrix, row, col));
        }
    }

    public void subtractRow(int row, double value) {
        try {
            for (int i = 0; i < matrix.size(); i++) {
                if (matrix.get(row)[i] != Double.MAX_VALUE) {
                    matrix.get(row)[i] -= value;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Can not subtract value %s from row %s", value, row));
        }
    }

    public void subtractColumn(int column, double value) {
        try {
            for (int row = 0; row < matrix.size(); row++) {
                if (matrix.get(row)[column] != Double.MAX_VALUE) {
                    matrix.get(row)[column] -= value;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Can not subtract value %s from column %s", value, column));
        }
    }

    public ReducedMatrix clone() throws CloneNotSupportedException {
        ReducedMatrix clone = (ReducedMatrix) super.clone();
        List<Double[]> rows = new ArrayList<>();
        for (int i = 0; i < this.getMatrix().size(); i++) {
            Double[] doubles = new Double[this.getMatrix().size()];
            for (int j = 0; j < this.getMatrix().size(); j++) {
                doubles[j] = this.getMatrix().get(i)[j];
            }
            rows.add(doubles);
        }
        clone.setMatrix(rows);
        clone.setReducedCost(this.getReducedCost());
        return clone;
    }
}
