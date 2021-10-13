/**
 * Copyright (C) 2011-2020 Red Hat, Inc. (https://github.com/Commonjava/indy)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.util.gateway;

import org.commonjava.o11yphant.honeycomb.HoneycombTracePlugin;
import org.commonjava.o11yphant.trace.SpanFieldsDecorator;
import org.commonjava.o11yphant.trace.TraceManager;
import org.commonjava.o11yphant.trace.spi.O11yphantTracePlugin;
import org.commonjava.o11yphant.trace.thread.TraceThreadContextualizer;
import org.commonjava.util.gateway.config.ProxyTraceConfObj;
import org.commonjava.util.gateway.config.TracerPlugin;
import org.commonjava.o11yphant.trace.spi.SpanFieldsInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TracingProducer
{
    TraceManager traceManager;

    TraceThreadContextualizer<?> traceThreadContextualizer;

    @Inject
    ProxyTraceConfObj config;

    @Inject
    Instance<SpanFieldsInjector> rsfInstance;

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @PostConstruct
    public void init()
    {
        if ( config.isEnabled() && !config.isValid() )
        {
            logger.error( "Invalid tracer configuration detected!" );
        }

        O11yphantTracePlugin<?> plugin;
//        if ( config.tracer.get() == TracerPlugin.opentelemetry )
//        {
//            logger.info( "Initializing Opentelemetry trace plugin" );
//            plugin = new OtelTracePlugin( config, config );
//        }
//        if ( config.tracer.get() == TracerPlugin.honeycomb )
//        {
            logger.info( "Initializing Honeycomb trace plugin" );

            plugin = new HoneycombTracePlugin( config, config, Optional.empty() );
//        }

        traceManager = new TraceManager<>( plugin, new SpanFieldsDecorator( getRootSpanFields() ), config );
        traceThreadContextualizer = traceManager.getTraceThreadContextualizer();
    }

    @Produces
    @Default
    public TraceThreadContextualizer getTraceThreadContextualizer()
    {
        return traceThreadContextualizer;
    }

    @Produces
    @Default
    public TraceManager getTraceManager()
    {
        return traceManager;
    }

    private List<SpanFieldsInjector> getRootSpanFields()
    {
        List<SpanFieldsInjector> result = new ArrayList<>();
        if ( !rsfInstance.isUnsatisfied() )
        {
            rsfInstance.forEach( result::add );
        }

        logger.trace( "Adding root-span field injectors: {}", result );
        return result;
    }
}
