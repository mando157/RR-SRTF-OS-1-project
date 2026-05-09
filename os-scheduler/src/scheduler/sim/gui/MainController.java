package scheduler.sim.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduler.sim.model.Process;
import scheduler.sim.model.Result;
import scheduler.sim.scheduler.RoundRobin;
import scheduler.sim.scheduler.SRTF;
import scheduler.sim.metrics.ComparisonRow;

import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private TextField pidField;
    @FXML private TextField arrivalField;
    @FXML private TextField burstField;
    @FXML private TextField quantumField;
    @FXML private Button addBtn;
    @FXML private Button runBtn;
    @FXML private Button deleteBtn;
    @FXML private Button clearBtn;

    // ── Scenario Buttons ──
    @FXML private Button scenarioABtn;
    @FXML private Button scenarioBBtn;
    @FXML private Button scenarioCBtn;
    @FXML private Button scenarioDBtn;

    @FXML private TableView<Process> processTable;
    @FXML private TableColumn<Process, String>  procPid;
    @FXML private TableColumn<Process, Integer> procAt;
    @FXML private TableColumn<Process, Integer> procBt;

    @FXML private TableView<Process> rrTable;
    @FXML private TableColumn<Process, String>  rrPid;
    @FXML private TableColumn<Process, Integer> rrAt;
    @FXML private TableColumn<Process, Integer> rrBt;
    @FXML private TableColumn<Process, Integer> rrWt;
    @FXML private TableColumn<Process, Integer> rrTat;
    @FXML private TableColumn<Process, Integer> rrRt;

    @FXML private TableView<Process> srtfTable;
    @FXML private TableColumn<Process, String>  srtfPid;
    @FXML private TableColumn<Process, Integer> srtfAt;
    @FXML private TableColumn<Process, Integer> srtfBt;
    @FXML private TableColumn<Process, Integer> srtfWt;
    @FXML private TableColumn<Process, Integer> srtfTat;
    @FXML private TableColumn<Process, Integer> srtfRt;

    @FXML private GanttChart rrChart;
    @FXML private GanttChart srtfChart;
    @FXML private ReadyQueueView readyQueueView;
    @FXML private Label conclusionLabel;
    @FXML private Label quantumLabel;
    @FXML private Label rrFairnessLabel;
    @FXML private Label srtfEfficiencyLabel;
    @FXML private Label fairnessVerdictLabel;

    @FXML private TableView<ComparisonRow> comparisonTable;
    @FXML private TableColumn<ComparisonRow, String> colMetric;
    @FXML private TableColumn<ComparisonRow, String> colRR;
    @FXML private TableColumn<ComparisonRow, String> colSRTF;
    @FXML private TableColumn<ComparisonRow, String> colWinner;

    private final javafx.collections.ObservableList<Process> processes =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        procPid.setCellValueFactory(new PropertyValueFactory<>("id"));
        procAt.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        procBt.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        processTable.setItems(processes);

        rrPid.setCellValueFactory(new PropertyValueFactory<>("id"));
        rrAt.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        rrBt.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        rrWt.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        rrTat.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        rrRt.setCellValueFactory(new PropertyValueFactory<>("responseTime"));
        rrRt.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "—" : String.valueOf(item));
            }
        });

        srtfPid.setCellValueFactory(new PropertyValueFactory<>("id"));
        srtfAt.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        srtfBt.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        srtfWt.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        srtfTat.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        srtfRt.setCellValueFactory(new PropertyValueFactory<>("responseTime"));
        srtfRt.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "—" : String.valueOf(item));
            }
        });

        colMetric.setCellValueFactory(new PropertyValueFactory<>("metric"));
        colRR.setCellValueFactory(new PropertyValueFactory<>("rr"));
        colSRTF.setCellValueFactory(new PropertyValueFactory<>("srtf"));
        colWinner.setCellValueFactory(new PropertyValueFactory<>("winner"));
        colWinner.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    if      (item.equals("RR"))   setStyle("-fx-text-fill: #a855f7; -fx-font-weight: bold;");
                    else if (item.equals("SRTF")) setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    else                          setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                }
            }
        });

        addBtn.setOnAction(e -> addProcess());
        runBtn.setOnAction(e -> runSimulation());
        deleteBtn.setOnAction(e -> deleteSelected());
        clearBtn.setOnAction(e -> clearAll());

        
        scenarioABtn.setOnAction(e -> loadScenario("A"));
        scenarioBBtn.setOnAction(e -> loadScenario("B"));
        scenarioCBtn.setOnAction(e -> loadScenario("C"));
        scenarioDBtn.setOnAction(e -> loadScenario("D"));
    }

   
    private void loadScenario(String scenario) {
        clearAll();
        switch (scenario) {
            case "A" -> {
                
                processes.add(new Process("P1", 0, 8));
                processes.add(new Process("P2", 1, 4));
                processes.add(new Process("P3", 2, 9));
                processes.add(new Process("P4", 3, 5));
                quantumField.setText("3");
            }
            case "B" -> {
               
                processes.add(new Process("P1", 0, 6));
                processes.add(new Process("P2", 0, 6));
                processes.add(new Process("P3", 0, 6));
                processes.add(new Process("P4", 0, 6));
                quantumField.setText("1");
            }
            case "C" -> {
                // Short-Job-Heavy
                processes.add(new Process("P1", 0, 1));
                processes.add(new Process("P2", 0, 2));
                processes.add(new Process("P3", 0, 1));
                processes.add(new Process("P4", 0, 8));
                processes.add(new Process("P5", 0, 2));
                quantumField.setText("3");
            }
            case "D" -> {
                // Interactive Fairness
                processes.add(new Process("P1", 0, 5));
                processes.add(new Process("P2", 1, 5));
                processes.add(new Process("P3", 2, 5));
                processes.add(new Process("P4", 3, 5));
                processes.add(new Process("P5", 4, 5));
                quantumField.setText("2");
            }
        }
    }

    private void addProcess() {
        try {
            String id = pidField.getText().trim();
            if (id.isEmpty()) { showError("Process ID cannot be empty!"); return; }

            int at = Integer.parseInt(arrivalField.getText().trim());
            int bt = Integer.parseInt(burstField.getText().trim());

            if (at < 0)  { showError("Arrival Time cannot be negative!"); return; }
            if (bt <= 0) { showError("Burst Time must be greater than 0!"); return; }

            for (Process p : processes) {
                if (p.getId().equals(id)) {
                    showError("Process ID '" + id + "' already exists!");
                    return;
                }
            }

            processes.add(new Process(id, at, bt));
            pidField.clear(); arrivalField.clear(); burstField.clear();
            pidField.requestFocus();
            System.out.println("[INFO] Process " + id + " added! Total: " + processes.size());

        } catch (NumberFormatException e) {
            showError("Arrival Time and Burst Time must be valid numbers!");
        }
    }

    private void refreshProcessTable() { /* ObservableList updates automatically */ }

    private void deleteSelected() {
        Process selected = processTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Please select a process to delete!"); return; }
        processes.remove(selected);
        rrTable.getItems().clear();
        srtfTable.getItems().clear();
        rrChart.draw(new ArrayList<>());
        srtfChart.draw(new ArrayList<>());
        readyQueueView.draw(new ArrayList<>());
        comparisonTable.getItems().clear();
        conclusionLabel.setText("Run a simulation to see the conclusion.");
        conclusionLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 13px;");
    }

    private void clearAll() {
        processes.clear();
        rrTable.getItems().clear();
        srtfTable.getItems().clear();
        rrChart.draw(new ArrayList<>());
        srtfChart.draw(new ArrayList<>());
        readyQueueView.draw(new ArrayList<>());
        comparisonTable.getItems().clear();
        conclusionLabel.setText("Run a simulation to see the conclusion.");
        conclusionLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 13px;");
        quantumField.clear();
        quantumLabel.setText("");
        rrFairnessLabel.setText("—");
        srtfEfficiencyLabel.setText("—");
        fairnessVerdictLabel.setText("Run a simulation to see the analysis.");
        fairnessVerdictLabel.setStyle("-fx-text-fill: #92400e; -fx-font-size: 12px;");
        pidField.requestFocus();
    }

    private void runSimulation() {
        if (processes.isEmpty()) { showError("Please add at least one process first!"); return; }
        try {
            int q = Integer.parseInt(quantumField.getText().trim());
            if (q <= 0) { showError("Quantum must be greater than 0!"); return; }

            List<Process> rrList   = cloneList(processes);
            List<Process> srtfList = cloneList(processes);

            Result rrResult   = new RoundRobin(q).schedule(rrList);
            Result srtfResult = new SRTF().schedule(srtfList);

            rrTable.setItems(FXCollections.observableArrayList(rrResult.getFinishedProcesses()));
            srtfTable.setItems(FXCollections.observableArrayList(srtfResult.getFinishedProcesses()));

            rrChart.draw(rrResult.getGanttChart());
            readyQueueView.draw(rrResult.getQueueSnapshots());
            srtfChart.draw(srtfResult.getGanttChart());

            showComparison(rrResult, srtfResult);
            showConclusion(rrResult, srtfResult, q);
            quantumLabel.setText("Time Quantum (Q) = " + q);
            showFairnessAnalysis(rrResult, srtfResult, q);

        } catch (NumberFormatException e) {
            showError("Quantum must be a valid number!");
        } catch (Exception e) {
            showError("Error running simulation: " + e.getMessage());
        }
    }

    private void showComparison(Result rr, Result srtf) {
        double rrWT   = avg(rr.getFinishedProcesses(),   Process::getWaitingTime);
        double srtfWT = avg(srtf.getFinishedProcesses(), Process::getWaitingTime);
        double rrRT   = avg(rr.getFinishedProcesses(),   Process::getResponseTime);
        double srtfRT = avg(srtf.getFinishedProcesses(), Process::getResponseTime);
        double rrTAT  = avg(rr.getFinishedProcesses(),   Process::getTurnaroundTime);
        double srtfTAT= avg(srtf.getFinishedProcesses(), Process::getTurnaroundTime);

        String wWt  = rrWT  < srtfWT  ? "RR" : srtfWT  < rrWT  ? "SRTF" : "Equal";
        String wRt  = rrRT  < srtfRT  ? "RR" : srtfRT  < rrRT  ? "SRTF" : "Equal";
        String wTat = rrTAT < srtfTAT ? "RR" : srtfTAT < rrTAT ? "SRTF" : "Equal";

        comparisonTable.setItems(FXCollections.observableArrayList(
                new ComparisonRow("Avg Wait Time", round(rrWT),  round(srtfWT),  wWt),
                new ComparisonRow("Avg Resp Time", round(rrRT),  round(srtfRT),  wRt),
                new ComparisonRow("Avg TAT",       round(rrTAT), round(srtfTAT), wTat)));
    }

    private void showConclusion(Result rr, Result srtf, int quantum) {
        double rrWT   = avg(rr.getFinishedProcesses(),   Process::getWaitingTime);
        double srtfWT = avg(srtf.getFinishedProcesses(), Process::getWaitingTime);
        double rrRT   = avg(rr.getFinishedProcesses(),   Process::getResponseTime);
        double srtfRT = avg(srtf.getFinishedProcesses(), Process::getResponseTime);
        double rrTAT  = avg(rr.getFinishedProcesses(),   Process::getTurnaroundTime);
        double srtfTAT= avg(srtf.getFinishedProcesses(), Process::getTurnaroundTime);

        String betterWT  = rrWT  < srtfWT  ? "Round Robin" : srtfWT  < rrWT  ? "SRTF" : "Both equally";
        String betterRT  = rrRT  < srtfRT  ? "Round Robin" : srtfRT  < rrRT  ? "SRTF" : "Both equally";
        String betterTAT = rrTAT < srtfTAT ? "Round Robin" : srtfTAT < rrTAT ? "SRTF" : "Both equally";

        int rrMaxWT   = rr.getFinishedProcesses().stream().mapToInt(Process::getWaitingTime).max().orElse(0);
        int rrMinWT   = rr.getFinishedProcesses().stream().mapToInt(Process::getWaitingTime).min().orElse(0);
        int srtfMaxWT = srtf.getFinishedProcesses().stream().mapToInt(Process::getWaitingTime).max().orElse(0);
        int srtfMinWT = srtf.getFinishedProcesses().stream().mapToInt(Process::getWaitingTime).min().orElse(0);
        boolean rrFairer = (rrMaxWT - rrMinWT) <= (srtfMaxWT - srtfMinWT);

        StringBuilder sb = new StringBuilder();
        sb.append("1. Waiting Time: ").append(betterWT).append(" performed better")
          .append(" (RR avg: ").append(round(rrWT)).append(", SRTF avg: ").append(round(srtfWT)).append(").\n\n");
        sb.append("2. Response Time: ").append(betterRT).append(" performed better")
          .append(" (RR avg: ").append(round(rrRT)).append(", SRTF avg: ").append(round(srtfRT)).append(").\n\n");
        sb.append("3. Turnaround Time: ").append(betterTAT).append(" performed better")
          .append(" (RR avg: ").append(round(rrTAT)).append(", SRTF avg: ").append(round(srtfTAT)).append(").\n\n");
        sb.append("4. Fairness: ")
          .append(rrFairer
                  ? "Round Robin appeared fairer — WT spread was smaller (max-min = "
                    + (rrMaxWT - rrMinWT) + " vs " + (srtfMaxWT - srtfMinWT) + " for SRTF)."
                  : "SRTF showed smaller WT spread (" + (srtfMaxWT - srtfMinWT)
                    + " vs " + (rrMaxWT - rrMinWT) + " for RR), favoring shorter jobs.")
          .append("\n\n");
        sb.append("5. Effect of Quantum (Q=").append(quantum).append("): ")
          .append("With quantum=").append(quantum)
          .append(", Round Robin preempts every ").append(quantum).append(" time unit(s). ")
          .append(quantum <= 2
                  ? "A small quantum improves response time but increases context switches."
                  : quantum >= 5
                          ? "A large quantum reduces context switches but may delay short processes."
                          : "A medium quantum balances responsiveness and overhead.")
          .append("\n\n");
        sb.append("6. Recommendation: ");
        if (srtfWT < rrWT && srtfTAT < rrTAT) {
            sb.append("SRTF is more efficient for this workload. ")
              .append("Round Robin is preferable when fairness across all processes is a priority.");
        } else if (rrWT <= srtfWT && rrTAT <= srtfTAT) {
            sb.append("Round Robin performed competitively or better on this workload. ")
              .append("SRTF may still be preferred for throughput-intensive environments.");
        } else {
            sb.append("Results are mixed. SRTF is more efficient on some metrics; ")
              .append("Round Robin provides fairer CPU distribution.");
        }

        conclusionLabel.setText(sb.toString());
        conclusionLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 13px;");
    }

    @FunctionalInterface
    interface Extractor { int get(Process p); }

    private double avg(List<Process> list, Extractor fn) {
        return list.stream().mapToInt(fn::get).average().orElse(0);
    }

    private String round(double val) {
        return String.valueOf(Math.round(val * 100.0) / 100.0);
    }

    private List<Process> cloneList(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) copy.add(new Process(p));
        return copy;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    private void showFairnessAnalysis(Result rr, Result srtf, int quantum) {
        double rrRT   = avg(rr.getFinishedProcesses(),   Process::getResponseTime);
        double srtfRT = avg(srtf.getFinishedProcesses(), Process::getResponseTime);
        double rrWT   = avg(rr.getFinishedProcesses(),   Process::getWaitingTime);
        double srtfWT = avg(srtf.getFinishedProcesses(), Process::getWaitingTime);

        int rrMax   = rr.getFinishedProcesses().stream().mapToInt(Process::getWaitingTime).max().orElse(0);
        int rrMin   = rr.getFinishedProcesses().stream().mapToInt(Process::getWaitingTime).min().orElse(0);
        int srtfMax = srtf.getFinishedProcesses().stream().mapToInt(Process::getWaitingTime).max().orElse(0);
        int srtfMin = srtf.getFinishedProcesses().stream().mapToInt(Process::getWaitingTime).min().orElse(0);

        rrFairnessLabel.setText(
                "Every process gets CPU every " + quantum + " time unit(s).\n" +
                "WT spread: " + (rrMax - rrMin) + " (max-min)\n" +
                "Avg Response Time: " + round(rrRT) + "\n" +
                "No process is starved regardless of burst time.");

        srtfEfficiencyLabel.setText(
                "Always runs the shortest remaining job.\n" +
                "WT spread: " + (srtfMax - srtfMin) + " (max-min)\n" +
                "Avg Response Time: " + round(srtfRT) + "\n" +
                "Short jobs finish fast; long jobs may wait.");

        boolean rrFairer      = (rrMax - rrMin) <= (srtfMax - srtfMin);
        boolean srtfEfficient = srtfWT < rrWT;

        fairnessVerdictLabel.setText(
                "Verdict: " +
                (rrFairer ? "Round Robin is fairer (smaller WT spread). " : "SRTF has a smaller WT spread. ") +
                (srtfEfficient ? "SRTF is more efficient (lower avg WT)." : "Round Robin achieved competitive efficiency."));
        fairnessVerdictLabel.setStyle("-fx-text-fill: #92400e; -fx-font-size: 12px; -fx-font-weight: bold;");
    }
}