package container.HighSpeed;


public interface MyMap<K, V> {

    int size();

    V put(K k, V v);

    V get(K k);

    V remove(K k);


    interface Entry<K, V> {

        K getKey();

        V getValue();

    }
}
