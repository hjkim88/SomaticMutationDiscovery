/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Hyunjin Kim
 */
public class MutationSet2 implements Comparable<MutationSet2> {
    
    private String mutationName;
    private double count;
    
    public MutationSet2() {}
    
    @Override
    public int compareTo(MutationSet2 m) {
        if(this.count < m.count) {
            return 1;
        }
        else if(this.count > m.count) {
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
    
    public String getMutationName() {
        return mutationName;
    }
    
    public double getCnt() {
        return count;
    }
}
