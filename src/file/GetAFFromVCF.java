/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Hyunjin Kim
 */
public class GetAFFromVCF {
    
    private String vcfFilePath;
    private String[] variants;
    private double[] af;
    private String[] results;
    private int variantLen;
    
    public GetAFFromVCF(String vcfFilePath) {
        variantLen = 0;
        
        initVariables(vcfFilePath);
        makeList();
    }
    
    private void initVariables(String vcfFilePath) {
        this.vcfFilePath = vcfFilePath;
    }
    
    private void makeList() {
        try {
            File f = new File(vcfFilePath);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            while(!br.readLine().startsWith("#CHROM")) {}
            
            while(br.readLine() != null) {
                variantLen++;
            }
            variants = new String[variantLen];
            af = new double[variantLen];
            results = new String[variantLen];
            
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            while(!br.readLine().startsWith("#CHROM")) {}
            
            String[] str;
            String[] temp;
            
            for(int i = 0; i < variantLen; i++) {
                str = br.readLine().split("\t");
                variants[i] = str[0] + "_" + str[1];
                temp = str[9].split(":");
                af[i] = Double.parseDouble(temp[2]);
                if(af[i] < 0 || af[i] > 1) {
                    System.out.println("ERROR: something wrong with allele fraction: " + vcfFilePath + " " + variants[i]);
                }
                results[i] = variants[i] + "\t" + af[i];
            }
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".makeList()");
        }
    }
    
    public String[] getVariants() {
        return variants;
    }
    
    public double[] getAF() {
        return af;
    }
    
    public String[] getResults() {
        return results;
    }
    
}
