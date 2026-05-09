package scheduler.sim.gui;
 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import scheduler.sim.model.QueueSnapshot;
 
import java.util.List;
 

public class ReadyQueueView extends ScrollPane {
 
   
    private static final String[] PROCESS_COLORS = {
    "#818cf8",
    "#38bdf8",
    "#34d399",
    "#fbbf24",
    "#f87171",
    "#f472b6",
    "#22d3ee",
    "#a3e635"
};
    private static final String IDLE_COLOR   = "#4b5563";
    private static final String DONE_COLOR   = "#374151";
 
    private final VBox container;
 
    public ReadyQueueView() {
        container = new VBox(8);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color:  #f8fafc;");
 
        setContent(container);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setStyle("-fx-background-color: #f8fafc; -fx-background: #f8fafc; " +
         "-fx-border-color: #94a3b8; -fx-border-radius: 10;");
        setPrefHeight(200);
    }
 
    
    public void draw(List<QueueSnapshot> snapshots) {
        container.getChildren().clear();
 
        if (snapshots == null || snapshots.isEmpty()) {
            Label empty = new Label("No data");
            empty.setStyle("-fx-text-fill:  #64748b;");
            container.getChildren().add(empty);
            return;
        }
 
        for (QueueSnapshot snap : snapshots) {
            container.getChildren().add(buildRow(snap));
        }
    }
 
   
 
    private HBox buildRow(QueueSnapshot snap) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 6, 4, 6));
        row.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8;");
 
       
        Label timeLabel = new Label("t=" + snap.getTime());
        timeLabel.setPrefWidth(45);
        timeLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-family: monospace;");
 
        
        StackPane runningBox = makeProcessBox(snap.getRunning(), true);
 
        
        Label arrow = new Label("→");
        arrow.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 14px;");
 
        
        Label qLabel = new Label("Queue:");
        qLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
 
        row.getChildren().addAll(timeLabel, runningBox, arrow, qLabel);
 
        if (snap.getQueue().isEmpty()) {
            Label emptyQ = new Label("(empty)");
            emptyQ.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 11px; -fx-font-style: italic;");
            row.getChildren().add(emptyQ);
        } else {
            for (int i = 0; i < snap.getQueue().size(); i++) {
                String pid = snap.getQueue().get(i);
                row.getChildren().add(makeProcessBox(pid, false));
 
               
                if (i < snap.getQueue().size() - 1) {
                    Label sep = new Label("›");
                    sep.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");
                    row.getChildren().add(sep);
                }
            }
        }
 
        return row;
    }
 
    
 
    private StackPane makeProcessBox(String pid, boolean isRunning) {
        Label label = new Label(pid);
        label.setStyle(
            "-fx-text-fill: #1e1b4b;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;"
        );
 
        StackPane box = new StackPane(label);
        box.setPadding(new Insets(3, 10, 3, 10));
 
        String color;
        if (pid.equals("—") || pid.equals("Idle")) {
            color = IDLE_COLOR;
        } else {
            color = colorFor(pid);
        }
 
        String border = isRunning
            ? "-fx-border-color: white; -fx-border-width: 1.5; -fx-border-radius: 6;"
            : "-fx-border-color: transparent; -fx-border-width: 0;";
 
        box.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 6;" +
            border
        );
 
        return box;
    }
 
 
    private String colorFor(String pid) {
        int hash = 0;
        for (char c : pid.toCharArray()) hash += c;
        return PROCESS_COLORS[hash % PROCESS_COLORS.length];
    }
}