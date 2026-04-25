package scheduler.sim;

import scheduler.sim.model.GanttEntry;
import scheduler.sim.model.Process;
import scheduler.sim.model.Result;
import scheduler.sim.scheduler.RoundRobin;
import scheduler.sim.scheduler.SRTF;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        List<Process> processes = new ArrayList<>();

        processes.add(new Process("P1", 0, 8));
        processes.add(new Process("P2", 1, 4));
        processes.add(new Process("P3", 2, 2));
        processes.add(new Process("P4", 3, 1));

        // ===== Round Robin =====
        RoundRobin rr = new RoundRobin(2); // quantum = 2
        Result rrResult = rr.schedule(processes);

        System.out.println("========== ROUND ROBIN ==========");
        printGantt(rrResult.getGanttChart());
        printResults(rrResult);

        // ===== SRTF =====
        SRTF srtf = new SRTF();
        Result srtfResult = srtf.schedule(processes);

        System.out.println("\n========== SRTF ==========");
        printGantt(srtfResult.getGanttChart());
        printResults(srtfResult);
    }

    // ===== طباعة Gantt Chart =====
    public static void printGantt(List<GanttEntry> gantt) {
        System.out.println("--- Gantt Chart ---");
        for (GanttEntry g : gantt) {
            System.out.println(g);
        }
    }

    //Reasult
    public static void printResults(Result result) {
        System.out.println("\n--- Results ---");
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