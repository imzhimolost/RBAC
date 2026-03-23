import java.util.ArrayList;
import java.util.List;

class MyTask implements Runnable {
    private final int orderNumber;
    private final int totalSteps;
    private final int baseDelay;

    private long threadId;
    private int currentStep = 0;
    private long duration = 0;
    private volatile boolean finished = false;

    public MyTask(int orderNumber, int totalSteps, int baseDelay) {
        this.orderNumber = orderNumber;
        this.totalSteps = totalSteps;
        this.baseDelay = baseDelay;
    }

    @Override
    public void run(){
        this.threadId = Thread.currentThread().threadId();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= totalSteps; i++) {
            try {
                Thread.sleep(baseDelay + (int) (Math.random() * 100));
                this.currentStep = i;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        this.duration = System.currentTimeMillis() - startTime;
        this.finished = true;
    }

    public String getStatusLine(){
        StringBuilder bar = new StringBuilder("[");
        for(int i = 0; i < totalSteps; i++){
            if(i < currentStep) bar.append("#");
            else bar.append("-");
        }
        bar.append("]");

        int percent = (currentStep * 100) / totalSteps;
        String timeInfo = finished ? String.format(" | Time: %d ms", duration) : " ";
        return String.format("Thread №%d | ID: %-3d | %s %3d%%%s\u001B[K",
                orderNumber, threadId, bar, percent, timeInfo);
    }

    public boolean isFinished(){
        return finished;
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int NUMBER_OF_THREADS = 5;
        int LENGTH = 20;
        int STEP_DELAY = 200;

        List<MyTask> tasks = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            MyTask task = new MyTask(i + 1, LENGTH, STEP_DELAY);
            tasks.add(task);
            Thread thread = new Thread(task);
            threads.add(thread);

            System.out.println();
            thread.start();
        }

        boolean allFinished = false;
        while(!allFinished){
            allFinished = true;

            System.out.print("\u001B[" + NUMBER_OF_THREADS + "F");

            for (MyTask task : tasks) {
                System.out.println(task.getStatusLine());
                if (!task.isFinished()) {
                    allFinished = false;
                }
            }

            Thread.sleep(50);
        }
    }
}
