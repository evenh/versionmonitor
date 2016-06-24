package net.evenh.versionmonitor.infrastructure.config;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.UUID;

@Component
public class RestConfiguration {
  @Autowired
  private VersionmonitorConfiguration props;

  @Bean
  public RestTemplate okHttpRestTemplate(OkHttpClient client) {
    return new RestTemplate(new OkHttpClientHttpRequestFactory(client));
  }

  @Bean
  public OkHttpClient okHttpClient(Cache cache) {
    return new OkHttpClient().setCache(cache);
  }

  @Bean
  public Cache cache(VersionmonitorConfiguration props) {
    final File cacheDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
    final Integer cacheSize = props.getHttp().getCache().getCachesize();

    return new Cache(cacheDir, cacheSize * 1024 * 1024);
  }
}
