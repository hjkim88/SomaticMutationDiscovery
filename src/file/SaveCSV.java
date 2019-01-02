/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 *
 * @author Hyunjin Kim
 */
public class SaveCSV {
    
    private String[] rowNames;
    private String[] colNames;
    private double[][] data;
    private int[][] data2;
    private String[][] data3;
    private String outPath;
    private util.MutationSet2[] ms;
    
    private int rowNum;
    private int colNum;
    private int msLen;
    
    private boolean isDataDouble;
    private boolean isDataMutationSet;
    private boolean isNoHeader;
    
    public SaveCSV(String[] rowNames, int rowNum, String[] colNames, int colNum, double[][] data, boolean isNoHeader, String path) {
        initVariables(rowNames, rowNum, colNames, colNum, data, isNoHeader, path);
    }
    
    public SaveCSV(String[] rowNames, int rowNum, String[] colNames, int colNum, int[][] data, boolean isNoHeader, String path) {
        initVariables(rowNames, rowNum, colNames, colNum, data, isNoHeader, path);
    }
    
    public SaveCSV(String[] rowNames, int rowNum, String[] colNames, int colNum, String[][] data, boolean isNoHeader, String path) {
        initVariables(rowNames, rowNum, colNames, colNum, data, isNoHeader, path);
    }
    
    public SaveCSV(util.MutationSet2[] data, int msLen, boolean isNoHeader, String path) {
        initVariables(data, msLen, isNoHeader, path);
    }
    
    private void initVariables(String[] rowNames, int rowNum, String[] colNames, int colNum, double[][] data, boolean isNoHeader, String path) {
        this.rowNames = rowNames;
        this.rowNum = rowNum;
        this.colNames = colNames;
        this.colNum = colNum;
        this.data = data;
        this.outPath = path;
        isDataDouble = true;
        this.isNoHeader = isNoHeader;
        isDataMutationSet = false;
    }
    
    private void initVariables(String[] rowNames, int rowNum, String[] colNames, int colNum, int[][] data, boolean isNoHeader, String path) {
        this.rowNames = rowNames;
        this.rowNum = rowNum;
        this.colNames = colNames;
        this.colNum = colNum;
        this.data2 = data;
        this.outPath = path;
        isDataDouble = false;
        this.isNoHeader = isNoHeader;
        isDataMutationSet = false;
    }
    
    private void initVariables(String[] rowNames, int rowNum, String[] colNames, int colNum, String[][] data, boolean isNoHeader, String path) {
        this.rowNames = rowNames;
        this.rowNum = rowNum;
        this.colNames = colNames;
        this.colNum = colNum;
        this.data3 = data;
        this.outPath = path;
        isDataDouble = false;
        this.isNoHeader = isNoHeader;
        isDataMutationSet = false;
    }
    
    private void initVariables(util.MutationSet2[] data, int msLen, boolean isNoHeader, String path) {
        this.ms = data;
        this.msLen = msLen;
        this.outPath = path;
        isDataDouble = false;
        this.isNoHeader = isNoHeader;
        isDataMutationSet = true;
    }
    
    private void save() {
        try {
            FileWriter fw = new FileWriter(outPath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);
            
            if(isNoHeader == false) {
                outFile.printf("\"\"");
                for(int i = 0; i < colNum; i++) {
                    outFile.print("," + colNames[i]);
                }
                outFile.println();
            }
            
            if(isDataDouble == true) {
                for(int i = 0; i < rowNum; i++) {
                    outFile.print(rowNames[i]);
                    for(int j = 0; j < colNum; j++) {
                        outFile.print("," + data[i][j]);
                    }
                    outFile.println();
                }
            }
            else if(isDataMutationSet == true) {
                for(int i = 0; i < msLen; i++) {
                    outFile.println(ms[i].getMutationName() + "," + ms[i].getCnt());
                }
            }
            else if(data2 != null){
                for(int i = 0; i < rowNum; i++) {
                    outFile.print(rowNames[i]);
                    for(int j = 0; j < colNum; j++) {
                        outFile.print("," + data2[i][j]);
                    }
                    outFile.println();
                }
            }
            else if(data3 != null){
                for(int i = 0; i < rowNum; i++) {
                    outFile.print(rowNames[i]);
                    for(int j = 0; j < colNum; j++) {
                        outFile.print("," + data3[i][j]);
                    }
                    outFile.println();
                }
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
        save();
    }
    
}
