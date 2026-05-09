#  Round Robin vs SRTF — CPU Scheduler Simulator

> **Operating Systems Course | Scheduling Comparison Project**  
> A JavaFX desktop application that simulates and compares **Round Robin (RR)** and **Shortest Remaining Time First (SRTF)** CPU scheduling algorithms.

  
##  Features
 
-  **Dynamic Process Input** — add any number of processes at runtime
-  **Full Input Validation** — rejects invalid quantum, negative AT, BT=0, duplicate IDs
-  **Separate Gantt Charts** — visual execution timeline for both algorithms
-  **Ready Queue View** — shows RR queue state after each quantum
-  **Results Tables** — WT, TAT, RT for every process in both algorithms
-  **Comparison Summary** — side-by-side averages with winner highlighted
-  **Fairness vs Efficiency Analysis** — dedicated section comparing both algorithms
-  **Final Conclusion** — auto-generated analysis based on simulation results
-  **Preset Test Scenarios** — load A, B1, B2, C, D, E with one click
-  **Delete / Clear** — manage process list easily

##  How to Run
 
### Prerequisites
- Java 17+
- JavaFX SDK 17+
- Maven or your IDE (IntelliJ / Eclipse)
### Steps
 
```bash
# Clone the repository
git clone https://github.com/mando157/RR-SRTF-OS-1-project.git
 
# Navigate to project
cd RR-SRTF-OS-1-project
 
# Run with Maven
mvn javafx:run
```
 
Or open in **IntelliJ IDEA** and run `MainFX.java`.



##  Algorithms
 
### Round Robin
- Processes are served in a circular queue
- Each process runs for at most **Q** time units (quantum)
- If not finished, it's re-added to the back of the queue
- New arrivals during execution are added after the current quantum ends
- **Ties** broken by arrival time, then by process ID
### SRTF (Shortest Remaining Time First)
- At every time unit, the process with the **shortest remaining burst time** runs
- If a new process arrives with a shorter remaining time, it **immediately preempts** the current process
- **Ties** broken by arrival time, then by process ID
- Idle periods handled when no process has arrived yet




##  Metrics
 
| Metric | Formula |
|---|---|
| **Completion Time (CT)** | Time when process finishes |
| **Turnaround Time (TAT)** | CT − Arrival Time |
| **Waiting Time (WT)** | TAT − Burst Time |
| **Response Time (RT)** | First CPU time − Arrival Time |
 
---
 
##  Analysis & Conclusions
 
| Metric | Better Algorithm |
|---|---|
| Avg Waiting Time | SRTF (minimizes WT) |
| Avg Response Time | RR (every process gets CPU quickly) |
| Avg Turnaround Time | SRTF |
| Fairness | RR (no starvation) |
| Short Job Performance | SRTF |



 
### Key Findings
 
- **SRTF** is more efficient — lower average WT and TAT in most workloads
- **Round Robin** is fairer — no process is starved regardless of burst time
- **Quantum Effect** — smaller Q improves RT but increases context switches; larger Q reduces overhead but approaches FCFS behavior
- **SRTF** can cause starvation for long processes when short jobs keep arriving
### Recommendation
- Use **SRTF** for batch/throughput-intensive environments
- Use **Round Robin** for interactive/time-sharing systems where fairness matters


## 🛠️ Technologies Used
 
- **Java 17**
- **JavaFX 17** — UI framework
- **FXML** — UI layout
- **CSS** — Styling
- **Maven** — Build tool

# Screenshots

## Scenario A
###  Description
This scenario represents a normal mixed workload with different arrival times and burst times.  
It is used to verify the correctness of both scheduling algorithms under realistic execution conditions.

###  Purpose
- Test basic scheduling behavior
- Verify Gantt chart correctness
- Compare fairness and efficiency
  
![Scenario A](os-scheduler/src/screenshots/Scenario%20A-1.png)
![Scenario A](os-scheduler/src/screenshots/Scenario%20A-2.png)
![Scenario A](os-scheduler/src/screenshots/Scenario%20A-3.png)



## Scenario B

### Description
This scenario uses a very small quantum value to demonstrate the effect of time slicing in Round Robin scheduling.

###  Purpose
- Show the impact of quantum size
- Demonstrate high context switching
- Compare responsiveness and overhead
 
![Scenario B](os-scheduler/src/screenshots/Scenario%20B1-1.png)
![Scenario B](os-scheduler/src/screenshots/Scenario%20B1-2.png)
![Scenario B](os-scheduler/src/screenshots/Scenario%20B1-3.png)



## Scenario C
###  Description
This workload contains many short processes and one long process to highlight the behaviour of SRTF.

###  Purpose
- Demonstrate SRTF efficiency
- Show fast completion of short jobs
- Reveal possible starvation of long processes
  
![Scenario C](os-scheduler/src/screenshots/Scenario%20C-1.png)
![Scenario C](os-scheduler/src/screenshots/Scenario%20C-2.png)
![Scenario C](os-scheduler/src/screenshots/Scenario%20C-3.png)



## Scenario D
###  Description
This scenario simulates an interactive system where processes arrive continuously and require fair CPU sharing.

###  Purpose
- Demonstrate Round Robin fairness
- Compare response times
- Show balanced CPU allocation
  
![Scenario D](os-scheduler/src/screenshots/Scenario%20D-1.png)
![Scenario D](os-scheduler/src/screenshots/Scenario%20D-2.png)
![Scenario D](os-scheduler/src/screenshots/Scenario%20D-3.png)

## Scenario E
###  Description
This scenario tests the system's ability to reject invalid input safely.

###  Purpose
- Verify input validation
- Prevent invalid scheduling behaviour
- Ensure system robustness


