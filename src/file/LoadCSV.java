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
public class LoadCSV {
    
    private String csvFilePath;
    private String[] colNames;
    private String[] rowNames;
    private String[][] data;
    private int colNum;
    private int rowNum;
    
    private boolean isHeader;
    
    public LoadCSV(String filePath, boolean isHeader) {
        initVariables(filePath, isHeader);
        loadFile();
    }
    
    private void initVariables(String filePath, boolean isHeader) {
        this.csvFilePath = filePath;
        colNum = 0;
        rowNum = 0;
        
        this.isHeader = isHeader;
    }
    
    private void loadFile() {
        try {
            File f = new File(csvFilePath);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            String[] str;
            if(isHeader == true) {
                str = br.readLine().split(",");
                colNum = str.length-1;
                colNames = new String[colNum];
                for(int i = 0; i < colNum; i++) {
                    colNames[i] = str[i+1];
                }
                while(br.readLine() != null) {
                    rowNum++;
                }
            }
            else {
                str = br.readLine().split(",");
                colNum = str.length-1;
                rowNum++;
                while(br.readLine() != null) {
                    rowNum++;
                }
            }
            
            rowNames = new String[rowNum];
            data = new String[rowNum][colNum];
            
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            if(isHeader == true) {
                br.readLine();
            }
            
            for(int i = 0; i < rowNum; i++) {
                str = br.readLine().split(",");
                rowNames[i] = str[0];
                for(int j = 0; j < colNum; j++) {
                    data[i][j] = str[j+1];
                }
            }
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".loadFile()");
        }
    }
    
    public String[][] getData() {
        return data;
    }
    
    public String[] getColNames() {
        return colNames;
    }
    
    public String[] getrowNames() {
        return rowNames;
    }
    
    public int[][] getDataInt() {
        int[][] results = new int[rowNum][colNum];
        for(int i = 0; i < rowNum; i++) {
            for(int j = 0; j < colNum; j++) {
                results[i][j] = Integer.parseInt(data[i][j]);
            }
        }
        
        return results;
    }
    
}
