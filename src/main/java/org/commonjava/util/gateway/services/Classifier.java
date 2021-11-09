package org.commonjava.util.gateway.services;

import io.opentelemetry.api.trace.Span;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.commonjava.util.gateway.config.ProxyConfiguration;
import org.commonjava.util.gateway.config.ServiceConfig;
import org.commonjava.util.gateway.exception.ServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@ApplicationScoped
public class Classifier
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    Vertx vertx;

    @Inject
    ProxyConfiguration serviceConfiguration;

    private Map<ServiceConfig, WebClient> clientMap = new ConcurrentHashMap<>();

    public <R> R classifyAnd( String path, HttpServerRequest request,
                              BiFunction<WebClient, ServiceConfig, R> action ) throws Exception
    {
        ServiceConfig service = getServiceConfig( path, request );

        if ( service == null )
        {
            throw new ServiceNotFoundException( "Service not found, path: " + path + ", method: " + request.method() );
        }

        Span.current().setAttribute( "name", "proxy" );
        Span.current().setAttribute( "gateway.target.host", service.host );
        Span.current().setAttribute( "gateway.target.port", service.port );
        Span.current().setAttribute( "gateway.target.method", request.method().name() );
        Span.current().setAttribute( "gateway.target.path", path );

        return action.apply( getWebClient( service ), service );
    }

    private ServiceConfig getServiceConfig( String path, HttpServerRequest request ) throws Exception
    {
        HttpMethod method = request.method();

        ServiceConfig service = null;

        Set<ServiceConfig> services = serviceConfiguration.getServices();
        if ( services != null )
        {
            for ( ServiceConfig sv : services )
            {
                if ( path.matches( sv.pathPattern ) && ( sv.methods == null || sv.methods.contains( method.name() ) ) )
                {
                    service = sv;
                    break;
                }
            }
        }
        return service;
    }

    private WebClient getWebClient( ServiceConfig service ) throws Exception
    {
        return clientMap.computeIfAbsent( service, k -> {
            WebClientOptions options = new WebClientOptions().setDefaultHost( k.host ).setDefaultPort( k.port );
            if ( k.ssl )
            {
                options.setSsl( true ).setVerifyHost( false ).setTrustAll( true );
            }
            return WebClient.create( vertx, options );
        } );
    }
}
