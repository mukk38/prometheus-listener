package tr.com.muskar.prometheus.listener.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import tr.com.muskar.prometheus.listener.model.response.AlertRule;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ApplicationStartupConfig {

    @Bean
    public Cache<String, AlertRule> sessions(){
        return CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        SimpleModule module = new SimpleModule();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(module)
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return mapper;
    }


    @Bean
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        RestTemplate restTemplate;
        try {
            CloseableHttpClient httpClient = getHttpClient();
            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

            restTemplate = new RestTemplate(requestFactory);
        } catch (Exception e) {
            restTemplate = restTemplateTemp();
        }
        return restTemplate;
    }

    private CloseableHttpClient getHttpClient()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return HttpClients.custom()
                .setConnectionManager(
                        PoolingHttpClientConnectionManagerBuilder.create()
                                .setSSLSocketFactory(
                                        SSLConnectionSocketFactoryBuilder.create()
                                                .setSslContext(
                                                        SSLContexts.custom()
                                                                .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                                                                .build())
                                                .setHostnameVerifier((s, sslSession) -> true)
                                                .build())
                                .build())
                .build();
    }

    public RestTemplate restTemplateTemp() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        final var restTemplate = new RestTemplate();

        ClientHttpRequestFactory requestFactory = disableSSlHttpClient5();
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory disableSSlHttpClient5()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial((X509Certificate[] certificateChain, String authType) -> true)  // <--- accepts each certificate
                .build();

        Registry<ConnectionSocketFactory> socketRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(URIScheme.HTTPS.getId(), new SSLConnectionSocketFactory(sslContext))
                .register(URIScheme.HTTP.getId(), new PlainConnectionSocketFactory())
                .build();

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager(socketRegistry))
                .setConnectionManagerShared(true)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}
