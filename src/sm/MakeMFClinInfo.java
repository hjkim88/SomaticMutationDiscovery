/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.File;
import java.util.Arrays;

/**
 *
 * @author kim01
 */
public class MakeMFClinInfo {
    
    private final String clinPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/clinical_info_Mar2017.csv";
    private final String brPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/both_results/somatic_results/BR/";
    private final String nsPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/both_results/somatic_results/NS/";
    private final String smokingPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/smoking-related/";
    private final String expPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/results/";
    private final String resultClinPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/exp/resultClinicalInfo.csv";
    
    private String[][] clin;
    private String[] clin_col, clin_row;
    private int clin_rowNum, clin_colNum;
    
    private String[] expSampleID, exp;
    private int expNum;
    
    private String[] smokingSampleID;
    private int[] smokingMutationNum;
    private int smokingNum;
    
    private String[] brSampleID, nsSampleID;
    private int[] brMutationNum, nsMutationNum;
    private int bothNum;
    
    private String[][] newClin;
    private String[] newClin_col;
    private int newClin_colNum;
    
    public MakeMFClinInfo() {
        initVariables();
    }
    
    private void initVariables() {
        loadData();
    }
    
    private void loadData() {
        file.LoadCSV lcsv = new file.LoadCSV(clinPath, true);
        clin_col = lcsv.getColNames();
        clin_row = lcsv.getrowNames();
        clin = lcsv.getData();
        clin_rowNum = clin.length;
        clin_colNum = clin_col.length;
        
        newClin_colNum = clin_colNum + 8;
        newClin_col = new String[newClin_colNum];
        System.arraycopy(clin_col, 0, newClin_col, 0, clin_colNum);
        newClin_col[newClin_colNum-8] = "BR_exp1";
        newClin_col[newClin_colNum-7] = "BR_exp2";
        newClin_col[newClin_colNum-6] = "NS_exp1";
        newClin_col[newClin_colNum-5] = "NS_exp2";
        newClin_col[newClin_colNum-4] = "SM_BR_exp1";
        newClin_col[newClin_colNum-3] = "SM_BR_exp2";
        newClin_col[newClin_colNum-2] = "SM_NS_exp1";
        newClin_col[newClin_colNum-1] = "SM_NS_exp2";
        newClin = new String[clin_rowNum][newClin_colNum];
        for(int i = 0; i < clin_rowNum; i++) {
            System.arraycopy(clin[i], 0, newClin[i], 0, clin_colNum);
            for(int j = clin_colNum; j < newClin_colNum; j++) {
                newClin[i][j] = "NA";
            }
        }
        
        loadExp();
        loadSmokingMutation();
        loadBothMutation();
    }
    
    private void loadExp() {
        File f = new File(expPath);
        File[] files = f.listFiles();
        
        expNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".vcf")) {
                //System.out.println(file.getName().substring(0, file.getName().length()-12));
                expNum++;
            }
        }
        System.out.println(expNum);
        
        expSampleID = new String[expNum];
        exp = new String[expNum];
        
        String tempID;
        expNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".vcf")) {
                tempID = file.getName().substring(0, file.getName().length() - 12);
                expSampleID[expNum] = refineSampleName(tempID);
                if(tempID.endsWith("_2")) {
                    exp[expNum] = "2";
                }
                else {
                    exp[expNum] = "1";
                }
                //System.out.println(sampleID[expNum] + " : exp" + exp[expNum]);
                expNum++;
            }
        }
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
    
    private void loadSmokingMutation() {
        File f = new File(smokingPath);
        File[] files = f.listFiles();
        
        smokingNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".mutations")) {
                //System.out.println(file.getName().substring(0, file.getName().length()-10));
                smokingNum++;
            }
        }
        System.out.println(smokingNum);
        
        smokingSampleID = new String[smokingNum];
        smokingMutationNum = new int[smokingNum];
        
        smokingNum = 0;
        for (File fe : files) {
            if (fe.getName().endsWith(".mutations")) {
                smokingSampleID[smokingNum] = fe.getName().substring(0, fe.getName().length() - 10);
                smokingMutationNum[smokingNum] = new file.GetMutationNum(fe.getAbsolutePath()).getNum();
                smokingNum++;
            }
        }
    }
    
    private void loadBothMutation() {
        File f = new File(brPath);
        File[] files = f.listFiles();
        
        bothNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".mutations")) {
                //System.out.println(file.getName().substring(0, file.getName().length()-10));
                bothNum++;
            }
        }
        System.out.println(bothNum);
        
        brSampleID = new String[bothNum];
        brMutationNum = new int[bothNum];
        nsSampleID = new String[bothNum];
        nsMutationNum = new int[bothNum];
        
        bothNum = 0;
        for (File fe : files) {
            if (fe.getName().endsWith(".mutations")) {
                brSampleID[bothNum] = fe.getName().substring(0, fe.getName().length() - 10);
                brMutationNum[bothNum] = new file.GetMutationNum(fe.getAbsolutePath()).getNum();
                bothNum++;
            }
        }
        
        f = new File(nsPath);
        files = f.listFiles();
        
        bothNum = 0;
        for (File fe : files) {
            if (fe.getName().endsWith(".mutations")) {
                nsSampleID[bothNum] = fe.getName().substring(0, fe.getName().length() - 10);
                nsMutationNum[bothNum] = new file.GetMutationNum(fe.getAbsolutePath()).getNum();
                bothNum++;
            }
        }
    }
    
    private boolean isSame(String studyName, String patientNum, String sampleID) {
        boolean r = false;
        
        String id = "S" + studyName.substring(5).toUpperCase() + "_" + changeDigits(patientNum);
        
        String[] temp = sampleID.split("_");
        String id2 = temp[0] + "_" + temp[1];
        
        if(id.equals(id2)) {
            r = true;
        }
        
        return r;
    }
    
    private void combineInOne() {
        for(int i = 0; i < clin_rowNum; i++) {
            //System.out.println(i);
            String sample_exp;
            boolean isBoth = !(clin[i][2].equals("bronc") || clin[i][2].equals("nasal"));
            int cnt = 0;
            for(int j = 0; j < expNum; j++) {
                if(isSame(clin[i][0], clin[i][1], expSampleID[j])) {
                    sample_exp = exp[j];
                    
                    for(int k = 0; k < bothNum; k++) {
                        if(expSampleID[j].equals(brSampleID[k])) {
                            if(sample_exp.equals("1")) {
                                newClin[i][clin_colNum] = "" + brMutationNum[k];
                            }
                            else {
                                newClin[i][clin_colNum+1] = "" + brMutationNum[k];
                            }
                        }
                        if(expSampleID[j].equals(nsSampleID[k])) {
                            if(sample_exp.equals("1")) {
                                newClin[i][clin_colNum+2] = "" + nsMutationNum[k];
                            }
                            else {
                                newClin[i][clin_colNum+3] = "" + nsMutationNum[k];
                            }
                        }
                    }
                    
                    for(int k = 0; k < smokingNum; k++) {
                        if(expSampleID[j].equals(smokingSampleID[k])) {
                            if(smokingSampleID[k].endsWith("BR")) {
                                if(sample_exp.equals("1")) {
                                    newClin[i][clin_colNum+4] = "" + smokingMutationNum[k];
                                }
                                else {
                                    newClin[i][clin_colNum+5] = "" + smokingMutationNum[k];
                                }
                            }
                            else {
                                if(sample_exp.equals("1")) {
                                    newClin[i][clin_colNum+6] = "" + smokingMutationNum[k];
                                }
                                else {
                                    newClin[i][clin_colNum+7] = "" + smokingMutationNum[k];
                                }
                            }
                        }
                    }
                    if(isBoth == false) {
                        break;
                    }
                    else if(cnt == 1) {
                        break;
                    }
                    cnt++;
                }
            }
        }
    }
    
    private void writeResult() {
        new file.SaveCSV(clin_row, clin_rowNum, newClin_col, newClin_colNum, newClin, false, resultClinPath).start();
    }
    
    public void start() {
        combineInOne();
        writeResult();
    }
    
}
