public interface ThreadPool<Job extends Runnable> {
  // execute a job, the job must implement Runnable
  public void execute(Job job);

  // shut down the ThreadPool
  public void shutdown();

  // add the number of workers by num
  public void addWorker(int num);

  // remove workers by num
  public void reduceWorker(int num);

  // get the job waiting to be done
  public int getJobNum();
}