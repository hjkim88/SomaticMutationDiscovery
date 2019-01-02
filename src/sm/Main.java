/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm;

import java.io.File;

/**
 *
 * @author kim01
 */
public class Main {

    private Main() {
        initVariables();
    }
    
    private void initVariables() {
        
    }
    
    private void mutationsInGene() {
        final String path = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Mutect/both_results/somatic_results/BR/Total/";
        //final String path = "F:/Dropbox/Research/CRUK_CI/Genotyping/Somatic_Mutation_Analysis_HJ/outlier_removed/";
        
        File f = new File(path);
        File[] files = f.listFiles();
        
        for (File file : files) {
            if(file.getAbsolutePath().endsWith(".csv")) {
                new sm.AnalyseSMasGene(file.getAbsolutePath()).start();
            }
        }
    }
    
    private void sanityCheck() {
        //final String path = "C:/Users/kim01/Dropbox/Research/CRUK_CI/Genotyping/Somatic_Mutation_Analysis_HJ/outlier_removed/";
        final String path = "F:/Dropbox/Research/CRUK_CI/Genotyping/Somatic_Mutation_Analysis_HJ/outlier_removed/";
        
        File f = new File(path);
        File[] files = f.listFiles();
        
        for (File file : files) {
            if(file.getAbsolutePath().endsWith(".txt")) {
                new test.SanityCheckSMasGene(file.getAbsolutePath()).start();
            }
        }
    }
    
    private void start() {
        //new sm.GetSomaticMutations().start();
        //mutationsInGene();
        //new sm.TotalMutationNum().start();
        //new test.GetGroupNum().start();
        //new sm.GetOLRemovedSomaticMutations().start();
        //new test.MakeRefinedGeneLociData().start();
        //sanityCheck();
        //new sm.CombineMutationNDNARepair().start();
        //new sm.MakeScriptsForBoth().start();
        //new sm.MakeAFData().start();
        //new sm.MakeAverageAFData().start();
        //new test.CheckMissingResults().start();
        //new sm.GetSomaticMutationsBoth().start();
        //new sm.TotalMutationNumBoth().start();
        //new sm.MakeDMGmatrix().start();
        //new sm.TotalAlterAlleleNum().start();
        //new sm.MakeMFClinInfo().start();
        //new sm.MakeCombinedInfo().start();
        //new sm.SmokingMutations().start();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new sm.Main().start();
    }
    
}
