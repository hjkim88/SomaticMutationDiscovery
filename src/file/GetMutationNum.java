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
 * @author kim01
 */
public class GetMutationNum {
    
    private int resultNum;
    
    public GetMutationNum(String path) {
        initVariables(path);
    }
    
    private void initVariables(String path) {
        resultNum = 0;
        extract(path);
    }
    
    private void extract(String path) {
        try {
            File f = new File(path);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            
            while(br.readLine() != null) {
                resultNum++;
            }
            
            br.close();
            fr.close();
        }
        catch(IOException ioe) {
            System.out.println(ioe.getMessage() + "\tPath: " + this.getClass().getCanonicalName() + ".extract()");
        }
    }
    
    public int getNum() {
        return resultNum;
    }
}
