package com.sytoss.article.app.resolution;

import java.util.List;
import java.util.stream.Collectors;

import com.sytoss.article.model.AnalyzeModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SynthesizedImagesAppResolution extends AppResolution {

    private double[] scales;
    private double[] shears;
    private double[] rotates;

    public SynthesizedImagesAppResolution(double[] scales, double[] shears, double[] rotates) {
        this.scales = scales;
        this.shears = shears;
        this.rotates = rotates;
    }

    public void execute(String ...paths){
        super.execute(paths);
        fillImagesData();
    }

    private void fillImagesData() {
        for (String name : ALGORITHM_NAMES) {
            List<AnalyzeModel> experiments = getExperimentsByName(name);
            if (experiments != null) {
                int i = 0;
                for (double scale : scales) {
                    for (double shear : shears) {
                        for (double rotate : rotates) {
                            List<AnalyzeModel> collect = getAllResult().stream()
                                    .filter(analyzeModel -> analyzeModel.getName().equals(name)
                                            && analyzeModel.getExpertRate() >= 0 && analyzeModel.getTransformValue().toString().equals("k(" + scale + ");hx(" + shear + ");a(" + rotate + ")"))
                                    .collect(Collectors.toList());
                            AnalyzeModel experiment = calculateAvarageParams(collect);
                            experiment.setId(i++);
                            experiment.setName(name);
                            experiments.add(experiment);
                        }
                    }
                }
            } else {
                System.err.println("Cannot map the analyze model.");
            }
        }
    }
}
