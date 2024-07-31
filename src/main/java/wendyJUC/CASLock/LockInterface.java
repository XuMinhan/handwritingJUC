package wendyJUC.CASLock;

import wendyJUC.container.LowSpeed.LeonHashMap;

import java.util.concurrent.locks.Lock;

public interface LockInterface {

     void lock();


    void unlock();
}
