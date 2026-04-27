package scheduler.sim.metrics;

import scheduler.sim.model.Process;
import scheduler.sim.model.Result;
import scheduler.sim.model.GanttEntry;

import java.util.List;

public class Metrics {

    public static String getResultsString(String algorithmName, Result result) {
        List<Process> processes = result.getFinishedProcesses();
        
        if (processes == null || processes.isEmpty()) {
            return "\n No processes to display for " + algorithmName;
        }

        StringBuilder sb = new StringBuilder();
        double totalTAT = 0, totalWT = 0, totalRT = 0;
        
        sb.append("\n╔════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║                    %-35s ║\n", algorithmName + " Results"));
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append("║  Process │ TAT  │  WT  │  RT  │ Burst │ Start │ Complete  ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        
        for (Process p : processes) {
            sb.append(String.format("║    %-4s │ %4d │ %4d │ %4d │  %3d  │  %3d  │    %3d    ║\n",
                    p.getId(), 
                    p.getTurnaroundTime(), 
                    p.getWaitingTime(), 
                    p.getResponseTime(),
                    p.getBurstTime(),
                    p.getStartTime(),
                    p.getCompletionTime()));
            
            totalTAT += p.getTurnaroundTime();
            totalWT += p.getWaitingTime();
            totalRT += p.getResponseTime();
        }
        
        int n = processes.size();
        double avgTAT = totalTAT / n;
        double avgWT = totalWT / n;
        double avgRT = totalRT / n;
        
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Averages │ %.2f │ %.2f │ %.2f │        │      │           ║\n",
                avgTAT, avgWT, avgRT));
        sb.append("╚════════════════════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    public static String getGanttChartString(String algorithmName, Result result) {
        List<GanttEntry> gantt = result.getGanttChart();
        
        if (gantt == null || gantt.isEmpty()) {
            return "\n No Gantt chart available for " + algorithmName;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(algorithmName).append(" - Gantt Chart:\n\n");
        
        sb.append("    ");
        for (int i = 0; i < gantt.size(); i++) {
            sb.append("+-------");
        }
        sb.append("+\n");
        
        sb.append("    ");
        for (GanttEntry entry : gantt) {
            sb.append(String.format("|  %-3s ", entry.getProcessId()));
        }
        sb.append("|\n");
        
        sb.append("    ");
        for (int i = 0; i < gantt.size(); i++) {
            sb.append("+-------");
        }
        sb.append("+\n");
        
        sb.append("    ");
        for (GanttEntry entry : gantt) {
            sb.append(String.format("%-8d", entry.getStartTime()));
        }
        sb.append(gantt.get(gantt.size() - 1).getEndTime()).append("\n\n");
        
        return sb.toString();
    }
    
    public static String getComparisonString(Result rrResult, Result srtfResult, String rrName, String srtfName) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                           ALGORITHMS COMPARISON                              ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════════════════════╣\n");
        
        List<Process> rrProcesses = rrResult.getFinishedProcesses();
        List<Process> srtfProcesses = srtfResult.getFinishedProcesses();
        
        sb.append(String.format("║ %-10s │ %15s │ %15s │ %15s ║\n", "Process", "RR - TAT/WT/RT", "SRTF - TAT/WT/RT", "Better"));
        sb.append("╠══════════════════════════════════════════════════════════════════════════════╣\n");
        
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
            
            sb.append(String.format("║ %-10s │ %3d/%3d/%3d │ %3d/%3d/%3d │ %15s ║\n",
                    rrP.getId(),
                    rrP.getTurnaroundTime(), rrP.getWaitingTime(), rrP.getResponseTime(),
                    srtfP.getTurnaroundTime(), srtfP.getWaitingTime(), srtfP.getResponseTime(),
                    better));
        }
        
        sb.append("╠══════════════════════════════════════════════════════════════════════════════╣\n");
        
        double rrAvgTAT = calculateAvg(rrProcesses, "TAT");
        double rrAvgWT = calculateAvg(rrProcesses, "WT");
        double rrAvgRT = calculateAvg(rrProcesses, "RT");
        
        double srtfAvgTAT = calculateAvg(srtfProcesses, "TAT");
        double srtfAvgWT = calculateAvg(srtfProcesses, "WT");
        double srtfAvgRT = calculateAvg(srtfProcesses, "RT");
        
        sb.append(String.format("║ %-10s │ %5.2f/%5.2f/%5.2f │ %5.2f/%5.2f/%5.2f │ %15s ║\n",
                "AVERAGE",
                rrAvgTAT, rrAvgWT, rrAvgRT,
                srtfAvgTAT, srtfAvgWT, srtfAvgRT,
                (srtfAvgTAT < rrAvgTAT ? "SRTF" : "RR")));
        
        sb.append("╚══════════════════════════════════════════════════════════════════════════════╝\n");
        
        sb.append("\n💡 ANALYSIS:\n");
        if (srtfAvgTAT < rrAvgTAT) {
            sb.append("   ✓ SRTF has better (lower) average Turnaround Time\n");
        } else {
            sb.append("   ✓ Round Robin has better (lower) average Turnaround Time\n");
        }
        
        if (srtfAvgWT < rrAvgWT) {
            sb.append("   ✓ SRTF has better (lower) average Waiting Time\n");
        } else {
            sb.append("   ✓ Round Robin has better (lower) average Waiting Time\n");
        }
        
        if (srtfAvgRT < rrAvgRT) {
            sb.append("   ✓ SRTF has better (lower) average Response Time\n");
        } else {
            sb.append("   ✓ Round Robin has better (lower) average Response Time\n");
        }
        
        sb.append("\n");
        
        return sb.toString();
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
