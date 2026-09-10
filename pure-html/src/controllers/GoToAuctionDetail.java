package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;

import beans.Auction;
import beans.Bid;
import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;
import dao.UserDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class GoToDetail
 */
@WebServlet("/GoToAuctionDetail/*")
public class GoToAuctionDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToAuctionDetail() {
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
		BidDAO bidDAO = new BidDAO(connection);
		
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
		
		Auction auction = null;
		List<Bid> bids = null;
		
		try {
			auction = auctionDAO.getAuctionDetailsByAuctionId(id_auction);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		try {
			bids = bidDAO.getBidsByAuctionId(id_auction);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		
		if(auction.getAuction_state().equals("closed")) {
			User buyer = null;
			UserDAO userDAO = new UserDAO(connection);
			
			int buyerId;
			
			try {
				buyerId = bidDAO.getBuyerIdByAuctionId(id_auction);
				buyer = userDAO.getUserById(buyerId);
			} catch(SQLException e) {
				Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
				return;
			}
			request.setAttribute("buyer", buyer);
		}
		
		request.setAttribute("auction", auction);
		request.setAttribute("bids", bids);
		
		Methods.forward(templateEngine, request, response, Path.pathToAuctionDetail);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
