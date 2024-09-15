import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class BankQueue {
    private BlockingQueue<Customer> queue;
    private int numTellers;
    private Semaphore tellers;
    private int servedCustomerCount=0;
    

    /// constractor
    public BankQueue(int numTellers, int maxQueueLength) {
        this.queue = new LinkedBlockingQueue<>(maxQueueLength);
        this.numTellers = numTellers;
        this.tellers = new Semaphore(numTellers);
    }

    public boolean addCustomer(Customer customer) {
        return queue.offer(customer);
    }

    public void serveCustomers() throws InterruptedException {
        while (!queue.isEmpty()) {
            tellers.acquire();
            Customer customer = queue.poll();
            if (customer != null) {
                customer.setServed(true);
                servedCustomerCount++;
                Thread.sleep(customer.getServiceTime());
            }
            tellers.release();
        }
    }

    public int getServedCustomerCount() {
        return servedCustomerCount;
    }
}