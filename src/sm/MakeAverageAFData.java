/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.text.DecimalFormat;

/**
 *
 * @author Hyunjin Kim
 */
public class MakeAverageAFData {
    
    private final String filePath = "F:/Dropbox/Research/CRUK_CI/Mutect/both_results/AF/NS/";
    //private final String filePath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/AF/";
    private final String outPath = filePath + "Average_AF.csv";
    private final String outPath2 = filePath + "Average_AF_interval.csv";
    private String[] fileNames;
    private int fileNum;
    
    private final int totalFractionNum = 3000;
    private double[] fraction;
    private int[] fractionCnt;
    private int realFractionNum;
    
    private util.FractionSet[] fs;
    private double[] fractionCnt_interval;
    
    public MakeAverageAFData() {
        initVariables();
    }
    
    private void initVariables() {
        fraction = new double[totalFractionNum];
        fractionCnt = new int[totalFractionNum];
        
        for(int i = 0; i < totalFractionNum; i++) {
            fraction[i] = -1;
            fractionCnt[i] = 0;
        }
        realFractionNum = 0;
        fractionCnt_interval = new double[20];
        
        for(int i = 0; i < fractionCnt_interval.length; i++) {
            fractionCnt_interval[i] = 0;
        }
        
        loadFileNames();
    }
    
    private void loadFileNames() {
        File f = new File(filePath);
        File[] files = f.listFiles();
        
        fileNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".af")) {
                fileNum++;
            }
        }
        System.out.println(fileNum);
        
        fileNames = new String[fileNum];
        
        fileNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".af")) {
                fileNames[fileNum] = file.getAbsolutePath();
                fileNum++;
            }
        }
    }
    
    private boolean isFractionExist(double f) {
        boolean r = false;
        
        for(int i = 0; i < realFractionNum; i++) {
            if(fraction[i] == f) {
                r = true;
                break;
            }
        }
        
        return r;
    }
    
    private void cntUp(double f) {
        for(int i = 0; i < realFractionNum; i++) {
            if(fraction[i] == f) {
                fractionCnt[i]++;
                break;
            }
        }
    }
    
    private void addUp(double f) {
        fraction[realFractionNum] = f;
        fractionCnt[realFractionNum]++;
        realFractionNum++;
    }
    
    private void calculateRatio() {
        for(int i = 0; i < fileNum; i++) {
            try {
                File f = new File(fileNames[i]);
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                
                double buf;
                String line;
                while((line = br.readLine()) != null) {
                    buf = Double.parseDouble(line);
                    if(isFractionExist(buf)) {
                        cntUp(buf);
                    }
                    else {
                        addUp(buf);
                    }
                }
                
                br.close();
                fr.close();
            }
            catch(IOException ioe) {
                System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".calculateRatio()");
            }
        }
        System.out.println("realFractionNum = " + realFractionNum);
        
        fs = new util.FractionSet[realFractionNum];
        
        for(int i = 0; i < realFractionNum; i++) {
            fs[i] = new util.FractionSet();
            fs[i].setFractionName(fraction[i]);
            fs[i].setCnt((double) fractionCnt[i] / fileNum);
        }
        Arrays.sort(fs);
    }
    
    private void write() {
        try {
            FileWriter fw = new FileWriter(outPath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);
            
            outFile.println("fraction,fractionCnt");
            for(int i = 0; i < realFractionNum; i++) {
                outFile.println(fs[i].getFractionName() + "," + fs[i].getCnt());
            }
            
            outFile.close();
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void makeInterval() {
        double interval = 0.05;
        DecimalFormat newFormat = new DecimalFormat("#.##");

        for(int i = 0; i < realFractionNum; i++) {
            for(int j = 0; j < fractionCnt_interval.length; j++) {
                if(fraction[i] >= (j*interval) && fraction[i] < ((j+1)*interval)) {
                    fractionCnt_interval[j] = fractionCnt_interval[j] + fractionCnt[i];
                    break;
                }        
            }
        }
        
        for(int i = 0; i < fractionCnt_interval.length; i++) {
            fractionCnt_interval[i] = fractionCnt_interval[i] / fileNum;
        }
        
        try {
            FileWriter fw = new FileWriter(outPath2);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);
            
            outFile.println("fraction,fractionCnt");
            for(int i = 0; i < fractionCnt_interval.length; i++) {
                outFile.println(Double.valueOf(newFormat.format(i*interval)) + "-" + Double.valueOf(newFormat.format((i+1)*interval)) + "," + fractionCnt_interval[i]);
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
        calculateRatio();
        write();
        makeInterval();
    }
    
}
