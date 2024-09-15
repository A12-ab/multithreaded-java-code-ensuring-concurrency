import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class GroceryQueue {
    private BlockingQueue<Customer> queue;

    public GroceryQueue(int maxQueueLength) {
        this.queue = new LinkedBlockingQueue<>(maxQueueLength);
    }

    public boolean addCustomer(Customer customer) {
        return queue.offer(customer);
    }

    public int size() {
        return queue.size();
    }

    public Customer poll() {
        return queue.poll();
    }
}

public class GroceryQueues {
    private List<GroceryQueue> queues;
    private int numCashiers;

    public GroceryQueues(int numCashiers, int maxQueueLength) {
        this.numCashiers = numCashiers;
        this.queues = new ArrayList<>();
        for (int i = 0; i < numCashiers; i++) {
            queues.add(new GroceryQueue(maxQueueLength));
        }
    }

    public boolean addCustomer(Customer customer) throws InterruptedException {
        GroceryQueue shortestQueue = findShortestQueue();
        if (shortestQueue == null) {
            Thread.sleep(100); // Wait for 100 mili seconds
            shortestQueue = findShortestQueue();
            if (shortestQueue == null) {
                return false;
            }
        }
        return shortestQueue.addCustomer(customer);
    }

    private GroceryQueue findShortestQueue() {
        GroceryQueue shortestQueue = null;
        int minSize = Integer.MAX_VALUE;
        List<GroceryQueue> shortestQueues = new ArrayList<>();

        for (GroceryQueue queue : queues) {
            int size = queue.size();
            if (size < minSize) {
                minSize = size;
                shortestQueues.clear();
                shortestQueues.add(queue);
            } else if (size == minSize) {
                shortestQueues.add(queue);
            }
        }

        if (!shortestQueues.isEmpty()) {
            shortestQueue = shortestQueues.get(new Random().nextInt(shortestQueues.size()));
        }

        return shortestQueue;
    }

    public void serveCustomers() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(numCashiers);
        for (GroceryQueue queue : queues) {
            executorService.submit(() -> {
                try {
                    while (true) {
                        Customer customer = queue.poll();
                        if (customer != null) {
                            customer.setServed(true);
                            Thread.sleep(customer.getServiceTime());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        executorService.shutdown();
    }
}