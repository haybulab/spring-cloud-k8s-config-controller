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
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

/**
 * @author Haytham Mohamed
 **/

@Component
public class ConfigMapEventHandler implements ResourceEventHandler<ConfigMap> {

	private static Logger logger = LoggerFactory.getLogger(ConfigMapEventHandler.class);

	private final ConfigMapInformerProperties properties;
	private final ConfigMapCache cache;
	private final ConfigMapMessenger messenger;

	public ConfigMapEventHandler(ConfigMapInformerProperties properties, ConfigMapCache cache, ConfigMapMessenger messenger) {
		this.properties = properties;
		this.cache = cache;
		this.messenger = messenger;
	}

	@Override
	public void onAdd(ConfigMap cm) {
		if (cache.isSynced()
				&& Util.isSpringConfigMap(cm, properties.getConfigmapLabelEnabled())
				&& !cache.exists(cm)
		) {
			cache.addToCache(cm);
			messenger.publish(cm);
			logger.debug("{} spring ConfigMap is added", cm.getMetadata().getName());
		}
	}

	@Override
	public void onUpdate(ConfigMap oldcm, ConfigMap newcm) {
		if (cache.isSynced()
				&& Util.isSpringConfigMap(oldcm, properties.getConfigmapLabelEnabled())
				&& Util.isSpringConfigMap(newcm, properties.getConfigmapLabelEnabled())
				&& !cache.exists(newcm)
		) {
			cache.removeFromCache(oldcm);
			cache.addToCache(newcm);
			messenger.publish(newcm);
			logger.debug("{} spring ConfigMap is updated", oldcm.getMetadata().getName());
		}
	}

	@Override
	public void onDelete(ConfigMap cm, boolean deletedFinalStateUnknown) {
		if (cache.isSynced()
				&& Util.isSpringConfigMap(cm, properties.getConfigmapLabelEnabled())
				&& cache.exists(cm)
		) {
			cache.removeFromCache(cm);
			messenger.publish(cm);
			logger.debug("{} spring ConfigMap is deleted", cm.getMetadata().getName());
		}
	}

}
