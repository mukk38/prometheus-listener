package tr.com.muskar.prometheus.listener.model.response;

import lombok.Data;

import java.util.List;

@Data
public class PrometheusData {
    private List<AlertGroup> groups;
}
