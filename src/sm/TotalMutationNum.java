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
public class TotalMutationNum {
    
    private final String dataPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/";
    //private final String dataPath = "F:/Dropbox/Research/CRUK_CI/Mutect/";
    private final String mutationPath = dataPath + "somatic_realigned3/";
    private final String clinPath = dataPath + "clinical_info.csv";
    
    private final String outputPath = mutationPath + "total_mutationNum/";
    
    private int sampleNum;
    private String[] sampleName;
    
    private String[][] status;      // [sampleNum][1: Cell (BR, NS), 2: CancerType (Squam, Adeno, NSCLC, Benign, HV), 3: Smoking (Current, m12, 1_12, b1, Never), 4: Gender(M, F)]
    private int[] mutationNum;    // [sampleNum]
    
    private int[] cancer_br, cancer_ns, benign_br, benign_ns, hv_ns, current_br, current_ns, ex_br, ex_ns, smoking_ns, never_ns;
    private String[] cancer_br_sampleName, cancer_ns_sampleName, benign_br_sampleName, benign_ns_sampleName, hv_ns_sampleName, current_br_sampleName, current_ns_sampleName, ex_br_sampleName, ex_ns_sampleName, smoking_ns_sampleName, never_ns_sampleName;
    private int cancer_br_sampleNum, cancer_ns_sampleNum, benign_br_sampleNum, benign_ns_sampleNum, hv_ns_sampleNum, current_br_sampleNum, current_ns_sampleNum, ex_br_sampleNum, ex_ns_sampleNum, smoking_ns_sampleNum, never_ns_sampleNum; 
    
    private int[][] cancer_br_smokingStatus, cancer_ns_smokingStatus, benign_br_smokingStatus, benign_ns_smokingStatus, hv_ns_smokingStatus, current_br_smokingStatus, current_ns_smokingStatus, ex_br_smokingStatus, ex_ns_smokingStatus, smoking_ns_smokingStatus, never_ns_smokingStatus;
    
    public TotalMutationNum() {
        initVariables();
    }
    
    private void initVariables() {
        loadMutationPath();
        loadClinData();
        decideSampleNum();
    }
    
    private void loadMutationPath() {
        File f = new File(mutationPath);
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
        status = new String[sampleNum][4];
        mutationNum = new int[sampleNum];
        
        int cnt = 0;
        for (File fe : files) {
            if (fe.getName().endsWith(".mutations")) {
                sampleName[cnt] = fe.getName().substring(0, fe.getName().length() - 10);
                mutationNum[cnt] = new file.GetMutationNum(fe.getAbsolutePath()).getNum();
                status[cnt][0] = "";
                cnt++;
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
                
                String buf = refineSampleName(str[0]);
                for(int i = 0; i < sampleNum; i++) {
                    if(buf.equals(sampleName[i].toUpperCase())) {
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
                    System.out.println("Error occurred : idx = " + i + ", sample name = " + sampleName[i]);
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
    
    private void decideSampleNum() {
        cancer_br_sampleNum = 0;
        cancer_ns_sampleNum = 0;
        benign_br_sampleNum = 0;
        benign_ns_sampleNum = 0;
        hv_ns_sampleNum = 0;
        current_br_sampleNum = 0;
        current_ns_sampleNum = 0;
        ex_br_sampleNum = 0;
        ex_ns_sampleNum = 0;
        smoking_ns_sampleNum = 0;
        never_ns_sampleNum = 0; 
        
        for(int i = 0; i < sampleNum; i++) {
            if(status[i][0].equals("Bronchial")) {
                if(status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS")) {
                    cancer_br_sampleNum++;
                }
                else if(status[i][1].equals("Benign")) {
                    benign_br_sampleNum++;
                }
                
                if(status[i][2].equals("current")) {
                    current_br_sampleNum++;
                }
                else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                    ex_br_sampleNum++;
                }
            }
            else if(status[i][0].equals("Nasal")) {
                if(status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS")) {
                    cancer_ns_sampleNum++;
                }
                else if(status[i][1].equals("Benign")) {
                    benign_ns_sampleNum++;
                }
                else if(status[i][1].equals("HV")) {
                    hv_ns_sampleNum++;
                }
                
                if(status[i][2].equals("current")) {
                    current_ns_sampleNum++;
                    smoking_ns_sampleNum++;
                }
                else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                    ex_ns_sampleNum++;
                    smoking_ns_sampleNum++;
                }
                else if(status[i][2].equals("never")) {
                    never_ns_sampleNum++;
                }
            }
        }
        
        cancer_br = new int[cancer_br_sampleNum];
        cancer_ns = new int[cancer_ns_sampleNum];
        benign_br = new int[benign_br_sampleNum];
        benign_ns = new int[benign_ns_sampleNum];
        hv_ns = new int[hv_ns_sampleNum];
        current_br = new int[current_br_sampleNum];
        current_ns = new int[current_ns_sampleNum];
        ex_br = new int[ex_br_sampleNum];
        ex_ns = new int[ex_ns_sampleNum];
        smoking_ns = new int[smoking_ns_sampleNum];
        never_ns = new int[never_ns_sampleNum];
        
        cancer_br_sampleName = new String[cancer_br_sampleNum];
        cancer_ns_sampleName = new String[cancer_ns_sampleNum];
        benign_br_sampleName = new String[benign_br_sampleNum];
        benign_ns_sampleName = new String[benign_ns_sampleNum];
        hv_ns_sampleName = new String[hv_ns_sampleNum];
        current_br_sampleName = new String[current_br_sampleNum];
        current_ns_sampleName = new String[current_ns_sampleNum];
        ex_br_sampleName = new String[ex_br_sampleNum];
        ex_ns_sampleName = new String[ex_ns_sampleNum];
        smoking_ns_sampleName = new String[smoking_ns_sampleNum];
        never_ns_sampleName = new String[never_ns_sampleNum];
        
        cancer_br_smokingStatus = new int[cancer_br_sampleNum][1];
        cancer_ns_smokingStatus = new int[cancer_ns_sampleNum][1];
        benign_br_smokingStatus = new int[benign_br_sampleNum][1];
        benign_ns_smokingStatus = new int[benign_ns_sampleNum][1];
        hv_ns_smokingStatus = new int[hv_ns_sampleNum][1];
        current_br_smokingStatus = new int[current_br_sampleNum][1];
        current_ns_smokingStatus = new int[current_ns_sampleNum][1];
        ex_br_smokingStatus = new int[ex_br_sampleNum][1];
        ex_ns_smokingStatus = new int[ex_ns_sampleNum][1];
        smoking_ns_smokingStatus = new int[smoking_ns_sampleNum][1];
        never_ns_smokingStatus = new int[never_ns_sampleNum][1];
        
        cancer_br_sampleNum = 0;
        cancer_ns_sampleNum = 0;
        benign_br_sampleNum = 0;
        benign_ns_sampleNum = 0;
        hv_ns_sampleNum = 0;
        current_br_sampleNum = 0;
        current_ns_sampleNum = 0;
        ex_br_sampleNum = 0;
        ex_ns_sampleNum = 0;
        smoking_ns_sampleNum = 0;
        never_ns_sampleNum = 0;
        
        for(int i = 0; i < sampleNum; i++) {
            if(status[i][0].equals("Bronchial")) {
                if(status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS")) {
                    cancer_br[cancer_br_sampleNum] = mutationNum[i];
                    cancer_br_smokingStatus[cancer_br_sampleNum][0] =  getIntVersionOfString(status[i][2]);
                    cancer_br_sampleName[cancer_br_sampleNum] = sampleName[i];
                    cancer_br_sampleNum++;
                }
                else if(status[i][1].equals("Benign")) {
                    benign_br[benign_br_sampleNum] = mutationNum[i];
                    benign_br_smokingStatus[benign_br_sampleNum][0] =  getIntVersionOfString(status[i][2]);
                    benign_br_sampleName[benign_br_sampleNum] = sampleName[i];
                    benign_br_sampleNum++;
                }
                
                if(status[i][2].equals("current")) {
                    current_br[current_br_sampleNum] = mutationNum[i];
                    current_br_smokingStatus[current_br_sampleNum][0] =  getIntVersionOfString(status[i][1]);
                    current_br_sampleName[current_br_sampleNum] = sampleName[i];
                    current_br_sampleNum++;
                }
                else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                    ex_br[ex_br_sampleNum] = mutationNum[i];
                    ex_br_smokingStatus[ex_br_sampleNum][0] =  getIntVersionOfString(status[i][1]);
                    ex_br_sampleName[ex_br_sampleNum] = sampleName[i];
                    ex_br_sampleNum++;
                }
            }
            else if(status[i][0].equals("Nasal")) {
                if(status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS")) {
                    cancer_ns[cancer_ns_sampleNum] = mutationNum[i];
                    cancer_ns_smokingStatus[cancer_ns_sampleNum][0] =  getIntVersionOfString(status[i][2]);
                    cancer_ns_sampleName[cancer_ns_sampleNum] = sampleName[i];
                    cancer_ns_sampleNum++;
                }
                else if(status[i][1].equals("Benign")) {
                    benign_ns[benign_ns_sampleNum] = mutationNum[i];
                    benign_ns_smokingStatus[benign_ns_sampleNum][0] =  getIntVersionOfString(status[i][2]);
                    benign_ns_sampleName[benign_ns_sampleNum] = sampleName[i];
                    benign_ns_sampleNum++;
                }
                else if(status[i][1].equals("HV")) {
                    hv_ns[hv_ns_sampleNum] = mutationNum[i];
                    hv_ns_smokingStatus[hv_ns_sampleNum][0] =  getIntVersionOfString(status[i][2]);
                    hv_ns_sampleName[hv_ns_sampleNum] = sampleName[i];
                    hv_ns_sampleNum++;
                }
                
                if(status[i][2].equals("current")) {
                    current_ns[current_ns_sampleNum] = mutationNum[i];
                    current_ns_smokingStatus[current_ns_sampleNum][0] =  getIntVersionOfString(status[i][1]);
                    current_ns_sampleName[current_ns_sampleNum] = sampleName[i];
                    current_ns_sampleNum++;
                    smoking_ns[smoking_ns_sampleNum] = mutationNum[i];
                    smoking_ns_smokingStatus[smoking_ns_sampleNum][0] =  getIntVersionOfString(status[i][1]);
                    smoking_ns_sampleName[smoking_ns_sampleNum] = sampleName[i];
                    smoking_ns_sampleNum++;
                }
                else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                    ex_ns[ex_ns_sampleNum] = mutationNum[i];
                    ex_ns_smokingStatus[ex_ns_sampleNum][0] =  getIntVersionOfString(status[i][1]);
                    ex_ns_sampleName[ex_ns_sampleNum] = sampleName[i];
                    ex_ns_sampleNum++;
                    smoking_ns[smoking_ns_sampleNum] = mutationNum[i];
                    smoking_ns_smokingStatus[smoking_ns_sampleNum][0] =  getIntVersionOfString(status[i][1]);
                    smoking_ns_sampleName[smoking_ns_sampleNum] = sampleName[i];
                    smoking_ns_sampleNum++;
                }
                else if(status[i][2].equals("never")) {
                    never_ns[never_ns_sampleNum] = mutationNum[i];
                    never_ns_smokingStatus[never_ns_sampleNum][0] =  getIntVersionOfString(status[i][1]);
                    never_ns_sampleName[never_ns_sampleNum] = sampleName[i];
                    never_ns_sampleNum++;
                }
            }
        }
    }
    
    private int getIntVersionOfString(String str) {
        int d = -1;
        
        if(str.equals("current") || str.equals("Adeno") || str.equals("Squam") || str.equals("NSCLC_NOS")) {
            d = 1;
        }
        else if(str.equals("ex.more12months") || str.equals("ex.1_to_12months") || str.equals("ex.less1month") || str.equals("Benign")) {
            d = 2;
        }
        else if(str.equals("never") || str.equals("HV")) {
            d = 3;
        }
        
        return d;
    }
    
    private String[] intArrToStrArr(int[] iArr) {
        String[] sArr = new String[iArr.length];
        
        for(int i = 0; i < sArr.length; i++) {
            sArr[i] = "" + iArr[i];
        }
        
        return sArr;
    }
    
    private void printResults() { 
        new file.SaveCSV(intArrToStrArr(cancer_br), cancer_br_sampleNum, new String[] {"s"}, 1, cancer_br_smokingStatus, true, outputPath + "cancer_br_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(cancer_ns), cancer_ns_sampleNum, new String[] {"s"}, 1, cancer_ns_smokingStatus, true, outputPath + "cancer_ns_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(benign_br), benign_br_sampleNum, new String[] {"s"}, 1, benign_br_smokingStatus, true, outputPath + "benign_br_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(benign_ns), benign_ns_sampleNum, new String[] {"s"}, 1, benign_ns_smokingStatus, true, outputPath + "benign_ns_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(hv_ns), hv_ns_sampleNum, new String[] {"s"}, 1, hv_ns_smokingStatus, true, outputPath + "hv_ns_total_mutationNum_s.txt").start();
        
        new file.SaveCSV(intArrToStrArr(current_br), current_br_sampleNum, new String[] {"s"}, 1, current_br_smokingStatus, true, outputPath + "current_br_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(current_ns), current_ns_sampleNum, new String[] {"s"}, 1, current_ns_smokingStatus, true, outputPath + "current_ns_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(ex_br), ex_br_sampleNum, new String[] {"s"}, 1, ex_br_smokingStatus, true, outputPath + "ex_br_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(ex_ns), ex_ns_sampleNum, new String[] {"s"}, 1, ex_ns_smokingStatus, true, outputPath + "ex_ns_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(smoking_ns), smoking_ns_sampleNum, new String[] {"s"}, 1, smoking_ns_smokingStatus, true, outputPath + "smoking_ns_total_mutationNum_s.txt").start();
        new file.SaveCSV(intArrToStrArr(never_ns), never_ns_sampleNum, new String[] {"s"}, 1, never_ns_smokingStatus, true, outputPath + "never_ns_total_mutationNum_s.txt").start();
    }
    
    private int[][] formatChange(int[] input) {
        int[][] output = new int[input.length][1];
        
        for(int i = 0; i < input.length; i++) {
            output[i][0] = input[i];
        }
        
        return output;
    }
    
    private void printResultsWithSampleName() {
        new file.SaveCSV(cancer_br_sampleName, cancer_br_sampleNum, new String[] {"s"}, 1, formatChange(cancer_br), true, outputPath + "cancer_br_total_mutationNum_sn.txt").start();
        new file.SaveCSV(cancer_ns_sampleName, cancer_ns_sampleNum, new String[] {"s"}, 1, formatChange(cancer_ns), true, outputPath + "cancer_ns_total_mutationNum_sn.txt").start();
        new file.SaveCSV(benign_br_sampleName, benign_br_sampleNum, new String[] {"s"}, 1, formatChange(benign_br), true, outputPath + "benign_br_total_mutationNum_sn.txt").start();
        new file.SaveCSV(benign_ns_sampleName, benign_ns_sampleNum, new String[] {"s"}, 1, formatChange(benign_ns), true, outputPath + "benign_ns_total_mutationNum_sn.txt").start();
        new file.SaveCSV(hv_ns_sampleName, hv_ns_sampleNum, new String[] {"s"}, 1, formatChange(hv_ns), true, outputPath + "hv_ns_total_mutationNum_sn.txt").start();
        
        new file.SaveCSV(current_br_sampleName, current_br_sampleNum, new String[] {"s"}, 1, formatChange(current_br), true, outputPath + "current_br_total_mutationNum_sn.txt").start();
        new file.SaveCSV(current_ns_sampleName, current_ns_sampleNum, new String[] {"s"}, 1, formatChange(current_ns), true, outputPath + "current_ns_total_mutationNum_sn.txt").start();
        new file.SaveCSV(ex_br_sampleName, ex_br_sampleNum, new String[] {"s"}, 1, formatChange(ex_br), true, outputPath + "ex_br_total_mutationNum_sn.txt").start();
        new file.SaveCSV(ex_ns_sampleName, ex_ns_sampleNum, new String[] {"s"}, 1, formatChange(ex_ns), true, outputPath + "ex_ns_total_mutationNum_sn.txt").start();
        new file.SaveCSV(smoking_ns_sampleName, smoking_ns_sampleNum, new String[] {"s"}, 1, formatChange(smoking_ns), true, outputPath + "smoking_ns_total_mutationNum_sn.txt").start();
        new file.SaveCSV(never_ns_sampleName, never_ns_sampleNum, new String[] {"s"}, 1, formatChange(never_ns), true, outputPath + "never_ns_total_mutationNum_sn.txt").start();
    }
    
    public void start() {
        printResults();
        printResultsWithSampleName();
    }
    
}
