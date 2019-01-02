/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.util.Arrays;

/**
 *
 * @author kim01
 */
public class CheckMissingResults {  // checking vcfs without idx
    
    //private final String checkingPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/both_results/";
    private final String checkingPath = "F:/Dropbox/Research/CRUK_CI/Mutect/both_results/";
    
    private String[] vcf;
    private String[] idx;
    private int vcfNum, idxNum;
    
    public CheckMissingResults() {
        initVariables();
    }
    
    private void initVariables() {
        loadVCFs();
    }
    
    private void loadVCFs() {
        File f = new File(checkingPath);
        File[] files = f.listFiles();
        
        vcfNum = 0;
        idxNum = 0;
        for (File file : files) {
            if(file.getName().endsWith(".vcf")) {
                vcfNum++;
            }
            else if(file.getName().endsWith(".idx")) {
                idxNum++;
            }
        }
        System.out.println(vcfNum);
        System.out.println(idxNum);
        
        vcf = new String[vcfNum];
        idx = new String[idxNum];
        
        vcfNum = 0;
        idxNum = 0;
        for (File file : files) {
            if(file.getName().endsWith(".vcf")) {
                vcf[vcfNum] = file.getName().substring(0, file.getName().length()-4);
                vcfNum++;
            }
            else if(file.getName().endsWith(".idx")) {
                idx[idxNum] = file.getName().substring(0, file.getName().length()-8);
                idxNum++;
            }
        }
        
        Arrays.sort(vcf);
        Arrays.sort(idx);
    }
    
    private void compare() {
        boolean isExist;
        
        for(int i = 0; i < vcfNum; i++) {
            isExist = false;
            for(int j = 0; j < idxNum; j++) {
                if(vcf[i].equals(idx[j])) {
                    isExist = true;
                }
            }
            if(isExist == false) {
                System.out.println(vcf[i]);
            }
        }
    }
    
    public void start() {
        compare();
    }
    
}
