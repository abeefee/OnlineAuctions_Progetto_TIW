package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;

import beans.User;
import dao.UserDAO;

import utils.TemplateHandler;
import utils.ConnectionHandler;
import utils.Path;
import utils.Methods;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	templateEngine = TemplateHandler.getEngine(servletContext, ".html");
    	conn = ConnectionHandler.getConnection(servletContext);
    }
    
    @Override
    public void destroy() {
    	try {
    		ConnectionHandler.closeConnection(conn);
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
			request.setAttribute("warning", "Username or password missing");
			Methods.forward(templateEngine, request, response, Path.pathToLogin);
			return;
		}
		
		UserDAO userDAO = new UserDAO(conn);
		User user = null;
		
		try {
			user = userDAO.findUser(username, password);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
		}
		
		if(user == null) {
			request.setAttribute("warning", "Wrong username and/or password");
			Methods.forward(templateEngine, request, response, Path.pathToLogin);
			return;
		}
		
		HttpSession session = request.getSession();
		session.setAttribute("user", user);
		
		Date currentDate = new Date();
		session.setAttribute("currentDate", currentDate);

		Methods.forward(templateEngine, request, response, Path.pathToHome);
	}

}
