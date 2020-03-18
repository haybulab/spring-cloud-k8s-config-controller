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
package io.agilehandy.k8s;

import javax.annotation.PreDestroy;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Haytham Mohamed
 **/

//@Component
public class DummyInformer {

	private static Logger logger = LoggerFactory.getLogger(DummyInformer.class);

	private SharedInformerFactory sharedInformerFactory;
	private SharedIndexInformer<ConfigMap> cmInformer;

	public DummyInformer() {
		Config config = new ConfigBuilder().build();
		final KubernetesClient client = new DefaultKubernetesClient(config);
		sharedInformerFactory = client.informers();
		cmInformer = sharedInformerFactory
				.sharedIndexInformerFor(ConfigMap.class
						, ConfigMapList.class
						, 30 * 1000L);
		logger.info("Informer factory initialized.");
	}

	public void run() throws InterruptedException {
		cmInformer.addEventHandler(
					new ResourceEventHandler<ConfigMap>() {
						@Override
						public void onAdd(ConfigMap cm) {
							logger.info("{} ConfigMap added", cm.getMetadata().getName());
						}

						@Override
						public void onUpdate(ConfigMap oldcm, ConfigMap newcm) {
							logger.info("{} ConfigMap updated", oldcm.getMetadata().getName());
						}

						@Override
						public void onDelete(ConfigMap cm, boolean deletedFinalStateUnknown) {
							logger.info("{} ConfigMap deleted", cm.getMetadata().getName());
						}
					}
			);

			logger.info("Starting all registered informers");
			sharedInformerFactory.startAllRegisteredInformers();
	}

	@PreDestroy
	public void destroy() {
		sharedInformerFactory.stopAllRegisteredInformers();
	}
}
