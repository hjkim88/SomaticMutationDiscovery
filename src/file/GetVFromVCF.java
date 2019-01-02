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
public class GetVFromVCF {
    
    private String vcfFilePath;
    private String[] variants;
    private int variantLen;
    
    public GetVFromVCF(String vcfFilePath) {
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
            
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            while(!br.readLine().startsWith("#CHROM")) {}
            
            String[] str;
            
            for(int i = 0; i < variantLen; i++) {
                str = br.readLine().split("\t");
                variants[i] = str[0] + "_" + str[1];
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
