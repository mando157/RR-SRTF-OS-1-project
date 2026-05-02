package scheduler.sim.scheduler;

import scheduler.sim.model.GanttEntry;
import scheduler.sim.model.Process;
import scheduler.sim.model.Result;
import scheduler.sim.model.QueueSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobin implements Scheduler {

    private final int quantum;

    public RoundRobin(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum must be greater than 0");
        }
        this.quantum = quantum;
    }

    @Override
    public Result schedule(List<Process> inputProcesses) {
        if (inputProcesses == null || inputProcesses.isEmpty()) {
            return new Result(new ArrayList<>(), new ArrayList<>());
        }

        List<Process> processes = copyAndSortProcesses(inputProcesses);
        Queue<Process> readyQueue = new LinkedList<>();
        List<GanttEntry> ganttChart = new ArrayList<>();
        List<Process> finishedProcesses = new ArrayList<>();
        List<QueueSnapshot> snapshots = new ArrayList<>();

        int currentTime = 0;
        int completedCount = 0;
        int nextArrivalIndex = 0;
        int totalProcesses = processes.size();

        while (completedCount < totalProcesses) {

            while (nextArrivalIndex < totalProcesses &&
                    processes.get(nextArrivalIndex).getArrivalTime() <= currentTime) {
                readyQueue.offer(processes.get(nextArrivalIndex));
                nextArrivalIndex++;
            }

            if (readyQueue.isEmpty()) {
                if (nextArrivalIndex < totalProcesses) {
                    currentTime = processes.get(nextArrivalIndex).getArrivalTime();
                    continue;
                }
            }

            Process current = readyQueue.poll();
            if (current == null) {
                continue;
            }

            if (current.getStartTime() == -1) {
                current.setStartTime(currentTime);
                current.setResponseTime(currentTime - current.getArrivalTime());
            }

            int executionTime = Math.min(quantum, current.getRemainingTime());
            int start = currentTime;
            int end = currentTime + executionTime;

            ganttChart.add(new GanttEntry(current.getId(), start, end));

            current.setRemainingTime(current.getRemainingTime() - executionTime);
            currentTime = end;

            while (nextArrivalIndex < totalProcesses &&
                    processes.get(nextArrivalIndex).getArrivalTime() <= currentTime) {
                readyQueue.offer(processes.get(nextArrivalIndex));
                nextArrivalIndex++;
            }

            if (current.getRemainingTime() > 0) {
                readyQueue.offer(current);
            } else {
                current.setCompletionTime(currentTime);
                current.setTurnaroundTime(current.getCompletionTime() - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                finishedProcesses.add(current);
                completedCount++;
            }
            // ── SNAPSHOT: record what is waiting after this quantum ──
            List<String> waiting = new ArrayList<>();
            for (Process p : readyQueue) {
                waiting.add(p.getId());
            }
            snapshots.add(new QueueSnapshot(
                    currentTime,
                    current.getRemainingTime() > 0 ? current.getId() : "—",
                    waiting
            ));

         

        }

        finishedProcesses.sort(Comparator.comparing(Process::getId));
        return new Result(ganttChart, finishedProcesses, snapshots);
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
