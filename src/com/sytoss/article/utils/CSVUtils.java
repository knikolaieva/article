package com.sytoss.article.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.orsoncharts.data.xyz.XYZDataItem;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;

public final class CSVUtils {

    private static final String CSV_SEPARATOR = ",";

    public static void writeToCSV(String name, XYZSeriesCollection<String> dataset) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name + ".csv"), "UTF-8"));
            String header = "x1" + CSV_SEPARATOR + "y1" + CSV_SEPARATOR + "z1";
            bw.write(header);
            bw.newLine();
            for (int i = 0; i < dataset.getSeriesCount(); i++) {
                XYZSeries<String> series = dataset.getSeries(i);
                for (XYZDataItem item : series.getItems()) {
                    StringBuilder oneLine = new StringBuilder();
                    oneLine.append(item.getX());
                    oneLine.append(CSV_SEPARATOR);
                    oneLine.append(item.getY());
                    oneLine.append(CSV_SEPARATOR);
                    oneLine.append(item.getX());
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
