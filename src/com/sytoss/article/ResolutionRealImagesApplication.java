package com.sytoss.article;

import java.io.File;

import com.sytoss.article.app.resolution.AppResolution;
import com.sytoss.article.app.resolution.ReaImagesAppResolution;

public class ResolutionRealImagesApplication {

    public static void main(String[] args) {
        String[] sytossNurePngPairsMikPaths = {
                "out/result/09_04_mik/result_v_artisans/tmp/",
                "out/result/09_04_mik/result_v_bark/tmp/",
                "out/result/09_04_mik/result_v_bip/tmp/",
                "out/result/09_04_mik/result_v_bird/tmp/",
                "out/result/09_04_mik/result_v_blueprint/tmp/",
                "out/result/09_04_mik/result_v_boat/tmp/",
                "out/result/09_04_mik/result_v_cartooncity/tmp/",
                "out/result/09_04_mik/result_v_eastsouth/tmp/",
                "out/result/09_04_mik/result_v_graffiti/tmp/",
                "out/result/09_04_mik/result_v_lbricks/tmp/"};
        String documentPath_sytossNurePngPairsMikPaths = "out/result/09_04_mik/AVERAGE_table_result.docx";

        String[] sytossNurePngPairs100Paths = {
                "out/result/07_27/result_building/tmp/",
                "out/result/07_27/result_graffiti_picture_outside/tmp/",
                "out/result/07_27/result_picture_inside/tmp/",
                "out/result/07_27/result_texture_artificial/tmp/",
                "out/result/07_27/result_texture_nature/tmp/"};
        String documentPath_sytossNurePngPairs100Paths = "out/result/07_27/AVERAGE_table_result.docx";

        ReaImagesAppResolution appResolution = new ReaImagesAppResolution();
        appResolution.execute(sytossNurePngPairs100Paths);
        appResolution.writeExperimentData(new File(documentPath_sytossNurePngPairs100Paths));
    }

}
