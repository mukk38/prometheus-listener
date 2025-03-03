package tr.com.muskar.prometheus.listener.task;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tr.com.muskar.prometheus.listener.service.PrometheusPullService;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private final PrometheusPullService prometheusPullService;

    @Scheduled(initialDelay = 1, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    private void checkPrometheusAlert() {
        prometheusPullService.pullPrometheusAlertFromRest();
    }
}
