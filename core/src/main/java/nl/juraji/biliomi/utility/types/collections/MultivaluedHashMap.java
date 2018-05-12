package nl.juraji.biliomi.utility.types.collections;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Juraji on 26-9-2017.
 * Biliomi
 */
public class MultivaluedHashMap<K, V> extends HashMap<K, List<V>> {

    public void putSingle(K key, V value) {
        this.putIfAbsent(key, new ArrayList<>());
        this.get(key).add(value);
    }
}
