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
 * @author kim01
 */
public class LoadDBSNP {
    
    private final String dbsnpPath = "C:/Users/kim01/Documents/Research/GenotypingData/dbsnp_GRCh37p13_nochr.vcf";
    //private final String dbsnpPath = "F:/Research/Genotyping/dbsnp_GRCh37p13_nochr.vcf";
    private String[] dbsnp;
    
    private int dbsnpLen;
    
    public LoadDBSNP() {
        initVariables();
    }
    
    private void initVariables() {
        dbsnpLen = 0;
        
        load();
    }
    
    private void load() {
        try {
            File f = new File(dbsnpPath);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            while(!br.readLine().startsWith("#CHROM")) {}
            
            while(br.readLine() != null) {
                dbsnpLen++;
            }
            System.out.println(dbsnpLen);
            
            dbsnp = new String[dbsnpLen];
            
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            while(!br.readLine().startsWith("#CHROM")) {}
            String[] line;
            
            for(int i = 0; i < dbsnpLen; i++) {
                line = br.readLine().split("\t");
                dbsnp[i] = line[0] + "_" + line[1];
            }
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public String[] getDBSNP() {
        return dbsnp;
    }
    
}
