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

import com.google.gson.Gson;

import beans.Auction;
import beans.Bid;
import beans.Item;
import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;
import dao.ItemDAO;
import dao.UserDAO;
import packets.PacketAuctionDetails;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GoToDetail
 */
@WebServlet("/GetAuctionDetails")
public class GetAuctionDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAuctionDetails() {
        super();
    }
    
    @Override
    public void init() throws ServletException{
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		String id_auctionString = request.getParameter("id_auction");
		
		if(id_auctionString == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Missing parameter");
			return;
		}
		
		int id_auction;
		
		try {
			id_auction = Integer.parseInt(id_auctionString);
		} catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Id must be a number");
			return;
		}
		
		
		boolean isMine = false;
				
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		BidDAO bidDAO = new BidDAO(connection);
		ItemDAO itemDAO = new ItemDAO(connection);
		
		try {
			isMine = auctionDAO.isMyAuction(user.getId_user(), id_auction);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		if(!isMine) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Not your auction!");
			return;
		}
		
		Auction auction = null;
		List<Bid> bids = null;
		List<Item> items = null;
		
		try {
			auction = auctionDAO.getAuctionDetailsByAuctionId(id_auction);
			bids = bidDAO.getBidsByAuctionId(id_auction);
			items = itemDAO.getItemsByAuctionId(id_auction);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		PacketAuctionDetails detailsPacket = new PacketAuctionDetails(auction, bids, items);
		
		User buyer = null;
		
		if(auction.getAuction_state().equals("closed")) {
			
			UserDAO userDAO = new UserDAO(connection);
			int buyerId;
			
			try {
				buyerId = bidDAO.getBuyerIdByAuctionId(id_auction);
				buyer = userDAO.getUserById(buyerId);
			} catch(SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Internal server error, retry later");
				return;
			}
			
			if(buyer != null) {
				detailsPacket.setBuyer(buyer);
			}
		}

		String auctionDetails = new Gson().toJson(detailsPacket);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(auctionDetails);
		
	}
}
