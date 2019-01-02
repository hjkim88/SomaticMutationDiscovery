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

/**
 *
 * @author kim01
 */
public class SmokingMutations {
    
    private final String mutationPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/somatic_realigned2/";
    private final String totalInfoPath = mutationPath + "../total_result.csv";
    
    private String[][] mutationInfo;
    private String[] sampleNames;
    private int fileNum;
    
    private String[][] totalInfo;
    private String[] tInfo_row;
    private int tInfo_rowNum;
    
    public SmokingMutations() {
        initVariables();
    }
    
    private void initVariables() {
        loadMutations();
        loadTotalInfo();
    }
    
    private void loadMutations() {
        File f = new File(mutationPath);
        File[] files = f.listFiles();
        
        fileNum = 0;
        for(File file : files) {
            if (file.getName().endsWith(".mutations")) {
                fileNum++;
            }
        }
        
        mutationInfo = new String[fileNum][0];
        sampleNames = new String[fileNum];
        
        fileNum = 0;
        for(File file : files) {
            if (file.getName().endsWith(".mutations")) {
                sampleNames[fileNum] = file.getName().substring(0, file.getName().length() - 10);
                fileNum++;
            }
        }
        
        for(int i = 0; i < fileNum; i++) {
            mutationInfo[i] = callMutations(mutationPath + sampleNames[i] + ".mutations");
        }
    }
    
    private void loadTotalInfo() {
        file.LoadCSV lcsv = new file.LoadCSV(totalInfoPath, true);
        tInfo_row = lcsv.getrowNames();
        tInfo_rowNum = tInfo_row.length;
        totalInfo = lcsv.getData();
    }
    
    private String[] callMutations(String path) {
        String[] list = null;
        
        int cnt = 0;
        try {
            File f = new File(path);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            while(br.readLine() != null) {
                cnt++;
            }
            
            list = new String[cnt];
            
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            for(int i = 0; i < cnt; i++) {
                list[i] = br.readLine();
            }
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".callMutations()");
        }
        
        return list;
    }
    
    private void extractSmokingMutations() {
        // Divide mutationInfo to smoking group and non-smoking group. Then find which mutations are frequently occurred in smoker group
        // Those may be real somatic mutations. Find what was wrong with the previous analysis
    }
    
    public void start() {
        extractSmokingMutations();
    }
    
}
