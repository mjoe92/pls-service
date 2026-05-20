package de.vw.paso.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
@EnableWebMvc
public class PasoConfiguration implements AsyncConfigurer, WebMvcConfigurer {

  private final UserInterceptor userInterceptor;
  private final LoggingInterceptor loggingInterceptor;

  PasoConfiguration(UserInterceptor userInterceptor, LoggingInterceptor loggingInterceptor) {
    this.userInterceptor = userInterceptor;
    this.loggingInterceptor = loggingInterceptor;
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(userInterceptor);
    registry.addInterceptor(loggingInterceptor);
  }
}
