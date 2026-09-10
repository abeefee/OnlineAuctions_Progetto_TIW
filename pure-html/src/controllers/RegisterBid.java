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
import dao.BidDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class RegisterBid
 */
@WebServlet("/RegisterBid")
public class RegisterBid extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterBid() {
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
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		String priceOfferString = request.getParameter("price");
		String id_auctionString = request.getParameter("id_auction");
		
		if(priceOfferString == null || id_auctionString == null || priceOfferString.isEmpty() || id_auctionString.isEmpty()) {
			Methods.forwardToErrorPage(templateEngine, request, response, "Some data are missing");
			return;
		}
		
		int price;
		int id_auction;
		
		try {
			price = Integer.parseInt(priceOfferString);
			id_auction = Integer.parseInt(id_auctionString);
		} catch(NumberFormatException e) {
			response.sendRedirect(getServletContext().getContextPath() + Path.goToBuy);
			return;
		}
		
		BidDAO bidDAO = new BidDAO(connection);
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
		boolean isMine = false;
		
		try {
			isMine = auctionDAO.isMyAuction(user.getId_user(), id_auction);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		
		if(isMine) {
			request.setAttribute("warning", "You can't bid for your auctions");
			RequestDispatcher dispatcher = request.getRequestDispatcher(Path.goToAuctionBids + "/?id_auction=" + id_auction);
			dispatcher.forward(request, response);
			return;
		}
		
		try {
			if(auctionDAO.canBeClosed(id_auction)) {
				request.setAttribute("warning", "This auction expired");
				RequestDispatcher dispatcher = request.getRequestDispatcher(Path.goToAuctionBids + "/?id_auction=" + id_auction);
				dispatcher.forward(request, response);
				return;
			}
		} catch(SQLException e) {
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
		if(minBid > price) {
			request.setAttribute("warning", "You have to offer more then the current price plus the minimun bid increment");
			RequestDispatcher dispatcher = request.getRequestDispatcher(Path.goToAuctionBids + "/?id_auction=" + id_auction);
			dispatcher.forward(request, response);
			return;
		}
		
		try {
			bidDAO.registerBid(user.getId_user(), id_auction, price);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + Path.goToAuctionBids + "/?id_auction=" + id_auction);
	}

}
