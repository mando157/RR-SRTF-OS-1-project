package scheduler.sim.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import scheduler.sim.model.GanttEntry;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GanttChart extends HBox {


    private static final String[] COLORS = {
    "#818cf8",  
    "#38bdf8",  
    "#34d399", 
    "#fbbf24",  
    "#f87171",  
    "#f472b6",  
    "#22d3ee",  
    "#a3e635" 
};

    private final Map<String, String> colorMap = new HashMap<>();
    private int colorIndex = 0;

    public GanttChart() {
        super(3); 
        this.getStyleClass().add("gantt-container");
        this.setPadding(new javafx.geometry.Insets(8));
    }

    public void draw(List<GanttEntry> timeline) {
        this.getChildren().clear();
        colorMap.clear();
        colorIndex = 0;

        if (timeline == null || timeline.isEmpty()) return;

        for (GanttEntry e : timeline) {
            int duration = e.getEndTime() - e.getStartTime();
            int width = Math.max(50, duration * 38);

            // Label block = new Label(e.getProcessId() + "\n" + e.getStartTime() + "→" + e.getEndTime());
            // block.setMinWidth(width);
            // block.setMaxWidth(width);
            // block.setMinHeight(50);
            // block.setAlignment(javafx.geometry.Pos.CENTER);
            // block.setWrapText(true);

            Label nameLabel = new Label(e.getProcessId());
            nameLabel.setMinWidth(width);
            nameLabel.setMaxWidth(width);
            nameLabel.setMinHeight(36);
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.setWrapText(false);

            if (e.getProcessId().equals("Idle")) {
                nameLabel.setStyle(
                    "-fx-background-color: #374151;" +
                    "-fx-background-radius: 6;" +
                   "-fx-text-fill: #0f172a;" +
                    "-fx-font-size: 12px;" +
                    // "-fx-alignment: center;" +
                    "-fx-font-style: italic;" +
                    "-fx-padding: 4 6 4 6;"
                );
            } else {
                String color = getColor(e.getProcessId());
                nameLabel.setStyle(
                    "-fx-background-color: " + color + ";" +
                    "-fx-background-radius: 6;" +
                    "-fx-text-fill: #0f172a;"  +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 12px;" +
                    "-fx-alignment: center;" +
                    "-fx-padding: 4 6 4 6;" +
                    "-fx-effect: dropshadow(gaussian, " + color + "99, 6, 0, 0, 0);"
                );
            }
            Label timeLabel = new Label(String.valueOf(e.getStartTime()));
            timeLabel.setMinWidth(width);
            timeLabel.setMaxWidth(width);
            timeLabel.setAlignment(Pos.CENTER_LEFT);
            timeLabel.setStyle(
               "-fx-text-fill: #0f172a;"+
                "-fx-font-size: 12px;" +
                "-fx-font-family: monospace;" +
                "-fx-padding: 2 0 0 2;"
            );

            VBox cell = new VBox(2, nameLabel, timeLabel);
            cell.setAlignment(Pos.TOP_LEFT);

            this.getChildren().add(cell);
        }
        if (!timeline.isEmpty()) {
            GanttEntry last = timeline.get(timeline.size() - 1);
            Label endLabel = new Label(String.valueOf(last.getEndTime()));
            endLabel.setAlignment(Pos.CENTER_LEFT);
            endLabel.setStyle(
                "-fx-text-fill: #0f172a;" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: monospace;" +
                "-fx-padding: 40 0 0 0;"
            );
            this.getChildren().add(endLabel);
        }

    }

    private String getColor(String processId) {
        if (!colorMap.containsKey(processId)) {
            colorMap.put(processId, COLORS[colorIndex % COLORS.length]);
            colorIndex++;
        }
        return colorMap.get(processId);
    }
}

