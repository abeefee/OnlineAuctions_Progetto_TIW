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

import org.thymeleaf.TemplateEngine;

import beans.Bid;
import beans.Item;
import dao.AuctionDAO;
import dao.BidDAO;
import dao.ItemDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class GoToBid
 */
@WebServlet("/GoToAuctionBids/*")
public class GoToAuctionBids extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToAuctionBids() {
        super();
        // TODO Auto-generated constructor stub
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
		String id_auctionString = request.getParameter("id_auction");
		
		if(id_auctionString == null) {
			response.sendRedirect(getServletContext().getContextPath() + Path.goToBuy);
			return;
		}
		
		int id_auction;
		
		try {
			id_auction = Integer.parseInt(id_auctionString);
		} catch(NumberFormatException e) {
			response.sendRedirect(getServletContext().getContextPath() + Path.goToBuy);
			return;
		}
		
		List<Bid> auctionBids = null;
		List<Item> auctionItems = null;
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		BidDAO bidDAO = new BidDAO(connection);
		ItemDAO itemDAO = new ItemDAO(connection);
		
		try {
			if(auctionDAO.isClosed(id_auction) || !auctionDAO.exists(id_auction)) {
				response.sendRedirect(getServletContext().getContextPath() + Path.goToBuy);
				return;
			}
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}	
		
		try {
			auctionBids = bidDAO.getBidsByAuctionId(id_auction);
		}catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}	
		try {
			auctionItems = itemDAO.getItemsByAuctionId(id_auction);
		}catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		
		int maxOffer = 0;
		int starting_bid;
		int bid_increment;
		int minBid = 0;
		
		try {
			maxOffer = bidDAO.getMaxBidByAuctionId(id_auction);
			bid_increment = auctionDAO.getBidIncrementByAuctionId(id_auction);
			starting_bid = auctionDAO.getStartingBidByAuctionId(id_auction);
		}catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		
		if(maxOffer == 0) {
			minBid = starting_bid;
		} else {
			minBid = maxOffer + bid_increment;
		}
		
		String warning = null;
		
		try {
			warning = (String) request.getAttribute("warning");
		} catch(Exception e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
		}
		
		request.setAttribute("minBid", minBid);
		request.setAttribute("warning", warning);
		request.setAttribute("auctionBids", auctionBids);
		request.setAttribute("auctionItems", auctionItems);
		request.setAttribute("id_auction", id_auction);
		
		Methods.forward(templateEngine, request, response, Path.pathToAuctionBids);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
