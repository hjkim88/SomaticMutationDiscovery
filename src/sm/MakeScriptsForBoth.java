/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author kim01
 */
public class MakeScriptsForBoth {
    
    private String[] sampleNames;
    private String[] bamIDs;
    private String[] exLines;
    private int bothSampleNum, sampleBamNum;
    
    private final String bamFilePath = "C:/Users/kim01/Documents/Research/Mutect/both_bams/";
    private final String scriptPath = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/both_scripts/";
    private final String exPath = scriptPath + "example_0504.sh";
    
    private final int idLine = 14;
    private final int exLine = 113;
    
    public MakeScriptsForBoth() {
        initVariables();
    }
    
    private void initVariables() {
        sampleNames = new sm.GetBothBrocNNasal().getBothSampleName();
        bothSampleNum = sampleNames.length;
        //testPrint();
        exLines = new String[exLine];
        loadExample();
        getIDs();
    }
    
    private void testPrint() {
        for(int i = 0; i < bothSampleNum; i++) {
            System.out.println(sampleNames[i]);
        }
        for(int i = 0; i < bothSampleNum; i++) {
            System.out.println("sbatch /mnt/scratchb/fmlab/hyunjin/scripts/" + sampleNames[i] + ".sh");
        }
    }
    
    private void loadExample() {
        try {
            File f = new File(exPath);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            for(int i = 0; i < exLine; i++) {
                exLines[i] = br.readLine();
            }
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".loadExample()");
        }
    }
    
    private void getIDs() {
        File f = new File(bamFilePath);
        File[] files = f.listFiles();
        
        sampleBamNum = 0;
        for (File file : files) {
            if (file.getName().endsWith(".bam")) {
                //System.out.println(file.getName());
                //System.out.println(file.getName().substring(0, file.getName().length()-4));
                //System.out.println("sbatch /mnt/scratchb/fmlab/hyunjin/scripts/" + file.getName().substring(0, file.getName().length()-4) + ".sh");
                sampleBamNum++;
            }
        }
        System.out.println(sampleBamNum);
        
        bamIDs = new String[sampleBamNum];
        
        int cnt = 0;
        for (File file : files) {
            if (file.getName().endsWith(".bam")) {
                bamIDs[cnt] = file.getName().substring(0, file.getName().length() - 4);
                cnt++;
            }
        }
    }
    
    private String toThreeDigits(String str) {
        String[] temp = str.split("_");
        String result = temp[0];
        
        switch (temp[1].length()) {
            case 1:
                result = result + "_" + "00" + temp[1];
                break;
            case 2:
                result = result + "_" + "0" + temp[1];
                break;
            default:
                result = result + "_" + temp[1];
                break;
        }
        
        return result;
    }
    
    private void writeScripts(String id) {
        try {
            FileWriter fw = new FileWriter(scriptPath + id + ".sh");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);
            
            String id1 = "", id2 = "";
            for(int i = 0; i < sampleBamNum; i++) {
                if(bamIDs[i].startsWith(id + "_") || bamIDs[i].startsWith(toThreeDigits(id) + "_")) {
                    if(bamIDs[i].contains("BR")) {
                        id1 = bamIDs[i];
                    }
                    else {
                        id2 = bamIDs[i];
                    }
                }
            }
            
            for(int i = 0; i < idLine; i++) {
                outFile.print(exLines[i] + '\n');
            }
            outFile.print("id1=" + id1 + '\n');
            outFile.print("id2=" + id2 + '\n');
            for(int i = (idLine+2); i < exLine; i++) {
                outFile.print(exLines[i] + '\n');
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
        for(int i = 0; i < bothSampleNum; i++) {
            writeScripts(sampleNames[i]);
        }
    }
    
}
