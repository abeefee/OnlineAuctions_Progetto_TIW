package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;

import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet Filter implementation class NotLoggedUser
 */

public class NotLoggedUser implements Filter {
	
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpFilter#HttpFilter()
     */
    public NotLoggedUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession s = req.getSession();

		if(s != null && s.getAttribute("user") != null) {
			Methods.forward(templateEngine, req, res, Path.pathToHome);
			return;
		}
		
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		ServletContext servletContext = fConfig.getServletContext();
		templateEngine = TemplateHandler.getEngine(servletContext, ".html");
	}

}
