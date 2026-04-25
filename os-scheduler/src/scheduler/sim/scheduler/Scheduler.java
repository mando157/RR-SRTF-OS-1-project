package scheduler.sim.scheduler;

import scheduler.sim.model.Process;
import scheduler.sim.model.Result;

import java.util.List;

public interface Scheduler {
    Result schedule(List<Process> processes);
}
