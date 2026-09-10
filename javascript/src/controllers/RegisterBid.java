package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;
import utils.ConnectionHandler;

/**
 * Servlet implementation class RegisterBid
 */
@WebServlet("/RegisterBid")
@MultipartConfig
public class RegisterBid extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterBid() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		String priceOfferString = request.getParameter("price");
		String id_auctionString = request.getParameter("id_auction");
		
		if(priceOfferString == null || id_auctionString == null || priceOfferString.isEmpty() || id_auctionString.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}
		
		int price;
		int id_auction;
		
		try {
			price = Integer.parseInt(priceOfferString);
			id_auction = Integer.parseInt(id_auctionString);
		} catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Price must be a number!");
			return;
		}
		
		BidDAO bidDAO = new BidDAO(connection);
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
		boolean isMine = false;
		
		try {
			isMine = auctionDAO.isMyAuction(user.getId_user(), id_auction);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		if(isMine) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("You can't bid for yours auctions!");
			return;
		}
		
		try {
			if(auctionDAO.canBeClosed(id_auction)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("This auction has expired!");
				return;
			}
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
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
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		if(maxOffer == 0) {
			minBid = starting_bid;
		} else {
			minBid = maxOffer + bid_increment;
		}
		if(minBid > price) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("You have to offer more then the current price plus the minimun bid increment");
			return;
		}
		
		try {
			bidDAO.registerBid(user.getId_user(), id_auction, price);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
