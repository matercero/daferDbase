/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.dafer.tercero.ma.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author mangel.tercero
 */
public class MapMap<K1,K2,V> implements Map<K1,Map<K2,V>> {

    private Map<K1, Map<K2,V>> underlyingMap = new HashMap<K1, Map<K2, V>>();

    public MapMap() {
    }

    public MapMap(Map<K1, Map<K2,V>> underlyingMap) {
        putAll(underlyingMap);
    }

    @Override
    public int size() {
        return underlyingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return underlyingMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return underlyingMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return underlyingMap.containsValue(o);
    }

    @Override
    public Map<K2, V> get(Object o) {
        return underlyingMap.get(o);
    }

    @Override
    public Map<K2, V> put(K1 k1, Map<K2, V> k2VMap) {
        return (k2VMap == null || k2VMap.isEmpty()) ? null : underlyingMap.put(k1, k2VMap);
    }

    @Override
    public Map<K2, V> remove(Object o) {
        return underlyingMap.remove(o);
    }

    /**
     * Puts the contents in a map of maps.
     * Note that existing content stored under an outer key will be overwritten by the provided content.
     * Note that no empty or null inner maps are stored.
     * @param map the content to be added
     */
    @Override
    public void putAll(Map<? extends K1, ? extends Map<K2, V>> map) {
        for(Entry<? extends K1,  ? extends Map<K2, V>> entry : map.entrySet()) {
           if (entry.getValue() != null && ! entry.getValue().isEmpty()) {
               underlyingMap.put(entry.getKey(), entry.getValue());
           }
        }
    }

    @Override
    public void clear() {
        underlyingMap.clear();
    }

    @Override
    public Set<K1> keySet() {
        return underlyingMap.keySet();
    }

    @Override
    public Collection<Map<K2, V>> values() {
        return underlyingMap.values();
    }

    @Override
    public Set<Entry<K1, Map<K2, V>>> entrySet() {
        return underlyingMap.entrySet();
    }

    /**
     * Retrieves a value from a map of maps
     * @param key1 the key for the outer map
     * @param key2 the key for the inner map
     * @return the inner value, or null if none exists
     */
    public V get(K1 key1, K2 key2) {
        Map<K2,V> map = underlyingMap.get(key1);
        return map == null ? null : map.get(key2);
    }

    /**
     * Puts a value in a map of maps.
     * Note that inner maps are created if necessary
     * @param key1 the key for the outer map
     * @param key2 the key for the inner map
     * @param value the value for the inner map
     * @return the former value, or null if none exists
     */
    public V put(K1 key1, K2 key2, V value) {
        Map<K2,V> map = underlyingMap.get(key1);
        if (map == null) {
            map = new HashMap<K2, V>();
            underlyingMap.put(key1, map);
        }
        return map.put(key2, value);
    }

    /**
     * Removes a value from a map of maps.
     * Note that if an inner map gets empty by this operation, it will be removed.
     * @param key1 the key for the outer map
     * @param key2 the key for the inner map
     * @return the former value, or null if none exists
     */
    public V remove(K1 key1, K2 key2) {
        Map<K2,V> map = underlyingMap.get(key1);
        if (map == null) {
            return null;
        } else {
            V result = map.remove(key2);
            if (map.isEmpty()) {
                remove(key1);
            }
            return result;
        }
    }

    /**
     * Returns a list of all values of inner maps to a given inner key
     * @param key2 the inner key
     * @return the list of values
     */
    public List<V> deepGet(K2 key2) {
        List<V> result = new ArrayList<V>();
        for(Map<K2, V> map : underlyingMap.values()) {
            V value = map.get(key2);
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * Puts a value in all existing inner maps
     * @param key2 the inner key
     * @param value the inner value
     * @return the list of former values
     */
    public List<V> deepPut(K2 key2, V value) {
        List<V> result = new ArrayList<V>();
        for(Map<K2, V> map : underlyingMap.values()) {
            V v = map.put(key2, value);
            if (v != null) {
                result.add(v);
            }
        }
        return result;
    }

    /**
     * Puts the contents in a map of maps.
     * Note that existing content won't be overwritten.
     * Note that no empty or null inner maps are stored.
     * @param map the content to be added
     */
    public void deepPutAll(Map<? extends K1, ? extends Map<K2, V>> map) {
        for(Entry<? extends K1,  ? extends Map<K2, V>> entry : map.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                Map<K2, V> innerMap = underlyingMap.get(entry.getKey());
                if (innerMap == null) {
                    underlyingMap.put(entry.getKey(), entry.getValue());
                } else {
                    innerMap.putAll(entry.getValue());
                }
            }
        }
    }


    /**
     * Removes content for a given key from inner maps.
     * Note that if an inner map gets empty by this operation, it will be removed.
     * @param key2 the inner key
     * @return the List of removed values
     */
    public List<V> deepRemove(K2 key2) {
        List<V> result = new ArrayList<V>();
        for(Entry<K1, Map<K2, V>> entry : underlyingMap.entrySet()) {
            Map<K2,V> map = entry.getValue();
            V value = map.remove(key2);
            if (value != null) {
                result.add(value);
            }
            if (map.isEmpty()) {
                remove(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Calculates the total size of all inner maps
     * @return total size
     */
    public int deepSize() {
        int result = 0;
        for(Map<K2,V> map : values()) {
            result += map.size();
        }
        return result;
    }

    /**
     * Returns a set of all inner keys
     * @return set of inner keys
     */
    public Set<K2> deepKeys() {
        Set<K2> result = new HashSet<K2>();
        for(Map<K2,V> map : values()) {
            result.addAll(map.keySet());
        }
        return result;
    }

    /**
     * Returns a list of all inner values
     * @return list of inner values
     */
    public List<V> deepValues() {
        List<V> result = new ArrayList<V>();
        for(Map<K2,V> map : values()) {
            result.addAll(map.values());
        }
        return result;
    }

    /**
     * Returns a list of all inner entries
     * @return list of inner entries
     */
    public List<Entry<K2,V>> deepEntries() {
        List<Entry<K2,V>> result = new ArrayList<Entry<K2,V>>();
        for(Map<K2,V> map : values()) {
            result.addAll(map.entrySet());
        }
        return result;
    }
}
