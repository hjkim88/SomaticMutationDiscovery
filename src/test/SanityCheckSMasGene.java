/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author kim01
 */
public class SanityCheckSMasGene {
    
    //private final String wd = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Genotyping/";
    private final String wd = "F:/Dropbox/Research/CRUK_CI/Genotyping/";
    private final String geneSymbolListPath = wd + "Gene_Loci_GRCh37_transcript(final).txt";
    
    private String fileName;
    
    private String[] smName;
    private double[] smCnt;
    private int smLen;
    
    private String[] geneSymbol;
    private int[] startP, endP, geneLen;
    private int geneNum;
    
    public SanityCheckSMasGene(String smPath) {
        initVariables(smPath);
    }
    
    private void initVariables(String smPath) {
        fileName = smPath.substring(0, smPath.length()-4);
        loadSM(smPath);
        loadGeneSymbol();
    }
    
    private void loadSM(String smPath) {
        file.LoadCSV lcsv = new file.LoadCSV(smPath, true);
        smName = lcsv.getrowNames();
        smLen = smName.length;
        
        String[][] temp = lcsv.getData();
        
        smCnt = new double[smLen];
        for(int i = 0; i < smLen; i++) {
            smCnt[i] = Double.parseDouble(temp[i][1]);
        }
    }
    
    private void loadGeneSymbol() {
        file.LoadCSV lcsv = new file.LoadCSV(geneSymbolListPath, true);
        geneSymbol = lcsv.getrowNames();
        geneNum = geneSymbol.length;
        System.out.println(geneNum);
        
        startP = new int[geneNum];
        endP = new int[geneNum];
        geneLen = new int[geneNum];
        
        String[][] temp = lcsv.getData();
        for(int i = 0; i < geneNum; i++) {
            startP[i] = Integer.parseInt(temp[i][1]);
            endP[i] = Integer.parseInt(temp[i][2]);
            geneLen[i] = Integer.parseInt(temp[i][3]);
        }
    }
    
    private int findIndex(String sName) {
        int idx = -1;
        
        for(int i = 0; i < geneNum; i++) {
            if(sName.equals(geneSymbol[i])) {
                idx = i;
                break;
            }
        }
        
        return idx;
    }
    
    private void organize() {
        if(smLen != geneNum) {
            System.out.println("smLen != geneNum");
        }
        
        try {
            FileWriter fw = new FileWriter(fileName + "_SC.dat");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);
            
            int idx;
            outFile.println("Gene_Name,Mutation_CNT,Gene_Length,Transcript_Length");
            for(int i = 0; i < smLen; i++) {
                idx = findIndex(smName[i]);
                outFile.println(smName[i] + "," + smCnt[i] + "," + (endP[idx] - startP[idx]) + "," + geneLen[idx]);
            }
            
            outFile.close();
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        
        System.out.println(fileName + "_SC.dat   DONE");
    }
    
    public void start() {
        organize();
    }
    
}
