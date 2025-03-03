package tr.com.muskar.prometheus.listener.model.response;

import lombok.Data;

@Data
public class AlertAnnotations {

    private String description;
    private String runbook_url;
    private String summary;
}
