/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agilehandy.k8s.configmap;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.client.informers.cache.Lister;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Haytham Mohamed
 **/

@Configuration
public class InformerConfiguration {

	private final ConfigMapInformerProperties properties;

	public InformerConfiguration(ConfigMapInformerProperties properties) {
		this.properties = properties;
	}

	@Bean
	public Config config() {
		return new ConfigBuilder().build();
	}

	@Bean
	public KubernetesClient client(Config config) {
		return new DefaultKubernetesClient(config);
	}

	@Bean
	public SharedInformerFactory sharedInformerFactory(KubernetesClient client) {
		SharedInformerFactory factory = client.informers();
		factory.sharedIndexInformerFor(ConfigMap.class
				, ConfigMapList.class
				, properties.getWatcherInterval() * 1000L);
		return factory;
	}

	@Bean
	public Lister<ConfigMap> lister( SharedInformerFactory factory
			, KubernetesClient client) {
		SharedIndexInformer<ConfigMap> informer =
				factory.getExistingSharedIndexInformer(ConfigMap.class);
		return new Lister(informer.getIndexer(), client.getNamespace());
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
