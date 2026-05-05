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
            p.setStartTime(-1);
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
                                    && (p.getArrivalTime() < shortest.getArrivalTime()
                                            ||
                                            (p.getArrivalTime() == shortest.getArrivalTime()
                                                    && p.getId().compareTo(shortest.getId()) < 0)))) {
                        shortest = p;
                    }
                }
            }

            if (shortest == null) {
                // int idleStart = currentTime;
                // currentTime++;

                // while (completedCount < n && !hasArrivedProcess(processes, currentTime)) {
                // currentTime++;
                // }
                int idleStart = currentTime;
                final int capturedTime = currentTime;
                int nextArrival = processes.stream()
                        .filter(p -> p.getArrivalTime() > capturedTime && p.getRemainingTime() > 0)
                        .mapToInt(Process::getArrivalTime)
                        .min().orElse(capturedTime + 1);

                currentTime = nextArrival;

                gantt.add(new GanttEntry("Idle", idleStart, currentTime));
                continue;
            }

            if (currentProcess != shortest) {

                if (currentProcess != null) {
                    gantt.add(new GanttEntry(
                            currentProcess.getId(),
                            startTime,
                            currentTime));
                }

                currentProcess = shortest;
                startTime = currentTime;

                if (currentProcess.getStartTime() == -1) {
                    currentProcess.setStartTime(currentTime);
                    currentProcess.setResponseTime(
                            currentTime - currentProcess.getArrivalTime());
                }
            }

            currentProcess.setRemainingTime(
                    currentProcess.getRemainingTime() - 1);

            currentTime++;

            if (currentProcess.getRemainingTime() == 0) {

                currentProcess.setCompletionTime(currentTime);

                currentProcess.setTurnaroundTime(
                        currentProcess.getCompletionTime()
                                - currentProcess.getArrivalTime());

                currentProcess.setWaitingTime(
                        currentProcess.getTurnaroundTime()
                                - currentProcess.getBurstTime());

                gantt.add(new GanttEntry(
                        currentProcess.getId(),
                        startTime,
                        currentTime));

                completed.add(currentProcess);
                completedCount++;
                currentProcess = null;
            }
        }

        completed.sort(Comparator.comparing(Process::getId));

        return new Result(gantt, completed);
    }

    private boolean hasArrivedProcess(List<Process> processes, int currentTime) {
        for (Process p : processes) {
            if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0) {
                return true;
            }
        }
        return false;
    }
}