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
public class MakeAFData {   // Make allele fraction data for visualizing in R
    
    //private final String dataPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/";
    private final String dataPath = "F:/Dropbox/Research/CRUK_CI/Mutect/both_results/";
    private final String vcfDirPath = dataPath + "NS/";
    private final String outputPath = dataPath + "AF/NS/";
    
    private String[] dbsnp;
    private double[][] af;   // [sampleNum][each sample's mutationNum]
    
    private int vcfNum;
    private String[] vcfIDs;
    private String[] sampleID;
    
    public MakeAFData() {
        initVariables();
    }
    
    private void initVariables() {
        dbsnp = new file.LoadDBSNP().getDBSNP();
        
        loadVCFPath();
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
    
    private String refineSampleName(String str) {
        String[] temp = str.split("_");
        String result = temp[0] + "_" + changeDigits(temp[1]) + "_" + temp[2];
        
        return result;
    }
    
    private void loadVCFPath() {
        File f = new File(vcfDirPath);
        File[] files = f.listFiles();
        
        vcfNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".vcf")) {
                //System.out.println(file.getName().substring(0, file.getName().length() - 12));
                vcfNum++;
            }
        }
        System.out.println(vcfNum);
        
        vcfIDs = new String[vcfNum];
        sampleID = new String[vcfNum];
        af = new double[vcfNum][0];
        
        int cnt = 0;
        for (File file : files) {
            if (file.getName().endsWith(".vcf")) {
                vcfIDs[cnt] = file.getName().substring(0, file.getName().length() - 12);
                sampleID[cnt] = refineSampleName(file.getName().substring(0, file.getName().length() - 12));
                //System.out.println(sampleID[cnt]);
                cnt++;
            }
        }
    }
    
    private String[] getMutationOnly(String[] fullList) {
        int fullLen = fullList.length;
        String[] temp = fullList;
        int idx;
        int moLen = 0;
        
        for(int i = 0; i < fullLen; i++) {
            idx = Arrays.binarySearch(dbsnp, fullList[i].split("\t")[0]);
            if(idx < 0) {
                temp[moLen] = fullList[i].split("\t")[1];
                moLen++;
            }
        }
        
        String[] mo = new String[moLen];
        System.arraycopy(temp, 0, mo, 0, moLen);
        
        return mo;
    }
    
    private void extract() {
        String[] variantList;
        String[] refinedVList;
        for(int i = 0; i < vcfNum; i++) {
            variantList = new file.GetAFFromVCF(vcfDirPath + vcfIDs[i] + "_mutect2.vcf").getResults();
            System.out.println(sampleID[i]);
            refinedVList = getMutationOnly(variantList);
            af[i] = new double[refinedVList.length];
            for(int j = 0; j < af[i].length; j++) {
                af[i][j] = Double.parseDouble(refinedVList[j]);
            }
            new file.SaveCSV(refinedVList, refinedVList.length, new String[] {}, 0, new int[][] {}, true, outputPath + sampleID[i] + ".af").start();
        }
    }
    
    public void start() {
        extract();
    }
    
}
