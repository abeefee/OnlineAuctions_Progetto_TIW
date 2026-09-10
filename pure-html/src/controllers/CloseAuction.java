package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;

import beans.User;
import dao.AuctionDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class CloseAuction
 */
@WebServlet("/CloseAuction")
public class CloseAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CloseAuction() {
        super();
    }

    @Override
    public void init() throws ServletException{
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
		String id_auctionString = request.getParameter("id_auction");
		
		if(id_auctionString == null) {
			response.sendRedirect(getServletContext().getContextPath() + Path.goToSell);
			return;
		}
		
		int id_auction;
		
		try {
			id_auction = Integer.parseInt(id_auctionString);
		} catch(NumberFormatException e) {
			response.sendRedirect(getServletContext().getContextPath() + Path.goToSell);
			return;
		}
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		boolean isMine = false;
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
		try {
			isMine = auctionDAO.isMyAuction(user.getId_user(), id_auction);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		
		if(!isMine) {
			response.sendRedirect(getServletContext().getContextPath() + Path.goToSell);
			return;
		}

		try {
			if(auctionDAO.canBeClosed(id_auction)) {
				auctionDAO.closeAuction(id_auction);
			}else {
				request.setAttribute("warning", "You can't close this auction. You must wait until the closure date!");
				RequestDispatcher dispatcher = request.getRequestDispatcher(Path.goToAuctionDetail + "/?id_auction=" + id_auction);
				dispatcher.forward(request, response);
				return;
			}
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		response.sendRedirect(getServletContext().getContextPath() + Path.goToAuctionDetail + "/?id_auction=" + id_auction);
		return;
	}

}
