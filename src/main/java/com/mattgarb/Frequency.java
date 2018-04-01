package com.mattgarb;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by mattgarb on 5/1/2017.
 * Frequency stores the frequency of each character used in a text as an array of ints.
 * Frequency can cast toString or toPieChart, possibly more in the future.
 */
public class Frequency {

    final private Integer[] frequencies;

    Frequency(String text) {
        this.frequencies = new Integer[26];
        Arrays.fill(frequencies, 0);
        text.toLowerCase().replaceAll("[^a-z]","").chars().forEach(e -> frequencies[e-97]++);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(26*4);
        IntStream.range(0, frequencies.length)
                .forEachOrdered(i -> sb.append((char) (i + 97)).append(": ").append(frequencies[i]).append(" "));
        return sb.toString();
    }

    public Integer[] getFrequencies() {
        return frequencies;
    }

    PieChart toPieChart(String title) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        IntStream.range(0, this.frequencies.length)
                .forEachOrdered(i -> {
                    if(this.frequencies[i] != null && this.frequencies[i] != 0) {
                        pieChartData.add(new PieChart.Data(String.valueOf((char) (i + 97)), this.frequencies[i]));
                    }
                });
        final PieChart chart = new PieChart(pieChartData);
        chart.setLegendVisible(false);
        //chart.setTitle(title);
        return chart;
    }
}
