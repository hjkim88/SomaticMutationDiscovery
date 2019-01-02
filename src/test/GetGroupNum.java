/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author kim01
 */
public class GetGroupNum {
    
    private final String dataPath = "C:/Users/kim01/Documents/Research/GenotypingData/";
    private final String mutationPath = dataPath + "somatic_results2/";
    private final String clinPath = dataPath + "clinical_info.csv";
    
    private int sampleNum;
    private String[] sampleName;
    
    private String[][] status;      // [sampleNum][1: Cell (BR, NS), 2: CancerType (Squam, Adeno, NSCLC, Benign, HV), 3: Smoking (Current, m12, 1_12, b1, Never), 4: Gender(M, F)]
    private int[] mutationNum;    // [sampleNum]
    
    public GetGroupNum() {
        initVariables();
    }
    
    private void initVariables() {
        loadMutationPath();
        loadClinData();
    }
    
    private void loadMutationPath() {
        File f = new File(mutationPath);
        File[] files = f.listFiles();
        
        sampleNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".mutations")) {
                System.out.println(file.getName().substring(0, file.getName().length()-13));
                sampleNum++;
            }
        }
        System.out.println(sampleNum);
        
        sampleName = new String[sampleNum];
        status = new String[sampleNum][4];
        
        int cnt = 0;
        for (File fe : files) {
            if (fe.getName().endsWith(".mutations")) {
                sampleName[cnt] = fe.getName().substring(0, fe.getName().length() - 10);
                cnt++;
            }
        }
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
                
                for(int i = 0; i < sampleNum; i++) {
                    if(str[0].equals(sampleName[i])) {
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
    
    private void printGroupNum() {
        int cancer_current_br = 0, cancer_current_ns = 0, cancer_ex_br = 0, cancer_ex_ns = 0, cancer_never_br = 0, cancer_never_ns = 0,
            benign_current_br = 0, benign_current_ns = 0, benign_ex_br = 0, benign_ex_ns = 0, benign_never_br = 0, benign_never_ns = 0,
            hv_current_br = 0, hv_current_ns = 0, hv_ex_br = 0, hv_ex_ns = 0, hv_never_br = 0, hv_never_ns = 0;
        
        for(int i = 0; i < sampleNum; i++) {
            if(status[i][0].equals("Bronchial")) {
                if(status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS")) {
                    if(status[i][2].equals("current")) {
                        cancer_current_br++;
                    }
                    else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                        cancer_ex_br++;
                    }
                    else if(status[i][2].equals("never")) {
                        cancer_never_br++;
                    }
                }
                else if(status[i][1].equals("Benign")) {
                    if(status[i][2].equals("current")) {
                        benign_current_br++;
                    }
                    else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                        benign_ex_br++;
                    }
                    else if(status[i][2].equals("never")) {
                        benign_never_br++;
                    }
                }
            }
            else if(status[i][0].equals("Nasal")) {
                if(status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS")) {
                    if(status[i][2].equals("current")) {
                        cancer_current_ns++;
                    }
                    else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                        cancer_ex_ns++;
                    }
                    else if(status[i][2].equals("never")) {
                        cancer_never_ns++;
                    }
                }
                else if(status[i][1].equals("Benign")) {
                    if(status[i][2].equals("current")) {
                        benign_current_ns++;
                    }
                    else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                        benign_ex_ns++;
                    }
                    else if(status[i][2].equals("never")) {
                        benign_never_ns++;
                    }
                }
                else if(status[i][1].equals("HV")) {
                    if(status[i][2].equals("current")) {
                        hv_current_ns++;
                    }
                    else if(status[i][2].equals("ex.more12months") || status[i][2].equals("ex.1_to_12months") || status[i][2].equals("ex.less1month")) {
                        hv_ex_ns++;
                    }
                    else if(status[i][2].equals("never")) {
                        hv_never_ns++;
                    }
                }
            }
        }
        
        System.out.println("BR  Cancer  Benign  HV");
        System.out.println("Current\t" + cancer_current_br + "\t" + benign_current_br + "\t" + hv_current_br);
        System.out.println("EX\t" + cancer_ex_br + "\t" + benign_ex_br + "\t" + hv_ex_br);
        System.out.println("Never\t" + cancer_never_br + "\t" + benign_never_br + "\t" + hv_never_br);
        System.out.println("------------------------------------------------------------");
        System.out.println("NS  Cancer  Benign  HV");
        System.out.println("Current\t" + cancer_current_ns + "\t" + benign_current_ns + "\t" + hv_current_ns);
        System.out.println("EX\t" + cancer_ex_ns + "\t" + benign_ex_ns + "\t" + hv_ex_ns);
        System.out.println("Never\t" + cancer_never_ns + "\t" + benign_never_ns + "\t" + hv_never_ns);
    }
    
    public void start() {
        printGroupNum();
    }
    
}
