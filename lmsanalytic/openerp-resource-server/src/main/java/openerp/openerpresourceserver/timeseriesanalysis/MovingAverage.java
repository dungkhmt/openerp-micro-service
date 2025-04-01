package openerp.openerpresourceserver.timeseriesanalysis;


import java.util.ArrayList;
import java.util.List;

public class MovingAverage {
    private List<Double> timeSeriesData;
    private int windowSize; // Size of the moving average window

    public MovingAverage(int windowSize, List<Double> sequences) {
        this.timeSeriesData = sequences;
        this.windowSize = windowSize;
    }

    // Add data point to the time series
    public void addDataPoint(double value) {
        timeSeriesData.add(value);
    }

    // Calculate simple moving average
    public double calculateMovingAverage(int endIndex) {
        if (endIndex < windowSize - 1 || endIndex >= timeSeriesData.size()) {
            throw new IllegalArgumentException("Invalid index or insufficient data");
        }

        double sum = 0;
        for (int i = endIndex - windowSize + 1; i <= endIndex; i++) {
            sum += timeSeriesData.get(i);
        }
        return sum / windowSize;
    }

    // Predict next value based on moving average
    public double predictNextValue() {
        if (timeSeriesData.size() < windowSize) {
            throw new IllegalStateException("Not enough data points for prediction");
        }

        // Use the last window to predict the next value
        int lastIndex = timeSeriesData.size() - 1;
        return calculateMovingAverage(lastIndex);
    }

    // Main method with example usage
    public static void main(String[] args) {
        // Create predictor with a window size of 3


        // Sample data (e.g., monthly sales figures)
        double[] sampleData = {100.0, 120.0, 115.0, 130.0, 125.0, 140.0};
        List<Double> sequences = new ArrayList<>();
        for(double v: sampleData) sequences.add(v);

        MovingAverage predictor = new MovingAverage(3, sequences);
        // Add data points
        System.out.println("Input Data:");
        for (double value : sampleData) {
            predictor.addDataPoint(value);
            System.out.print(value + " ");
        }
        System.out.println("\n");

        // Calculate and display moving averages
        System.out.println("Moving Averages (window size = 3):");
        for (int i = 2; i < sampleData.length; i++) {
            double ma = predictor.calculateMovingAverage(i);
            System.out.printf("MA at index %d: %.2f%n", i, ma);
        }

        // Make prediction
        try {
            double prediction = predictor.predictNextValue();
            System.out.printf("\nPredicted next value: %.2f%n", prediction);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}