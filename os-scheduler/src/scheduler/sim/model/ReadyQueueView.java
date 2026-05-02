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
 
/**
 * Displays the Round Robin ready queue state after each quantum.
 *
 * Layout per step:
 *   [t=X]  [Running: P1]  →  [P2] [P3] [P4]  (waiting)
 */
public class ReadyQueueView extends ScrollPane {
 
    // Colour palette – matches GanttChart process colours
    private static final String[] PROCESS_COLORS = {
        "#a855f7", "#06b6d4", "#10b981", "#f59e0b",
        "#ef4444", "#3b82f6", "#ec4899", "#84cc16"
    };
    private static final String IDLE_COLOR   = "#4b5563";
    private static final String DONE_COLOR   = "#374151";
 
    private final VBox container;
 
    public ReadyQueueView() {
        container = new VBox(8);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #1a1a2e;");
 
        setContent(container);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setStyle("-fx-background-color: #1a1a2e; -fx-background: #1a1a2e; " +
                 "-fx-border-color: #2d2d4e; -fx-border-radius: 10;");
        setPrefHeight(200);
    }
 
    /** Called from MainController after simulation runs. */
    public void draw(List<QueueSnapshot> snapshots) {
        container.getChildren().clear();
 
        if (snapshots == null || snapshots.isEmpty()) {
            Label empty = new Label("No data");
            empty.setStyle("-fx-text-fill: #6b7280;");
            container.getChildren().add(empty);
            return;
        }
 
        for (QueueSnapshot snap : snapshots) {
            container.getChildren().add(buildRow(snap));
        }
    }
 
    // ── one row per snapshot ────────────────────────────────────────────────
 
    private HBox buildRow(QueueSnapshot snap) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 6, 4, 6));
        row.setStyle("-fx-background-color: #12122a; -fx-background-radius: 8;");
 
        // Time label
        Label timeLabel = new Label("t=" + snap.getTime());
        timeLabel.setPrefWidth(45);
        timeLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px; -fx-font-family: monospace;");
 
        // Running box
        StackPane runningBox = makeProcessBox(snap.getRunning(), true);
 
        // Arrow  →
        Label arrow = new Label("→");
        arrow.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 14px;");
 
        // "Queue:" label
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
 
                // small right-arrow between queue items
                if (i < snap.getQueue().size() - 1) {
                    Label sep = new Label("›");
                    sep.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");
                    row.getChildren().add(sep);
                }
            }
        }
 
        return row;
    }
 
    // ── coloured process pill ───────────────────────────────────────────────
 
    private StackPane makeProcessBox(String pid, boolean isRunning) {
        Label label = new Label(pid);
        label.setStyle(
            "-fx-text-fill: white;" +
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
 
    /** Deterministic colour from process ID. */
    private String colorFor(String pid) {
        int hash = 0;
        for (char c : pid.toCharArray()) hash += c;
        return PROCESS_COLORS[hash % PROCESS_COLORS.length];
    }
}