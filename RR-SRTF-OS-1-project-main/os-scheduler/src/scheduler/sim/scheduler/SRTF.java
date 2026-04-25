package scheduler.sim.scheduler;

import scheduler.sim.model.GanttEntry;
import scheduler.sim.model.Process;
import scheduler.sim.model.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SRTF implements Scheduler {

    @Override
    public Result schedule(List<Process> inputProcesses) {
        if (inputProcesses == null || inputProcesses.isEmpty()) {
            return new Result(new ArrayList<>(), new ArrayList<>());
        }

        List<Process> processes = copyAndSortProcesses(inputProcesses);
        List<GanttEntry> ganttChart = new ArrayList<>();
        List<Process> finishedProcesses = new ArrayList<>();

        int currentTime = 0;
        int completedCount = 0;
        int totalProcesses = processes.size();

        Process previousProcess = null;
        int segmentStartTime = 0;

        while (completedCount < totalProcesses) {

            Process currentProcess = getShortestRemainingProcess(processes, currentTime);

            if (currentProcess == null) {
                currentTime++;
                previousProcess = null;
                segmentStartTime = currentTime;
                continue;
            }

            if (currentProcess.getStartTime() == -1) {
                currentProcess.setStartTime(currentTime);
                currentProcess.setResponseTime(currentTime - currentProcess.getArrivalTime());
            }

            if (previousProcess == null || !previousProcess.getId().equals(currentProcess.getId())) {
                if (previousProcess != null) {
                    ganttChart.add(new GanttEntry(
                            previousProcess.getId(),
                            segmentStartTime,
                            currentTime
                    ));
                }

                segmentStartTime = currentTime;
                previousProcess = currentProcess;
            }

            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
            currentTime++;

            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnaroundTime(currentProcess.getCompletionTime() - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());

                finishedProcesses.add(currentProcess);
                completedCount++;
            }
        }

        if (previousProcess != null) {
            ganttChart.add(new GanttEntry(
                    previousProcess.getId(),
                    segmentStartTime,
                    currentTime
            ));
        }

        finishedProcesses.sort(Comparator.comparing(Process::getId));
        return new Result(ganttChart, finishedProcesses);
    }

    private Process getShortestRemainingProcess(List<Process> processes, int currentTime) {
        Process shortest = null;

        for (Process process : processes) {
            boolean hasArrived = process.getArrivalTime() <= currentTime;
            boolean notFinished = process.getRemainingTime() > 0;

            if (hasArrived && notFinished) {
                if (shortest == null) {
                    shortest = process;
                } else if (process.getRemainingTime() < shortest.getRemainingTime()) {
                    shortest = process;
                } else if (process.getRemainingTime() == shortest.getRemainingTime()) {
                    if (process.getArrivalTime() < shortest.getArrivalTime()) {
                        shortest = process;
                    } else if (process.getArrivalTime() == shortest.getArrivalTime()
                            && process.getId().compareTo(shortest.getId()) < 0) {
                        shortest = process;
                    }
                }
            }
        }

        return shortest;
    }

    private List<Process> copyAndSortProcesses(List<Process> inputProcesses) {
        List<Process> copied = new ArrayList<>();

        for (Process process : inputProcesses) {
            copied.add(new Process(process));
        }

        copied.sort(
                Comparator.comparingInt(Process::getArrivalTime)
                        .thenComparing(Process::getId)
        );

        return copied;
    }
}
