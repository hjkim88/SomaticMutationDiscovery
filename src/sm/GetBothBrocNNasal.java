/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.File;
import java.util.Arrays;

/**
 *
 * @author Hyunjin Kim
 */
public class GetBothBrocNNasal {
    
    private int sampleNum;
    private String[] sampleName;
    
    private int bothSampleNum;
    private String[] bothSampleName;
    
    private final String mutectPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/somatic_results/";
    
    public GetBothBrocNNasal() {
        initVariables();
    }
    
    private void initVariables() {
        calSampleName();
        getRedundant();
    }
    
    private void calSampleName() {
        File f = new File(mutectPath);
        File[] files = f.listFiles();
        
        sampleNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".mutations")) {
                //System.out.println(file.getName().substring(0, file.getName().length()-10));
                sampleNum++;
            }
        }
        System.out.println(sampleNum);
        
        sampleName = new String[sampleNum];
        
        int cnt = 0;
        for (File fe : files) {
            if (fe.getName().endsWith(".mutations")) {
                sampleName[cnt] = fe.getName().substring(0, fe.getName().length() - 10);
                cnt++;
            }
        }
    }
    
    private void getRedundant() {
        Arrays.sort(sampleName);
        String temp;
        bothSampleNum = 0;
        for(int i = 1; i < sampleNum; i++) {
            temp = sampleName[i-1].substring(0, sampleName[i-1].length()-3);
            if(temp.equals(sampleName[i].substring(0, sampleName[i].length()-3))) {
                bothSampleNum++;
            }
        }
        
        bothSampleName = new String[bothSampleNum];
        bothSampleNum = 0;
        
        for(int i = 1; i < sampleNum; i++) {
            temp = sampleName[i-1].substring(0, sampleName[i-1].length()-3);
            if(temp.equals(sampleName[i].substring(0, sampleName[i].length()-3))) {
                bothSampleName[bothSampleNum] = temp;
                bothSampleNum++;
            }
        }
    }
    
    public String[] getBothSampleName() {
        return bothSampleName;
    }
    
}
