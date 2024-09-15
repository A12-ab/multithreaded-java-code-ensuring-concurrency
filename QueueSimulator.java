import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueSimulator {
    private long currentTime = 0;
    private BankQueue bankQueue;
    private GroceryQueues groceryQueues;
    private int leftCustomerCount = 0;

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
            if (currentTime >= nextCustomerTime) {
                Customer customer = new Customer(currentTime);// notun customer create hobe 
                boolean addedToBank = bankQueue.addCustomer(customer);// bankqueue te add hoilo
                boolean addedToGrocery = groceryQueues.addCustomer(customer);// grocery queue te add hoilo
                
                if (!addedToBank) {
                    leftCustomerCount++;
                    System.out.println("Customer left bank queue at " + currentTime);
                }
                if (!addedToGrocery) {
                    System.out.println("Customer left grocery queue at " + currentTime);
                }

                nextCustomerTime = currentTime + generateNextCustomerTime();
            }
            
            Thread.sleep(100); // 100 mili sec dhorsi
            currentTime++;
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
        return leftCustomerCount;
    }


}
