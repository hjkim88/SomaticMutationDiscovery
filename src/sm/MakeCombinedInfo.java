/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.File;

/**
 *
 * @author kim01
 */
public class MakeCombinedInfo {
    
    private final String wd = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/";
    private final String vcfPath = wd + "realigned_results/";
    private final String infoPath = wd + "clinical_info_Mar2017.csv";
    private final String omaPath = wd + "UK292_oma_new.csv";
    private final String outPath = wd + "total_result.csv";
    
    private final String mf1Path = wd + "somatic_realigned/";
    private final String mf2Path = wd + "somatic_realigned2/";
    private final String mf3Path = wd + "somatic_realigned3/";
    private final String mf4Path = wd + "somatic_realigned4/";
    private final String mf5Path = wd + "somatic_realigned5/";
    
    private String[] info_col;
    private String[][] info;
    private int info_rowNum, info_colNum;
    
    private String[] fileNames;
    private int fileNum;
    
    private String[][] oma;
    private int oma_sampleNum;
    
    private String[][] result;
    private String[] result_col;
    private int result_colNum;
    
    public MakeCombinedInfo() {
        initVariables();
    }
    
    private void initVariables() {
        loadInfo();
        loadFileNames();
        loadOMA();
        
        result_colNum = info_colNum+9;
        result_col = new String[result_colNum];
        System.arraycopy(info_col, 0, result_col, 0, info_colNum);
        result_col[info_colNum] = "UK40";
        result_col[info_colNum+1] = "UK20";
        result_col[info_colNum+2] = "IL40";
        result_col[info_colNum+3] = "Exp";
        result_col[info_colNum+4] = "MF";
        result_col[info_colNum+5] = "MF(R>=10)";
        result_col[info_colNum+6] = "SMF(R>=10)";
        result_col[info_colNum+7] = "MF(R>=20)";
        result_col[info_colNum+8] = "SMF(R>=20)";
        result = new String[fileNum][result_colNum];
    }
    
    private void loadInfo() {
        file.LoadCSV lcsv = new file.LoadCSV(infoPath, true);
        info_col = lcsv.getColNames();
        info = lcsv.getData();
        info_rowNum = info.length;
        info_colNum = info_col.length;
    }
    
    private void loadFileNames() {
        File f = new File(vcfPath);
        File[] files = f.listFiles();
        
        fileNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".vcf")) {
                //System.out.println(file.getName().substring(0, file.getName().length() - 12));
                fileNum++;
            }
        }
        
        fileNames = new String[fileNum];
        
        fileNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".vcf")) {
                fileNames[fileNum] = file.getName().substring(0, file.getName().length() - 12);
                fileNum++;
            }
        }
        
    }
    
    private void loadOMA() {
        oma = new file.LoadCSV(omaPath, true).getData();
        oma_sampleNum = oma.length;
    }
    
    private String refineSampleName(String str) {
        String[] temp = str.split("_");
        String result = temp[0] + "_" + changeDigits(temp[1]) + "_" + temp[2];
        
        return result;
    }
    
    private String changeDigits(String three) {
        String result = three;
        
        if(!three.contains("-")) {
            if(three.substring(0, 1).equals("0")) {
                if(three.substring(1, 2).equals("0")) {
                    result = three.substring(2, 3);
                }
                else {
                    result = three.substring(1, 3);
                }
            }
        }
        
        return result;
    }
    
    private boolean isTheSame(String vcfName, String studyName, String pNum) {
        boolean r = false;
        
        String[] temp = vcfName.split("_");
        String buf = "study" + temp[0].substring(1);
        
        if(studyName.equals(buf) && changeDigits(temp[1]).equals(changeDigits(pNum))) {
            r = true;
        }
        
        return r;
    }
    
    private String changeOMASampleName(String str) {
        String[] temp = str.split("-");
        
        if(str.charAt(0) == '1') {
            return temp[0] + "_" + temp[1];
        }
        else {
            return str;
        }
    }
    
    private void makeCombined() {
        for(int i = 0; i < fileNum; i++) {
            for(int j = 0; j < info_rowNum; j++) {
                if(isTheSame(fileNames[i], info[j][0], info[j][1])) {
                    System.arraycopy(info[j], 0, result[i], 0, info_colNum);
                    break;
                }
            }
            
            if(fileNames[i].contains("BR")) {
                result[i][2] = "BR";
            }
            else if(fileNames[i].contains("NS")) {
                result[i][2] = "NS";
            }
            
            for(int j = 0; j < oma_sampleNum; j++) {
                if(fileNames[i].contains(changeOMASampleName(oma[j][0]))) {
                    result[i][info_colNum] = oma[j][23];
                    result[i][info_colNum+1] = oma[j][24];
                    result[i][info_colNum+2] = oma[j][25];
                    break;
                }
            }
            
            if(fileNames[i].endsWith("_2")) {
                result[i][info_colNum+3] = "EXP2";
            }
            else {
                result[i][info_colNum+3] = "EXP1";
            }
            
            result[i][info_colNum+4] = "" + new file.GetMutationNum(mf1Path + refineSampleName(fileNames[i]) + ".mutations").getNum();
            result[i][info_colNum+5] = "" + new file.GetMutationNum(mf2Path + refineSampleName(fileNames[i]) + ".mutations").getNum();
            result[i][info_colNum+6] = "" + new file.GetMutationNum(mf3Path + refineSampleName(fileNames[i]) + ".mutations").getNum();
            result[i][info_colNum+7] = "" + new file.GetMutationNum(mf4Path + refineSampleName(fileNames[i]) + ".mutations").getNum();
            result[i][info_colNum+8] = "" + new file.GetMutationNum(mf5Path + refineSampleName(fileNames[i]) + ".mutations").getNum();
        }
    }
    
    private void saveResult() {
        new file.SaveCSV(fileNames, fileNum, result_col, result_colNum, result, false, outPath).start();
    }
    
    public void start() {
        makeCombined();
        saveResult();
    }
    
}
