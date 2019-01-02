/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author kim01
 */
public class MakeDMGmatrix {
    
    //private final String dataPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/smoking-related/";
    private final String dataPath = "F:/Dropbox/Research/CRUK_CI/Mutect/somatic_realigned2/";
    private final String outputPath = dataPath + "Total/DMG.csv";
    private final String clinPath = dataPath + "../clinical_info.csv";
    private final String geneSymbolListPath = "F:/Dropbox/Research/CRUK_CI/Genotyping/Gene_Loci_GRCh37_transcript(final).txt";
    
    private String[] sampleID;
    private int sampleNum;
    
    private String[][] sm_chr;      // [sampleNum][each sample's mutationNum]
    private int[][] sm_position;    // [sampleNum][each sample's mutationNum]
    private String[][] status;      // [sampleNum][1: Cell (BR, NS), 2: CancerType (Squam, Adeno, NSCLC, Benign, HV), 3: Smoking (Current, m12, 1_12, b1, Never), 4: Gender(M, F)]
    
    private String[] geneSymbol, chr;
    private int[] startP, endP, geneLen;
    private int geneNum;
    
    private double[][] output;
    private String[] col_cancer, col_smoking;
    
    public MakeDMGmatrix() {
        initVariables();
    }
    
    private void initVariables() {
        loadMutationPath();
        loadClinData();
        loadMutations();
        loadGeneSymbol();
        makeColumns();
    }
    
    private void makeColumns() {
        col_cancer = new String[sampleNum];
        col_smoking = new String[sampleNum];
        
        for(int i = 0; i < sampleNum; i++) {
            col_cancer[i] = status[i][1];
            col_smoking[i] = status[i][2];
        }
    }
    
    private void loadMutationPath() {
        File f = new File(dataPath);
        File[] files = f.listFiles();
        
        sampleNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".mutations")) {
                sampleNum++;
            }
        }
        System.out.println(sampleNum);
        
        sampleID = new String[sampleNum];
        sm_chr = new String[sampleNum][0];
        sm_position = new int[sampleNum][0];
        status = new String[sampleNum][4];
        
        sampleNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".mutations")) {
                sampleID[sampleNum] = refineSampleName(file.getName().substring(0, file.getName().length() - 10)).toLowerCase();
                status[sampleNum][0] = "";
                //System.out.println(sampleID[cnt]);
                sampleNum++;
            }
        }
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
    
    private void loadClinData() {
        try {
            File f = new File(clinPath);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            String[] str;
            String line;
            br.readLine();
            
            while((line = br.readLine()) != null) {
                str = line.split(",");
                int idx = -1;
                
                String buf = refineSampleName(str[0]).toLowerCase();
                for(int i = 0; i < sampleNum; i++) {
                    if(buf.equals(sampleID[i])) {
                        idx = i;
                        break;
                    }
                }
                
                if(idx >= 0) {
                    status[idx][0] = str[2];
                    status[idx][1] = str[5];
                    status[idx][2] = str[7];
                    status[idx][3] = str[6];
                }
            }
            
            for(int  i = 0; i < sampleNum; i++) {
                if(status[i][0].equals("")) {
                    System.out.println("Error occurred : idx = " + i + ", sample name = " + sampleID[i]);
                    System.exit(0);
                }
            }
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".loadClinData()");
        }
    }
    
    private void loadMutations() {
        for(int i = 0; i < sampleNum; i++) {
            try {
                File f = new File(dataPath + sampleID[i] + ".mutations");
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                
                int cnt = 0;
                while(br.readLine() != null) {
                    cnt++;
                }
                
                sm_chr[i] = new String[cnt];
                sm_position[i] = new int[cnt];
                
                fr = new FileReader(f);
                br = new BufferedReader(fr);
                
                String[] temp;
                for(int j = 0; j < cnt; j++) {
                    temp = br.readLine().split("_");
                    sm_chr[i][j] = temp[0];
                    sm_position[i][j] = Integer.parseInt(temp[1]);
                }
                
                br.close();
                fr.close();
            }
            catch(IOException ioe) {
                System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".loadMutations()");
            }
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
        output = new double[geneNum][sampleNum];
        
        String[][] temp = lcsv.getData();
        for(int i = 0; i < geneNum; i++) {
            chr[i] = temp[i][0];
            startP[i] = Integer.parseInt(temp[i][1]);
            endP[i] = Integer.parseInt(temp[i][2]);
            geneLen[i] = Integer.parseInt(temp[i][3]);
        }
    }
    
    private void analyse() {
        int[] geneCnt = new int[geneNum];
        
        for(int i = 0; i < sampleNum; i++) {
            for(int j = 0; j < geneNum; j++) {
                geneCnt[j] = 0;
                for(int k = 0; k < sm_position[i].length; k++) {
                    if((chr[j].equals(sm_chr[i][k])) && (sm_position[i][k] >= startP[j]) && (sm_position[i][k] <= endP[j])) {
                        geneCnt[j]++;
                    }
                }
                output[j][i] = (double) geneCnt[j] / geneLen[j];
            }
        }
    }
    
    private void writeMatrix() {
        try {
            FileWriter fw = new FileWriter(outputPath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);
            
            outFile.print("\"\"");
            for(int i = 0; i < sampleNum; i++) {
                outFile.print("," + sampleID[i]);
            }
            outFile.println();
            
            outFile.print("\"\"");
            for(int i = 0; i < sampleNum; i++) {
                outFile.print("," + col_cancer[i]);
            }
            outFile.println();
            
            outFile.print("\"\"");
            for(int i = 0; i < sampleNum; i++) {
                outFile.print("," + col_smoking[i]);
            }
            outFile.println();
            
            for(int i = 0; i < geneNum; i++) {
                outFile.print(geneSymbol[i]);
                for(int j = 0; j < sampleNum; j++) {
                    outFile.print("," + output[i][j]);
                }
                outFile.println();
            }
            
            outFile.close();
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void start() {
        analyse();
        writeMatrix();
    }
}
