package in.elanic.elanicchatdemo.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay Rambhia on 2/22/16.
 */
public class DualList<T,V> {

    private List<T> t;
    private List<V> v;

    public DualList() {
        t = new ArrayList<T>();
        v = new ArrayList<V>();
    }

    public DualList(List<T> t, List<V> v) {
        this.t = t;
        this.v = v;
    }

    public List<T> getT() {
        return t;
    }

    public List<V> getV() {
        return v;
    }

    public void add(T tItem, V vItem) {
        t.add(tItem);
        v.add(vItem);
    }

    public boolean isEmpty() {
        return t.isEmpty() || v.isEmpty();
    }

    public V getItem(T key) {
        int index = t.indexOf(key);
        if (index == -1) {
            return null;
        }

        return v.get(index);
    }

}
