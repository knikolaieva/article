package com.sytoss.article;

import java.util.List;

import com.orsoncharts.Chart3D;
import com.orsoncharts.fx.Chart3DViewer;
import com.sytoss.article.app.resolution.AppResolution;
import com.sytoss.article.app.resolution.SynthesizedImagesAppResolution;
import com.sytoss.article.handler.Plot3DHandler;
import com.sytoss.article.model.AnalyzeModel;
import com.sytoss.article.model.TypeModel;
import com.sytoss.article.utils.DataInitializer;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class VisualSynthesizedImages3DApplication extends Application {

    private static Node createPixelChart(String algorithm, List<AnalyzeModel> items) {
        Plot3DHandler plotHandler = new Plot3DHandler();
        Chart3D chart = plotHandler.plotPixel3D(items, algorithm);
        return new Chart3DViewer(chart);
    }

    private static Node createParamChart(String algorithm, TypeModel type, List<AnalyzeModel> items) {
        Plot3DHandler plotHandler = new Plot3DHandler();
        Chart3D chart = plotHandler.plotParam3D(items, algorithm, type);
        return new Chart3DViewer(chart);
    }

    private static Node createParamChart(String algorithm, List<AnalyzeModel> items) {
        Plot3DHandler plotHandler = new Plot3DHandler();
        Chart3D chart = plotHandler.plotParamFromDelta3D(items, algorithm);
        return new Chart3DViewer(chart);
    }

    @Override
    public void start(Stage stage) throws Exception {
        double[] scales = DataInitializer.getScaleData();
        double[] shears = DataInitializer.getShearData();
        double[] rotates = DataInitializer.getRotateData();

        String path = "out/result/SYTOSS_NURE_png_Pairs50_scale/result_building/tmp/";
        SynthesizedImagesAppResolution app = new SynthesizedImagesAppResolution(scales, shears, rotates);
        app.execute(path);

        SplitPane root = new SplitPane();
        SplitPane pixelSplitter = getPixelCharts(app);
        SplitPane scaleSplitter = getParamCharts(app, TypeModel.SCALE);
        SplitPane shearSplitter = getParamCharts(app, TypeModel.SHEAR);
        SplitPane rotateSplitter = getParamCharts(app, TypeModel.ROTATE);
        SplitPane pixelParamSplitter = getPixelParamCharts(app);
        Button button1 = new Button("Pixel delta");
        button1.setOnAction(event -> {
            root.getItems().set(1, pixelSplitter);
        });
        Button button2 = new Button("Scale-param delta");
        button2.setOnAction(event -> {
            root.getItems().set(1, scaleSplitter);
        });
        Button button3 = new Button("Shear-param delta");
        button3.setOnAction(event -> {
            root.getItems().set(1, shearSplitter);
        });
        Button button4 = new Button("Rotate-param delta");
        button4.setOnAction(event -> {
            root.getItems().set(1, rotateSplitter);
        });
        Button button5 = new Button("Pixel-param delta");
        button5.setOnAction(event -> {
            root.getItems().set(1, pixelParamSplitter);
        });

        SplitPane options = new SplitPane();
        options.getItems().addAll(button1, button2, button3, button4, button5);

        root.setOrientation(Orientation.VERTICAL);
        root.getItems().addAll(options, pixelSplitter);
        root.setDividerPositions(0.05f, 0.95f);

        Scene scene = new Scene(root, 1768, 712);
        stage.setScene(scene);
        stage.setTitle("Charts:");
        stage.show();
    }

    private static SplitPane getPixelCharts(AppResolution app){
        SplitPane splitter = new SplitPane();
        Node pixelNode1 = createPixelChart("SIFT", app.getSift());
        Node pixelNode2 = createPixelChart("SURF128", app.getSurf128());
        Node pixelNode3 = createPixelChart("ORB", app.getOrb());
        Node pixelNode4 = createPixelChart("SURF64", app.getSurf64());
        Node pixelNode5 = createPixelChart("ORB1000", app.getOrb1000());
        Node pixelNode6 = createPixelChart("KAZE", app.getKaze());
        Node pixelNode7 = createPixelChart("BRISK", app.getBrisk());
        Node pixelNode8 = createPixelChart("AKAZE",app.getAkaze());

        SplitPane splitter1 = getSplitterRow(pixelNode1, pixelNode2);
        SplitPane splitter2 = getSplitterRow(pixelNode3, pixelNode4);
        SplitPane splitter3 = getSplitterRow(pixelNode5, pixelNode6);
        SplitPane splitter4 = getSplitterRow(pixelNode7, pixelNode8);
        splitter.getItems().addAll(splitter1, splitter2, splitter3, splitter4);
        splitter.setDividerPositions(0.25f, 0.5f, 0.75f, 1.0f);
        return splitter;
    }

    private static SplitPane getPixelParamCharts(AppResolution app){
        SplitPane splitter = new SplitPane();
        Node pixelNode1 = createParamChart("SIFT", app.getSift());
        Node pixelNode2 = createParamChart("SURF128", app.getSurf128());
        Node pixelNode3 = createParamChart("ORB", app.getOrb());
        Node pixelNode4 = createParamChart("SURF64", app.getSurf64());
        Node pixelNode5 = createParamChart("ORB1000", app.getOrb1000());
        Node pixelNode6 = createParamChart("KAZE", app.getKaze());
        Node pixelNode7 = createParamChart("BRISK", app.getBrisk());
        Node pixelNode8 = createParamChart("AKAZE",app.getAkaze());

        SplitPane splitter1 = getSplitterRow(pixelNode1, pixelNode2);
        SplitPane splitter2 = getSplitterRow(pixelNode3, pixelNode4);
        SplitPane splitter3 = getSplitterRow(pixelNode5, pixelNode6);
        SplitPane splitter4 = getSplitterRow(pixelNode7, pixelNode8);
        splitter.getItems().addAll(splitter1, splitter2, splitter3, splitter4);
        splitter.setDividerPositions(0.25f, 0.5f, 0.75f, 1.0f);
        return splitter;
    }

    private static SplitPane getParamCharts(AppResolution app, TypeModel type){
        SplitPane splitter = new SplitPane();
        Node pixelNode1 = createParamChart("SIFT", type, app.getSift());
        Node pixelNode2 = createParamChart("SURF128", type, app.getSurf128());
        Node pixelNode3 = createParamChart("ORB", type, app.getOrb());
        Node pixelNode4 = createParamChart("SURF64", type, app.getSurf64());
        Node pixelNode5 = createParamChart("ORB1000", type, app.getOrb1000());
        Node pixelNode6 = createParamChart("KAZE", type, app.getKaze());
        Node pixelNode7 = createParamChart("BRISK", type, app.getBrisk());
        Node pixelNode8 = createParamChart("AKAZE", type,app.getAkaze());

        SplitPane splitter1 = getSplitterRow(pixelNode1, pixelNode2);
        SplitPane splitter2 = getSplitterRow(pixelNode3, pixelNode4);
        SplitPane splitter3 = getSplitterRow(pixelNode5, pixelNode6);
        SplitPane splitter4 = getSplitterRow(pixelNode7, pixelNode8);
        splitter.getItems().addAll(splitter1, splitter2, splitter3, splitter4);
        splitter.setDividerPositions(0.25f, 0.5f, 0.75f, 1.0f);
        return splitter;
    }

    private static SplitPane getSplitterRow(Node node1, Node node2){
        SplitPane splitter1 = new SplitPane();
        splitter1.setOrientation(Orientation.VERTICAL);
        StackPane sp1 = new StackPane();
        sp1.getChildren().add(node1);
        StackPane sp2 = new StackPane();
        sp2.getChildren().add(node2);
        splitter1.getItems().addAll(sp1, sp2);
        return splitter1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
