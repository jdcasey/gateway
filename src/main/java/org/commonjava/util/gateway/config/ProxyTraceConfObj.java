package org.commonjava.util.gateway.config;

import io.quarkus.arc.config.ConfigProperties;
import org.commonjava.o11yphant.honeycomb.HoneycombConfiguration;
import org.commonjava.o11yphant.trace.TracerConfiguration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ConfigProperties( prefix = "trace" )
public class ProxyTraceConfObj
                implements TracerConfiguration, HoneycombConfiguration
{
    public Optional<Boolean> enabled;

    @ConfigProperty( name = "console-transport" )
    public Optional<Boolean> consoleTransport;

    @ConfigProperty( name = "honeycomb-dataset" )
    public Optional<String> dataset;

    @ConfigProperty( name = "honeycomb-write-key" )
    public Optional<String> writeKey;

    @ConfigProperty( name = "base-sample-rate" )
    public Optional<Integer> baseSampleRate;

    public Optional<TracerPlugin> tracer = Optional.of( TracerPlugin.honeycomb );

    @Override
    public boolean isEnabled()
    {
        return enabled.isPresent() ? enabled.get() : false;
    }

    @Override
    public boolean isConsoleTransport()
    {
        return consoleTransport.isPresent() ? consoleTransport.get() : true;
    }

    @Override
    public String getWriteKey()
    {
        return writeKey.get();
    }

    @Override
    public String getDataset()
    {
        return dataset.get();
    }

    @Override
    public String getServiceName()
    {
        return "gateway";
    }

    @Override
    public String getNodeId()
    {
        String nodeId = System.getenv("NODE_NAME");
        return nodeId == null ? "unknown_node" : nodeId;
    }

    @Override
    public Integer getBaseSampleRate()
    {
        return baseSampleRate.isPresent() ? baseSampleRate.get() : TracerConfiguration.super.getBaseSampleRate();
    }

    @Override
    public int getSampleRate( Method method )
    {
        return getBaseSampleRate();
    }

    @Override
    public Integer getSampleRate( String classifier )
    {
        return getBaseSampleRate();
    }

    public boolean isValid()
    {
        return tracer.get() == TracerPlugin.honeycomb
                        && dataset.isPresent() && writeKey.isPresent();
    }
}
