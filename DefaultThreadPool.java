import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
  // the maximum number of workers in a pool
  private static final int maxNum = 100;

  // default worker number
  private static final int defaultNum = 10;

  // smallest number of worker
  private static final int minNum = 1;

  // use linkedlist to maintain the jobs to be done
  private final LinkedList<Job> jobs = new LinkedList<Job>();

  // list of worker thread
  private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());

  // number of current worker
  private int workerNum;

  // ID of each thread
  private AtomicLong threadNum = new AtomicLong();

  public DefaultThreadPool (int num) {
    // if the initializing num is greater than the max num, set it to default
    if (num > maxNum) {
      this.workerNum = defaultNum;
    } else {
      this.workerNum = num;
    }
    initializeWorkers(workerNum);
  }

  private void initializeWorkers(int num) {
    for (int i = 0; i < num; i++) {
      Worker worker = new Worker();
      workers.add(worker);
      Thread thread = new Thread(worker);
      thread.start();
    }
  }

  @Override
  public void execute(Job job) {
        // if job is null, throw exception
        if (job==null){
            throw new NullPointerException();
        }
        // When there are more jobs than asked, add to the jobs list
        if (job != null) {
            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notify();
            }
        }

    }

  @Override
  public void shutdown(){
    for (Worker worker: workers) {
      worker.shutdown();
    }
  }
  @Override 
  public void addWorker(int num) {
    // locked to avoid when this thread hasn't completed adding while next thread adds
    synchronized (jobs) {
      if (num + this.workerNum > maxNum) {
        num = maxNum - this.workerNum;
      }
      initializeWorkers(num);
      this.workerNum += num;
    }
  }

  @Override
    public void reduceWorker(int num) {
        synchronized (jobs) {
            if(num>=this.workerNum){
                throw new IllegalArgumentException("More than existing thread number");
            }
            for (int i = 0; i < num; i++) {
                Worker worker = workers.get(i);
                if (worker != null) {
                    worker.shutdown();
                    workers.remove(i);
                }
            }
            this.workerNum -= num;
        }

    }

    @Override
  public int getJobNum() {
      return workers.size();
  }

  class Worker implements Runnable {
        // whether this worker is running
        private volatile boolean running = true;
        @Override
        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {
                    if (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            // feel the interruption outside
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    // get a job
                    job = jobs.removeFirst();
                }
                // do the job
                if (job != null) {
                    job.run();
                }
            }
        }
       public void shutdown() {
            running = false;
        }
}


}