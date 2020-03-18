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
import io.fabric8.kubernetes.client.informers.cache.Lister;
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

	private final ConfigMapInformerProperties properties;
	private final Lister<ConfigMap> lister;

	public WebController(ConfigMapInformerProperties properties, Lister<ConfigMap> lister) {
		this.properties = properties;
		this.lister = lister;
	}

	@GetMapping("/configmaps")
	public Flux<ConfigMapModel> springEnabledConfigMaps() {
		Function<ConfigMap, String> getProfileFunc = (ConfigMap cm) ->
				cm.getMetadata().getLabels().get(properties.getConfigmapLabelProfile()) != null?
						cm.getMetadata().getLabels().get(properties.getConfigmapLabelProfile()):"default";

		Predicate<ConfigMap> isSpringEnabledPredicate = (ConfigMap cm) -> {
			String cfg = cm.getMetadata().getLabels() != null?
					cm.getMetadata().getLabels().containsKey(properties.getConfigmapLabelEnabled())?
							cm.getMetadata().getLabels().get(properties.getConfigmapLabelEnabled()):"false"
					: "false";
			return Boolean.valueOf(cfg.toLowerCase()).booleanValue();
		};

		Set<ConfigMapModel> cmModels = lister.list().stream()
				.filter(isSpringEnabledPredicate::test)
				.map(cm -> new ConfigMapModel(cm.getMetadata().getName()
						,cm.getMetadata().getNamespace()
						, getProfileFunc.apply(cm)
						, cm.getMetadata().getLabels().get(properties.getConfigmapLabelLabel())
						, cm.getData()))
				.collect(Collectors.toSet());

		return Flux.fromStream(cmModels.stream());
	}

	@GetMapping("/configmaps/{name}")
	public Mono<ConfigMapModel> getConfigMapByName(@PathVariable("name") String name) {
		return this.springEnabledConfigMaps()
				.filter(model -> model.getName().equalsIgnoreCase(name))
				.next();
	}

	@GetMapping("/configmaps/{name}/namespaces/{namespace}")
	public Mono<ConfigMapModel> getConfigMapByNameAndByNamespace(@PathVariable("name") String name,
			@PathVariable("namespace") String namespace) {
		return this.springEnabledConfigMaps()
				.filter(model -> model.getName().equalsIgnoreCase(name)
						&& model.getNamespace().equalsIgnoreCase(namespace))
				.next();
	}

	@GetMapping("/configmaps/{name}/profiles/{profile}")
	public Mono<ConfigMapModel> getConfigMapByNameAndByProfile(@PathVariable("name") String name,
			@PathVariable("profile") String profile) {
		return this.springEnabledConfigMaps()
				.filter(model -> model.getName().equalsIgnoreCase(name)
						&& model.getProfile().equalsIgnoreCase(profile))
				.next();
	}

	@GetMapping("/configmaps/{name}/namespaces/{namespace}/profiles/{profile}")
	public Mono<ConfigMapModel> getConfigMap(@PathVariable("name") String name,
			@PathVariable("namespace") String namespace,
			@PathVariable("profile") String profile) {
		return this.springEnabledConfigMaps()
				.filter(model -> model.getName().equalsIgnoreCase(name)
								&& model.getNamespace().equalsIgnoreCase(namespace)
								&& model.getProfile().equalsIgnoreCase(profile))
				.next();
	}

}
