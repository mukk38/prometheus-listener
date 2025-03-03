package tr.com.muskar.prometheus.listener.service;


import com.google.common.cache.Cache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tr.com.muskar.prometheus.listener.model.response.AlertGroup;
import tr.com.muskar.prometheus.listener.model.response.AlertRule;
import tr.com.muskar.prometheus.listener.model.response.PrometheusResponse;
import tr.com.muskar.prometheus.listener.websocket.WebsocketService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrometheusPullService {

    @Value("${prometheus.service.url}")
    private String prometheusServiceUrl;
    private final RestTemplate restTemplate;
    private final Cache<String, AlertRule> sessions;
    private final WebsocketService websocketService;

    @PostConstruct
    private void init() {
        if (!prometheusServiceUrl.endsWith("/")) {
            prometheusServiceUrl = prometheusServiceUrl + "/";
        }
    }

    public void pullPrometheusAlertFromRest() {
        try {
            ResponseEntity<PrometheusResponse> prometheusResponseResponseEntity = restTemplate.
                    exchange(createPrometheusPullService(), HttpMethod.GET, createRequestEntity(), PrometheusResponse.class);
            if (prometheusResponseResponseEntity.getStatusCode() == HttpStatusCode.valueOf(200)) {
                processPrometheusResponse(prometheusResponseResponseEntity.getBody());
            } else {
                log.error(prometheusResponseResponseEntity.getStatusCode() + " error code from prometheus service.");
            }
        } catch (Exception e) {
            log.error("Error when requesting prometheus . Error Message: " + e.getMessage());
        }
    }

    private void processPrometheusResponse(PrometheusResponse prometheusResponse) {
        log.info("Status: " + prometheusResponse.getStatus());
        List<AlertGroup> alertGroupList = prometheusResponse.getData().getGroups();
        for (AlertGroup alertGroup : alertGroupList) {
            log.info(alertGroup.toString());
            for (AlertRule rule : alertGroup.getRules()) {
                if (rule.getState().equals("firing")) {
                    String key = alertGroup.getName() + "-" + rule.getName();
                    if (Objects.isNull(sessions.getIfPresent(key))) {
                        websocketService.sendPrometheusAlertToUser(rule);
                    }
                    sessions.put(key, rule);
                }
            }
        }
    }

    private String createPrometheusPullService() {
        return prometheusServiceUrl + "api/v1/rules?type=alert";
    }

    private HttpEntity<String> createRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(null, headers);
    }
}
