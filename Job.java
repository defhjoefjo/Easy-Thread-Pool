public class Job implements Runnable{
    @Override
    public void run() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Current thread name:"+Thread.currentThread().getName()+";"+"job is executed");
    }
}
