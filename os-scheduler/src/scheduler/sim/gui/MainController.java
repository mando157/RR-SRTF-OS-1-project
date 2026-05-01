package scheduler.sim.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduler.sim.model.Process;
import scheduler.sim.model.Result;
import scheduler.sim.scheduler.RoundRobin;
import scheduler.sim.scheduler.SRTF;

import java.util.ArrayList;
import java.util.List;

public class MainController {

  
    @FXML private TextField pidField;
    @FXML private TextField arrivalField;
    @FXML private TextField burstField;
    @FXML private TextField quantumField;
    @FXML private Button addBtn;
    @FXML private Button runBtn;

 
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

   
    @FXML private TextArea comparisonArea;

    
    private final List<Process> processes = new ArrayList<>();

    @FXML
    public void initialize() {
       
        rrPid.setCellValueFactory(new PropertyValueFactory<>("id"));
        rrAt.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        rrBt.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        rrWt.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        rrTat.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        rrRt.setCellValueFactory(new PropertyValueFactory<>("responseTime"));

        srtfPid.setCellValueFactory(new PropertyValueFactory<>("id"));
        srtfAt.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        srtfBt.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        srtfWt.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        srtfTat.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        srtfRt.setCellValueFactory(new PropertyValueFactory<>("responseTime"));

        addBtn.setOnAction(e -> addProcess());
        runBtn.setOnAction(e -> runSimulation());
    }

   
    private void addProcess() {
        try {
            String id = pidField.getText().trim();
            if (id.isEmpty()) {
                showError("Process ID cannot be empty!");
                return;
            }

            int at = Integer.parseInt(arrivalField.getText().trim());
            int bt = Integer.parseInt(burstField.getText().trim());

            if (at < 0) {
                showError("Arrival Time cannot be negative!");
                return;
            }
            if (bt <= 0) {
                showError("Burst Time must be greater than 0!");
                return;
            }

           
            for (Process p : processes) {
                if (p.getId().equals(id)) {
                    showError("Process ID '" + id + "' already exists!");
                    return;
                }
            }

            processes.add(new Process(id, at, bt));

            pidField.clear();
            arrivalField.clear();
            burstField.clear();
            pidField.requestFocus();

            showInfo("Process " + id + " added! Total: " + processes.size());

        } catch (NumberFormatException e) {
            showError("Arrival Time and Burst Time must be valid numbers!");
        }
    }

    private void runSimulation() {
        if (processes.isEmpty()) {
            showError("Please add at least one process first!");
            return;
        }

        try {
            int q = Integer.parseInt(quantumField.getText().trim());
            if (q <= 0) {
                showError("Quantum must be greater than 0!");
                return;
            }

           
            List<Process> rrList   = cloneList(processes);
            List<Process> srtfList = cloneList(processes);

            Result rrResult   = new RoundRobin(q).schedule(rrList);
            Result srtfResult = new SRTF().schedule(srtfList);

            rrTable.setItems(
                FXCollections.observableArrayList(rrResult.getFinishedProcesses())
            );
            srtfTable.setItems(
                FXCollections.observableArrayList(srtfResult.getFinishedProcesses())
            );

          
            rrChart.draw(rrResult.getGanttChart());
            srtfChart.draw(srtfResult.getGanttChart());

           
            showComparison(rrResult, srtfResult);

        } catch (NumberFormatException e) {
            showError("Quantum must be a valid number!");
        } catch (Exception e) {
            showError("Error running simulation: " + e.getMessage());
        }
    }

   
    private void showComparison(Result rr, Result srtf) {
        double rrWT    = avg(rr.getFinishedProcesses(),   Process::getWaitingTime);
        double srtfWT  = avg(srtf.getFinishedProcesses(), Process::getWaitingTime);
        double rrRT    = avg(rr.getFinishedProcesses(),   Process::getResponseTime);
        double srtfRT  = avg(srtf.getFinishedProcesses(), Process::getResponseTime);
        double rrTAT   = avg(rr.getFinishedProcesses(),   Process::getTurnaroundTime);
        double srtfTAT = avg(srtf.getFinishedProcesses(), Process::getTurnaroundTime);

        String wWt  = rrWT  <= srtfWT  ? "RR" : "SRTF";
        String wRt  = rrRT  <= srtfRT  ? "RR" : "SRTF";
        String wTat = rrTAT <= srtfTAT ? "RR" : "SRTF";

        comparisonArea.setText(
            "===== COMPARISON SUMMARY =====\n" +
            "Metric         |   RR    |  SRTF   | Winner\n" +
            "---------------|---------|---------|-------\n" +
            "Avg Wait Time  | " + round(rrWT)   + "  | " + round(srtfWT)  + "  | " + wWt  + "\n" +
            "Avg Resp Time  | " + round(rrRT)   + "  | " + round(srtfRT)  + "  | " + wRt  + "\n" +
            "Avg TAT        | " + round(rrTAT)  + "  | " + round(srtfTAT) + "  | " + wTat + "\n"
        );
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
        for (Process p : original)
            copy.add(new Process(p));
        return copy;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    private void showInfo(String msg) {
       
        System.out.println("[INFO] " + msg);
    }
}

