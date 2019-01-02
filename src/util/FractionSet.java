/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Hyunjin Kim
 */
public class FractionSet implements Comparable<FractionSet> {
    
    private double fractionName;
    private double count;
    
    public FractionSet() {}
    
    @Override
    public int compareTo(FractionSet m) {
        if(this.fractionName > m.fractionName) {
            return 1;
        }
        else if(this.fractionName < m.fractionName) {
            return -1;
        }
        else {
            return 0;
        }
    }
    
    public void setFractionName(double m) {
        this.fractionName = m;
    }
    
    public void setCnt(double cnt) {
        this.count = cnt;
    }
    
    public double getFractionName() {
        return fractionName;
    }
    
    public double getCnt() {
        return count;
    }
}
