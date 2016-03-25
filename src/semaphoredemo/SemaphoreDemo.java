/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package semaphoredemo;

 interface Semaphore {
// Запрашивает разрешение. Если есть свободное захватывает его.
//Если нет - приостанавливает поток до тех пор пока не появится свободное разрешение.
public void acquire()throws InterruptedException;
// Запрашивает переданое количество разрешений.
//Если есть переданое количество свободных разрешений захватывает их. 
// Если нет - приостанавливает поток до тех пор пока не появится переданое колтчество свободных разрешений.
public void acquire(int permits) throws InterruptedException;
// Отпускает разрешение возвращая его семафору.
public void release();
// Отпускает переданое количество разрешений возварщая их семафору.
public void release(int permits);
// Возвращает количество свободных разрешений на данный момент
public int getAvailablePermits();

}
class SemaphoreImplementation implements Semaphore {
    
    private final Object lock = new Object();  
    private boolean isWait;
    private boolean nextIteration;
    private static volatile int numberOfThreads = 0;
    private int permits;
    
    public SemaphoreImplementation()
    {
        this.isWait = false;
        permits = 1;
        this.nextIteration = false;
    }
        public void somemethod() {
        synchronized(lock) {            
        }   
        
    }
   public void acquire() throws InterruptedException{
          synchronized(lock)
        {
        if(!isWait)
        {
            lock.notify();
            this.isWait = true;
        }
        else 
            lock.wait();
        }      
   }    
   
      public void acquire(int permits) throws InterruptedException{
          
          numberOfThreads++;
          this.permits = permits;
          synchronized(lock)
        {
        if(permits==numberOfThreads)
        {  
           if(this.nextIteration)
            lock.wait();   
            System.out.println("numberOfThreads =" + numberOfThreads);
            numberOfThreads=0;
            System.out.println("numberOfThreads =" + numberOfThreads);
            lock.notify();
            if(this.nextIteration)
            lock.wait();
           this.nextIteration = true;       
        }
        else           
            lock.wait();
        }      
   }  
   
   
    public void release() {
         synchronized(lock)
        {
         lock.notify();
         this.isWait = false;
        }
     }
    
    public void release(int permits)
    {
       try {
           if(permits != this.permits)
               throw new InterruptedException("Wrong number of threads!!!");
      synchronized(lock)
        {
         lock.notifyAll();
     
        }             
  } catch(InterruptedException exception) {
          System.out.println(exception.toString());
       }
    }
    
    public int getAvailablePermits() {
        return this.permits;
    }
    
}

class NewFirstThread implements Runnable {
  Thread t;
  Semaphore lock;

  NewFirstThread(Semaphore lock ) {
    // Create a new, second thread
    t = new Thread(this, "Demo First Thread");
    System.out.println("Child First thread: " + t);
    t.start(); // Start the thread
    this.lock = lock;
  }
  public void run() {
    try {
        
    lock.acquire();
     //lock.acquire(2);
         for(int i = 5; i > 0; i--) {
        System.out.println("Child First Thread: " + i);
        Thread.sleep(500);
        }
    } catch (InterruptedException e) {
      System.out.println("Child interrupted.");
    }
    System.out.println("Exiting First child thread.");
    lock.release();
     //lock.release(2);
    }
}

class NewSecondThread implements Runnable {
  Thread t;
  Semaphore lock;
  NewSecondThread(Semaphore lock) {
    this.lock = lock;
    t = new Thread(this, "Demo Second Thread");
    System.out.println("Child Second thread: " + t);
    t.start(); // Start the thread
  }
  public void run() {
    try {
   lock.acquire();
   // lock.acquire(2);
      for(int i = 0; i < 10; ++i) {
        System.out.println("Child Second Thread: " + i);
        Thread.sleep(500);
         }
    } catch (InterruptedException e) {
      System.out.println("Child interrupted.");
    }
    System.out.println("Exiting child Second thread.");
    lock.release();
    // lock.release(2);
   }
}


class NewThirdThread implements Runnable {
  Thread t;
  Semaphore lock;
  NewThirdThread(Semaphore lock) {
    this.lock = lock;
    t = new Thread(this, "Demo Third Thread");
    System.out.println("Child Third thread: " + t);
    t.start(); // Start the thread
  }
  public void run() {
    try {

       lock.acquire();
    //    lock.acquire(2);
      for(int i = 0; i < 100; i+=10) {
        System.out.println("Child Third Thread: " + i);
        Thread.sleep(800);
        }
    } catch (InterruptedException e) {
      System.out.println("Child interrupted.");
    }
    System.out.println("Exiting child Third thread.");
  lock.release();
  //  lock.release(2); 
    }
}


public class SemaphoreDemo {

    public static void main(String[] args) throws InterruptedException {
  Semaphore lock = new SemaphoreImplementation();  
new NewThirdThread(lock); // create a new thread
new NewFirstThread(lock); // create a new thread
new NewSecondThread(lock);// create a new thread
new NewThirdThread(lock);// create a new thread
   
 
    }
}
