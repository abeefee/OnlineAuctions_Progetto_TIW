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

import org.thymeleaf.TemplateEngine;

import beans.Auction;
import beans.Bid;
import beans.Item;
import beans.User;
import dao.BidDAO;
import dao.ItemDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class GoToBuy
 */
@WebServlet("/GoToBuy/*")
public class GoToBuy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToBuy() {
        super();
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
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		BidDAO bidDAO = new BidDAO(connection);
		ItemDAO itemDAO = new ItemDAO(connection);
		
		List<Bid> awardedAuctions = null;
		List<Item> awardedItems = null;
		List<Auction> keywordAuctions = null;
		try {
			awardedAuctions = bidDAO.getAwardedAuctionsByUserId(user.getId_user());
		}catch(SQLException e){
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
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
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
		}
		
		try {
			keywordAuctions = (List<Auction>) request.getAttribute("keywordAuctions");
		} catch(Exception e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
		}
		
		request.setAttribute("keywordAuctions", keywordAuctions);
		request.setAttribute("awardedItems", awardedItems);
		request.setAttribute("awardedAuctions", awardedAuctions);
		
		Methods.forward(templateEngine, request, response, Path.pathToBuy);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
