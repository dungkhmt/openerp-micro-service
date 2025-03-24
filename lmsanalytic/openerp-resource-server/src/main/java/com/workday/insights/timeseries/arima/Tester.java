package com.workday.insights.timeseries.arima;

import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

import java.util.Random;

public class Tester {
    public static void main(String[] args){
        int n = 1000;
        double[] data = new double[n];
        Random R = new Random();
        for(int i = 0; i < n; i++){
            int v = R.nextInt(1000) + 1;
            data[i] = (double)v*1.0/5;
        }
        int forecastSize = 7;
        int p = 3;
        int d = 0;
        int q = 0;
        int P = 1;
        int D = 0;
        int Q = 1;
        int m = 12;

        final ForecastResult res = Arima
                .forecast_arima(data, forecastSize, new ArimaParams(p, d, q, P, D, Q, m));
        for(double v: res.getForecast()) System.out.println(v);
    }
}
