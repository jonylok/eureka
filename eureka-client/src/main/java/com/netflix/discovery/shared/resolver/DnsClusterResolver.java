/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.discovery.shared.resolver;

import java.util.Collections;
import java.util.List;

import com.netflix.discovery.DnsResolver;

/**
 * Resolves cluster addresses from DNS. If the provided name contains only CNAME entry, the cluster server pool
 * contains effectively single item, that may however resolve to different IPs. This is not recommended configuration.
 * In the configuration where DNS name points to A record, all IPs from that record are loaded. This resolver
 * is not zone aware.
 *
 * @author Tomasz Bak
 */
public class DnsClusterResolver implements ClusterResolver {

    private final List<EurekaEndpoint> eurekaEndpoints;

    /**
     * @param rootClusterDNS cluster DNS name containing CNAME or A record.
     * @param port Eureka sever port number
     * @param relativeUri service relative URI that will be appended to server address
     */
    public DnsClusterResolver(String rootClusterDNS, int port, boolean isSecure, String relativeUri) {
        List<String> addresses = DnsResolver.resolveARecord(rootClusterDNS);
        if (addresses == null) {
            this.eurekaEndpoints = Collections.singletonList(new EurekaEndpoint(rootClusterDNS, port, isSecure, relativeUri, null));
        } else {
            this.eurekaEndpoints = EurekaEndpoint.createForServerList(addresses, port, isSecure, relativeUri, null);
        }
    }

    @Override
    public List<EurekaEndpoint> getClusterEndpoints() {
        return eurekaEndpoints;
    }

    @Override
    public void shutdown() {
    }
}
