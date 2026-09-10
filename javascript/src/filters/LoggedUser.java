package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utils.Path;

/**
 * Servlet Filter implementation class LoggedUser
 */
public class LoggedUser implements Filter {
       
    /**
     * @see HttpFilter#HttpFilter()
     */
    public LoggedUser() {
        super();
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession s = req.getSession();
		
		System.out.println("Filtro logged user");
		
		if(s != null && s.getAttribute("user") != null) {
			System.out.println("Entro if perchè sessione non nulla e utente non nullo");
			chain.doFilter(request, response);
			return;
		}
		
		res.sendRedirect(req.getServletContext().getContextPath() + Path.pathToLoginPage);
	}
}
