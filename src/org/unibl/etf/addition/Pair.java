package org.unibl.etf.addition;

public class Pair<T1, T2>{
    public T1 first;
    public T2 second;

    public Pair(T1 first, T2 second){
        this.first = first;
        this.second = second;
    }

    public boolean contains(T1 other)
    {
        return this.first.equals(other) || this.second.equals(other);
    }
    @Override
    public String toString() {
        return "First: " + first + "\nSecond: " + second;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Pair) {
            Pair temp = (Pair) obj;
            if (this.first.equals(temp.first) && this.second.equals(temp.second)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int hashCode(){
        int hash = 3;
        hash = 7 * hash + first.hashCode();
        hash = 7 * hash + second.hashCode();
        return hash;
    }
}