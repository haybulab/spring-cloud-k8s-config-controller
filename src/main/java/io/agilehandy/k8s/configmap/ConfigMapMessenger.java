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


import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author Haytham Mohamed
 **/

@Component
public class ConfigMapMessenger {

	private final RestTemplate restTemplate;

	public ConfigMapMessenger(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void publish(ConfigMap configMap) {
		String uri = "localhost:8080/actuator/bus-refresh";
		HttpEntity<Map<String, String>> request =
				new HttpEntity(configMap.getData(), null);
		restTemplate.postForLocation(uri, request);
	}


}
