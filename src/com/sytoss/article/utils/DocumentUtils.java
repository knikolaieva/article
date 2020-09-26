package com.sytoss.article.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.opencv.core.Size;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import com.sytoss.article.model.AnalyzeModel;

public final class DocumentUtils {

    private static void setOrientationToAlbum(XWPFDocument document) {
        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();

        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
        CTSectPr section = body.getSectPr();

        if (!section.isSetPgSz()) {
            section.addNewPgSz();
        }
        CTPageSz pageSize = section.getPgSz();
        pageSize.setW(BigInteger.valueOf(15840));
        pageSize.setH(BigInteger.valueOf(12240));
    }

    public static void writeTitle(XWPFDocument document, File image1, File image2, String path, Size size1, Size size2) {
        setOrientationToAlbum(document);
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setText("Normalization results for pairs " + image1.getName() + "_" + image2.getName() + " from subset " + image1.getParent().substring(image1.getParent().indexOf("dataset") + 8));
        paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        try {
            int width1 = (int) (size1.width / (size1.height / 100));
            int width2 = (int) (size2.width / (size2.height / 100));
            FileInputStream fileInputStream1 = new FileInputStream(path + "/image1_original.jpg");
            FileInputStream fileInputStream2 = new FileInputStream(path + "/image2_original.jpg");

            run.setText(image1.getName() + ": ");
            run.addPicture(fileInputStream1, XWPFDocument.PICTURE_TYPE_PNG, image1.getName(), Units.toEMU(width1), Units.toEMU(100));
            run.setText(image2.getName() + ": ");
            run.addPicture(fileInputStream2, XWPFDocument.PICTURE_TYPE_PNG, image2.getName(), Units.toEMU(width2), Units.toEMU(100));

            fileInputStream1.close();
            fileInputStream2.close();
            Files.delete(Paths.get(path + "/image1_original.jpg"));
            Files.delete(Paths.get(path + "/image2_original.jpg"));
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeExperimentOfMinimaxDelta(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setText("Table 1 - Found homography parameters and normalization accuracy assessment");

        XWPFTable table = document.createTable();
        DecimalFormat df = new DecimalFormat("#.###");
        XWPFTableRow tableRowOne = table.getRow(0);
        XWPFTableCell cell = tableRowOne.getCell(0);
        cell.setText("Original:");
        XWPFParagraph paragraph2 = cell.addParagraph();
        run = paragraph2.createRun();
        run.setText("img1: " + df.format(list.get(0).getOriginalOverlapRatioImg1()));
        run.addBreak();
        run = paragraph2.createRun();
        run.setText("img2: " + df.format(list.get(0).getOriginalOverlapRatioImg2()));

        tableRowOne.addNewTableCell().setText("h11");
        tableRowOne.addNewTableCell().setText("h12");
        tableRowOne.addNewTableCell().setText("h13");
        tableRowOne.addNewTableCell().setText("h21");
        tableRowOne.addNewTableCell().setText("h22");
        tableRowOne.addNewTableCell().setText("h23");
        tableRowOne.addNewTableCell().setText("h31");
        tableRowOne.addNewTableCell().setText("h32");
        tableRowOne.addNewTableCell().setText("h33");

        if (list.get(0).getTransformationMatrix() != null) {
            tableRowOne.addNewTableCell().setText("avg ErrPar (param)");
        }
        tableRowOne.addNewTableCell().setText("avg ErrIM (pixel)");
        tableRowOne.addNewTableCell().setText("max ErrIM");
        if (list.get(0).getTransformationMatrix() != null) {
            tableRowOne.addNewTableCell().setText("avg ErrCPM (corner)");
            tableRowOne.addNewTableCell().setText("max ErrCPM");
        }
        tableRowOne.addNewTableCell().setText("Expert rate");

        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();

            int index = 0;
            tableRowTwo.getCell(index++).setText("" + model.getName());
            if (model.getHomoghraphy() == null) {
                model.setHomoghraphy(new double[3][3]);
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    tableRowTwo.getCell(index++).setText(df.format(model.getHomoghraphy()[i][j]));
                }
            }
            if (list.get(0).getTransformationMatrix() != null) {
                tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrPar()));
            }
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrIM()));
            tableRowTwo.getCell(index++).setText(df.format(model.getMaxErrIM()));
            if (list.get(0).getTransformationMatrix() != null) {
                tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrCPM()));
                tableRowTwo.getCell(index).setText(df.format(model.getMaxErrCPM()));
            }
        }
        XWPFTableRow tableRowThree = table.createRow();

        double[][] matrix = list.get(0).getTransformationMatrix();
        if (matrix != null) {
            int index = 0;
            tableRowThree.getCell(index++).setText("Original matrix");
            if (matrix != null) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        tableRowThree.getCell(index++).setText(df.format(matrix[i][j]));
                    }
                }
            }
        }
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("where h11, h12, h13, h21, h22, h23, h31, h32, h33 - parameters of found homography matrix H* or of original one H; ");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("avgErrPar - average error of found parameters, the accuracy of found homography matrix parameters with a preliminary minimax normalization of found and original matrices;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("avgErrIM - average error of inlier coordinate matching;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("inliers - correct matches; outliers - false matches;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("maxErrIM – maximum error of inlier coordinate matching;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("avgErrCPM - average error of corner point matching;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("maxErrCPM - maximum error of corner point matching;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("expert rate - experts assessment of normalization accuracy with a 5-point scale: 0 - normalization failed, 1 - insufficient, 2 - satisfactory, 3 - good, 4 – excellent.");
    }

    public static void writeExperimentOfOverlapArea(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.addBreak();
        run.setText("Table 2 - Quantitative evaluation of descriptors, precision and recall estimation (with found overlap)");

        XWPFTable table = document.createTable();
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("Name");
        tableRowOne.addNewTableCell().setText("NP1");
        tableRowOne.addNewTableCell().setText("NP2");
        tableRowOne.addNewTableCell().setText("NM");
        tableRowOne.addNewTableCell().setText("NI");
        tableRowOne.addNewTableCell().setText("DP1");
        tableRowOne.addNewTableCell().setText("DP2");
        tableRowOne.addNewTableCell().setText("OP1");
        tableRowOne.addNewTableCell().setText("OP2");
        tableRowOne.addNewTableCell().setText("NPO1");
        tableRowOne.addNewTableCell().setText("NPO2");
        tableRowOne.addNewTableCell().setText("NMO");
        tableRowOne.addNewTableCell().setText("DPO1");
        tableRowOne.addNewTableCell().setText("DPO2");
        tableRowOne.addNewTableCell().setText("Precision");
        tableRowOne.addNewTableCell().setText("RecallO1");
        tableRowOne.addNewTableCell().setText("Comments");
        DecimalFormat df = new DecimalFormat("#.####");

        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();
            int index = 0;
            tableRowTwo.getCell(index++).setText(model.getName());
            tableRowTwo.getCell(index++).setText(df.format(model.getNP1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNP2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNM()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNI()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDP1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDP2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getOP1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getOP2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNPO1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNPO2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNMO()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDPO1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDPO2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getPrecision()));
            tableRowTwo.getCell(index++).setText(df.format(model.getRecallO1()));
            tableRowTwo.getCell(index).setText("");
        }

        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("where NP - number of found key points;  NP1, NP2 - number of found key points for image1 and image2 respectively;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("NM - number of matches found with NNDR method, NM=NI+NO;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("NI - number of inliers found with RANSAC method;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("NO - number of outliers discarded with RANSAC method; ");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("DP - density of key points (per cent), DP= NP/(height x width) , height, width - vertical and horizontal image sizes respectively (pixels)");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("DP1, DP2 - density of key points points for image1 and image2 respectively;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("OP1, OP2 - overlap percentage for image1 and image2 respectively;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("NPO1, NPO2 - number of found key points on overlap for image1 and image2 respectivelyy;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("NMO - number of matches  on overlap found with NNDR method;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("DPO1, DPO2 - density of key points points on overlap for image1 and image2 respectively; ");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("OA  - overlap area (pixels), OA1, OA2  - overlap area for image1 and image2 respectively;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("DPO1= NPO1/OA1; DPO2= NPO2/OA2;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("Precision = NI/ NM - the rate to evaluate the part of inliers to all found matches with NNDR method;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("RecallO1 = NI/ NPO1 -  completeness of inlier retrieval relative to the number of all key points on overlap for image1. The rate illustrates usefulness of the found key points for normalization.");
    }


    public static void writeExperimentOfTime(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.addBreak();
        run.setText("Table 3 - Estimation of time costs");

        XWPFTable table = document.createTable();
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("Name");
        tableRowOne.addNewTableCell().setText("NP1");
        tableRowOne.addNewTableCell().setText("NP2");
        tableRowOne.addNewTableCell().setText("DesT1, sec");
        tableRowOne.addNewTableCell().setText("DesT2, sec");
        tableRowOne.addNewTableCell().setText("MatchT, sec");
        tableRowOne.addNewTableCell().setText("InlierT, sec");
        tableRowOne.addNewTableCell().setText("AvgDesT, ms");
        tableRowOne.addNewTableCell().setText("AvgMatchT, ms");
        tableRowOne.addNewTableCell().setText("AvgInlierT, ms");
        tableRowOne.addNewTableCell().setText("TotalNormT, sec");
        DecimalFormat df = new DecimalFormat("#.######");
        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();
            int index = 0;
            tableRowTwo.getCell(index++).setText(model.getName());
            tableRowTwo.getCell(index++).setText("" + model.getNP1());
            tableRowTwo.getCell(index++).setText("" + model.getNP2());
            tableRowTwo.getCell(index++).setText(df.format(model.getDesT1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDesT2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getMatchT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getInlierT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgDesT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgMatchT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgInlierT()));
            tableRowTwo.getCell(index).setText(df.format(model.getTotalTime()));
        }

        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("where DesT – descriptor creation time (time for key point detection and descriptor computation),  DesT1, DesT2 – creation time of descriptors for image1 and image2 respectively;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("MatchT - retrieval time of matching found with NNDR method for the pair;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("InlierT – inlier retrieval time found  with RANSAC method for the pair;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("AvgDesT – average time for one descriptor creation;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("AvgMatchT - average time for one match retrieval with NNDR method;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("AvgInlierT – average time for one inlier retrieval with RANSAC method;");
        run.addBreak();
        run = paragraph.createRun();
        run.setText("TotalNormT – total normalization time for the pair.");
    }


    public static void writeExperimentOfMinimaxDeltaSynthesized(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setText("Table 1.2");

        XWPFTable table = document.createTable();
        DecimalFormat df = new DecimalFormat("#.###");

        XWPFTableRow tableRowOne = table.getRow(0);

        XWPFTableCell cell = tableRowOne.getCell(0);
        cell.setText("");

        tableRowOne.addNewTableCell().setText("Name");
        tableRowOne.addNewTableCell().setText("h11");
        tableRowOne.addNewTableCell().setText("h12");
        tableRowOne.addNewTableCell().setText("h13");
        tableRowOne.addNewTableCell().setText("h21");
        tableRowOne.addNewTableCell().setText("h22");
        tableRowOne.addNewTableCell().setText("h23");
        tableRowOne.addNewTableCell().setText("h31");
        tableRowOne.addNewTableCell().setText("h32");
        tableRowOne.addNewTableCell().setText("h33");

        tableRowOne.addNewTableCell().setText("avg ErrPar (param)");
        tableRowOne.addNewTableCell().setText("avg ErrIM (pixel)");
        tableRowOne.addNewTableCell().setText("max ErrIM");
        tableRowOne.addNewTableCell().setText("avg ErrCPM (corner)");
        tableRowOne.addNewTableCell().setText("max ErrCPM");
        tableRowOne.addNewTableCell().setText("OP1");
        tableRowOne.addNewTableCell().setText("OP2");
        tableRowOne.addNewTableCell().setText("maxErrOP");
        tableRowOne.addNewTableCell().setText("Expert rate");

        int counter = 0;
        int counter2 = 0;
        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();

            int index = 0;
            if (counter % 8 == 0) {
                tableRowTwo.getCell(index++).setText("k=" + model.getTransformValue().getScale() +
                        " Original: img1:" + df.format(model.getOriginalOverlapRatioImg1())
                        + ";img2:" + df.format(model.getOriginalOverlapRatioImg2()));
                counter2 += 8;
            } else {
                tableRowTwo.getCell(index++).setText("");
            }

            tableRowTwo.getCell(index++).setText("" + model.getName());
            if (model.getHomoghraphy() == null) {
                model.setHomoghraphy(new double[3][3]);
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    tableRowTwo.getCell(index++).setText(df.format(model.getHomoghraphy()[i][j]));
                }
            }

            tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrPar()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrIM()));
            tableRowTwo.getCell(index++).setText(df.format(model.getMaxErrIM()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrCPM()));
            tableRowTwo.getCell(index++).setText(df.format(model.getMaxErrCPM()));

            tableRowTwo.getCell(index++).setText(df.format(model.getOP1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getOP2()));
            tableRowTwo.getCell(index).setText(df.format(Math.max((100.0 - model.getOP1()), (100.0 - model.getOP2()))));
            counter++;
        }
    }

    public static void writeExperimentOfOverlapAreaSynthesized(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.addBreak();
        run.setText("Table 1.1");

        XWPFTable table = document.createTable();
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("");
        tableRowOne.addNewTableCell().setText("Name");
        tableRowOne.addNewTableCell().setText("NP1");
        tableRowOne.addNewTableCell().setText("NP2");
        tableRowOne.addNewTableCell().setText("NM");
        tableRowOne.addNewTableCell().setText("NI");
        tableRowOne.addNewTableCell().setText("NP_CC2");
        tableRowOne.addNewTableCell().setText("NP_DSCC2");
        tableRowOne.addNewTableCell().setText("DP2");
        tableRowOne.addNewTableCell().setText("Precision");
        tableRowOne.addNewTableCell().setText("Recall");
        tableRowOne.addNewTableCell().setText("DetRep");
        tableRowOne.addNewTableCell().setText("DesRep");
        tableRowOne.addNewTableCell().setText("DetDesRep");
        tableRowOne.addNewTableCell().setText("Comments");
        DecimalFormat df = new DecimalFormat("#.####");
        DecimalFormat df2 = new DecimalFormat("#.######");

        int counter = 0;
        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();
            int index = 0;

            if (counter % 8 == 0) {
                tableRowTwo.getCell(index++).setText("k=" + model.getTransformValue().getScale());
            } else {
                tableRowTwo.getCell(index++).setText("");
            }

            tableRowTwo.getCell(index++).setText("" + model.getName());
            tableRowTwo.getCell(index++).setText("" + model.getNP1());
            tableRowTwo.getCell(index++).setText("" + model.getNP2());
            tableRowTwo.getCell(index++).setText("" + model.getNM());
            tableRowTwo.getCell(index++).setText("" + model.getNI());
            tableRowTwo.getCell(index++).setText(df.format(model.getNP_CC2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNP_DSCC2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDP2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getPrecision()));
            tableRowTwo.getCell(index++).setText(df.format(model.getRecallO1()));
            tableRowTwo.getCell(index++).setText(df2.format(model.getDetRep()));
            tableRowTwo.getCell(index++).setText(df2.format(model.getDesRep()));
            tableRowTwo.getCell(index++).setText(df2.format(model.getDetDesRep()));
            tableRowTwo.getCell(index).setText("");
            counter++;
        }
    }


    public static void writeExperimentOfTimeSynthesized(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.addBreak();
        run.setText("Table 1.3 ");

        XWPFTable table = document.createTable();
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("");
        tableRowOne.addNewTableCell().setText("Name");
        tableRowOne.addNewTableCell().setText("NP1");
        tableRowOne.addNewTableCell().setText("NP2");
        tableRowOne.addNewTableCell().setText("DesT1, sec");
        tableRowOne.addNewTableCell().setText("DesT2, sec");
        tableRowOne.addNewTableCell().setText("MatchT, sec");
        tableRowOne.addNewTableCell().setText("InlierT, sec");
        tableRowOne.addNewTableCell().setText("AvgDesT, ms");
        tableRowOne.addNewTableCell().setText("AvgMatchT, ms");
        tableRowOne.addNewTableCell().setText("AvgInlierT, ms");
        tableRowOne.addNewTableCell().setText("TotalNormT, sec");
        DecimalFormat df = new DecimalFormat("#.######");

        int counter = 0;
        int counter2 = 0;
        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();
            int index = 0;

            if (counter % 8 == 0) {
                tableRowTwo.getCell(index++).setText("k=" + model.getTransformValue().getScale());
            } else {
                tableRowTwo.getCell(index++).setText("");
            }


            tableRowTwo.getCell(index++).setText(model.getName());
            tableRowTwo.getCell(index++).setText(df.format(model.getNP1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNP2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDesT1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDesT2()));
            tableRowTwo.getCell(index++).setText(df.format(model.getMatchT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getInlierT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgDesT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgMatchT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgInlierT()));
            tableRowTwo.getCell(index).setText(df.format((model.getTotalTime())));
            counter++;
        }
    }

    public static void writeExperimentOfMinimaxDelta2(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setText("Table 1 - Found homography parameters and normalization accuracy assessment");

        XWPFTable table = document.createTable();
        DecimalFormat df = new DecimalFormat("#.###");
        XWPFTableRow tableRowOne = table.getRow(0);
        XWPFTableCell cell = tableRowOne.getCell(0);
        cell.setText("#");

        tableRowOne.addNewTableCell().setText("avg ErrPar (param)");
        tableRowOne.addNewTableCell().setText("avg ErrIM (pixel)");
        tableRowOne.addNewTableCell().setText("max ErrIM");
        tableRowOne.addNewTableCell().setText("avg ErrCPM (corner)");
        tableRowOne.addNewTableCell().setText("max ErrCPM");
        tableRowOne.addNewTableCell().setText("Expert rate");

        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();

            int index = 0;
            tableRowTwo.getCell(index++).setText("" + model.getName());
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrPar()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrIM()));
            tableRowTwo.getCell(index++).setText(df.format(model.getMaxErrIM()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrCPM()));
            tableRowTwo.getCell(index).setText(df.format(model.getMaxErrCPM()));
        }
    }

    public static void writeAverageExperimentOfOverlapArea(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.addBreak();
        run.setText("Table 1 - Quantitative evaluation of descriptors, precision and recall estimation (with found overlap)");

        XWPFTable table = document.createTable();
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("Name");
        tableRowOne.addNewTableCell().setText("NP");
        tableRowOne.addNewTableCell().setText("DP");
        tableRowOne.addNewTableCell().setText("NMO");
        tableRowOne.addNewTableCell().setText("NI");
        tableRowOne.addNewTableCell().setText("Precision");
        tableRowOne.addNewTableCell().setText("RecallO1");
        tableRowOne.addNewTableCell().setText("avg ErrIM (pixel)");
        tableRowOne.addNewTableCell().setText("max ErrIM");
        if (list.get(0).getTransformValue() != null) {
            tableRowOne.addNewTableCell().setText("avg ErrCPM (corner)");
            tableRowOne.addNewTableCell().setText("max ErrCPM");
        }
        DecimalFormat df = new DecimalFormat("#.###");

        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();
            int index = 0;
            tableRowTwo.getCell(index++).setText("" + model.getName());
            tableRowTwo.getCell(index++).setText(df.format(model.getNP()));
            tableRowTwo.getCell(index++).setText(df.format(model.getDP()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNMO()));
            tableRowTwo.getCell(index++).setText(df.format(model.getNI()));
            tableRowTwo.getCell(index++).setText(df.format(model.getPrecision()));
            tableRowTwo.getCell(index++).setText(df.format(model.getRecallO1()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrIM()));
            tableRowTwo.getCell(index++).setText(df.format(model.getMaxErrIM()));
            if (list.get(0).getTransformValue() != null) {
                tableRowTwo.getCell(index++).setText(df.format(model.getAvgErrCPM()));
                tableRowTwo.getCell(index).setText(df.format(model.getMaxErrCPM()));
            }
        }
    }

    public static void writeAverageExperiment_1(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setText("Table 1 - Average table");

        XWPFTable table = document.createTable();
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("Name");

        tableRowOne.addNewTableCell().setText("NP");
        tableRowOne.addNewTableCell().setText("NMO");
        tableRowOne.addNewTableCell().setText("NI");
        tableRowOne.addNewTableCell().setText("Precision");
        tableRowOne.addNewTableCell().setText("RecallO1");

        tableRowOne.addNewTableCell().setText("DesT, sec");
        tableRowOne.addNewTableCell().setText("MatchT, sec");
        tableRowOne.addNewTableCell().setText("InlierT, sec");
        tableRowOne.addNewTableCell().setText("AvgDesT, ms");
        tableRowOne.addNewTableCell().setText("AvgMatchT, ms");
        tableRowOne.addNewTableCell().setText("AvgInlierT, ms");
        tableRowOne.addNewTableCell().setText("TotalNormT, sec");
        tableRowOne.addNewTableCell().setText("ER winner");
        tableRowOne.addNewTableCell().setText("ER -1 and 0");
        DecimalFormat df = new DecimalFormat("#.######");
        DecimalFormat df2 = new DecimalFormat("#.###");
        for (AnalyzeModel model : list) {
            XWPFTableRow tableRowTwo = table.createRow();
            int index = 0;
            double AvgDesT = ((model.getFeaturesDetected1ImageTime() / model.getFeaturesDetected1Image()) + (model.getFeaturesDetected2ImageTime() / model.getFeaturesDetected2Image())) / 2;
            double AvgNndrT = model.getNndrMatchingFeaturesTime() / model.getNndrMatchingFeatures();
            double AvgInlierT = model.getRansacMatchingFeaturesTime() / model.getRansacMatchingFeatures();

            tableRowTwo.getCell(index++).setText(model.getName());
            tableRowTwo.getCell(index++).setText(df2.format(model.getNP()));
            tableRowTwo.getCell(index++).setText(df2.format(model.getNMO()));
            tableRowTwo.getCell(index++).setText(df2.format(model.getNI()));
            tableRowTwo.getCell(index++).setText(df2.format(model.getPrecision()));
            tableRowTwo.getCell(index++).setText(df2.format(model.getRecallO1()));

            tableRowTwo.getCell(index++).setText(df.format(model.getDesT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getMatchT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getInlierT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgDesT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgMatchT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getAvgInlierT()));
            tableRowTwo.getCell(index++).setText(df.format(model.getTotalTime()));
            tableRowTwo.getCell(index++).setText("" + model.getRate().getExpertRate4Count());
            tableRowTwo.getCell(index).setText("" + (model.getRate().getExpertRate_1Count() + model.getRate().getExpertRate0Count()));
        }
    }

    public static void writeAverageExperiment_2(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.addBreak();
        run.setText("Table 2 - Average table per 1-8 scale");

        XWPFTable table = document.createTable();
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("Name");
        tableRowOne.addNewTableCell().setText("NP");
        tableRowOne.addNewTableCell().setText("NMO");
        tableRowOne.addNewTableCell().setText("NI");
        tableRowOne.addNewTableCell().setText("Precision");
        tableRowOne.addNewTableCell().setText("RecallO1");

        tableRowOne.addNewTableCell().setText("DesT, sec");
        tableRowOne.addNewTableCell().setText("MatchT, sec");
        tableRowOne.addNewTableCell().setText("InlierT, sec");
        tableRowOne.addNewTableCell().setText("AvgDesT, ms");
        tableRowOne.addNewTableCell().setText("AvgMatchT, ms");
        tableRowOne.addNewTableCell().setText("AvgInlierT, ms");
        tableRowOne.addNewTableCell().setText("TotalNormT, sec");
        tableRowOne.addNewTableCell().setText("ER winner");
        tableRowOne.addNewTableCell().setText("ER -1 and 0");

        DecimalFormat df = new DecimalFormat("#.######");
        DecimalFormat df2 = new DecimalFormat("#.#");

        double[] NPScales = rankArrayByMaxScale(list.stream().mapToDouble(AnalyzeModel::getNP).toArray());
        double[] NMOScales = rankArrayByMaxScale(list.stream().mapToDouble(AnalyzeModel::getNMO).toArray());
        double[] NIScales = rankArrayByMaxScale(list.stream().mapToDouble(AnalyzeModel::getNI).toArray());
        double[] PrecisionScales = rankArrayByMaxScale(list.stream().mapToDouble(AnalyzeModel::getPrecision).toArray());
        double[] RecallO1Scales = rankArrayByMaxScale(list.stream().mapToDouble(AnalyzeModel::getRecallO1).toArray());

        double[] DesTScales = rankArrayByMinScale(list.stream().mapToDouble(AnalyzeModel::getDesT).toArray());
        double[] MatchTScales = rankArrayByMinScale(list.stream().mapToDouble(AnalyzeModel::getMatchT).toArray());
        double[] InlierTScales = rankArrayByMinScale(list.stream().mapToDouble(AnalyzeModel::getInlierT).toArray());
        double[] AvgDesTScales = rankArrayByMinScale(list.stream().mapToDouble(AnalyzeModel::getAvgDesT).toArray());
        double[] AvgNndrTScales = rankArrayByMinScale(list.stream().mapToDouble(AnalyzeModel::getAvgMatchT).toArray());
        double[] AvgInlierTScales = rankArrayByMinScale(list.stream().mapToDouble(AnalyzeModel::getInlierT).toArray());
        double[] TotalNormTScales = rankArrayByMinScale(list.stream().mapToDouble(AnalyzeModel::getTotalTime).toArray());
        double[] ERWScales = rankArrayByMaxScale(list.stream().mapToDouble(AnalyzeModel::getER4).toArray());
        double[] ERLScales = rankArrayByMinScale(list.stream().mapToDouble(AnalyzeModel::getER_1_0).toArray());

        for (int i = 0; i < 9; i++) {
            XWPFTableRow tableRowTwo = table.createRow();
            int index = 0;
            String name;
            if (i == 0) {
                name = "max-min";
            } else {
                name = list.get(i - 1).getName();
            }
            tableRowTwo.getCell(index++).setText("" + name);
            tableRowTwo.getCell(index++).setText(df2.format(NPScales[i]));
            tableRowTwo.getCell(index++).setText(df2.format(NMOScales[i]));
            tableRowTwo.getCell(index++).setText(df2.format(NIScales[i]));
            tableRowTwo.getCell(index++).setText(df.format(PrecisionScales[i]));
            tableRowTwo.getCell(index++).setText(df.format(RecallO1Scales[i]));
            if (i == 0) {
                tableRowTwo.getCell(index++).setText(df.format(DesTScales[i] / 1000000000.0));
            } else {
                tableRowTwo.getCell(index++).setText(df2.format(DesTScales[i]));
            }
            if (i == 0) {
                tableRowTwo.getCell(index++).setText(df.format(MatchTScales[i] / 1000000000.0));
            } else {
                tableRowTwo.getCell(index++).setText(df2.format(MatchTScales[i]));
            }
            if (i == 0) {
                tableRowTwo.getCell(index++).setText(df.format(InlierTScales[i] / 1000000000.0));
            } else {
                tableRowTwo.getCell(index++).setText(df2.format(InlierTScales[i]));
            }
            if (i == 0) {
                tableRowTwo.getCell(index++).setText(df.format(AvgDesTScales[i] / 1000000.0));
            } else {
                tableRowTwo.getCell(index++).setText(df2.format(AvgDesTScales[i]));
            }
            if (i == 0) {
                tableRowTwo.getCell(index++).setText(df.format(AvgNndrTScales[i] / 1000000.0));
            } else {
                tableRowTwo.getCell(index++).setText(df2.format(AvgNndrTScales[i]));
            }
            if (i == 0) {
                tableRowTwo.getCell(index++).setText(df.format(AvgInlierTScales[i] / 1000000.0));
            } else {
                tableRowTwo.getCell(index++).setText(df2.format(AvgInlierTScales[i]));
            }
            if (i == 0) {
                tableRowTwo.getCell(index++).setText(df.format(TotalNormTScales[i] / 1000000000.0));
            } else {
                tableRowTwo.getCell(index++).setText(df2.format(TotalNormTScales[i]));
            }
            tableRowTwo.getCell(index++).setText(df2.format(ERWScales[i]));
            tableRowTwo.getCell(index).setText(df2.format(ERLScales[i]));
        }
    }

    private static double[] rankArrayByMaxScale(double[] doubleArray) {
        double[] scales = new double[9];
        double min = Arrays.stream(doubleArray).min().getAsDouble();
        double max = Arrays.stream(doubleArray).max().getAsDouble();
        double step = (max - min) / 8;
        scales[0] = (max - min);
        int counter = 1;
        for (double value : doubleArray) {
            if (value >= min && value <= min + step) {
                scales[counter] = 1;
            } else if (value > step && value <= min + (2 * step)) {
                scales[counter] = 2;
            } else if (value > min + (2 * step) && value <= min + (3 * step)) {
                scales[counter] = 3;
            } else if (value > min + (3 * step) && value <= min + (4 * step)) {
                scales[counter] = 4;
            } else if (value > min + (4 * step) && value <= min + (5 * step)) {
                scales[counter] = 5;
            } else if (value > min + (5 * step) && value <= min + (6 * step)) {
                scales[counter] = 6;
            } else if (value > min + (6 * step) && value <= min + (7 * step)) {
                scales[counter] = 7;
            } else if (value > min + (7 * step) && value <= min + (8 * step)) {
                scales[counter] = 8;
            }
            counter++;
        }

        return scales;
    }


    private static double[] rankArrayByMinScale(double[] doubleArray) {
        double[] scales = new double[9];
        double min = Arrays.stream(doubleArray).min().getAsDouble();
        double max = Arrays.stream(doubleArray).max().getAsDouble();
        double step = (max - min) / 8;
        scales[0] = (max - min);
        int counter = 1;
        for (double value : doubleArray) {
            if (value >= min && value <= min + step) {
                scales[counter] = 8;
            } else if (value > step && value <= min + (2 * step)) {
                scales[counter] = 7;
            } else if (value > min + (2 * step) && value <= min + (3 * step)) {
                scales[counter] = 6;
            } else if (value > min + (3 * step) && value <= min + (4 * step)) {
                scales[counter] = 5;
            } else if (value > min + (4 * step) && value <= min + (5 * step)) {
                scales[counter] = 4;
            } else if (value > min + (5 * step) && value <= min + (6 * step)) {
                scales[counter] = 3;
            } else if (value > min + (6 * step) && value <= min + (7 * step)) {
                scales[counter] = 2;
            } else if (value > min + (7 * step) && value <= min + (8 * step)) {
                scales[counter] = 1;
            }
            counter++;
        }

        return scales;
    }

    public static void writeFeaturesData(XWPFDocument document, List<AnalyzeModel> list) {
        XWPFTable table = document.createTable();

        XWPFTableRow tableRowOne = table.getRow(0);

        tableRowOne.getCell(0).setText("# " + list.get(0).getName());
        tableRowOne.addNewTableCell().setText("кол-во соответ после ранзака/кол_во найд соответствий (до ранзака)");
        tableRowOne.addNewTableCell().setText("кол-во соответ после ранзака/кол_во точек на эталоне");
        tableRowOne.addNewTableCell().setText("кол-во соответствий (до ранзака)/кол_во точек на эталоне");

        DecimalFormat df = new DecimalFormat("#.##");
        for (int i = 0; i < list.size(); i++) {
            AnalyzeModel experiment = list.get(i);

            XWPFTableRow tableRow = table.createRow();

            tableRow.getCell(0).setText("" + (i + 1));
            tableRow.getCell(1).setText(df.format(experiment.getRansacMatchingFeatures() / experiment.getNndrMatchingFeatures()));
            tableRow.getCell(2).setText(df.format(experiment.getRansacMatchingFeatures() / experiment.getFeaturesDetected1Image()));
            tableRow.getCell(3).setText(df.format(experiment.getNndrMatchingFeatures() / experiment.getFeaturesDetected1Image()));
        }

        double RansacMatchingFeaturesByFeaturesDetected = list.stream().filter(p -> (p.getRansacMatchingFeatures() > 0 && p.getNndrMatchingFeatures() > 0)).mapToDouble(p -> p.getRansacMatchingFeatures() / p.getNndrMatchingFeatures()).average().getAsDouble();
        double RansacMatchingFeaturesByFeaturesDetected1Image = list.stream().filter(p -> (p.getRansacMatchingFeatures() > 0 && p.getNndrMatchingFeatures() > 0)).mapToDouble(p -> p.getRansacMatchingFeatures() / p.getFeaturesDetected1Image()).average().getAsDouble();
        double FeaturesDetectedByFeaturesDetected1Image = list.stream().filter(p -> (p.getRansacMatchingFeatures() > 0 && p.getNndrMatchingFeatures() > 0)).mapToDouble(p -> p.getNndrMatchingFeatures() / p.getFeaturesDetected1Image()).average().getAsDouble();

        XWPFTableRow tableRow = table.createRow();
        tableRow.getCell(0).setText("Average");
        tableRow.getCell(1).setText(df.format(RansacMatchingFeaturesByFeaturesDetected));
        tableRow.getCell(2).setText(df.format(RansacMatchingFeaturesByFeaturesDetected1Image));
        tableRow.getCell(3).setText(df.format(FeaturesDetectedByFeaturesDetected1Image));
    }

    public static void writePngJpgRealExperiments(XWPFDocument document, List<AnalyzeModel> pngList, List<AnalyzeModel> jpgList) {
        setOrientationToAlbum(document);

        XWPFTable table = document.createTable();

        XWPFTableRow tableRowOne = table.getRow(0);

        tableRowOne.getCell(0).setText("Name");

        tableRowOne.addNewTableCell().setText("PNG среднее д. пиксель");
        tableRowOne.addNewTableCell().setText("PNG кол-во д. пикселей [0, 1.4]");
        tableRowOne.addNewTableCell().setText("PNG кол-во д. пикселей [1.5, 2.4]");
        tableRowOne.addNewTableCell().setText("PNG кол-во д. пикселей [2.5, 3.4]");
        tableRowOne.addNewTableCell().setText("PNG кол-во д. пикселей >3.5");

        tableRowOne.addNewTableCell().setText("Name");
        tableRowOne.addNewTableCell().setText("JPG среднее д. пиксель");
        tableRowOne.addNewTableCell().setText("JPG кол-во д. пикселей [0, 1.4]");
        tableRowOne.addNewTableCell().setText("JPG кол-во д. пикселей [1.5, 2.4]");
        tableRowOne.addNewTableCell().setText("JPG кол-во д. пикселей [2.5, 3.4]");
        tableRowOne.addNewTableCell().setText("JPG кол-во д. пикселей >3.5");

        for (int i = 0; i < pngList.size(); i++) {
            AnalyzeModel pngExperiment = pngList.get(i);
            AnalyzeModel jpgExperiment = jpgList.get(i);

            XWPFTableRow tableRow = table.createRow();
            DecimalFormat df = new DecimalFormat("#.###");

            tableRow.getCell(0).setText("" + pngExperiment.getName());
            tableRow.getCell(2).setText(df.format(pngExperiment.getPixelDelta()));
            tableRow.getCell(3).setText("" + pngExperiment.getRange1Count());
            tableRow.getCell(4).setText("" + pngExperiment.getRange2Count());
            tableRow.getCell(5).setText("" + pngExperiment.getRange3Count());
            tableRow.getCell(6).setText("" + pngExperiment.getRange4Count());

            tableRow.getCell(7).setText("" + jpgExperiment.getName());
            tableRow.getCell(9).setText(df.format(jpgExperiment.getPixelDelta()));
            tableRow.getCell(10).setText("" + jpgExperiment.getRange1Count());
            tableRow.getCell(11).setText("" + jpgExperiment.getRange2Count());
            tableRow.getCell(12).setText("" + jpgExperiment.getRange3Count());
            tableRow.getCell(13).setText("" + jpgExperiment.getRange4Count());

            for (int j = 1; j < 7; j++) {
                XWPFTableCell cell = tableRow.getCell(j);
                if (pngExperiment.getPixelDelta() >= 0 && pngExperiment.getPixelDelta() <= 1.4) {
                    cell.setColor("FF0013");
                } else if (pngExperiment.getPixelDelta() > 1.4 && pngExperiment.getPixelDelta() <= 2.4) {
                    cell.setColor("FF681D");
                } else if (pngExperiment.getPixelDelta() > 2.4 && pngExperiment.getPixelDelta() <= 3.4) {
                    cell.setColor("FFCC22");
                } else if (pngExperiment.getPixelDelta() > 3.4) {
                    cell.setColor("C3E205");
                }
            }

            for (int j = 8; j < 14; j++) {
                XWPFTableCell cell = tableRow.getCell(j);
                if (jpgExperiment.getPixelDelta() >= 0 && jpgExperiment.getPixelDelta() <= 1.4) {
                    cell.setColor("FF0013");
                } else if (jpgExperiment.getPixelDelta() > 1.4 && jpgExperiment.getPixelDelta() <= 2.4) {
                    cell.setColor("FF681D");
                } else if (jpgExperiment.getPixelDelta() > 2.4 && jpgExperiment.getPixelDelta() <= 3.4) {
                    cell.setColor("FFCC22");
                } else if (jpgExperiment.getPixelDelta() > 3.4) {
                    cell.setColor("C3E205");
                }
            }
        }

        document.createParagraph().createRun().setText("");
    }

    public static void writePngJpgExperiments(XWPFDocument document, List<AnalyzeModel> pngList, List<AnalyzeModel> jpgList) {
        setOrientationToAlbum(document);

        XWPFTable table = document.createTable();

        XWPFTableRow tableRowOne = table.getRow(0);

        tableRowOne.getCell(0).setText("параметр \n масштаба");
        tableRowOne.addNewTableCell().setText("параметр \n косого сдвига");
        tableRowOne.addNewTableCell().setText("параметр \n поворота");

        tableRowOne.addNewTableCell().setText("PNG д. масштаба");
        tableRowOne.addNewTableCell().setText("PNG д. косого сдвига");
        tableRowOne.addNewTableCell().setText("PNG д. поворота");
        tableRowOne.addNewTableCell().setText("PNG среднее д. пиксель");
        tableRowOne.addNewTableCell().setText("PNG кол-во д. пикселей [0, 1.4]");
        tableRowOne.addNewTableCell().setText("PNG кол-во д. пикселей [1.5, 2.4]");
        tableRowOne.addNewTableCell().setText("PNG кол-во д. пикселей [2.5, 3.4]");
        tableRowOne.addNewTableCell().setText("PNG кол-во д. пикселей >3.5");

        tableRowOne.addNewTableCell().setText("JPG д. масштаба");
        tableRowOne.addNewTableCell().setText("JPG д. косого сдвига");
        tableRowOne.addNewTableCell().setText("JPG д. поворота");
        tableRowOne.addNewTableCell().setText("JPG среднее д. пиксель");
        tableRowOne.addNewTableCell().setText("JPG кол-во д. пикселей [0, 1.4]");
        tableRowOne.addNewTableCell().setText("JPG кол-во д. пикселей [1.5, 2.4]");
        tableRowOne.addNewTableCell().setText("JPG кол-во д. пикселей [2.5, 3.4]");
        tableRowOne.addNewTableCell().setText("JPG кол-во д. пикселей >3.5");

        for (int i = 0; i < pngList.size(); i++) {
            AnalyzeModel pngExperiment = pngList.get(i);
            AnalyzeModel jpgExperiment = jpgList.get(i);

            XWPFTableRow tableRow = table.createRow();
            DecimalFormat df = new DecimalFormat("#.###");
            tableRow.getCell(0).setText("" + pngExperiment.getTransformValue().getScale());
            tableRow.getCell(1).setText("" + pngExperiment.getTransformValue().getShear());
            tableRow.getCell(2).setText("" + pngExperiment.getTransformValue().getRotate());

            tableRow.getCell(4).setText(df.format(pngExperiment.getScaleDelta()));
            tableRow.getCell(5).setText(df.format(pngExperiment.getShearDelta()));
            tableRow.getCell(6).setText(df.format(pngExperiment.getRotateDelta()));
            tableRow.getCell(7).setText(df.format(pngExperiment.getPixelDelta()));
            tableRow.getCell(8).setText("" + pngExperiment.getRange1Count());
            tableRow.getCell(9).setText("" + pngExperiment.getRange2Count());
            tableRow.getCell(10).setText("" + pngExperiment.getRange3Count());
            tableRow.getCell(11).setText("" + pngExperiment.getRange4Count());

            tableRow.getCell(13).setText(df.format(jpgExperiment.getScaleDelta()));
            tableRow.getCell(14).setText(df.format(jpgExperiment.getShearDelta()));
            tableRow.getCell(15).setText(df.format(jpgExperiment.getRotateDelta()));
            tableRow.getCell(16).setText(df.format(jpgExperiment.getPixelDelta()));
            tableRow.getCell(17).setText("" + jpgExperiment.getRange1Count());
            tableRow.getCell(18).setText("" + jpgExperiment.getRange2Count());
            tableRow.getCell(19).setText("" + jpgExperiment.getRange3Count());
            tableRow.getCell(20).setText("" + jpgExperiment.getRange4Count());

            for (int j = 3; j < 12; j++) {
                XWPFTableCell cell = tableRow.getCell(j);
                if (pngExperiment.getPixelDelta() >= 0 && pngExperiment.getPixelDelta() <= 1.4) {
                    cell.setColor("FF0013");
                } else if (pngExperiment.getPixelDelta() > 1.4 && pngExperiment.getPixelDelta() <= 2.4) {
                    cell.setColor("FF681D");
                } else if (pngExperiment.getPixelDelta() > 2.4 && pngExperiment.getPixelDelta() <= 3.4) {
                    cell.setColor("FFCC22");
                } else if (pngExperiment.getPixelDelta() > 3.4) {
                    cell.setColor("C3E205");
                }
            }

            for (int j = 12; j < 21; j++) {
                XWPFTableCell cell = tableRow.getCell(j);
                if (jpgExperiment.getPixelDelta() >= 0 && jpgExperiment.getPixelDelta() <= 1.4) {
                    cell.setColor("FF0013");
                } else if (jpgExperiment.getPixelDelta() > 1.4 && jpgExperiment.getPixelDelta() <= 2.4) {
                    cell.setColor("FF681D");
                } else if (jpgExperiment.getPixelDelta() > 2.4 && jpgExperiment.getPixelDelta() <= 3.4) {
                    cell.setColor("FFCC22");
                } else if (jpgExperiment.getPixelDelta() > 3.4) {
                    cell.setColor("C3E205");
                }
            }
        }

        document.createParagraph().createRun().setText("");
    }

    public static void writeExperiments(XWPFDocument document, List<AnalyzeModel> list) {
        setOrientationToAlbum(document);

        XWPFTable table = document.createTable();

        XWPFTableRow tableRowOne = table.getRow(0);

        tableRowOne.getCell(0).setText("параметр \n масштаба");
        tableRowOne.addNewTableCell().setText("параметр \n косого сдвига");
        tableRowOne.addNewTableCell().setText("параметр \n поворота");
        tableRowOne.addNewTableCell().setText("д. масштаба");
        tableRowOne.addNewTableCell().setText("д. косого сдвига");
        tableRowOne.addNewTableCell().setText("д. поворота");
        tableRowOne.addNewTableCell().setText("д. a11, %");
        tableRowOne.addNewTableCell().setText("д. a12, %");
        tableRowOne.addNewTableCell().setText("д. a21, %");
        tableRowOne.addNewTableCell().setText("д. a22, %");
        tableRowOne.addNewTableCell().setText("среднее д. a, %");
        tableRowOne.addNewTableCell().setText("среднее д. пиксель");
        tableRowOne.addNewTableCell().setText("максимальное отклонение от среднего");
        tableRowOne.addNewTableCell().setText("кол-во д. пикселей [0, 1.4]");
        tableRowOne.addNewTableCell().setText("кол-во д. пикселей [1.5, 2.4]");
        tableRowOne.addNewTableCell().setText("кол-во д. пикселей [2.5, 3.4]");
        tableRowOne.addNewTableCell().setText("кол-во д. пикселей >3.5");

        for (AnalyzeModel experiment : list) {
            XWPFTableRow tableRow = table.createRow();
            DecimalFormat df = new DecimalFormat("#.###");
            tableRow.getCell(0).setText("" + experiment.getTransformValue().getScale());
            tableRow.getCell(1).setText("" + experiment.getTransformValue().getShear());
            tableRow.getCell(2).setText("" + experiment.getTransformValue().getRotate());
            tableRow.getCell(4).setText(df.format(experiment.getScaleDelta()));
            tableRow.getCell(5).setText(df.format(experiment.getShearDelta()));
            tableRow.getCell(6).setText(df.format(experiment.getRotateDelta()));

            tableRow.getCell(7).setText(df.format(experiment.getA11Delta()));
            tableRow.getCell(8).setText(df.format(experiment.getA12Delta()));
            tableRow.getCell(9).setText(df.format(experiment.getA21Delta()));
            tableRow.getCell(10).setText(df.format(experiment.getA22Delta()));
            tableRow.getCell(11).setText(df.format(experiment.getADelta()));

            tableRow.getCell(12).setText(df.format(experiment.getPixelDelta()));

            tableRow.getCell(13).setText("???");

            tableRow.getCell(14).setText("" + experiment.getRange1Count());
            tableRow.getCell(15).setText("" + experiment.getRange2Count());
            tableRow.getCell(16).setText("" + experiment.getRange3Count());
            tableRow.getCell(17).setText("" + experiment.getRange4Count());

            for (int i = 0; i < 18; i++) {
                if (experiment.getPixelDelta() >= 0 && experiment.getPixelDelta() <= 1.4) {
                    tableRow.getCell(i).setColor("FF0013");
                } else if (experiment.getPixelDelta() > 1.4 && experiment.getPixelDelta() <= 2.4) {
                    tableRow.getCell(i).setColor("FF681D");
                } else if (experiment.getPixelDelta() > 2.4 && experiment.getPixelDelta() <= 3.4) {
                    tableRow.getCell(i).setColor("FFCC22");
                } else if (experiment.getPixelDelta() > 3.4) {
                    tableRow.getCell(i).setColor("C3E205");
                }
            }
        }

        document.createParagraph().createRun().setText("");
    }

    public static void addImage(XWPFParagraph paragraph, String fileName, int width, int height) throws InvalidFormatException {
        XWPFRun run = paragraph.createRun();
        String path11 = "/Users/user/IdeaProjects/diplom/diplom/output/" + fileName;
        run.setText(fileName);
        try {
            run.addPicture(new FileInputStream(path11), XWPFDocument.PICTURE_TYPE_JPEG, fileName, Units.toEMU(width), Units.toEMU(height));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
