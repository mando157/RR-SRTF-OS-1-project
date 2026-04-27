package scheduler.sim.metrics;

import scheduler.sim.model.Process;
import scheduler.sim.model.Result;
import scheduler.sim.model.GanttEntry;

import java.util.List;

public class Metrics {

    public static void printResults(String algorithmName, Result result) {
        List<Process> processes = result.getFinishedProcesses();
        
        if (processes == null || processes.isEmpty()) {
            System.out.println("\n No processes to display for " + algorithmName);
            return;
        }

       
        double totalTAT = 0, totalWT = 0, totalRT = 0;
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                    %-35s ║\n", algorithmName + " Results");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  Process │ TAT  │  WT  │  RT  │ Burst │ Start │ Complete  ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        for (Process p : processes) {
            System.out.printf("║    %-4s │ %4d │ %4d │ %4d │  %3d  │  %3d  │    %3d    ║\n",
                    p.getId(), 
                    p.getTurnaroundTime(), 
                    p.getWaitingTime(), 
                    p.getResponseTime(),
                    p.getBurstTime(),
                    p.getStartTime(),
                    p.getCompletionTime());
            
            totalTAT += p.getTurnaroundTime();
            totalWT += p.getWaitingTime();
            totalRT += p.getResponseTime();
        }
        
        int n = processes.size();
        double avgTAT = totalTAT / n;
        double avgWT = totalWT / n;
        double avgRT = totalRT / n;
        
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Averages │ %.2f │ %.2f │ %.2f │        │      │           ║\n",
                avgTAT, avgWT, avgRT);
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
    
  
    public static void printGanttChart(String algorithmName, Result result) {
        List<GanttEntry> gantt = result.getGanttChart();
        
        if (gantt == null || gantt.isEmpty()) {
            System.out.println("\n No Gantt chart available for " + algorithmName);
            return;
        }
        
        System.out.println( algorithmName + " - Gantt Chart:");
        System.out.println();
        
   
        System.out.print("    ");
        for (int i = 0; i < gantt.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println("+");
        
        
        System.out.print("    ");
        for (GanttEntry entry : gantt) {
            System.out.printf("|  %-3s ", entry.getProcessId());
        }
        System.out.println("|");
        
 
        System.out.print("    ");
        for (int i = 0; i < gantt.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println("+");
        
        
        System.out.print("    ");
        for (GanttEntry entry : gantt) {
            System.out.printf("%-8d", entry.getStartTime());
        }
        System.out.println(gantt.get(gantt.size() - 1).getEndTime());
        System.out.println();
    }
    

    public static void printComparison(Result rrResult, Result srtfResult, String rrName, String srtfName) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           ALGORITHMS COMPARISON                              ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        
        List<Process> rrProcesses = rrResult.getFinishedProcesses();
        List<Process> srtfProcesses = srtfResult.getFinishedProcesses();
        
        System.out.printf("║ %-10s │ %15s │ %15s │ %15s ║\n", "Process", "RR - TAT/WT/RT", "SRTF - TAT/WT/RT", "Better");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        
        for (int i = 0; i < rrProcesses.size(); i++) {
            Process rrP = rrProcesses.get(i);
            Process srtfP = srtfProcesses.get(i);
            
            String better = "";
            if (rrP.getTurnaroundTime() < srtfP.getTurnaroundTime()) {
                better = "RR";
            } else if (srtfP.getTurnaroundTime() < rrP.getTurnaroundTime()) {
                better = "SRTF";
            } else {
                better = "Equal";
            }
            
            System.out.printf("║ %-10s │ %3d/%3d/%3d │ %3d/%3d/%3d │ %15s ║\n",
                    rrP.getId(),
                    rrP.getTurnaroundTime(), rrP.getWaitingTime(), rrP.getResponseTime(),
                    srtfP.getTurnaroundTime(), srtfP.getWaitingTime(), srtfP.getResponseTime(),
                    better);
        }
        
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        
       
        double rrAvgTAT = calculateAvg(rrProcesses, "TAT");
        double rrAvgWT = calculateAvg(rrProcesses, "WT");
        double rrAvgRT = calculateAvg(rrProcesses, "RT");
        
        double srtfAvgTAT = calculateAvg(srtfProcesses, "TAT");
        double srtfAvgWT = calculateAvg(srtfProcesses, "WT");
        double srtfAvgRT = calculateAvg(srtfProcesses, "RT");
        
        System.out.printf("║ %-10s │ %5.2f/%5.2f/%5.2f │ %5.2f/%5.2f/%5.2f │ %15s ║\n",
                "AVERAGE",
                rrAvgTAT, rrAvgWT, rrAvgRT,
                srtfAvgTAT, srtfAvgWT, srtfAvgRT,
                (srtfAvgTAT < rrAvgTAT ? "SRTF" : "RR"));
        
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
   
        System.out.println("\n💡 ANALYSIS:");
        if (srtfAvgTAT < rrAvgTAT) {
            System.out.println("   ✓ SRTF has better (lower) average Turnaround Time");
        } else {
            System.out.println("   ✓ Round Robin has better (lower) average Turnaround Time");
        }
        
        if (srtfAvgWT < rrAvgWT) {
            System.out.println("   ✓ SRTF has better (lower) average Waiting Time");
        } else {
            System.out.println("   ✓ Round Robin has better (lower) average Waiting Time");
        }
        
        if (srtfAvgRT < rrAvgRT) {
            System.out.println("   ✓ SRTF has better (lower) average Response Time");
        } else {
            System.out.println("   ✓ Round Robin has better (lower) average Response Time");
        }
        
        System.out.println();
    }
    
    private static double calculateAvg(List<Process> processes, String metric) {
        if (processes == null || processes.isEmpty()) return 0;
        
        double sum = 0;
        for (Process p : processes) {
            switch (metric) {
                case "TAT":
                    sum += p.getTurnaroundTime();
                    break;
                case "WT":
                    sum += p.getWaitingTime();
                    break;
                case "RT":
                    sum += p.getResponseTime();
                    break;
            }
        }
        return sum / processes.size();
    }
}
