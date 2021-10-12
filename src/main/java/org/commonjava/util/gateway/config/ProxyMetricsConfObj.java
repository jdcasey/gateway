package org.commonjava.util.gateway.config;

import io.quarkus.arc.config.ConfigProperties;
import org.commonjava.o11yphant.metrics.conf.ConsoleConfig;
import org.commonjava.o11yphant.metrics.conf.ELKConfig;
import org.commonjava.o11yphant.metrics.conf.GraphiteConfig;
import org.commonjava.o11yphant.metrics.conf.MetricsConfig;
import org.commonjava.o11yphant.metrics.conf.PrometheusConfig;

@ConfigProperties( prefix = "metrics" )
public class ProxyMetricsConfObj implements MetricsConfig
{
    @Override
    public String getNodePrefix()
    {
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public String getReporter()
    {
        return null;
    }

    @Override
    public ConsoleConfig getConsoleConfig()
    {
        return null;
    }

    @Override
    public GraphiteConfig getGraphiteConfig()
    {
        return null;
    }

    @Override
    public PrometheusConfig getPrometheusConfig()
    {
        return null;
    }

    @Override
    public ELKConfig getELKConfig()
    {
        return null;
    }

    @Override
    public int getMeterRatio()
    {
        return 0;
    }
}
