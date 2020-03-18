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
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.annotation.PostConstruct;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.cache.Lister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

/**
 * @author Haytham Mohamed
 **/

@Component
public class ConfigMapCache {

	private static Logger logger = LoggerFactory.getLogger(ConfigMapCache.class);

	// set of resourceVersion
	private final Set<String> cache = new ConcurrentSkipListSet();

	private final KubernetesClient client;
	private final ConfigMapInformerProperties properties;
	private final Lister<ConfigMap> lister;

	private boolean synced;

	public ConfigMapCache(KubernetesClient client, ConfigMapInformerProperties properties, Lister<ConfigMap> lister) {
		this.client = client;
		this.properties = properties;
		this.lister = lister;
		synced = false;
	}

	public boolean isSynced() { return synced; }

	public boolean exists(ConfigMap cm) {
		return cache.contains(cm.getMetadata().getResourceVersion());
	}

	public void removeFromCache(ConfigMap cm) {
		cache.remove(cm.getMetadata().getResourceVersion());
	}

	public void addToCache(ConfigMap cm) {
		cache.add(cm.getMetadata().getResourceVersion());
	}

	@PostConstruct
	private void boostrapCache() {
		logger.debug("Start syncing cache.");
		lister.list()
				.stream()
				.filter(cm -> Util.isSpringConfigMap(cm, properties.getConfigmapLabelEnabled()))
				.forEach(cm -> cache.add(cm.getMetadata().getResourceVersion()))
		;
		logger.debug("Cache is sync with {} configmaps", cache.size());
		synced = true;
	}

}
