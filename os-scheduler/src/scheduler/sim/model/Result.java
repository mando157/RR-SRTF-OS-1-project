package scheduler.sim.model;


 
import java.util.List;
 
public class Result {
    private List<GanttEntry> ganttChart;
    private List<Process> finishedProcesses;
    private List<QueueSnapshot> queueSnapshots; // NEW
 
    public Result(List<GanttEntry> ganttChart, List<Process> finishedProcesses) {
        this.ganttChart = ganttChart;
        this.finishedProcesses = finishedProcesses;
    }
 
    public Result(List<GanttEntry> ganttChart, List<Process> finishedProcesses, List<QueueSnapshot> queueSnapshots) {
        this.ganttChart = ganttChart;
        this.finishedProcesses = finishedProcesses;
        this.queueSnapshots = queueSnapshots;
    }
 
    public List<GanttEntry> getGanttChart() { return ganttChart; }
    public List<Process> getFinishedProcesses() { return finishedProcesses; }
    public List<QueueSnapshot> getQueueSnapshots() { return queueSnapshots; }
}