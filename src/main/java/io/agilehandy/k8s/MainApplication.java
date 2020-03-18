package io.agilehandy.k8s;

import io.agilehandy.k8s.configmap.ConfigMapInformer;
import io.agilehandy.k8s.configmap.ConfigMapInformerProperties;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties(ConfigMapInformerProperties.class)
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(ConfigMapInformer configMapInformer) {
		return args -> configMapInformer.run();
	}

	@Bean
	public KubernetesClient client(Config config) {
		return new DefaultKubernetesClient(config);
	}

	@Bean
	public Config config() {
		return new ConfigBuilder().build();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
