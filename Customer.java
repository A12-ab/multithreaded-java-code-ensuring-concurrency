import java.util.Random;

public class Customer {
    private long arrivalTime;
    private int serviceTime;
    private boolean served;

    public Customer(long arrivalTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = new Random().nextInt(301) + 300; // 300 to 600 mili sec
        this.served = false;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }
}