package com.sytoss.article.app.resolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sytoss.article.model.AnalyzeModel;
import com.sytoss.article.model.RateModel;

public class ReaImagesAppResolution extends AppResolution {

    public ReaImagesAppResolution() {
    }

    public void execute(String... paths) {
        super.execute(paths);

        fillImagesData();
    }

    private void fillImagesData() {
        for (String name : ALGORITHM_NAMES) {
            List<AnalyzeModel> collect = getAllResult().stream().filter(analyzeModel -> analyzeModel.getName().equals(name) && analyzeModel.getExpertRate() >= 0).collect(Collectors.toList());
            List<AnalyzeModel> experiments = getExperimentsByName(name);
            if (experiments != null) {
                AnalyzeModel experiment = calculateAvarageParams(collect);
                experiment.setName(name);
                fillRatesResult(experiment, name);
                experiments.add(experiment);
            } else {
                System.err.println("Cannot map the analyze model.");
            }
        }
    }

    private void fillRatesResult(AnalyzeModel experimentModel, String algorithmName){
        List<AnalyzeModel> list = getAllResult().stream().filter(analyzeModel -> analyzeModel.getName().equals(algorithmName)).collect(Collectors.toList());

        RateModel rateModel = experimentModel.getRate();
        rateModel.setExpertRate_1Count(rateModel.getExpertRate_1Count() + (int) list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == -1).count());
        rateModel.setExpertRate0Count(rateModel.getExpertRate0Count() + (int) list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 0).count());
        rateModel.setExpertRate1Count(rateModel.getExpertRate1Count() + (int) list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 1).count());
        rateModel.setExpertRate2Count(rateModel.getExpertRate2Count() + (int) list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 2).count());
        rateModel.setExpertRate3Count(rateModel.getExpertRate3Count() + (int) list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 3).count());
        rateModel.setExpertRate4Count(rateModel.getExpertRate4Count() + (int) list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 4).count());

        rateModel.getRate_1().addAll(list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == -1).collect(Collectors.toCollection(ArrayList::new)));
        rateModel.getRate0().addAll(list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 0).collect(Collectors.toCollection(ArrayList::new)));
        rateModel.getRate1().addAll(list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 1).collect(Collectors.toCollection(ArrayList::new)));
        rateModel.getRate2().addAll(list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 2).collect(Collectors.toCollection(ArrayList::new)));
        rateModel.getRate3().addAll(list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 3).collect(Collectors.toCollection(ArrayList::new)));
        rateModel.getRate4().addAll(list.stream().filter(analyzeModel -> analyzeModel.getExpertRate() == 4).collect(Collectors.toCollection(ArrayList::new)));

        double[] siftRate1 = list.stream().mapToDouble(AnalyzeModel::getExpertRate).sorted().toArray();
        if (siftRate1.length > 0) {
            rateModel.setMedianaRate_1_4(siftRate1[getMinIndex(siftRate1.length)]);
            rateModel.setAverageRate_1_4(Arrays.stream(siftRate1).average().getAsDouble());
        }

        double[] siftRate2 = list.stream().mapToDouble(AnalyzeModel::getExpertRate).sorted().filter(value -> value >= 1).toArray();
        if (siftRate2.length > 0) {
            rateModel.setMedianaRate1_4(siftRate2[getMinIndex(siftRate2.length)]);
            rateModel.setAverageRate1_4(Arrays.stream(siftRate2).average().getAsDouble());
        }

        double[] siftRate3 = list.stream().mapToDouble(AnalyzeModel::getExpertRate).sorted().filter(value -> value >= 0).toArray();
        if (siftRate3.length > 0) {
            rateModel.setMedianaRate0_4(siftRate3[getMinIndex(siftRate3.length)]);
            rateModel.setAverageRate0_4(Arrays.stream(siftRate3).average().getAsDouble());
        }

        double[] siftRate4 = list.stream().mapToDouble(AnalyzeModel::getExpertRate).sorted().filter(value -> value < 1).toArray();
        if (siftRate4.length > 0) {
            rateModel.setMedianaRate_1_0(siftRate4[getMinIndex(siftRate4.length)]);
            rateModel.setAverageRate_1_0(Arrays.stream(siftRate4).average().getAsDouble());
        }
    }

    private int getMinIndex(int len) {
        if (len <= 1) {
            return 0;
        }
        int index = (len / 2);
        if (len % 2 == 0) {
            index = index - 1;
        }

        return index;
    }
}
