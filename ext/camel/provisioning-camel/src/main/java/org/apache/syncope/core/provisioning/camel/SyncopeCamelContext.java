/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.provisioning.camel;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.core.persistence.api.dao.CamelRouteDAO;
import org.apache.syncope.core.persistence.api.entity.CamelRoute;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SyncopeCamelContext {

    private static final Logger LOG = LoggerFactory.getLogger(SyncopeCamelContext.class);

    @Autowired
    private CamelRouteDAO routeDAO;

    private SpringCamelContext camelContext;

    public CamelContext getCamelContext() {
        synchronized (this) {
            if (camelContext == null) {
                camelContext = ApplicationContextProvider.getBeanFactory().getBean(SpringCamelContext.class);
                camelContext.addRoutePolicyFactory(new MetricsRoutePolicyFactory());
            }

            if (camelContext.getRoutes().isEmpty()) {
                List<CamelRoute> routes = routeDAO.findAll();
                LOG.debug("{} route(s) are going to be loaded ", routes.size());
                loadRouteDefinitions(CollectionUtils.collect(routes, new Transformer<CamelRoute, String>() {

                    @Override
                    public String transform(final CamelRoute input) {
                        return input.getContent();
                    }
                }));
            }

            return camelContext;
        }
    }

    private void loadRouteDefinitions(final Collection<String> routes) {
        try {
            RoutesDefinition routeDefs = camelContext.loadRoutesDefinition(
                    IOUtils.toInputStream("<routes xmlns=\"http://camel.apache.org/schema/spring\">"
                            + StringUtils.join(routes)
                            + "</routes>", StandardCharsets.UTF_8));
            camelContext.addRouteDefinitions(routeDefs.getRoutes());
        } catch (Exception e) {
            LOG.error("While adding route definitions into Camel Context {}", camelContext, e);
            throw new CamelException(e);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateContext(final String routeKey) {
        if (!camelContext.getRouteDefinitions().isEmpty()) {
            camelContext.getRouteDefinitions().remove(camelContext.getRouteDefinition(routeKey));
            loadRouteDefinitions(Arrays.asList(routeDAO.find(routeKey).getContent()));
        }
    }

    public void restoreRoute(final String routeKey, final String routeContent) {
        try {
            camelContext.getRouteDefinitions().remove(camelContext.getRouteDefinition(routeKey));
            loadRouteDefinitions(Arrays.asList(routeContent));
        } catch (Exception e) {
            LOG.error("While restoring Camel route {}", routeKey, e);
            throw new CamelException(e);
        }
    }

    public void restartContext() {
        try {
            camelContext.stop();
            camelContext.start();
        } catch (Exception e) {
            LOG.error("While restarting Camel context", e);
            throw new CamelException(e);
        }
    }

}
