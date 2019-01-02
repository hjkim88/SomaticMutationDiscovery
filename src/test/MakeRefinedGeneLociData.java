/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 *
 * @author kim01
 */
public class MakeRefinedGeneLociData {
    
    //private final String dataPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Genotyping/";
    private final String dataPath = "F:/Dropbox/Research/CRUK_CI/Genotyping/";
    private final String inputPath = dataPath + "Gene_Loci_GRCh37_transcript.txt";
    private final String outputPath = dataPath + "Gene_Loci_GRCh37_transcript(final).txt";
    
    public MakeRefinedGeneLociData() {
        initVariables();
    }
    
    private void initVariables() {
        
    }
    
    private void make() {
        try {
            File f = new File(inputPath);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            FileWriter fw = new FileWriter(outputPath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);
            
            br.readLine();
            outFile.println("Gene_Symbol,Chromosome,Gene_Start,Gene_End,Transcript_length");
            
            String line;
            String[] str;
            
            while((line = br.readLine()) != null) {
                str = line.split("\t");
                if(!str[4].equals("")) {
                    outFile.print(str[4] + "," + str[5] + "," + str[6] + "," + str[7] + ",");
                    int tLen = Integer.parseInt(str[10]);
                    for(int i = 0; i < (Integer.parseInt(str[11])-1); i++) {
                        str = br.readLine().split("\t");
                        tLen = tLen + Integer.parseInt(str[10]);
                    }
                    outFile.println(tLen);
                }
            }
            
            outFile.close();
            bw.close();
            fw.close();
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".make()");
        }
    }
    
    public void start() {
        make();
    }
    
}
