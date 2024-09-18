import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class BankQueue {
    private BlockingQueue<Customer> queue;
    private int numTellers;
    private Semaphore tellers;
    private int servedCustomerCount = 0;
    private ReentrantLock lock = new ReentrantLock();

    /// constructor
    public BankQueue(int numTellers, int maxQueueLength) {
        this.queue = new LinkedBlockingQueue<>(maxQueueLength);
        this.numTellers = numTellers;
        this.tellers = new Semaphore(numTellers);
    }

    public boolean addCustomer(Customer customer) {
        lock.lock();
        try {
            return queue.offer(customer);
        } finally {
            lock.unlock();
        }
    }

    public void serveCustomers() throws InterruptedException {
        while (!queue.isEmpty()) {
            tellers.acquire();
            Customer customer = null;
            lock.lock();
            try {
                customer = queue.poll();
                if (customer != null) {
                    customer.setServed(true);
                    servedCustomerCount++;
                }
            } finally {
                lock.unlock();
            }
            if (customer != null) {
                Thread.sleep(customer.getServiceTime());
            }
            tellers.release();
        }
    }

    public int getServedCustomerCount() {
        lock.lock();
        try {
            return servedCustomerCount;
        } finally {
            lock.unlock();
        }
    }
}