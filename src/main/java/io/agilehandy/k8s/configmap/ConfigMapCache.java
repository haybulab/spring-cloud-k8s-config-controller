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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

/**
 * @author Haytham Mohamed
 **/

@Component
public class ConfigMapCache {

	private static Logger logger = LoggerFactory.getLogger(ConfigMapCache.class);

	private final Map<String, ConfigMap> cache = new ConcurrentHashMap<>();

	private final KubernetesClient client;
	private final ConfigMapInformerProperties properties;

	private boolean synced;

	public ConfigMapCache(KubernetesClient client, ConfigMapInformerProperties properties) {
		this.client = client;
		this.properties = properties;
		synced = false;
	}

	public boolean isSynced() { return synced; }

	public boolean isNotSeen(ConfigMap newcm) {
		String version = newcm.getMetadata().getResourceVersion();
		boolean isNotSeen = false;
		if (!cache.containsKey(version)) {
			isNotSeen = true;
		}
		return isNotSeen;
	}

	public void removeFromCache(ConfigMap cm) {
		if (cache.containsKey(cm.getMetadata().getResourceVersion())) {
			cache.remove(cm.getMetadata().getResourceVersion());
		}
	}

	public void addToCache(ConfigMap cm) {
		cache.put(cm.getMetadata().getResourceVersion(), cm);
	}

	@PostConstruct
	private void boostrapCache() {
		logger.info("Start syncing cache.");
		client.configMaps()
				.list()
				.getItems()
				.stream()
				.filter(cm -> Util.isSpringConfigMap(cm, properties.getConfigmapLabelEnabled()))
				.forEach(cm -> cache.put(cm.getMetadata().getResourceVersion(), cm))
		;
		synced = true;
		logger.info("Cache is sync with {} configmaps", cache.size());
		cache.values().stream().forEach(cm -> logger.info(cm.getMetadata().getName()));
	}

	public Collection<ConfigMap> getConfigMaps() {
		return this.cache.values();
	}

}
