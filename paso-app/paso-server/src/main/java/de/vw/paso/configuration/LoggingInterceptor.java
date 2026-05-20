package de.vw.paso.configuration;

import java.time.Instant;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

  private final EntityManagerFactory entityManagerFactory;

  public LoggingInterceptor(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    SessionFactory sf = entityManagerFactory.unwrap(SessionFactory.class);
    Statistics statistics = sf.getStatistics();
    statistics.clear();
    statistics.setStatisticsEnabled(true);
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    SessionFactory sf = entityManagerFactory.unwrap(SessionFactory.class);
    Statistics statistics = sf.getStatistics();
    statistics.setStatisticsEnabled(false);
    String logLine = String.format("Executed %s (%s ms / %s queries / %s sql)",
      getFullURL(request),
      Instant.now().toEpochMilli() - statistics.getStart().toEpochMilli(),
      statistics.getQueryExecutionCount(),
      statistics.getPrepareStatementCount());
    LOGGER.info(logLine);
  }
  public static String getFullURL(HttpServletRequest request) {
    StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
    String queryString = request.getQueryString();

    if (queryString == null) {
      return requestURL.toString();
    } else {
      return requestURL.append('?').append(queryString).toString();
    }
  }
}
