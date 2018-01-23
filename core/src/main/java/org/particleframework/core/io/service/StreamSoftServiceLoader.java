/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.core.io.service;

import org.particleframework.core.reflect.ClassUtils;
import org.particleframework.core.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

/**
 * Variation of {@link SoftServiceLoader} that returns a stream instead of an iterable thus allowing parallel loading etc.
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public class StreamSoftServiceLoader {

    @SuppressWarnings("unchecked")
    public static <T> Stream<ServiceDefinition<T>> loadParallel(Class<T> serviceType, ClassLoader classLoader) {
        Enumeration<URL> serviceConfigs;
        String name = serviceType.getName();
        try {
            serviceConfigs = classLoader.getResources(SoftServiceLoader.META_INF_SERVICES + '/' + name);
        } catch (IOException e) {
            throw new ServiceConfigurationError("Failed to load resources for service: " + name, e);
        }
        Set<URL> urlSet = CollectionUtils.enumerationToSet(serviceConfigs);

        return urlSet
                .stream()
                .parallel()
                .flatMap(url -> {
                    List<String> lines = new ArrayList<>();
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                                String line = reader.readLine();
                                while(line != null) {
                                    if( line.length() != 0 && line.charAt(0) != '#' ) {
                                        int i = line.indexOf('#');
                                        if (i > -1) {
                                            line = line.substring(0, i);
                                        }
                                        lines.add(line);
                                    }
                                    line = reader.readLine();
                                }
                            } catch (IOException e) {
                                throw new ServiceConfigurationError("Failed to load resources for URL: " + url, e);
                            }
                            return lines.stream();
                        }
                ).map(serviceName -> {
            Optional<Class> loadedClass = ClassUtils.forName(serviceName, classLoader);
            return new DefaultServiceDefinition(name, loadedClass);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> loadPresentParallel(Class<T> serviceType, ClassLoader classLoader) {
        return loadParallel(serviceType, classLoader)
                    .filter(ServiceDefinition::isPresent)
                    .map(ServiceDefinition::load);
    }
}
