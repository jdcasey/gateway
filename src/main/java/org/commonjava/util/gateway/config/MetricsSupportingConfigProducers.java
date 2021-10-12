package org.commonjava.util.gateway.config;

import org.commonjava.cdi.util.weft.config.DefaultWeftConfig;
import org.commonjava.cdi.util.weft.config.WeftConfig;
import org.commonjava.o11yphant.metrics.sli.GoldenSignalsMetricSet;
import org.commonjava.o11yphant.metrics.system.StoragePathProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collection;
import java.util.Collections;

@ApplicationScoped
public class MetricsSupportingConfigProducers
{

    @Produces
    public WeftConfig getWeftConfig()
    {
        return new DefaultWeftConfig();
    }
}
