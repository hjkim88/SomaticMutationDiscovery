/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.File;

/**
 *
 * @author Hyunjin Kim
 */
public class CombineMutationNDNARepair {
    
    //private final String OMAPath = "F:/Dropbox/Research/CRUK_CI/DNA_repair/OMA_score/";
    //private final String mutectPath = "F:/Dropbox/Research/CRUK_CI/Mutect/somatic_results/";
    private final String OMAPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/DNA_repair/OMA_score/";
    private final String mutectPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/somatic_results/";
    private final String OMAInfoPath = OMAPath + "oma_patients_info2.csv";
    private final String outPath = OMAPath + "oma_patients_info2_mf.csv";
    
    private String[] omaColNames, omaRowNames;
    private String[][] omaData;
    private int omaColNum, omaRowNum;
    
    private int sampleNum;
    private String[] sampleName;
    private int[] mutationNum;
    
    private String[][] outputData;
    private String[] outputCol, outputRow;
    private int outputRowNum;
    
    public CombineMutationNDNARepair() {
        initVariables();
    }
    
    private void initVariables() {
        loadOMAInfo();
        calMutNums();
    }
    
    private void loadOMAInfo() {
        file.LoadCSV lcsv = new file.LoadCSV(OMAInfoPath, true);
        omaColNames = lcsv.getColNames();
        omaColNum = omaColNames.length;
        omaRowNames = lcsv.getrowNames();
        omaRowNum = omaRowNames.length;
        omaData = lcsv.getData();
    }
    
    private void calMutNums() {
        File f = new File(mutectPath);
        File[] files = f.listFiles();
        
        sampleNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".mutations")) {
                //System.out.println(file.getName().substring(0, file.getName().length()-10));
                sampleNum++;
            }
        }
        System.out.println(sampleNum);
        
        sampleName = new String[sampleNum];
        mutationNum = new int[sampleNum];
        
        int cnt = 0;
        for (File fe : files) {
            if (fe.getName().endsWith(".mutations")) {
                sampleName[cnt] = fe.getName().substring(0, fe.getName().length() - 10);
                mutationNum[cnt] = new file.GetMutationNum(fe.getAbsolutePath()).getNum();
                cnt++;
            }
        }
    }
    
    private String threeDigits(String str) {
        String r = str;
        
        if(str.length() == 1) {
            r = "00" + str;
        }
        else if(str.length() == 2) {
            r = "0" + str;
        }
        
        return r;
    }
    
    private String changeSampleName(String sn) {
        String[] buf = sn.toLowerCase().split("_");
        String result = "";
        
        if(buf[0].charAt(1) == '1') {
            result = buf[0].substring(1) + "-" + threeDigits(buf[1]);
        }
        else if(buf[0].charAt(1) == '2') {
            result = buf[1];
        }
        else {
            System.out.println("ERROR: " + buf[0]);
        }
        
        return result;
    }
    
    private void makeOutputData() {
        outputRowNum = 0;
        for(int i = 0; i < omaRowNum; i++) {
            for(int j = 0; j < sampleNum; j++) {
                if(omaRowNames[i].equals(changeSampleName(sampleName[j]))) {
                    outputRowNum++;
                    break;
                }
            }
        }
        System.out.println(outputRowNum);
        
        outputCol = new String[omaColNum+1];
        outputRow = new String[outputRowNum];
        outputData = new String[outputRowNum][omaColNum+1];
        
        System.arraycopy(omaColNames, 0, outputCol, 0, omaColNum);
        outputCol[omaColNum] = "Mutation_Frequency";
        
        outputRowNum = 0;
        for(int i = 0; i < omaRowNum; i++) {
            for(int j = 0; j < sampleNum; j++) {
                if(omaRowNames[i].equals(changeSampleName(sampleName[j]))) {
                    outputRow[outputRowNum] = omaRowNames[i];
                    System.arraycopy(omaData[i], 0, outputData[outputRowNum], 0, omaColNum);
                    outputData[outputRowNum][omaColNum] = "" + mutationNum[j];
                    outputRowNum++;
                    break;
                }
            }
        }
        
        new file.SaveCSV(outputRow, outputRowNum, outputCol, omaColNum+1, outputData, false, outPath).start();
    }
    
    public void start() {
        makeOutputData();
        System.out.println(sampleName[2] + " " + mutationNum[2]);
        System.out.println(sampleName[45] + " " + mutationNum[45]);
    }
    
}
