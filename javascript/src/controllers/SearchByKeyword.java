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

import com.google.gson.Gson;

import beans.Auction;
import dao.AuctionDAO;
import packets.PacketAuctions;
import utils.ConnectionHandler;

/**
 * Servlet implementation class SearchByKeyword
 */
@WebServlet("/SearchByKeyword")
@MultipartConfig
public class SearchByKeyword extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchByKeyword() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyword = request.getParameter("keyword");
		
		if(keyword == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("keyword must be not null");
			return;
		}
		
		List<Auction> keywordAuctions = null;
		AuctionDAO auctionDAO = new AuctionDAO(conn);
		
		try {
			keywordAuctions = auctionDAO.searchByKeyword(keyword);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		String auctions = new Gson().toJson(new PacketAuctions(keywordAuctions, "keyword"));
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(auctions);
	}

}
