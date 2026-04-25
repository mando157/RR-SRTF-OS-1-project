package scheduler.sim.scheduler;

import scheduler.sim.model.GanttEntry;
import scheduler.sim.model.Process;
import scheduler.sim.model.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SRTF implements Scheduler {

    @Override
    public Result schedule(List<Process> processes) {

        List<GanttEntry> gantt = new ArrayList<>();
        List<Process> completed = new ArrayList<>();

        int currentTime = 0;
        int completedCount = 0;
        int n = processes.size();

        for (Process p : processes) {
            p.setRemainingTime(p.getBurstTime());
        }

        Process currentProcess = null;
        int startTime = 0;

        while (completedCount < n) {

            Process shortest = null;

            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0) {
                    if (shortest == null ||
                            p.getRemainingTime() < shortest.getRemainingTime() ||
                            (p.getRemainingTime() == shortest.getRemainingTime()
                                    && p.getArrivalTime() < shortest.getArrivalTime())) {
                        shortest = p;
                    }
                }
            }

            if (shortest == null) {
                currentTime++;
                continue;
            }

            if (currentProcess != shortest) {
                if (currentProcess != null) {
                    gantt.add(new GanttEntry(currentProcess.getId(), startTime, currentTime));
                }

                currentProcess = shortest;
                startTime = currentTime;

                if (currentProcess.getStartTime() == -1) {
                    currentProcess.setStartTime(currentTime);
                    currentProcess.setResponseTime(currentTime - currentProcess.getArrivalTime());
                }
            }

            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;

            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnaroundTime(currentProcess.getCompletionTime() - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());

                gantt.add(new GanttEntry(currentProcess.getId(), startTime, currentTime));

                completed.add(currentProcess);
                completedCount++;
                currentProcess = null;
            }
        }

        completed.sort(Comparator.comparing(Process::getId));
        return new Result(gantt, completed);
    }
}