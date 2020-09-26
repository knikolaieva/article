package com.sytoss.article.handler;

import java.awt.*;
import java.util.List;

import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.axis.NumberAxis3D;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;
import com.orsoncharts.graphics3d.Dimension3D;
import com.orsoncharts.graphics3d.ViewPoint3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.xyz.ScatterXYZRenderer;
import com.sytoss.article.model.AnalyzeModel;
import com.sytoss.article.model.TypeModel;
import com.sytoss.article.model.ValueModel;

public class Plot3DHandler {

    private Chart3D create3DChart(String algorithm, String type, XYZSeriesCollection<String> dataset) {
       // writeToCSV(algorithm + type, dataset);
        Chart3D chart = Chart3DFactory.createScatterChart(algorithm, "", dataset, "Scale", "Shear", "Rotate");
        XYZPlot plot = (XYZPlot) chart.getPlot();
        plot.setDimensions(new Dimension3D(10.0, 4.0, 4.0));
        ScatterXYZRenderer renderer = (ScatterXYZRenderer) plot.getRenderer();
        renderer.setSize(0.15);
        Color[] result = new Color[]{
                new Color(255, 0, 19),
                new Color(255, 104, 29),
                new Color(255, 204, 34),
                new Color(195, 226, 5),
                new Color(130, 186, 34),
                new Color(44, 171, 203),
        };
        renderer.setColors(result);
        chart.setViewPoint(ViewPoint3D.createAboveLeftViewPoint(40));
        return chart;
    }


    public Chart3D plotPixel3D(List<AnalyzeModel> experimentModels, String algorithm){
        XYZSeriesCollection<String> dataset = new XYZSeriesCollection<>();
        XYZSeries<String> s1 = new XYZSeries<>("[0;2]");
        XYZSeries<String> s2 = new XYZSeries<>("(2;4]");
        XYZSeries<String> s3 = new XYZSeries<>(">4");
        XYZSeries<String> s4 = new XYZSeries<>("-1");
        experimentModels.forEach(plot -> {
            ValueModel value = plot.getTransformValue();
            double pixel = plot.getPixelDelta();
            if (pixel >= 0.0 && pixel <= 2.0) {
                s1.add(value.getScale(), value.getShear(), value.getRotate());
            } else if (pixel > 2.0 && pixel <= 4.0){
                s2.add(value.getScale(), value.getShear(), value.getRotate());
            } else if (pixel > 4){
                s3.add(value.getScale(), value.getShear(), value.getRotate());
            } else if (value != null){
                s4.add(value.getScale(), value.getShear(), value.getRotate());
            }
        });
        dataset.add(s1);
        dataset.add(s2);
        dataset.add(s3);
        dataset.add(s4);
        return create3DChart(algorithm, "pixel", dataset);
    }

    public Chart3D plotParam3D(List<AnalyzeModel> experimentModels, String algorithm, TypeModel type){
        XYZSeriesCollection<String> dataset = new XYZSeriesCollection<>();
        XYZSeries<String> s1;
        XYZSeries<String> s2;
        XYZSeries<String> s3 ;
        XYZSeries<String> s4 ;
        XYZSeries<String> s5;
        XYZSeries<String> s6;
        if (type == TypeModel.ROTATE){
            s1 = new XYZSeries<>("[0;1.0]");
            s2 = new XYZSeries<>("(1.0;3]");
            s3 = new XYZSeries<>("(3;10]");
            s4 = new XYZSeries<>("(10;20]");
            s5 = new XYZSeries<>("(20;50]");
            s6 = new XYZSeries<>(">50");
        } else {
            s1 = new XYZSeries<>("[0;0.2]");
            s2 = new XYZSeries<>("(0.2;0.5]");
            s3 = new XYZSeries<>("(0.5;1.0]");
            s4 = new XYZSeries<>("(1.0;3.0]");
            s5 = new XYZSeries<>(">3");
            s6 = new XYZSeries<>("-1");
        }
        experimentModels.forEach(plot -> {
            ValueModel value = plot.getTransformValue();
            double param = getDeltaParam(plot, type);
            if (param != Double.MAX_VALUE) {
                if (type == TypeModel.ROTATE){
                    if (param >= 0.0 && param <= 1.0) {
                        s1.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 1.0 && param <= 3.0) {
                        s2.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 3.0 && param <= 10.0) {
                        s3.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 10.0 && param <= 20.0) {
                        s4.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 20 && param <= 50.0) {
                        s5.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 50){
                        s6.add(value.getScale(), value.getShear(), value.getRotate());
                    }
                } else {
                    if (param >= 0.0 && param <= 0.2) {
                        s1.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 0.2 && param <= 0.5) {
                        s2.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 0.5 && param <= 1.0) {
                        s3.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 1.0 && param <= 3.0) {
                        s4.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (param > 3) {
                        s5.add(value.getScale(), value.getShear(), value.getRotate());
                    } else if (value != null){
                        s6.add(value.getScale(), value.getShear(), value.getRotate());
                    }
                }
            }
        });
        dataset.add(s1);
        dataset.add(s2);
        dataset.add(s3);
        dataset.add(s4);
        dataset.add(s5);
        dataset.add(s6);
        return create3DChart(algorithm, type.name(), dataset);
    }

    public Chart3D plotParamFromDelta3D(List<AnalyzeModel> experimentModels, String algorithm){
        XYZSeriesCollection<String> dataset = new XYZSeriesCollection<>();
        XYZSeries<String> s1 = new XYZSeries<>("[0;2]");
        XYZSeries<String> s2 = new XYZSeries<>("(2;4]");
        XYZSeries<String> s3 = new XYZSeries<>(">4");
        XYZSeries<String> s4 = new XYZSeries<>("-1");

        experimentModels.forEach(plot -> {
            double param = plot.getPixelDelta();
            if (param != Double.MAX_VALUE && param < 11) {
                if (param >= 0.0 && param <= 2.0) {
                    s1.add(plot.getScaleDelta(), plot.getShearDelta(), plot.getRotateDelta());
                } else if (param > 2.0 && param <= 4.0) {
                    s2.add(plot.getScaleDelta(), plot.getShearDelta(), plot.getRotateDelta());
                } else if (param > 4.0) {
                    s3.add(plot.getScaleDelta(), plot.getShearDelta(), plot.getRotateDelta());
                } else  {
                    s4.add(plot.getScaleDelta(), plot.getShearDelta(), plot.getRotateDelta());
                }
            }
        });
        dataset.add(s1);
        dataset.add(s2);
        dataset.add(s3);
        dataset.add(s4);
        Chart3D chart = create3DChart(algorithm, "param_from_pixel", dataset);
        XYZPlot plot = (XYZPlot) chart.getPlot();
        NumberAxis3D yAxis = (NumberAxis3D)plot.getYAxis();
        yAxis.setRange(0, 0.5);
        NumberAxis3D xAxis = (NumberAxis3D)plot.getXAxis();
        xAxis.setRange(0, 0.5);
        NumberAxis3D zAxis = (NumberAxis3D)plot.getZAxis();
        zAxis.setRange(0, 10);
        return chart;
    }

    private double getDeltaParam(AnalyzeModel plot, TypeModel type){
        double param = Double.MAX_VALUE;
        switch (type){
            case SCALE:
                param = plot.getScaleDelta();
                break;
            case SHEAR:
                param = plot.getShearDelta();
                break;
            case ROTATE:
                param = plot.getRotateDelta();
                break;
        }
        return param;
    }
}
