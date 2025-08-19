package com.mc.scl.logger;

import com.mc.scl.util.VariablesConstant;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.mc.scl.logger.AppLogger;
import com.mc.scl.logger.model.LogLayout;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Configuration
public class LoggingHandler {

	@After("execution(* com.mc.*.*.controller.*.*(..)))")
	public void after(JoinPoint joinPoint) {
		try {
			// Get the current request in a thread-safe way
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletRequest request = attributes.getRequest();

			// Capture request body for POST, PUT, PATCH methods
		String httpMethod = request.getMethod();
		if (("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod)) 
			&& joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
			Object argObjValue = joinPoint.getArgs()[0];
			if (Objects.nonNull(argObjValue)) {
				request.setAttribute(VariablesConstant.REQUEST, argObjValue.toString());
				Thread.currentThread().setName(argObjValue.toString());
			}
		}
		} catch (Exception e) {
		LogLayout logLayout = new LogLayout(
				VariablesConstant.ERROR,
			"LoggingHandler",
			"shared-code-library",
			this.getClass().getSimpleName(),
			"after",
			"",
			"",
			"",
			"Failed to process request logging: " + e.getMessage()
		);
		AppLogger appLogger = new AppLogger(null);
		appLogger.writeLog(logLayout, e, null);
	}
	}
}
