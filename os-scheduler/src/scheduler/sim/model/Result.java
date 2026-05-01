package scheduler.sim.model;

import java.util.List;

public class Result {
    private List<GanttEntry> ganttChart;
    private List<Process> finishedProcesses;

    public Result(List<GanttEntry> ganttChart, List<Process> finishedProcesses) {
        this.ganttChart = ganttChart;
        this.finishedProcesses = finishedProcesses;
    }

    public List<GanttEntry> getGanttChart() {
        return ganttChart;
    }

    public List<Process> getFinishedProcesses() {
        return finishedProcesses;
    }
}
