package openerp.openerpresourceserver.generaltimetabling.helper;

public class Converter {

    public static String convertOpenBatchToLearningWeeks(String openBatch) {
        switch (openBatch){
            case "A":
                return "1-8";
            case "B":
                return "10-17";
            case "chẵn":
                return "2-2,4-4,6-6,8-8";
            case "lẻ":
                return "1-1,3-3,5-5,7-7";
            default:
                return null;
        }
    }
}
