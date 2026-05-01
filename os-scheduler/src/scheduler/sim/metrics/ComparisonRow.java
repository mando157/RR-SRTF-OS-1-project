package scheduler.sim.metrics;

import javafx.beans.property.SimpleStringProperty;

public class ComparisonRow {

    private final SimpleStringProperty metric;
    private final SimpleStringProperty rr;
    private final SimpleStringProperty srtf;
    private final SimpleStringProperty winner;

    public ComparisonRow(String metric, String rr, String srtf, String winner) {
        this.metric = new SimpleStringProperty(metric);
        this.rr     = new SimpleStringProperty(rr);
        this.srtf   = new SimpleStringProperty(srtf);
        this.winner = new SimpleStringProperty(winner);
    }

    public String getMetric() { return metric.get(); }
    public String getRr()     { return rr.get(); }
    public String getSrtf()   { return srtf.get(); }
    public String getWinner() { return winner.get(); }

    public SimpleStringProperty metricProperty() { return metric; }
    public SimpleStringProperty rrProperty()     { return rr; }
    public SimpleStringProperty srtfProperty()   { return srtf; }
    public SimpleStringProperty winnerProperty() { return winner; }
}