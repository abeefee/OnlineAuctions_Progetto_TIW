package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import dao.ItemDAO;
import utils.ConnectionHandler;

/**
 * Servlet implementation class CreateAuction
 */
@WebServlet("/CreateAuction")
@MultipartConfig
public class CreateAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateAuction() {
        super();
        // TODO Auto-generated constructor stub
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		String bidIncrementString = request.getParameter("bid_increment");
		String closingDateString = request.getParameter("closing_date");
		String[] ids_itemString = request.getParameterValues("ids_item");
		
		int[] ids_item = null;
		if(ids_itemString != null) {
			ids_item = new int[ids_itemString.length];
			for(int i = 0; i < ids_itemString.length; i++) {
				ids_item[i] = Integer.parseInt(ids_itemString[i]);
			}
		}
				
		if(bidIncrementString == null || closingDateString == null || ids_itemString == null || bidIncrementString.isEmpty() || closingDateString.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Missing parameter!");
			return;
		}
		
		boolean allItemMine = false;
		
		ItemDAO itemDAO = new ItemDAO(conn);
		
		try {
			allItemMine = itemDAO.allItemMine(user.getId_user(), ids_item);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		if(!allItemMine) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("you don't have permission!");
			return;
		}
		
		int bidIncrement;
		
		try {
			bidIncrement = Integer.parseInt(bidIncrementString);
		} catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("data type not number!");
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date closingDate = null;
		
		try{
			closingDate = dateFormat.parse(closingDateString);
		} catch(ParseException e) {
			e.printStackTrace();
			return;
		}
		
		AuctionDAO auctionDAO = new AuctionDAO(conn);
		
		try {
			auctionDAO.createAuction(user.getId_user(), bidIncrement, closingDate, ids_item);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);	
	}

}
