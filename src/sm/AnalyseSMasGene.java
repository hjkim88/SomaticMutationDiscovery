/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author kim01
 */
public class AnalyseSMasGene {
    private final String wd = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Genotyping/";
    //private final String wd = "F:/Dropbox/Research/CRUK_CI/Genotyping/";
    //private final String geneSymbolListPath = wd + "Gene_Loci_GRCh37(gene_length).txt";
    private final String geneSymbolListPath = wd + "Gene_Loci_GRCh37_transcript(final).txt";
    private String fileName;
    
    private String[] sm_chr;
    private int[] sm_position;
    private double[] smCnt;
    private int smLen;
    
    private String[] geneSymbol, chr;
    private double[] geneCnt;
    private double[] normGeneCnt;
    private int[] startP, endP, geneLen;
    private int geneNum;
    
    private util.MutationSet3[] ms3;
    
    public AnalyseSMasGene(String smPath) {
        initVariables(smPath);
    }
    
    private void initVariables(String smPath) {
        fileName = smPath.substring(0, smPath.length()-4);
        loadSM(smPath);
        loadGeneSymbol();
        
        for(int i = 0; i < geneNum; i++) {
            geneCnt[i] = 0;
        }
    }
    
    private void loadSM(String smPath) {
        file.LoadCSV lcsv = new file.LoadCSV(smPath, false);
        String[] smName = lcsv.getrowNames();
        smLen = smName.length;
        sm_chr = new String[smLen];
        sm_position = new int[smLen];
        smCnt = new double[smLen];
        
        String[] str;
        for(int i = 0; i < smLen; i++) {
            str = smName[i].split("_");
            sm_chr[i] = str[0];
            sm_position[i] = Integer.parseInt(str[1]);
        }
        
        String[][] temp = lcsv.getData();        
        for(int i = 0; i < smLen; i++) {
            smCnt[i] = Double.parseDouble(temp[i][0]);
        }
    }
    
    private void loadGeneSymbol() {
        file.LoadCSV lcsv = new file.LoadCSV(geneSymbolListPath, true);
        geneSymbol = lcsv.getrowNames();
        geneNum = geneSymbol.length;
        System.out.println(geneNum);
        
        chr = new String[geneNum];
        startP = new int[geneNum];
        endP = new int[geneNum];
        geneLen = new int[geneNum];
        geneCnt = new double[geneNum];
        normGeneCnt = new double[geneNum];
        
        String[][] temp = lcsv.getData();
        for(int i = 0; i < geneNum; i++) {
            chr[i] = temp[i][0];
            startP[i] = Integer.parseInt(temp[i][1]);
            endP[i] = Integer.parseInt(temp[i][2]);
            geneLen[i] = Integer.parseInt(temp[i][3]);
        }
    }
    
    private void analyse() {
        for(int i = 0; i < geneNum; i++) {
            if(i % (geneNum / 10) == 0) {
                System.out.println(i + " / " + geneNum);
            }
            for(int j = 0; j < smLen; j++) {
                if((chr[i].equals(sm_chr[j])) && (sm_position[j] >= startP[i]) && (sm_position[j] <= endP[i])) {
                    geneCnt[i] = geneCnt[i] + smCnt[j];
                }
            }
            normGeneCnt[i] = (double) geneCnt[i] / geneLen[i];
        }
        
        ms3 = new util.MutationSet3[geneNum];
        for(int i = 0; i < geneNum; i++) {
            ms3[i] = new util.MutationSet3();
            ms3[i].setMutationName(geneSymbol[i]);
            ms3[i].setCnt(geneCnt[i]);
            ms3[i].setNormCnt(normGeneCnt[i]);
        }
        Arrays.sort(ms3);
    }
    
    private void saveResults() {
        try {
            FileWriter fw = new FileWriter(fileName + "(Gene).txt");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);
            
            outFile.println("Gene_Symbol,Mutation_CNT,Norm_CNT");
            
            for(int i = 0; i < geneNum; i++) {
                outFile.println(ms3[i].getMutationName() + "," + ms3[i].getCnt() + "," + ms3[i].getNormCnt());
            }
            
            outFile.close();
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        
        System.out.println(fileName + "(Gene).txt   DONE");
    }
    
    public void start() {
        analyse();
        saveResults();
    }
    
}
