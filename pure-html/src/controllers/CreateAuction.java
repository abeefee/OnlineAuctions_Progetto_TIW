package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import dao.ItemDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class CreateAuction
 */
@WebServlet("/CreateAuction")
public class CreateAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateAuction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	this.templateEngine = TemplateHandler.getEngine(servletContext, ".html");
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
		// TODO Auto-generated method stub
		doPost(request, response);
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
			Methods.forwardToErrorPage(templateEngine, request, response, "Some data are missing");
			return;
		}
		
		boolean allItemMine = false;
		
		ItemDAO itemDAO = new ItemDAO(conn);
		
		try {
			allItemMine = itemDAO.allItemMine(user.getId_user(), ids_item);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}
		
		if(!allItemMine) {
			Methods.forwardToErrorPage(templateEngine, request, response, "You don't have permission");
			return;
		}
		
		int bidIncrement;
		
		try {
			bidIncrement = Integer.parseInt(bidIncrementString);
		} catch(NumberFormatException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, "Data type not numbers");
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date closingDate = null;
		
		try{
			System.out.println(closingDateString);
			closingDate = dateFormat.parse(closingDateString);
			System.out.println(closingDate);
		} catch(ParseException e) {
			e.printStackTrace();
			return;
		}
		
		AuctionDAO auctionDAO = new AuctionDAO(conn);
		
		try {
			auctionDAO.createAuction(user.getId_user(), bidIncrement, closingDate, ids_item);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
			return;
		}

		response.sendRedirect(getServletContext().getContextPath() + Path.goToSell);
		return;
	}

}
