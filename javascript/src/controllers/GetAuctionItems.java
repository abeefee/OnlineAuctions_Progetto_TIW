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

import com.google.gson.Gson;

import beans.Item;
import dao.AuctionDAO;
import dao.ItemDAO;
import packets.PacketItems;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GetAuctionItems
 */
@WebServlet("/GetAuctionItems")
public class GetAuctionItems extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAuctionItems() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
		List<Item> auctionedItems = null;
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		ItemDAO itemDAO = new ItemDAO(connection);
		
		try {
			if(auctionDAO.isClosed(id_auction)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("This auction has expired!");
				return;
			}
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		try {
			auctionedItems = itemDAO.getItemsByAuctionId(id_auction);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		String items = new Gson().toJson(new PacketItems(auctionedItems));
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(items);
		
	}
}
