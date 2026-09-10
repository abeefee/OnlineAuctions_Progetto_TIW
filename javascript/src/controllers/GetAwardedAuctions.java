package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import beans.Bid;
import beans.Item;
import beans.User;
import dao.BidDAO;
import dao.ItemDAO;
import packets.PacketAuctions;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GetAwardedAuctions
 */
@WebServlet("/GetAwardedAuctions")
@MultipartConfig
public class GetAwardedAuctions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAwardedAuctions() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		this.conn = ConnectionHandler.getConnection(servletContext);
    }
    
    @Override
    public void destroy() {
		try {
			ConnectionHandler.closeConnection(conn);
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
		
		BidDAO bidDAO = new BidDAO(conn);
		ItemDAO itemDAO = new ItemDAO(conn);
		
		List<Bid> awardedAuctions = null;
		List<Item> awardedItems = null;
		
		try {
			awardedAuctions = bidDAO.getAwardedAuctionsByUserId(user.getId_user());
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		int[] ids_auction = null;
		if(awardedAuctions != null) {
			ids_auction = new int[awardedAuctions.size()];
			for(int i = 0; i < awardedAuctions.size(); i++) {
				ids_auction[i] = awardedAuctions.get(i).getId_auction();
			}
		}
		
		try {
			if(ids_auction.length >= 1) {
				awardedItems = itemDAO.getWonItemsByAuctionIds(ids_auction);				
			}
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		PacketAuctions auctions = new PacketAuctions(awardedAuctions, awardedItems);
			
		String packetAwardedAuction = new Gson().toJson(auctions);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(packetAwardedAuction);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
