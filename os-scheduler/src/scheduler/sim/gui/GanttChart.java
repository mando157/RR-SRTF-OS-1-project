package scheduler.sim.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import scheduler.sim.model.GanttEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GanttChart extends HBox {


    private static final String[] COLORS = {
        "#7c3aed", 
        "#0ea5e9", 
        "#10b981", 
        "#f59e0b", 
        "#ef4444", 
        "#ec4899", 
        "#06b6d4", 
        "#84cc16"  
    };

    private final Map<String, String> colorMap = new HashMap<>();
    private int colorIndex = 0;

    public GanttChart() {
        super(4); 
        this.getStyleClass().add("gantt-container");
    }

    public void draw(List<GanttEntry> timeline) {
        this.getChildren().clear();
        colorMap.clear();
        colorIndex = 0;

        for (GanttEntry e : timeline) {
            int duration = e.getEndTime() - e.getStartTime();
            int width = Math.max(40, duration * 35);

            Label block = new Label(e.getProcessId() + "\n" + e.getStartTime() + "→" + e.getEndTime());
            block.setMinWidth(width);
            block.setMaxWidth(width);
            block.setMinHeight(50);
            block.setAlignment(javafx.geometry.Pos.CENTER);
            block.setWrapText(true);

            if (e.getProcessId().equals("Idle")) {
                block.setStyle(
                    "-fx-background-color: #374151;" +
                    "-fx-background-radius: 6;" +
                    "-fx-text-fill: #9ca3af;" +
                    "-fx-font-size: 11px;" +
                    "-fx-alignment: center;" +
                    "-fx-font-style: italic;" +
                    "-fx-padding: 4;"
                );
            } else {
                String color = getColor(e.getProcessId());
                block.setStyle(
                    "-fx-background-color: " + color + ";" +
                    "-fx-background-radius: 6;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 11px;" +
                    "-fx-alignment: center;" +
                    "-fx-padding: 4;" +
                    "-fx-effect: dropshadow(gaussian, " + color + "99, 6, 0, 0, 0);"
                );
            }

            this.getChildren().add(block);
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