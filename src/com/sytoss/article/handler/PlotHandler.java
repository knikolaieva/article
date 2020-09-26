package com.sytoss.article.handler;

import static java.util.Arrays.asList;
import static org.knowm.xchart.BitmapEncoder.BitmapFormat.PNG;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.ToDoubleFunction;

import org.knowm.xchart.*;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.sytoss.article.model.AnalyzeModel;
import com.sytoss.article.model.AnalyzeModel;

import lombok.Getter;

@Getter
public class PlotHandler {

    public void makePlots(String resultFile, List<AnalyzeModel> surf128P1, List<AnalyzeModel> surf64P1, List<AnalyzeModel> siftP1, List<AnalyzeModel> orb1000P1,
                          List<AnalyzeModel> orbP1, List<AnalyzeModel> kazeP1, List<AnalyzeModel> akazeP1, List<AnalyzeModel> briskP1) {
        PlotHandler plotHandler = new PlotHandler();
        XYChart chart01 = plotHandler.plotFeatures(siftP1);
        XYChart chart02 = plotHandler.plotFeatures(surf128P1);
        XYChart chart03 = plotHandler.plotFeatures(surf64P1);
        XYChart chart04 = plotHandler.plotFeatures(orbP1);
        XYChart chart05 = plotHandler.plotFeatures(orb1000P1);
        XYChart chart06 = plotHandler.plotFeatures(briskP1);
        XYChart chart07 = plotHandler.plotFeatures(kazeP1);
        XYChart chart08 = plotHandler.plotFeatures(akazeP1);
        List<Chart> charts1 = asList(chart01, chart02, chart03, chart04, chart05, chart06, chart07, chart08);

        XYChart xyChart2 = new XYChartBuilder().width(1700).height(600)
                .title("Density of key points")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("DP for synthesized image")
                .build();
        plotHandler.setStyle2(xyChart2);

        ToDoubleFunction<AnalyzeModel> mapper2 = AnalyzeModel::getDP2;
        plotHandler.plot(xyChart2, siftP1, mapper2);
        plotHandler.plot(xyChart2, surf128P1, mapper2);
        plotHandler.plot(xyChart2, surf64P1, mapper2);
        plotHandler.plot(xyChart2, orbP1, mapper2);
        plotHandler.plot(xyChart2, orb1000P1, mapper2);
        plotHandler.plot(xyChart2, briskP1, mapper2);
        plotHandler.plot(xyChart2, kazeP1, mapper2);
        plotHandler.plot(xyChart2, akazeP1, mapper2);

        XYChart xyChart3 = new XYChartBuilder().width(1700).height(600)
                .title("Precision")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("value of precision")
                .build();
        plotHandler.setStyle2(xyChart3);

        ToDoubleFunction<AnalyzeModel> mapper3 = AnalyzeModel::getPrecision;
        plotHandler.plot(xyChart3, siftP1, mapper3);
        plotHandler.plot(xyChart3, surf128P1, mapper3);
        plotHandler.plot(xyChart3, surf64P1, mapper3);
        plotHandler.plot(xyChart3, orbP1, mapper3);
        plotHandler.plot(xyChart3, orb1000P1, mapper3);
        plotHandler.plot(xyChart3, briskP1, mapper3);
        plotHandler.plot(xyChart3, kazeP1, mapper3);
        plotHandler.plot(xyChart3, akazeP1, mapper3);

        XYChart xyChart4 = new XYChartBuilder().width(1700).height(600)
                .title("Recall")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("value of recall")
                .build();
        plotHandler.setStyle2(xyChart4);

        ToDoubleFunction<AnalyzeModel> mapper4 = AnalyzeModel::getRecallO1;
        plotHandler.plot(xyChart4, siftP1, mapper4);
        plotHandler.plot(xyChart4, surf128P1, mapper4);
        plotHandler.plot(xyChart4, surf64P1, mapper4);
        plotHandler.plot(xyChart4, orbP1, mapper4);
        plotHandler.plot(xyChart4, orb1000P1, mapper4);
        plotHandler.plot(xyChart4, briskP1, mapper4);
        plotHandler.plot(xyChart4, kazeP1, mapper4);
        plotHandler.plot(xyChart4, akazeP1, mapper4);

        XYChart xyChart5 = new XYChartBuilder().width(1700).height(600)
                .title("Detector repeatability")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("DetRep")
                .build();
        plotHandler.setStyle2(xyChart5);

        ToDoubleFunction<AnalyzeModel> mapper5 = AnalyzeModel::getDetRep;
        plotHandler.plot(xyChart5, siftP1, mapper5);
        plotHandler.plot(xyChart5, surf128P1, mapper5);
        plotHandler.plot(xyChart5, surf64P1, mapper5);
        plotHandler.plot(xyChart5, orbP1, mapper5);
        plotHandler.plot(xyChart5, orb1000P1, mapper5);
        plotHandler.plot(xyChart5, briskP1, mapper5);
        plotHandler.plot(xyChart5, kazeP1, mapper5);
        plotHandler.plot(xyChart5, akazeP1, mapper5);

        XYChart xyChart6 = new XYChartBuilder().width(1700).height(600)
                .title("Descriptor repeatability (\u03BE=0,2)")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("DesRep")
                .build();
        plotHandler.setStyle2(xyChart6);

        ToDoubleFunction<AnalyzeModel> mapper6 = AnalyzeModel::getDesRep;
        plotHandler.plot(xyChart6, siftP1, mapper6);
        plotHandler.plot(xyChart6, surf128P1, mapper6);
        plotHandler.plot(xyChart6, surf64P1, mapper6);
        plotHandler.plot(xyChart6, orbP1, mapper6);
        plotHandler.plot(xyChart6, orb1000P1, mapper6);
        plotHandler.plot(xyChart6, briskP1, mapper6);
        plotHandler.plot(xyChart6, kazeP1, mapper6);
        plotHandler.plot(xyChart6, akazeP1, mapper6);

        XYChart xyChart7 = new XYChartBuilder().width(1700).height(600)
                .title("Detector-descriptor repeatability (\u03BE=0,2)")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("DetDesRep")
                .build();
        plotHandler.setStyle2(xyChart7);

        ToDoubleFunction<AnalyzeModel> mapper7 = AnalyzeModel::getDetDesRep;
        plotHandler.plot(xyChart7, siftP1, mapper7);
        plotHandler.plot(xyChart7, surf128P1, mapper7);
        plotHandler.plot(xyChart7, surf64P1, mapper7);
        plotHandler.plot(xyChart7, orbP1, mapper7);
        plotHandler.plot(xyChart7, orb1000P1, mapper7);
        plotHandler.plot(xyChart7, briskP1, mapper7);
        plotHandler.plot(xyChart7, kazeP1, mapper7);
        plotHandler.plot(xyChart7, akazeP1, mapper7);

        XYChart xyChart8 = new XYChartBuilder().width(1700).height(600)
                .title("Average error of inlier coordinate matching")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("avgErrIM")
                .build();
        plotHandler.setStyle2(xyChart8);

        ToDoubleFunction<AnalyzeModel> mapper8 = AnalyzeModel::getAvgErrIM;
        plotHandler.plot(xyChart8, siftP1, mapper8);
        plotHandler.plot(xyChart8, surf128P1, mapper8);
        plotHandler.plot(xyChart8, surf64P1, mapper8);
        plotHandler.plot(xyChart8, orbP1, mapper8);
        plotHandler.plot(xyChart8, orb1000P1, mapper8);
        plotHandler.plot(xyChart8, briskP1, mapper8);
        plotHandler.plot(xyChart8, kazeP1, mapper8);
        plotHandler.plot(xyChart8, akazeP1, mapper8);

        XYChart xyChart9 = new XYChartBuilder().width(1700).height(600)
                .title("Maximum error of inlier coordinate matching")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("maxErrIM")
                .build();
        plotHandler.setStyle2(xyChart9);

        ToDoubleFunction<AnalyzeModel> mapper9 = AnalyzeModel::getMaxErrIM;
        plotHandler.plot(xyChart9, siftP1, mapper9);
        plotHandler.plot(xyChart9, surf128P1, mapper9);
        plotHandler.plot(xyChart9, surf64P1, mapper9);
        plotHandler.plot(xyChart9, orbP1, mapper9);
        plotHandler.plot(xyChart9, orb1000P1, mapper9);
        plotHandler.plot(xyChart9, briskP1, mapper9);
        plotHandler.plot(xyChart9, kazeP1, mapper9);
        plotHandler.plot(xyChart9, akazeP1, mapper9);

        XYChart xyChart10 = new XYChartBuilder().width(1700).height(600)
                .title("Average error of corner point matching")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("avgErrCPM")
                .build();
        plotHandler.setStyle2(xyChart10);
        xyChart10.getStyler().setYAxisMax(150.0);

        ToDoubleFunction<AnalyzeModel> mapper10 = AnalyzeModel::getAvgErrCPM;
        plotHandler.plot(xyChart10, siftP1, mapper10);
        plotHandler.plot(xyChart10, surf128P1, mapper10);
        plotHandler.plot(xyChart10, surf64P1, mapper10);
        plotHandler.plot(xyChart10, orbP1, mapper10);
        plotHandler.plot(xyChart10, orb1000P1, mapper10);
        plotHandler.plot(xyChart10, briskP1, mapper10);
        plotHandler.plot(xyChart10, kazeP1, mapper10);
        plotHandler.plot(xyChart10, akazeP1, mapper10);

        XYChart xyChart11 = new XYChartBuilder().width(1700).height(600)
                .title("Maximum error of corner point matching")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("maxErrCPM")
                .build();
        plotHandler.setStyle2(xyChart11);
        xyChart11.getStyler().setYAxisMax(150.0);

        ToDoubleFunction<AnalyzeModel> mapper11 = AnalyzeModel::getMaxErrCPM;
        plotHandler.plot(xyChart11, siftP1, mapper11);
        plotHandler.plot(xyChart11, surf128P1, mapper11);
        plotHandler.plot(xyChart11, surf64P1, mapper11);
        plotHandler.plot(xyChart11, orbP1, mapper11);
        plotHandler.plot(xyChart11, orb1000P1, mapper11);
        plotHandler.plot(xyChart11, briskP1, mapper11);
        plotHandler.plot(xyChart11, kazeP1, mapper11);
        plotHandler.plot(xyChart11, akazeP1, mapper11);

        XYChart xyChart12 = new XYChartBuilder().width(1700).height(600)
                .title("Maximum error of found overlaps")
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("maxErrOP, %")
                .build();
        plotHandler.setStyle2(xyChart12);

        ToDoubleFunction<AnalyzeModel> mapper12 = value -> Math.max((100.0 - value.getOverlapRatioImg1()), (100.0 - value.getOverlapRatioImg2()));
        plotHandler.plot(xyChart12, siftP1, mapper12);
        plotHandler.plot(xyChart12, surf128P1, mapper12);
        plotHandler.plot(xyChart12, surf64P1, mapper12);
        plotHandler.plot(xyChart12, orbP1, mapper12);
        plotHandler.plot(xyChart12, orb1000P1, mapper12);
        plotHandler.plot(xyChart12, briskP1, mapper12);
        plotHandler.plot(xyChart12, kazeP1, mapper12);
        plotHandler.plot(xyChart12, akazeP1, mapper12);

        try {
            BitmapEncoder.saveBitmap(charts1, 4, 2, resultFile + "/figure1", PNG);
            BitmapEncoder.saveBitmap(xyChart2, resultFile + "/figure2", PNG);
            BitmapEncoder.saveBitmap(xyChart3, resultFile + "/figure3", PNG);
            BitmapEncoder.saveBitmap(xyChart4, resultFile + "/figure4", PNG);
            BitmapEncoder.saveBitmap(xyChart5, resultFile + "/figure5", PNG);
            BitmapEncoder.saveBitmap(xyChart6, resultFile + "/figure6", PNG);
            BitmapEncoder.saveBitmap(xyChart7, resultFile + "/figure7", PNG);
            BitmapEncoder.saveBitmap(xyChart8, resultFile + "/figure8", PNG);
            BitmapEncoder.saveBitmap(xyChart9, resultFile + "/figure9", PNG);
            BitmapEncoder.saveBitmap(xyChart10, resultFile + "/figure10", PNG);
            BitmapEncoder.saveBitmap(xyChart11, resultFile + "/figure11", PNG);
            BitmapEncoder.saveBitmap(xyChart12, resultFile + "/figure12", PNG);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void plotSynthesizedDelta(String algorithm, List<AnalyzeModel> AnalyzeModels, List<XYChart> charts) {
        Map<Double, Object> o2 = new HashMap<>();
        AnalyzeModels.forEach(plotItem -> o2.put(plotItem.getId(), plotItem.getTransformValue().toString()));
        double[] id = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getId).toArray();
        double[] deltaScale = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getScaleDelta).toArray();
        XYChart chart1 = charts.get(0);
        chart1.addSeries(algorithm, id, deltaScale);
        chart1.getStyler().setYAxisMax(0.2);
        chart1.setXAxisLabelOverrideMap(o2);

        double[] deltaShear = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getShearDelta).toArray();
        XYChart chart2 = charts.get(1);
        chart2.addSeries(algorithm, id, deltaShear);
        chart2.getStyler().setYAxisMax(0.4);
        chart2.setXAxisLabelOverrideMap(o2);

        double[] deltaRotate = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getRotateDelta).toArray();
        XYChart chart3 = charts.get(2);
        chart3.addSeries(algorithm, id, deltaRotate);
        chart3.getStyler().setYAxisMax(20.0);
        chart3.setXAxisLabelOverrideMap(o2);

        double[] deltaPixel = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getPixelDelta).toArray();
        XYChart chart4 = charts.get(3);
        chart4.addSeries(algorithm, id, deltaPixel);
        chart4.getStyler().setYAxisMax(20.0);
        chart4.setXAxisLabelOverrideMap(o2);
    }

    public void plotByFixedRotate(String algorithm, List<AnalyzeModel> AnalyzeModels, List<XYChart> charts) {
        double[] rotate = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getDegree).toArray();
        double[] deltaScale = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getScaleDelta).toArray();
        double[] deltaShear = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getShearDelta).toArray();
        double[] deltaRotate = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getRotateDelta).toArray();
        double[] deltaPixel = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getPixelDelta).toArray();

        XYChart chart1 = charts.get(0);
        chart1.setXAxisTitle("Параметры поворота");
        chart1.addSeries(algorithm, rotate, deltaScale);
        XYChart chart2 = charts.get(1);
        chart2.setXAxisTitle("Параметры поворота");
        chart2.addSeries(algorithm, rotate, deltaShear);
        XYChart chart3 = charts.get(2);
        chart3.setXAxisTitle("Параметры поворота");
        chart3.addSeries(algorithm, rotate, deltaRotate);
        XYChart chart4 = charts.get(3);
        chart4.setXAxisTitle("Параметры поворота");
        chart4.addSeries(algorithm, rotate, deltaPixel);
    }

    public void plotByFixedScale(String algorithm, List<AnalyzeModel> AnalyzeModels, List<XYChart> charts) {
        double[] scale = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getScale).toArray();
        double[] deltaScale = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getScaleDelta).toArray();
        double[] deltaShear = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getShearDelta).toArray();
        double[] deltaRotate = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getRotateDelta).toArray();
        double[] deltaPixel = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getPixelDelta).toArray();

        XYChart chart1 = charts.get(0);
        chart1.setXAxisTitle("Параметры масштаба");
        chart1.addSeries(algorithm, scale, deltaScale);
        XYChart chart2 = charts.get(1);
        chart2.setXAxisTitle("Параметры масштаба");
        chart2.addSeries(algorithm, scale, deltaShear);
        XYChart chart3 = charts.get(2);
        chart3.setXAxisTitle("Параметры масштаба");
        chart3.addSeries(algorithm, scale, deltaRotate);
        XYChart chart4 = charts.get(3);
        chart4.setXAxisTitle("Параметры масштаба");
        chart4.addSeries(algorithm, scale, deltaPixel);
    }

    public void plotByFixedShear(String algorithm, List<AnalyzeModel> AnalyzeModels, List<XYChart> charts) {
        double[] shear = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getShear).toArray();
        double[] deltaScale = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getScaleDelta).toArray();
        double[] deltaShear = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getShearDelta).toArray();
        double[] deltaRotate = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getRotateDelta).toArray();
        double[] deltaPixel = AnalyzeModels.stream().mapToDouble(AnalyzeModel::getPixelDelta).toArray();

        XYChart chart1 = charts.get(0);
        chart1.setXAxisTitle("Параметры сдвига");
        chart1.addSeries(algorithm, shear, deltaScale);
        XYChart chart2 = charts.get(1);
        chart2.setXAxisTitle("Параметры сдвига");
        chart2.addSeries(algorithm, shear, deltaShear);
        XYChart chart3 = charts.get(2);
        chart3.setXAxisTitle("Параметры сдвига");
        chart3.addSeries(algorithm, shear, deltaRotate);
        XYChart chart4 = charts.get(3);
        chart4.setXAxisTitle("Параметры сдвига");
        chart4.addSeries(algorithm, shear, deltaPixel);
    }

    public void plotFix2DValues(String title, List<AnalyzeModel> surf128P1, List<AnalyzeModel> surf64P1, List<AnalyzeModel> siftP1, List<AnalyzeModel> orb1000P1,
                                List<AnalyzeModel> orbP1, List<AnalyzeModel> kazeP1, List<AnalyzeModel> akazeP1, List<AnalyzeModel> briskP1) {
        List<AnalyzeModel> surf128_rotate = getMeanForValue(surf128P1);
        List<AnalyzeModel> surf64_rotate = getMeanForValue(surf64P1);
        List<AnalyzeModel> sift_rotate = getMeanForValue(siftP1);
        List<AnalyzeModel> orb1000_rotate = getMeanForValue(orb1000P1);
        List<AnalyzeModel> orb_rotate = getMeanForValue(orbP1);
        List<AnalyzeModel> kaze_rotate = getMeanForValue(kazeP1);
        List<AnalyzeModel> akaze_rotate = getMeanForValue(akazeP1);
        List<AnalyzeModel> brisk_rotate = getMeanForValue(briskP1);

        List<AnalyzeModel> surf128_scale = getMeanForValueScale(surf128P1);
        List<AnalyzeModel> surf64_scale = getMeanForValueScale(surf64P1);
        List<AnalyzeModel> sift_scale = getMeanForValueScale(siftP1);
        List<AnalyzeModel> orb1000_scale = getMeanForValueScale(orb1000P1);
        List<AnalyzeModel> orb_scale = getMeanForValueScale(orbP1);
        List<AnalyzeModel> kaze_scale = getMeanForValueScale(kazeP1);
        List<AnalyzeModel> akaze_scale = getMeanForValueScale(akazeP1);
        List<AnalyzeModel> brisk_scale = getMeanForValueScale(briskP1);

        List<AnalyzeModel> surf128_shear = getMeanForValueShear(surf128P1);
        List<AnalyzeModel> surf64_shear = getMeanForValueShear(surf64P1);
        List<AnalyzeModel> sift_shear = getMeanForValueShear(siftP1);
        List<AnalyzeModel> orb1000_shear = getMeanForValueShear(orb1000P1);
        List<AnalyzeModel> orb_shear = getMeanForValueShear(orbP1);
        List<AnalyzeModel> kaze_shear = getMeanForValueShear(kazeP1);
        List<AnalyzeModel> akaze_shear = getMeanForValueShear(akazeP1);
        List<AnalyzeModel> brisk_shear = getMeanForValueShear(briskP1);

        List<XYChart> charts = createCharts();

        PlotHandler plotHandler = new PlotHandler();
        plotHandler.plotByFixedRotate("SIFT", sift_rotate, charts);
        plotHandler.plotByFixedRotate("ORB", orb_rotate, charts);
        plotHandler.plotByFixedRotate("ORB1000", orb1000_rotate, charts);
        plotHandler.plotByFixedRotate("BRISK", brisk_rotate, charts);
        plotHandler.plotByFixedRotate("SURF128", surf128_rotate, charts);
        plotHandler.plotByFixedRotate("SURF64", surf64_rotate, charts);
        plotHandler.plotByFixedRotate("KAZE", kaze_rotate, charts);
        plotHandler.plotByFixedRotate("AKAZE", akaze_rotate, charts);

        SwingWrapper swingWrapper = new SwingWrapper<>(charts, 4, 8);
        swingWrapper.displayChartMatrix(title);

        List<XYChart> charts2 = createCharts();

        PlotHandler plotHandler2 = new PlotHandler();
        plotHandler2.plotByFixedScale("SIFT", sift_scale, charts2);
        plotHandler2.plotByFixedScale("ORB", orb_scale, charts2);
        plotHandler2.plotByFixedScale("ORB1000", orb1000_scale, charts2);
        plotHandler2.plotByFixedScale("BRISK", brisk_scale, charts2);
        plotHandler2.plotByFixedScale("SURF128", surf128_scale, charts2);
        plotHandler2.plotByFixedScale("SURF64", surf64_scale, charts2);
        plotHandler2.plotByFixedScale("KAZE", kaze_scale, charts2);
        plotHandler2.plotByFixedScale("AKAZE", akaze_scale, charts2);
        SwingWrapper swingWrapper2 = new SwingWrapper<>(charts2, 4, 8);
        swingWrapper2.displayChartMatrix(title);

        List<XYChart> charts3 = createCharts();

        PlotHandler plotHandler3 = new PlotHandler();
        plotHandler3.plotByFixedShear("SIFT", sift_shear, charts3);
        plotHandler3.plotByFixedShear("ORB", orb_shear, charts3);
        plotHandler3.plotByFixedShear("ORB1000", orb1000_shear, charts3);
        plotHandler3.plotByFixedShear("BRISK", brisk_shear, charts3);
        plotHandler3.plotByFixedShear("SURF128", surf128_shear, charts3);
        plotHandler3.plotByFixedShear("SURF64", surf64_shear, charts3);
        plotHandler3.plotByFixedShear("KAZE", kaze_shear, charts3);
        plotHandler3.plotByFixedShear("AKAZE", akaze_shear, charts3);
        SwingWrapper swingWrapper3 = new SwingWrapper<>(charts3, 4, 8);
        swingWrapper3.displayChartMatrix(title);
    }

    public void plotFix2DValuesPngJpg(String title, List<AnalyzeModel> siftPng, List<AnalyzeModel> siftJpg) {
        List<AnalyzeModel> siftPng_rotate = getMeanForValue(siftPng);
        List<AnalyzeModel> siftJpg_rotate = getMeanForValue(siftJpg);

        List<AnalyzeModel> siftPng_scale = getMeanForValueScale(siftPng);
        List<AnalyzeModel> siftJpg_scale = getMeanForValueScale(siftJpg);

        List<AnalyzeModel> siftPng_shear = getMeanForValueShear(siftPng);
        List<AnalyzeModel> siftJpg_shear = getMeanForValueShear(siftJpg);

        List<XYChart> charts = createCharts();

        PlotHandler plotHandler = new PlotHandler();
        plotHandler.plotByFixedRotate("SIFT_PNG", siftPng_rotate, charts);
        plotHandler.plotByFixedRotate("SIFT_JPG", siftJpg_rotate, charts);

        SwingWrapper swingWrapper = new SwingWrapper<>(charts, 4, 8);
        swingWrapper.displayChartMatrix(title);

        List<XYChart> charts2 = createCharts();

        PlotHandler plotHandler2 = new PlotHandler();
        plotHandler2.plotByFixedScale("SIFT_PNG", siftPng_scale, charts2);
        plotHandler2.plotByFixedScale("SIFT_JPG", siftJpg_scale, charts2);
        SwingWrapper swingWrapper2 = new SwingWrapper<>(charts2, 4, 8);
        swingWrapper2.displayChartMatrix(title);

        List<XYChart> charts3 = createCharts();

        PlotHandler plotHandler3 = new PlotHandler();
        plotHandler3.plotByFixedShear("SIFT_PNG", siftPng_shear, charts3);
        plotHandler3.plotByFixedShear("SIFT_JPG", siftJpg_shear, charts3);
        SwingWrapper swingWrapper3 = new SwingWrapper<>(charts3, 4, 8);
        swingWrapper3.displayChartMatrix(title);
    }

    private List<AnalyzeModel> getMeanForValue(List<AnalyzeModel> items) {
        Map<Double, List<AnalyzeModel>> map = new LinkedHashMap<>();
        try {
            items.stream().forEach(plotItem -> {
                if (plotItem.getTransformValue().getShear() == 0.0 && plotItem.getTransformValue().getScale() == 1.0) {
                    List<AnalyzeModel> result = map.get(plotItem.getTransformValue().getRotate());
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(plotItem);
                    map.put(plotItem.getTransformValue().getRotate(), result);
                }
            });
        } catch (NullPointerException e) {
        }

        List<AnalyzeModel> result = new ArrayList<>();
        for (Map.Entry<Double, List<AnalyzeModel>> set : map.entrySet()) {
            double averageRotate = set.getValue().stream().mapToDouble(AnalyzeModel::getRotateDelta).average().getAsDouble();
            double averageScale = set.getValue().stream().mapToDouble(AnalyzeModel::getScaleDelta).average().getAsDouble();
            double averageShear = set.getValue().stream().mapToDouble(AnalyzeModel::getShearDelta).average().getAsDouble();
            double averagePixel = set.getValue().stream().mapToDouble(AnalyzeModel::getPixelDelta).average().getAsDouble();
            AnalyzeModel AnalyzeModel = new AnalyzeModel();
            AnalyzeModel.setDegree(set.getKey());
            AnalyzeModel.setRotateDelta(averageRotate);
            AnalyzeModel.setShearDelta(averageShear);
            AnalyzeModel.setScaleDelta(averageScale);
            AnalyzeModel.setPixelDelta(averagePixel);
            result.add(AnalyzeModel);
        }
        return result;
    }

    private List<AnalyzeModel> getMeanForValueScale(List<AnalyzeModel> items) {
        Map<Double, List<AnalyzeModel>> map = new LinkedHashMap<>();
        try {
            items.stream().forEach(plotItem -> {
                if (plotItem.getTransformValue().getShear() == 0.0 && plotItem.getTransformValue().getRotate() == 0.0) {
                    List<AnalyzeModel> result = map.get(plotItem.getTransformValue().getScale());
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(plotItem);
                    map.put(plotItem.getTransformValue().getScale(), result);
                }
            });
        } catch (NullPointerException e) {

        }

        List<AnalyzeModel> result = new ArrayList<>();
        for (Map.Entry<Double, List<AnalyzeModel>> set : map.entrySet()) {
            double averageRotate = set.getValue().stream().mapToDouble(AnalyzeModel::getRotateDelta).average().getAsDouble();
            double averageScale = set.getValue().stream().mapToDouble(AnalyzeModel::getScaleDelta).average().getAsDouble();
            double averageShear = set.getValue().stream().mapToDouble(AnalyzeModel::getShearDelta).average().getAsDouble();
            double averagePixel = set.getValue().stream().mapToDouble(AnalyzeModel::getPixelDelta).average().getAsDouble();
            AnalyzeModel AnalyzeModel = new AnalyzeModel();
            AnalyzeModel.setRotateDelta(averageRotate);
            AnalyzeModel.setShearDelta(averageShear);
            AnalyzeModel.setScaleDelta(averageScale);
            AnalyzeModel.setPixelDelta(averagePixel);
            AnalyzeModel.setScale(set.getKey());
            result.add(AnalyzeModel);
        }
        return result;
    }

    private List<AnalyzeModel> getMeanForValueShear(List<AnalyzeModel> items) {
        Map<Double, List<AnalyzeModel>> map = new LinkedHashMap<>();
        try {
            items.stream().forEach(plotItem -> {
                if (plotItem.getTransformValue().getScale() == 1.0 && plotItem.getTransformValue().getRotate() == 0.0) {
                    List<AnalyzeModel> result = map.get(plotItem.getTransformValue().getShear());
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(plotItem);
                    map.put(plotItem.getTransformValue().getShear(), result);
                }
            });
        } catch (NullPointerException e) {

        }
        List<AnalyzeModel> result = new ArrayList<>();
        for (Map.Entry<Double, List<AnalyzeModel>> set : map.entrySet()) {
            double averageRotate = set.getValue().stream().mapToDouble(AnalyzeModel::getRotateDelta).average().getAsDouble();
            double averageScale = set.getValue().stream().mapToDouble(AnalyzeModel::getScaleDelta).average().getAsDouble();
            double averageShear = set.getValue().stream().mapToDouble(AnalyzeModel::getShearDelta).average().getAsDouble();
            double averagePixel = set.getValue().stream().mapToDouble(AnalyzeModel::getPixelDelta).average().getAsDouble();
            AnalyzeModel AnalyzeModel = new AnalyzeModel();
            AnalyzeModel.setRotateDelta(averageRotate);
            AnalyzeModel.setShearDelta(averageShear);
            AnalyzeModel.setScaleDelta(averageScale);
            AnalyzeModel.setPixelDelta(averagePixel);
            AnalyzeModel.setShear(set.getKey());
            result.add(AnalyzeModel);
        }
        return result;
    }

    public XYChart plotFeatures(List<AnalyzeModel> experiments) {
        XYChart xyChart = new XYChartBuilder().width(1000).height(300)
                .title(experiments.get(0).getName())
                .xAxisTitle("scale changes (k)")
                .yAxisTitle("number of key points")
                .build();
        setStyle(xyChart);
        double[] scale = experiments.stream().mapToDouble(AnalyzeModel::getScale).toArray();

        double[] keypoints = experiments.stream().mapToDouble(AnalyzeModel::getFeaturesDetected2Image).toArray();
        xyChart.addSeries("NP", scale, keypoints);

        double[] nndr = experiments.stream().mapToDouble(AnalyzeModel::getNndrMatchingFeatures).toArray();
        xyChart.addSeries("NM", scale, nndr);

        double[] ransac = experiments.stream().mapToDouble(AnalyzeModel::getRansacMatchingFeatures).toArray();
        xyChart.addSeries("NI", scale, ransac);

        xyChart.addSeries("NP_CC2", scale, experiments.stream().mapToDouble(AnalyzeModel::getNP_CC2).toArray());
        xyChart.addSeries("NP_DSCC2", scale, experiments.stream().mapToDouble(AnalyzeModel::getNP_DSCC2).toArray());
        Map<Double, Object> o2 = new HashMap<>();
        experiments.forEach(plotItem -> o2.put(plotItem.getScale(), "" + plotItem.getScale()));
        xyChart.setXAxisLabelOverrideMap(o2);
        return xyChart;
    }

    public void plot(XYChart xyChart, List<AnalyzeModel> models, ToDoubleFunction<AnalyzeModel> mapper) {
        double[] scale = models.stream().mapToDouble(AnalyzeModel::getScale).toArray();
        double[] matching = models.stream().mapToDouble(mapper).filter(value ->
                value != Double.NaN
                        && value != Double.POSITIVE_INFINITY
                        && value != Double.NEGATIVE_INFINITY).toArray();
        try {
            xyChart.addSeries("" + models.get(0).getName(), scale, matching);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        Map<Double, Object> o2 = new HashMap<>();
        models.forEach(plotItem -> o2.put(plotItem.getScale(), "" + plotItem.getScale()));
        xyChart.setXAxisLabelOverrideMap(o2);
    }

    public XYChart plotCompressionFeatures(List<AnalyzeModel> models) {
        XYChart xyChart = new XYChartBuilder().width(500).height(1300)
                .title(models.get(0).getName())
                .xAxisTitle("Параметры сжатия")
                .yAxisTitle("Кол-во точек")
                .build();
        setStyle2(xyChart);
        xyChart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        Collections.sort(models, Comparator.comparingInt(AnalyzeModel::getCompression));
        double[] compression = models.stream().mapToDouble(AnalyzeModel::getCompression).toArray();
        xyChart.addSeries("NP", compression, models.stream().mapToDouble(AnalyzeModel::getFeaturesDetected2Image).toArray());
        xyChart.addSeries("NM", compression, models.stream().mapToDouble(AnalyzeModel::getNndrMatchingFeatures).toArray());
        xyChart.addSeries("NI", compression, models.stream().mapToDouble(AnalyzeModel::getRansacMatchingFeatures).toArray());
        xyChart.addSeries("NP_CC2", compression, models.stream().mapToDouble(AnalyzeModel::getNP_CC2).toArray());
        xyChart.addSeries("NP_DSCC2", compression, models.stream().mapToDouble(AnalyzeModel::getNP_DSCC2).toArray());

        Map<Double, Object> o2 = new HashMap<>();
        models.forEach(plotItem -> o2.put(Double.valueOf(plotItem.getCompression()), plotItem.getCompression() == 14 ? "PNG" : "" + plotItem.getCompression()));
        xyChart.setXAxisLabelOverrideMap(o2);

        return xyChart;
    }

    public void plotCompression(XYChart xyChart, List<AnalyzeModel> models, ToDoubleFunction<AnalyzeModel> mapper) {
        Collections.sort(models, Comparator.comparingInt(AnalyzeModel::getCompression));
        double[] compression = models.stream().mapToDouble(AnalyzeModel::getCompression).toArray();
        double[] matching = models.stream().mapToDouble(mapper).toArray();
        xyChart.addSeries("" + models.get(0).getName(), compression, matching);

        Map<Double, Object> o2 = new HashMap<>();
        models.forEach(plotItem -> o2.put(Double.valueOf(plotItem.getCompression()), plotItem.getCompression() == 14 ? "PNG" : "" + plotItem.getCompression()));
        xyChart.setXAxisLabelOverrideMap(o2);
    }

    private List<XYChart> createCharts() {
        List<XYChart> charts = new ArrayList<>();
        XYChart chart1 = new XYChartBuilder().width(500).height(1300)
                .title("Масштаб")
                .xAxisTitle("Параметры масштаба")
                .yAxisTitle("Параметер точности")
                .build();
        chart1.getStyler().setYAxisMax(0.05);
        charts.add(chart1);
        XYChart chart2 = new XYChartBuilder().width(1500).height(1000)
                .title("Сдвиг")
                .xAxisTitle("Параметры косого сдвига")
                .yAxisTitle("Параметер точности")
                .build();
        chart2.getStyler().setYAxisMax(0.05);
        charts.add(chart2);
        XYChart chart3 = new XYChartBuilder().width(1500).height(1000)
                .title("Поворот")
                .xAxisTitle("Параметры поворота")
                .yAxisTitle("Параметер точности")
                .build();
        //   this.chart3.getStyler().setYAxisMax(0.5);
        charts.add(chart3);
        XYChart chart4 = new XYChartBuilder().width(1500).height(1000)
                .title("Пиксели")
                .xAxisTitle("Параметр преобразования")
                .yAxisTitle("Параметер точности")
                .build();
        chart4.getStyler().setYAxisMax(5.0);
        charts.add(chart4);
        setStyle(chart1);
        setStyle(chart2);
        setStyle(chart3);
        setStyle(chart4);
        return charts;
    }

    private void setStyle(XYChart chart) {
        chart.getStyler().setXAxisLabelRotation(90);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendSeriesLineLength(1);
        chart.getStyler().setLegendLayout(Styler.LegendLayout.Horizontal);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideN);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
    }

    public void setStyle2(XYChart chart) {
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendLayout(Styler.LegendLayout.Horizontal);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setChartTitleFont(new Font(Font.SERIF, Font.PLAIN, 18));
        chart.getStyler().setAxisTitleFont(new Font(Font.SERIF, Font.PLAIN, 18));
        chart.getStyler().setLegendFont(new Font(Font.SERIF, Font.PLAIN, 14));
        Color[] result = new Color[]{
//                new Color(0, 0, 0),
//                new Color(255, 0, 19),
//                new Color(0, 139, 245, 180),
//                new Color(20, 155, 42, 180),
//                new Color(255, 104, 29),
                new Color(255, 0, 19),
                new Color(255, 104, 29),
                new Color(230, 197, 46),
                new Color(128, 26, 8),
                new Color(0, 0, 34),
                new Color(44, 171, 203),
                new Color(44, 25, 203),
                new Color(0, 171, 0),
//                new Color(0, 0, 0),
//                new Color(75, 75, 75),
//                new Color(185, 185, 185)
        };
        chart.getStyler().setSeriesColors(result);
//        chart.getStyler().setPlotBackgroundColor(new Color(246, 246, 246));
        Marker[] markers = new Marker[]{
                SeriesMarkers.CIRCLE,
                SeriesMarkers.DIAMOND,
                SeriesMarkers.SQUARE,
                SeriesMarkers.PLUS,
                SeriesMarkers.TRAPEZOID,
                SeriesMarkers.OVAL,
                SeriesMarkers.TRIANGLE_DOWN,
                SeriesMarkers.TRIANGLE_UP
        };
        //  chart.getStyler().setSeriesMarkers(markers);
    }
}
