package scheduler.sim.metrics;

import scheduler.sim.model.Process;
import scheduler.sim.model.GanttEntry;
import scheduler.sim.model.Result;
import java.util.*;

public class Metrics {

    public static void printResults(Result result) {
        System.out.println("\n=========== Scheduling Results ===========");
        System.out.println("+---------+------+------+------+-------+");
        System.out.printf("| Process | TAT  | WT   | RT   | Burst |\n");
        System.out.println("+---------+------+------+------+-------+");
        
        for (Process p : result.getFinishedProcesses()) {
            System.out.printf("|   %-4s | %4d | %4d | %4d | %5d |\n",
                    p.getId(), 
                    p.getTurnaroundTime(), 
                    p.getWaitingTime(), 
                    p.getResponseTime(),
                    p.getBurstTime());
        }
        
        System.out.println("+---------+------+------+------+-------+");
        System.out.printf("| Average | %.2f | %.2f | %.2f |       |\n",
                result.getAvgTurnaroundTime(),
                result.getAvgWaitingTime(),
                result.getAvgResponseTime());
        System.out.println("+-----------------------------------------+");
    }
}
