package utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

public class Methods {
	public static void forward(TemplateEngine templateEngine, HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException{
		final WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	public static void forwardToErrorPage(TemplateEngine templateEngine, HttpServletRequest request, HttpServletResponse response, String error) throws ServletException, IOException{
		request.setAttribute("error", error);
		forward(templateEngine, request, response, Path.pathToError);
		return;
	}
}
