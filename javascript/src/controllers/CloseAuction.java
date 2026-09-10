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
import utils.ConnectionHandler;

/**
 * Servlet implementation class CloseAuction
 */
@WebServlet("/CloseAuction")
@MultipartConfig
public class CloseAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CloseAuction() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			response.getWriter().println("Data type not number");
			return;
		}
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		boolean isMine = false;
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
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

		try {
			if(auctionDAO.canBeClosed(id_auction)) {
				auctionDAO.closeAuction(id_auction);
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("This auction has not expired yet!");
				return;
			}
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);	
	}

}
