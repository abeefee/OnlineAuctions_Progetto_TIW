package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

import beans.Auction;
import dao.AuctionDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class SearchByKeyWord
 */
@WebServlet("/SearchByKeyword")
public class SearchByKeyword extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchByKeyword() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	this.templateEngine = TemplateHandler.getEngine(servletContext, ".html");
    	this.connection = ConnectionHandler.getConnection(servletContext);
    }

    @Override
    public void destroy() {
    	try {
    		ConnectionHandler.closeConnection(connection);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String keyword = request.getParameter("keyword");
		
		List<Auction> keywordAuctions = null;
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
		try {
			keywordAuctions = auctionDAO.searchByKeyword(keyword);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		
		request.setAttribute("keywordAuctions", keywordAuctions);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(Path.goToBuy);
		dispatcher.forward(request, response);
	}

}
