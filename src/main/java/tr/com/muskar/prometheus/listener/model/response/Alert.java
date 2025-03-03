package tr.com.muskar.prometheus.listener.model.response;

import lombok.Data;

import java.util.Map;

@Data
public class Alert {
    private Map<String, String> labels;
    private AlertAnnotations annotations;
    private String state;
    private String activeAt;
    private String value;
}
