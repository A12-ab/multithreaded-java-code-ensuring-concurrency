public class Main {
    public static void main(String[] args) {
        int simulationMinutes = 2; // 2 minute dhorsi
        int numTellers = 3;
        int numCashiers = 3;
        int bankMaxQueueLength = 5;
        int groceryMaxQueueLength = 2;

        QueueSimulator simulator = new QueueSimulator(numTellers, bankMaxQueueLength, numCashiers, groceryMaxQueueLength);

        try {
            simulator.simulate(simulationMinutes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Bank customers served: " + simulator.getBankCustomerCount());
        System.out.println("Bank customers left without server: " + simulator.getLeftCustomerCount() );

    }
}