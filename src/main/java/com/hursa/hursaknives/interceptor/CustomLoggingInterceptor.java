package com.hursa.hursaknives.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CustomLoggingInterceptor implements HandlerInterceptor {
  private static final Logger log = LoggerFactory.getLogger(CustomLoggingInterceptor.class);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String logInfo =
        String.format(
            "[preHandle][%s][%s][%s][%s][%s]",
            request,
            request.getMethod(),
            request.getRequestURI(),
            getParameters(request),
            getRemoteAddr(request));

    if (!("GET").equals(request.getMethod())) {
      log.info(logInfo);
    } else {
      log.debug(logInfo);
    }

    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {
    String logInfo =
        String.format(
            "[postHandle][%s][%s][%s][%s][%s]",
            request,
            request.getMethod(),
            request.getRequestURI(),
            getParameters(request),
            getRemoteAddr(request));

    if (!("GET").equals(request.getMethod())) {
      log.info(logInfo);
    } else {
      log.debug(logInfo);
    }
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    if (ex != null) {
      log.error("[afterCompletion][" + request + "][exception: " + ex + "]");
    }

    String logInfo =
        String.format(
            "[afterCompletion][%s][%s][%s][%s][%s]",
            request,
            request.getMethod(),
            request.getRequestURI(),
            getParameters(request),
            getRemoteAddr(request));

    if (!("GET").equals(request.getMethod())) {
      log.info(logInfo);
    } else {
      log.debug(logInfo);
    }
  }

  private String getParameters(HttpServletRequest request) {
    StringBuilder posted = new StringBuilder();
    Enumeration<?> e = request.getParameterNames();
    if (e != null) {
      posted.append("?");
    }
    while (e != null && e.hasMoreElements()) {
      if (posted.length() > 1) {
        posted.append("&");
      }
      String curr = (String) e.nextElement();
      posted.append(curr).append("=");
      if (curr.contains("password")
          || curr.contains("pass")
          || curr.contains("pwd")
          || curr.contains("csrf")) {
        posted.append("*****");
      } else {
        posted.append(request.getParameter(curr));
      }
    }
    String ip = request.getHeader("X-FORWARDED-FOR");
    String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
    if (ipAddr != null && !ipAddr.isEmpty()) {
      posted.append("&_psip=").append(ipAddr);
    }
    return posted.toString();
  }

  private String getRemoteAddr(HttpServletRequest request) {
    String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
    if (ipFromHeader != null && !ipFromHeader.isEmpty()) {
      log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
      return ipFromHeader;
    }
    return request.getRemoteAddr();
  }
}
