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
public class GetVwithThreshold {
    
    private String vcfFilePath;
    private String[] variants;
    private int variantLen;
    private int readCoverageThreshold;
    private boolean isSmoking;
    
    public GetVwithThreshold(String vcfFilePath, int threshold, boolean isSmoking) {
        variantLen = 0;
        
        initVariables(vcfFilePath, threshold, isSmoking);
        makeList();
    }
    
    private void initVariables(String vcfFilePath, int threshold, boolean isSmoking) {
        this.vcfFilePath = vcfFilePath;
        this.readCoverageThreshold = threshold;
        this.isSmoking = isSmoking;
    }
    
    private void makeList() {
        try {
            File f = new File(vcfFilePath);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            String line;
            String[] str, str2;
            int readNum;
            
            while(!br.readLine().startsWith("#CHROM")) {}
            while((line = br.readLine()) != null) {
                str = line.split("\t");
                if(str[8].contains("AD")) {
                    str2 = str[9].split(":")[1].split(",");
                    readNum = Integer.parseInt(str2[0]) + Integer.parseInt(str2[1]);
                    if(readNum >= readCoverageThreshold) {
                        if(isSmoking == false) {
                            variantLen++;
                        }
                        else if(str[3].equals("C") && str[4].equals("A")){
                            variantLen++;
                        }
                        else if(str[3].equals("G") && str[4].equals("T")) {
                            variantLen++;
                        }
                    }
                }
            }
            variants = new String[variantLen];
            
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            while(!br.readLine().startsWith("#CHROM")) {}
            
            variantLen = 0;
            while((line = br.readLine()) != null) {
                str = line.split("\t");
                if(str[8].contains("AD")) {
                    str2 = str[9].split(":")[1].split(",");
                    readNum = Integer.parseInt(str2[0]) + Integer.parseInt(str2[1]);
                    if(readNum >= readCoverageThreshold) {
                        if(isSmoking == false) {
                            variants[variantLen] = str[0] + "_" + str[1];
                            variantLen++;
                        }
                        else if(str[3].equals("C") && str[4].equals("A")){
                            variants[variantLen] = str[0] + "_" + str[1];
                            variantLen++;
                        }
                        else if(str[3].equals("G") && str[4].equals("T")) {
                            variants[variantLen] = str[0] + "_" + str[1];
                            variantLen++;
                        }
                    }
                }
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
    
}
