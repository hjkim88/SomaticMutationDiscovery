/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Hyunjin Kim
 */
public class MutationSet3 implements Comparable<MutationSet3> {
    
    private String mutationName;
    private double count;
    private double norm_count;
    
    public MutationSet3() {}
    
    @Override
    public int compareTo(MutationSet3 m) {
        if(this.norm_count < m.norm_count) {
            return 1;
        }
        else if(this.norm_count > m.norm_count) {
            return -1;
        }
        else if(this.mutationName.compareTo(m.mutationName) > 0) {
            return 1;
        }
        else if(this.mutationName.compareTo(m.mutationName) < 0) {
            return -1;
        }
        else {
            return 0;
        }
    }
    
    public void setMutationName(String m) {
        this.mutationName = m;
    }
    
    public void setCnt(double cnt) {
        this.count = cnt;
    }
    
    public void setNormCnt(double cnt) {
        this.norm_count = cnt;
    }
    
    public String getMutationName() {
        return mutationName;
    }
    
    public double getCnt() {
        return count;
    }
    
    public double getNormCnt() {
        return norm_count;
    }
}
