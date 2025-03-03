package tr.com.muskar.prometheus.listener.model.response;

import lombok.Data;

@Data
public class PrometheusResponse {
    private String status;
    private PrometheusData data;
}
