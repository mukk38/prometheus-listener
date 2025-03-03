package tr.com.muskar.prometheus.listener.model.response;

import lombok.Data;

import java.util.List;

@Data
public class AlertGroup {
    private String name;
    private String file;
    private List<AlertRule> rules;
    private int interval;
    private double evaluationTime;
    private String lastEvaluation;
}
