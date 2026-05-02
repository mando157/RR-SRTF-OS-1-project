package scheduler.sim.model;
 
import java.util.List;
 
/**
 * Represents the state of the ready queue at a specific point in time.
 * Captured after each quantum in Round Robin.
 */
public class QueueSnapshot {
    private final int time;           // at what time was this snapshot taken
    private final String running;     // process currently running
    private final List<String> queue; // processes waiting in ready queue
 
    public QueueSnapshot(int time, String running, List<String> queue) {
        this.time = time;
        this.running = running;
        this.queue = queue;
    }
 
    public int getTime()        { return time; }
    public String getRunning()  { return running; }
    public List<String> getQueue() { return queue; }
}