import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueSimulator {
    private long currentTime = 0;
    private BankQueue bankQueue;
    private GroceryQueues groceryQueues;
    private int leftCustomerCount = 0;
    private final Lock lock = new ReentrantLock(); 

    public QueueSimulator(int numTellers, int bankMaxQueueLength, int numCashiers, int groceryMaxQueueLength) {
        this.bankQueue = new BankQueue(numTellers, bankMaxQueueLength);
        this.groceryQueues = new GroceryQueues(numCashiers, groceryMaxQueueLength);
    }

    public void simulate(int minutes) throws InterruptedException {
        long endTime = minutes * 60L;
        long nextCustomerTime = generateNextCustomerTime();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                bankQueue.serveCustomers();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executorService.submit(() -> {
            try {
                groceryQueues.serveCustomers();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        while (currentTime < endTime) {
            lock.lock(); // Added lock
            try {
                if (currentTime >= nextCustomerTime) {
                    Customer customer = new Customer(currentTime);

                    boolean addedToBank = bankQueue.addCustomer(customer);
                    boolean addedToGrocery = groceryQueues.addCustomer(customer);

                    if (!addedToBank) {
                        leftCustomerCount++;
                        System.out.println("Customer left bank queue at " + currentTime);
                    }
                    if (!addedToGrocery) {
                        System.out.println("Customer left grocery queue at " + currentTime);
                    }

                    nextCustomerTime = currentTime + generateNextCustomerTime();
                }

                currentTime++;
            } finally {
                lock.unlock(); 
            }

            Thread.sleep(100); // 100 mili sec dhorsi
        }

        executorService.shutdownNow();
    }

    private long generateNextCustomerTime() {
        return new Random().nextInt(41) + 20; // 20 to 60 mili
    }

    public int getBankCustomerCount() {
        return bankQueue.getServedCustomerCount();
    }

    public int getLeftCustomerCount() {
        lock.lock(); 
        try {
            return leftCustomerCount;
        } finally {
            lock.unlock(); 
        }
    }
}