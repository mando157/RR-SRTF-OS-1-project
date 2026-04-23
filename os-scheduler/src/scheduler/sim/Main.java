package scheduler.sim;

import scheduler.sim.model.GanttEntry;
import scheduler.sim.model.Process;
import scheduler.sim.model.Result;
import scheduler.sim.scheduler.RoundRobin;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 5));
        processes.add(new Process("P2", 1, 3));
        processes.add(new Process("P3", 2, 1));
        processes.add(new Process("P4", 3, 2));

        RoundRobin roundRobin = new RoundRobin(2);
        Result result = roundRobin.schedule(processes);

        System.out.println("=== Gantt Chart ===");
        for (GanttEntry entry : result.getGanttChart()) {
            System.out.println(entry);
        }

        System.out.println("\n=== Process Results ===");
        for (Process p : result.getFinishedProcesses()) {
            System.out.println(
                    p.getId() +
                    " | WT=" + p.getWaitingTime() +
                    " | TAT=" + p.getTurnaroundTime() +
                    " | RT=" + p.getResponseTime() +
                    " | CT=" + p.getCompletionTime()
            );
        }
    }
}
