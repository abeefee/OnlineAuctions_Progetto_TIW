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
import beans.User;
import dao.AuctionDAO;
import packets.PacketAuctions;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GetClosedAuctions
 */
@WebServlet("/GetClosedAuctions")
public class GetClosedAuctions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetClosedAuctions() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
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
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		AuctionDAO auctionDAO = new AuctionDAO(conn);
		
		List<Auction> closedAuctions = null;
		
		try {
			closedAuctions = auctionDAO.getClosedAuctionsByUserId(user.getId_user());
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		String auctions = new Gson().toJson(new PacketAuctions(closedAuctions, "closed"));
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(auctions);
	}

}
