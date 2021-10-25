public class Main {
    public static void main(String[] args) {
        DefaultThreadPool defaultThreadPool = new DefaultThreadPool(10);
        for (int i=0;i<10000;i++){
            if (i==30){
                defaultThreadPool.addWorker(10);
            }
            Job job = new Job();
            defaultThreadPool.execute(job);
        }
    }
}
