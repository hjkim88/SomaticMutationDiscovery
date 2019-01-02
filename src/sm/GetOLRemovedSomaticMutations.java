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
import java.util.Arrays;

/**
 *
 * @author kim01
 */
public class GetOLRemovedSomaticMutations {
    
    private final String dataPath = "C:/Users/kim01/Documents/Research/GenotypingData/";
    //private final String dataPath = "F:/Research/Genotyping/";
    private final String vcfDirPath = dataPath + "new_files/";
    private final String outputPath = dataPath + "somatic_results2/";
    private final String clinPath = dataPath + "clinical_info.csv";
    
    private final String cancer_br_samplePath = dataPath + "total_mutationNum/cancer_br_sampleNames_aro.txt";
    private final String cancer_ns_samplePath = dataPath + "total_mutationNum/cancer_ns_sampleNames_aro.txt";
    private final String benign_br_samplePath = dataPath + "total_mutationNum/benign_br_sampleNames_aro.txt";
    private final String benign_ns_samplePath = dataPath + "total_mutationNum/benign_ns_sampleNames_aro.txt";
    private final String hv_ns_samplePath = dataPath + "total_mutationNum/hv_ns_sampleNames_aro.txt";
    private final String current_br_samplePath = dataPath + "total_mutationNum/current_br_sampleNames_aro.txt";
    private final String current_ns_samplePath = dataPath + "total_mutationNum/current_ns_sampleNames_aro.txt";
    private final String ex_br_samplePath = dataPath + "total_mutationNum/ex_br_sampleNames_aro.txt";
    private final String ex_ns_samplePath = dataPath + "total_mutationNum/ex_ns_sampleNames_aro.txt";
    private final String never_ns_samplePath = dataPath + "total_mutationNum/never_ns_sampleNames_aro.txt";
    
    private String[] cancer_br_sample, cancer_ns_sample, benign_br_sample, benign_ns_sample, hv_ns_sample;
    private String[] current_br_sample, current_ns_sample, ex_br_sample, ex_ns_sample, never_ns_sample;
    
    private String[] dbsnp;
    private int vcfNum;
    private String[] vcfIDs;
    private String[] sampleID;
    
    private String[][] mutations;   // [sampleNum][each sample's mutationNum]
    private String[][] status;      // [sampleNum][1: Cell (BR, NS), 2: CancerType (Squam, Adeno, NSCLC, Benign, HV), 3: Smoking (Current, m12, 1_12, b1, Never), 4: Gender(M, F)]
    
    int cancer_br_num, benign_br_num, hv_br_num, cancer_ns_num, benign_ns_num, hv_ns_num;
    int current_br_num, m12_br_num, b1_12_br_num, b1_br_num, never_br_num, current_ns_num, m12_ns_num, b1_12_ns_num, b1_ns_num, never_ns_num;
    
    int cancer_br_sampleNum, benign_br_sampleNum, hv_br_sampleNum, cancer_ns_sampleNum, benign_ns_sampleNum, hv_ns_sampleNum;
    int current_br_sampleNum, m12_br_sampleNum, b1_12_br_sampleNum, b1_br_sampleNum, never_br_sampleNum, current_ns_sampleNum, m12_ns_sampleNum, b1_12_ns_sampleNum, b1_ns_sampleNum, never_ns_sampleNum;
    
    public GetOLRemovedSomaticMutations() {
        initVariables();
    }
    
    private void initVariables() {
        dbsnp = new file.LoadDBSNP().getDBSNP();
        
        loadVCFPath();
        loadClinData();
        loadSampleNames();
    }
    
    private String[] loadSample(String path) {
        String[] list = {};
        
        try {
            File f = new File(path);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            int rowNum = 0;
            while(br.readLine() != null) {
                rowNum++;
            }
            
            list = new String[rowNum];
            
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            for(int i = 0; i < rowNum; i++) {
                list[i] = br.readLine();
            }
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".loadSample()");
        }
        
        return list;
    }
    
    private void loadSampleNames() {
        cancer_br_sample = loadSample(cancer_br_samplePath);
        cancer_ns_sample = loadSample(cancer_ns_samplePath);
        benign_br_sample = loadSample(benign_br_samplePath);
        benign_ns_sample = loadSample(benign_ns_samplePath);
        hv_ns_sample = loadSample(hv_ns_samplePath);
        current_br_sample = loadSample(current_br_samplePath);
        current_ns_sample = loadSample(current_ns_samplePath);
        ex_br_sample = loadSample(ex_br_samplePath);
        ex_ns_sample = loadSample(ex_ns_samplePath);
        never_ns_sample = loadSample(never_ns_samplePath);
    }
    
    private void loadVCFPath() {
        File f = new File(vcfDirPath);
        File[] files = f.listFiles();
        
        vcfNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".vcf")) {
                //System.out.println(file.getName().substring(0, file.getName().length()-13));
                vcfNum++;
            }
        }
        System.out.println(vcfNum);
        
        vcfIDs = new String[vcfNum];
        sampleID = new String[vcfNum];
        mutations = new String[vcfNum][0];
        status = new String[vcfNum][4];
        
        int cnt = 0;
        for (File file : files) {
            if (file.getName().endsWith(".vcf")) {
                vcfIDs[cnt] = file.getName().substring(0, file.getName().length() - 13);
                sampleID[cnt] = refineSampleName(file.getName().substring(0, file.getName().length() - 13));
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
                
                for(int i = 0; i < vcfNum; i++) {
                    if(str[0].equals(sampleID[i])) {
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
            
            for(int  i = 0; i < vcfNum; i++) {
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
    
    private String[] getMutationOnly(String[] fullList) {
        int fullLen = fullList.length;
        String[] temp = fullList;
        int idx;
        int moLen = 0;
        
        for(int i = 0; i < fullLen; i++) {
            idx = Arrays.binarySearch(dbsnp, fullList[i]);
            if(idx < 0) {
                temp[moLen] = fullList[i];
                moLen++;
            }
        }
        
        String[] mo = new String[moLen];
        System.arraycopy(temp, 0, mo, 0, moLen);
        
        return mo;
    }
    
    private void extract() {
        String[] variantList;
        String[] refinedVList;
        for(int i = 0; i < vcfNum; i++) {
            variantList = new file.GetVFromVCF(vcfDirPath + vcfIDs[i] + "_filtered.vcf").getVariants();
            System.out.println(sampleID[i]);
            refinedVList = getMutationOnly(variantList);
            mutations[i] = refinedVList;
            //new file.SaveCSV(refinedVList, refinedVList.length, new String[] {}, 0, new int[][] {}, true, outputPath + sampleID[i] + ".mutations").start();
        }
    }
    
    private void addUp(String[] mutations, util.MutationSet[] ms, String type) {
        int num = -1;
        
        switch (type) {
            case "cancer_br":
                num = cancer_br_num;
                break;
            case "benign_br":
                num = benign_br_num;
                break;
            case "hv_br":
                num = hv_br_num;
                break;
            case "cancer_ns":
                num = cancer_ns_num;
                break;
            case "benign_ns":
                num = benign_ns_num;
                break;
            case "hv_ns":
                num = hv_ns_num;
                break;
            case "current_br":
                num = current_br_num;
                break;
            case "m12_br":
                num = m12_br_num;
                break;
            case "b1_12_br":
                num = b1_12_br_num;
                break;
            case "b1_br":
                num = b1_br_num;
                break;
            case "never_br":
                num = never_br_num;
                break;
            case "current_ns":
                num = current_ns_num;
                break;
            case "m12_ns":
                num = m12_ns_num;
                break;
            case "b1_12_ns":
                num = b1_12_ns_num;
                break;
            case "b1_ns":
                num = b1_ns_num;
                break;
            case "never_ns":
                num = never_ns_num;
                break;
            default:
                break;
        }
        
        String[] temp = new String[num];
        for(int i = 0; i < num; i++) {
            temp[i] = ms[i].getMutationName();
        }
        
        for(int i = 0; i < mutations.length; i++) {
            int idx = Arrays.binarySearch(temp, mutations[i]);
            
            if(idx >= 0) {
                ms[idx].setCnt(ms[idx].getCnt() + 1);
            }
            else {
                ms[num] = new util.MutationSet();
                ms[num].setMutationName(mutations[i]);
                ms[num].setCnt(1);
                num++;
            }
        }
        
        Arrays.sort(ms, 0, num);
        
        switch (type) {
            case "cancer_br":
                cancer_br_num = num;
                break;
            case "benign_br":
                benign_br_num = num;
                break;
            case "hv_br":
                hv_br_num = num;
                break;
            case "cancer_ns":
                cancer_ns_num = num;
                break;
            case "benign_ns":
                benign_ns_num = num;
                break;
            case "hv_ns":
                hv_ns_num = num;
                break;
            case "current_br":
                current_br_num = num;
                break;
            case "m12_br":
                m12_br_num = num;
                break;
            case "b1_12_br":
                b1_12_br_num = num;
                break;
            case "b1_br":
                b1_br_num = num;
                break;
            case "never_br":
                never_br_num = num;
                break;
            case "current_ns":
                current_ns_num = num;
                break;
            case "m12_ns":
                m12_ns_num = num;
                break;
            case "b1_12_ns":
                b1_12_ns_num = num;
                break;
            case "b1_ns":
                b1_ns_num = num;
                break;
            case "never_ns":
                never_ns_num = num;
                break;
            default:
                break;
        }
    }
    
    private boolean isInTheList(String[] list, String test) {
        boolean r = false;
        
        for (String list1 : list) {
            if (refineSampleName(list1).equals(test)) {
                r = true;
                break;
            }
        }
        
        return r;
    }
    
    private void analyse_cancerType() {
        int cancer_br_tvNum = 0, benign_br_tvNum = 0, hv_br_tvNum = 0, cancer_ns_tvNum = 0, benign_ns_tvNum = 0, hv_ns_tvNum = 0;
        
        for(int i = 0; i < vcfNum; i++) {
            if(status[i][0].equals("Bronchial")) {
                if(isInTheList(cancer_br_sample, sampleID[i]) && (status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS"))) {
                    cancer_br_tvNum = cancer_br_tvNum + mutations[i].length;
                }
                else if(isInTheList(benign_br_sample, sampleID[i]) && (status[i][1].equals("Benign"))) {
                    benign_br_tvNum = benign_br_tvNum + mutations[i].length;
                }
            }
            else if(status[i][0].equals("Nasal")) {
                if(isInTheList(cancer_ns_sample, sampleID[i]) && (status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS"))) {
                    cancer_ns_tvNum = cancer_ns_tvNum + mutations[i].length;
                }
                else if(isInTheList(benign_ns_sample, sampleID[i]) && (status[i][1].equals("Benign"))) {
                    benign_ns_tvNum = benign_ns_tvNum + mutations[i].length;
                }
                else if(isInTheList(hv_ns_sample, sampleID[i]) && (status[i][1].equals("HV"))) {
                    hv_ns_tvNum = hv_ns_tvNum + mutations[i].length;
                }
            }
        }
        
        util.MutationSet[] cancer_br = new util.MutationSet[cancer_br_tvNum];
        util.MutationSet[] benign_br = new util.MutationSet[benign_br_tvNum];
        util.MutationSet[] hv_br = new util.MutationSet[hv_br_tvNum];
        
        util.MutationSet[] cancer_ns = new util.MutationSet[cancer_ns_tvNum];
        util.MutationSet[] benign_ns = new util.MutationSet[benign_ns_tvNum];
        util.MutationSet[] hv_ns = new util.MutationSet[hv_ns_tvNum];
        
        cancer_br_num = 0;
        benign_br_num = 0;
        hv_br_num = 0;
        cancer_ns_num = 0;
        benign_ns_num = 0;
        hv_ns_num = 0;
        
        cancer_br_sampleNum = 0;
        benign_br_sampleNum = 0;
        hv_br_sampleNum = 0;
        cancer_ns_sampleNum = 0;
        benign_ns_sampleNum = 0;
        hv_ns_sampleNum = 0;
        
        for(int i = 0; i < vcfNum; i++) {
            if(status[i][0].equals("Bronchial")) {
                if(isInTheList(cancer_br_sample, sampleID[i]) && (status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS"))) {
                    addUp(mutations[i], cancer_br, "cancer_br");
                    cancer_br_sampleNum++;
                }
                else if(isInTheList(benign_br_sample, sampleID[i]) && (status[i][1].equals("Benign"))) {
                    addUp(mutations[i], benign_br, "benign_br");
                    benign_br_sampleNum++;
                }
            }
            else if(status[i][0].equals("Nasal")) {
                if(isInTheList(cancer_ns_sample, sampleID[i]) && (status[i][1].equals("Adeno") || status[i][1].equals("Squam") || status[i][1].equals("NSCLC_NOS"))) {
                    addUp(mutations[i], cancer_ns, "cancer_ns");
                    cancer_ns_sampleNum++;
                }
                else if(isInTheList(benign_ns_sample, sampleID[i]) && (status[i][1].equals("Benign"))) {
                    addUp(mutations[i], benign_ns, "benign_ns");
                    benign_ns_sampleNum++;
                }
                else if(isInTheList(hv_ns_sample, sampleID[i]) && (status[i][1].equals("HV"))) {
                    addUp(mutations[i], hv_ns, "hv_ns");
                    hv_ns_sampleNum++;
                }
            }
        }
        
        util.MutationSet2[] ms2 = new util.MutationSet2[cancer_br_num];
        for(int i = 0; i < cancer_br_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(cancer_br[i].getMutationName());
            ms2[i].setCnt(cancer_br[i].getCnt() / (double) cancer_br_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, cancer_br_num, true, outputPath + "Total/cancer_br_aro.csv").start();
        
        ms2 = new util.MutationSet2[benign_br_num];
        for(int i = 0; i < benign_br_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(benign_br[i].getMutationName());
            ms2[i].setCnt(benign_br[i].getCnt() / (double) benign_br_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, benign_br_num, true, outputPath + "Total/benign_br_aro.csv").start();
        
        ms2 = new util.MutationSet2[cancer_ns_num];
        for(int i = 0; i < cancer_ns_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(cancer_ns[i].getMutationName());
            ms2[i].setCnt(cancer_ns[i].getCnt() / (double) cancer_ns_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, cancer_ns_num, true, outputPath + "Total/cancer_ns_aro.csv").start();
        
        ms2 = new util.MutationSet2[benign_ns_num];
        for(int i = 0; i < benign_ns_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(benign_ns[i].getMutationName());
            ms2[i].setCnt(benign_ns[i].getCnt() / (double) benign_ns_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, benign_ns_num, true, outputPath + "Total/benign_ns_aro.csv").start();
        
        ms2 = new util.MutationSet2[hv_ns_num];
        for(int i = 0; i < hv_ns_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(hv_ns[i].getMutationName());
            ms2[i].setCnt(hv_ns[i].getCnt() / (double) hv_ns_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, hv_ns_num, true, outputPath + "Total/hv_ns_aro.csv").start();
    }
    
    private void analyse_smoking() {
        int current_br_tvNum = 0, m12_br_tvNum = 0, b1_12_br_tvNum = 0, b1_br_tvNum = 0, never_br_tvNum = 0, 
                current_ns_tvNum = 0, m12_ns_tvNum = 0, b1_12_ns_tvNum = 0, b1_ns_tvNum = 0, never_ns_tvNum = 0;
        
        for(int i = 0; i < vcfNum; i++) {
            if(status[i][0].equals("Bronchial")) {
                if(isInTheList(current_br_sample, sampleID[i]) && (status[i][2].equals("current"))) {
                    current_br_tvNum = current_br_tvNum + mutations[i].length;
                }
                else if(isInTheList(ex_br_sample, sampleID[i]) && (status[i][2].equals("ex.more12months"))) {
                    m12_br_tvNum = m12_br_tvNum + mutations[i].length;
                }
                else if(isInTheList(ex_br_sample, sampleID[i]) && (status[i][2].equals("ex.1_to_12months"))) {
                    b1_12_br_tvNum = b1_12_br_tvNum + mutations[i].length;
                }
                else if(isInTheList(ex_br_sample, sampleID[i]) && (status[i][2].equals("ex.less1month"))) {
                    b1_br_tvNum = b1_br_tvNum + mutations[i].length;
                }
            }
            else if(status[i][0].equals("Nasal")) {
                if(isInTheList(current_ns_sample, sampleID[i]) && (status[i][2].equals("current"))) {
                    current_ns_tvNum = current_ns_tvNum + mutations[i].length;
                }
                else if(isInTheList(ex_ns_sample, sampleID[i]) && (status[i][2].equals("ex.more12months"))) {
                    m12_ns_tvNum = m12_ns_tvNum + mutations[i].length;
                }
                else if(isInTheList(ex_ns_sample, sampleID[i]) && (status[i][2].equals("ex.1_to_12months"))) {
                    b1_12_ns_tvNum = b1_12_ns_tvNum + mutations[i].length;
                }
                else if(isInTheList(ex_ns_sample, sampleID[i]) && (status[i][2].equals("ex.less1month"))) {
                    b1_ns_tvNum = b1_ns_tvNum + mutations[i].length;
                }
                else if(isInTheList(never_ns_sample, sampleID[i]) && (status[i][2].equals("never"))) {
                    never_ns_tvNum = never_ns_tvNum + mutations[i].length;
                }
            }
        }
        
        util.MutationSet[] current_br = new util.MutationSet[current_br_tvNum];
        util.MutationSet[] m12_br = new util.MutationSet[m12_br_tvNum];
        util.MutationSet[] b1_12_br = new util.MutationSet[b1_12_br_tvNum];
        util.MutationSet[] b1_br = new util.MutationSet[b1_br_tvNum];
        util.MutationSet[] never_br = new util.MutationSet[never_br_tvNum];
        
        util.MutationSet[] current_ns = new util.MutationSet[current_ns_tvNum];
        util.MutationSet[] m12_ns = new util.MutationSet[m12_ns_tvNum];
        util.MutationSet[] b1_12_ns = new util.MutationSet[b1_12_ns_tvNum];
        util.MutationSet[] b1_ns = new util.MutationSet[b1_ns_tvNum];
        util.MutationSet[] never_ns = new util.MutationSet[never_ns_tvNum];
        
        current_br_num = 0;
        m12_br_num = 0;
        b1_12_br_num = 0;
        b1_br_num = 0;
        never_br_num = 0;
        
        current_ns_num = 0;
        m12_ns_num = 0;
        b1_12_ns_num = 0;
        b1_ns_num = 0;
        never_ns_num = 0;
        
        current_br_sampleNum = 0;
        m12_br_sampleNum = 0;
        b1_12_br_sampleNum = 0;
        b1_br_sampleNum = 0;
        never_br_sampleNum = 0;
        
        current_ns_sampleNum = 0;
        m12_ns_sampleNum = 0;
        b1_12_ns_sampleNum = 0;
        b1_ns_sampleNum = 0;
        never_ns_sampleNum = 0;
        
        for(int i = 0; i < vcfNum; i++) {
            if(status[i][0].equals("Bronchial")) {
                if(isInTheList(current_br_sample, sampleID[i]) && (status[i][2].equals("current"))) {
                    addUp(mutations[i], current_br, "current_br");
                    current_br_sampleNum++;
                }
                else if(isInTheList(ex_br_sample, sampleID[i]) && (status[i][2].equals("ex.more12months"))) {
                    addUp(mutations[i], m12_br, "m12_br");
                    m12_br_sampleNum++;
                }
                else if(isInTheList(ex_br_sample, sampleID[i]) && (status[i][2].equals("ex.1_to_12months"))) {
                    addUp(mutations[i], b1_12_br, "b1_12_br");
                    b1_12_br_sampleNum++;
                }
                else if(isInTheList(ex_br_sample, sampleID[i]) && (status[i][2].equals("ex.less1month"))) {
                    addUp(mutations[i], b1_br, "b1_br");
                    b1_br_sampleNum++;
                }
            }
            else if(status[i][0].equals("Nasal")) {
                if(isInTheList(current_ns_sample, sampleID[i]) && (status[i][2].equals("current"))) {
                    addUp(mutations[i], current_ns, "current_ns");
                    current_ns_sampleNum++;
                }
                else if(isInTheList(ex_ns_sample, sampleID[i]) && (status[i][2].equals("ex.more12months"))) {
                    addUp(mutations[i], m12_ns, "m12_ns");
                    m12_ns_sampleNum++;
                }
                else if(isInTheList(ex_ns_sample, sampleID[i]) && (status[i][2].equals("ex.1_to_12months"))) {
                    addUp(mutations[i], b1_12_ns, "b1_12_ns");
                    b1_12_ns_sampleNum++;
                }
                else if(isInTheList(ex_ns_sample, sampleID[i]) && (status[i][2].equals("ex.less1month"))) {
                    addUp(mutations[i], b1_ns, "b1_ns");
                    b1_ns_sampleNum++;
                }
                else if(isInTheList(never_ns_sample, sampleID[i]) && (status[i][2].equals("never"))) {
                    addUp(mutations[i], never_ns, "never_ns");
                    never_ns_sampleNum++;
                }
            }
        }
        
        util.MutationSet2[] ms2 = new util.MutationSet2[current_br_num];
        for(int i = 0; i < current_br_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(current_br[i].getMutationName());
            ms2[i].setCnt(current_br[i].getCnt() / (double) current_br_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, current_br_num, true, outputPath + "Total/current_br_aro.csv").start();
        
        ms2 = new util.MutationSet2[m12_br_num];
        for(int i = 0; i < m12_br_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(m12_br[i].getMutationName());
            ms2[i].setCnt(m12_br[i].getCnt() / (double) m12_br_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, m12_br_num, true, outputPath + "Total/m12_br_aro.csv").start();
        
        ms2 = new util.MutationSet2[b1_12_br_num];
        for(int i = 0; i < b1_12_br_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(b1_12_br[i].getMutationName());
            ms2[i].setCnt(b1_12_br[i].getCnt() / (double) b1_12_br_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, b1_12_br_num, true, outputPath + "Total/b1_12_br_aro.csv").start();
        
        ms2 = new util.MutationSet2[b1_br_num];
        for(int i = 0; i < b1_br_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(b1_br[i].getMutationName());
            ms2[i].setCnt(b1_br[i].getCnt() / (double) b1_br_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, b1_br_num, true, outputPath + "Total/b1_br_aro.csv").start();
        
        ms2 = new util.MutationSet2[current_ns_num];
        for(int i = 0; i < current_ns_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(current_ns[i].getMutationName());
            ms2[i].setCnt(current_ns[i].getCnt() / (double) current_ns_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, current_ns_num, true, outputPath + "Total/current_ns_aro.csv").start();
        
        ms2 = new util.MutationSet2[m12_ns_num];
        for(int i = 0; i < m12_ns_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(m12_ns[i].getMutationName());
            ms2[i].setCnt(m12_ns[i].getCnt() / (double) m12_ns_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, m12_ns_num, true, outputPath + "Total/m12_ns_aro.csv").start();
        
        ms2 = new util.MutationSet2[b1_12_ns_num];
        for(int i = 0; i < b1_12_ns_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(b1_12_ns[i].getMutationName());
            ms2[i].setCnt(b1_12_ns[i].getCnt() / (double) b1_12_ns_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, b1_12_ns_num, true, outputPath + "Total/b1_12_ns_aro.csv").start();
        
        ms2 = new util.MutationSet2[b1_ns_num];
        for(int i = 0; i < b1_ns_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(b1_ns[i].getMutationName());
            ms2[i].setCnt(b1_ns[i].getCnt() / (double) b1_ns_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, b1_ns_num, true, outputPath + "Total/b1_ns_aro.csv").start();
        
        ms2 = new util.MutationSet2[never_ns_num];
        for(int i = 0; i < never_ns_num; i++) {
            ms2[i] = new util.MutationSet2();
            ms2[i].setMutationName(never_ns[i].getMutationName());
            ms2[i].setCnt(never_ns[i].getCnt() / (double) never_ns_sampleNum);
        }
        Arrays.sort(ms2);
        new file.SaveCSV(ms2, never_ns_num, true, outputPath + "Total/never_ns_aro.csv").start();
    }
    
    public void start() {
        extract();
        analyse_cancerType();
        analyse_smoking();
    }
    
}
