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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.ConfigMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Haytham Mohamed
 **/

@RestController
public class WebController {

	private final ConfigMapCache cache;
	private final ConfigMapInformerProperties properties;

	public WebController(ConfigMapCache cache, ConfigMapInformerProperties properties) {
		this.cache = cache;
		this.properties = properties;
	}

	@GetMapping("/cache")
	public Flux<ConfigMapModel> getConfig() {
		Collection<ConfigMap> configmaps = cache.getConfigMaps();

		Function<ConfigMap, String> applicationProfileFunc = (ConfigMap cm) ->
				cm.getMetadata().getLabels().get(properties.getConfigmapLabelProfile()) != null?
						cm.getMetadata().getLabels().get(properties.getConfigmapLabelProfile()):"default";

		Set<ConfigMapModel> cmModels = configmaps.stream()
				.map(cm -> new ConfigMapModel(cm.getMetadata().getName()
						, applicationProfileFunc.apply(cm)
						, cm.getMetadata().getLabels().get(properties.getConfigmapLabelLabel())
						, cm.getData()))
				.collect(Collectors.toSet());

		return Flux.fromStream(cmModels.stream());
	}

	@GetMapping("/cache/config/namespace/{namespace}/name/{name}/profile/{profile}")
	public Mono<ConfigMapModel> getConfigMap(@PathVariable("name") String name,
			@PathVariable("namespace") String namespace,
			@PathVariable("profile") String profile) {

		Collection<ConfigMap> configmaps = cache.getConfigMaps();

		Function<ConfigMap, String> applicationProfileFunc = (ConfigMap cm) ->
				cm.getMetadata().getLabels().get(properties.getConfigmapLabelProfile()) != null?
						cm.getMetadata().getLabels().get(properties.getConfigmapLabelProfile()):"default";

		Predicate<ConfigMap> selected = (ConfigMap cm) ->
				cm.getMetadata().getName().equalsIgnoreCase(name)
				&& cm.getMetadata().getNamespace().equalsIgnoreCase(namespace)
				&& applicationProfileFunc.apply(cm).equalsIgnoreCase(profile);

		ConfigMapModel configmap = configmaps.stream()
				.filter(cm -> selected.test(cm))
				.map(cm -> new ConfigMapModel(cm.getMetadata().getName()
						, applicationProfileFunc.apply(cm)
						, cm.getMetadata().getLabels().get(properties.getConfigmapLabelLabel())
						, cm.getData()))
				.findFirst().orElse(new ConfigMapModel(name, profile))
				;

		return Mono.just(configmap);
	}
}
