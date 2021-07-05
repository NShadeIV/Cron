/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aztecmc.plugins.cron.util;

import java.util.Map;

/**
 * Defines a container holding a pair of two objects
 * 
 * Note: this is only semantic sugar for Map.Entry with some added methods
 * 
 * @author crash
 * @param <K> The type of the first object to hold (the key)
 * @param <V> The type of the second object to hold (the value)
 */
public class Pair<K,V> implements Map.Entry<K,V>{
    public K a;
    public V b;
    
    /**
     * Construct a pair from two objects
     * @param first the first object in the pair (the key)
     * @param second the second object in the pair (the value)
     */
    public Pair(K first, V second){
        this.a=first;
        this.b=second;
    }
    
    /**
     * Construct a pair from a Map.Entry holding two objects
     * 
     * The key of the entry will become the first object in the pair, the value will be the second.
     * 
     * @param entry a map entry that holds two objects
     */
    public Pair(Map.Entry<K,V> entry){
        a=entry.getKey();
        b=entry.getValue();
    }

    /**
     * Get an object by index from the pair container
     * @param i an index in the inclusive range [0,1]
     * @return if the input was 0: the key (first) object,  if the input was 1: the value (second) object
     * @throws IndexOutOfBoundsException if the index was neither 0 nor 1
     */
    public Object get(int i){
        switch (i) {
            case 0:
                return a;
            case 1:
                return b;
            default:
                throw new IndexOutOfBoundsException("Pair indexes may only be 0 or 1, not "+i);// or return null ??
        }
    }

    /**
     * Set an object by index from the pair container
     * @param i an index in the inclusive range [0,1]
     * @param v the object to set at the index
     * @throws IndexOutOfBoundsException if the index was neither 0 nor 1
     */
    @SuppressWarnings("unchecked")
    public void set(int i,Object v){
        switch (i) {
            case 0:
                a = (K) v;
                break;
            case 1:
                b = (V) v;
                break;
            default:
                throw new IndexOutOfBoundsException("Pair indexes may only be 0 or 1, not "+i);
        }
    }
    
    /**
     * @return the key
     */
    @Override
    public K getKey() {
        return a;
    }

    /**
     * @param first the key to set
     */
    public void setKey(K first) {
        this.a = first;
    }

    /**
     * @return the value
     */
    @Override
    public V getValue() {
        return b;
    }

    /**
     * @param second the value to set
     * @return the previous value held
     */
    @Override
    public V setValue(V second) {
        V old = this.b;
        this.b = second;
        return old;
    }
}
