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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Haytham Mohamed
 **/

@ConfigurationProperties("k8s.informer")
public class ConfigMapInformerProperties {

	private int watcherInterval;
	private String configmapLabelLabel;
	private String configmapLabelEnabled;
	private String configmapLabelProfile;

	public int getWatcherInterval() {
		return watcherInterval;
	}

	public void setWatcherInterval(int watcherInterval) {
		this.watcherInterval = watcherInterval;
	}

	public String getConfigmapLabelLabel() {
		return configmapLabelLabel;
	}

	public void setConfigmapLabelLabel(String configmapLabelLabel) {
		this.configmapLabelLabel = configmapLabelLabel;
	}

	public String getConfigmapLabelEnabled() {
		return configmapLabelEnabled;
	}

	public void setConfigmapLabelEnabled(String configmapLabelEnabled) {
		this.configmapLabelEnabled = configmapLabelEnabled;
	}

	public String getConfigmapLabelProfile() {
		return configmapLabelProfile;
	}

	public void setConfigmapLabelProfile(String configmapLabelProfile) {
		this.configmapLabelProfile = configmapLabelProfile;
	}

}
