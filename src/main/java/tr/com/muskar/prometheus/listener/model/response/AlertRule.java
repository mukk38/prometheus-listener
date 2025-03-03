package tr.com.muskar.prometheus.listener.model.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AlertRule {
    private String state;
    private String name;
    private String query;
    private int duration;
    private Map<String, String> labels;
    private AlertAnnotations annotations;
    private List<Alert> alerts;
    private String health;
    private double evaluationTime;
    private String lastEvaluation;
    private String type;
}
