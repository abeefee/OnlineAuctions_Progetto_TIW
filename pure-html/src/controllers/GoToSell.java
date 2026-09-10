package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import dao.AuctionDAO;
import dao.BidDAO;
import dao.ItemDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class GoToSell
 */
@WebServlet("/GoToSell")
public class GoToSell extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToSell() {
        super();
        // TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Date currDate = (Date) session.getAttribute("currentDate");
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		ItemDAO itemDAO = new ItemDAO(connection);
		BidDAO bidDAO = new BidDAO(connection);
		
		List<Auction> openedAuctions = null;
		List<Auction> closedAuctions = null;
		List<Item> notAuctionedItems = null;
		List<Item> itemsOfAuctions = new ArrayList<>();
		List<Bid> maxBids = new ArrayList<>();
		Map<Auction,Long> dateDays = new HashMap<>();
		Map<Auction,Long> dateHours = new HashMap<>();
		
		try {
			openedAuctions = auctionDAO.getOpenedAuctionsByUserId(user.getId_user());
			if(!openedAuctions.isEmpty()) {
				
				int[] ids_auction = new int[openedAuctions.size()];
				for(int i = 0; i < openedAuctions.size(); i++) {
					ids_auction[i] = openedAuctions.get(i).getId_auction();
					Bid bid = bidDAO.getMaxWholeBidByAuctionId(ids_auction[i]);
					if(bid != null) {
						System.out.println(bid.getPrice());
						maxBids.add(bid);
					}
				}

				for(Auction auction : openedAuctions) {
					Date closingDate = auction.getClosing_date();
					long remainingDate = closingDate.getTime() - currDate.getTime();
					if(remainingDate > 0) {
						long diffInDays = TimeUnit.MILLISECONDS.toDays(remainingDate);
						long diffInHours = TimeUnit.MILLISECONDS.toHours(remainingDate) % 24;
						System.out.println(diffInHours);
						dateDays.put(auction, Long.valueOf(diffInDays));
						dateHours.put(auction, Long.valueOf(diffInHours));
					}else {
						dateDays.put(auction,Long.valueOf(0));
						dateHours.put(auction,Long.valueOf(0));
					}
				}
				
				
				itemsOfAuctions.addAll(itemDAO.getWonItemsByAuctionIds(ids_auction));
			}
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
		}
		try {
			closedAuctions = auctionDAO.getClosedAuctionsByUserId(user.getId_user());
			
			if(!closedAuctions.isEmpty()) {
				int[] ids_auction = new int[closedAuctions.size()];
				for(int i = 0; i < closedAuctions.size(); i++) {
					ids_auction[i] = closedAuctions.get(i).getId_auction();
					Bid bid = bidDAO.getMaxWholeBidByAuctionId(ids_auction[i]);
					if(bid != null) {
						System.out.println(bid.getPrice());
						maxBids.add(bid);
					}
				}

				for(Auction auction : closedAuctions) {
					Date closingDate = auction.getClosing_date();
					long remainingDate = closingDate.getTime() - currDate.getTime();
					if(remainingDate > 0) {
						long diffInDays = TimeUnit.MILLISECONDS.toDays(remainingDate);
						long diffInHours = TimeUnit.MILLISECONDS.toHours(remainingDate) % 24;
						System.out.println(diffInHours);
						dateDays.put(auction, Long.valueOf(diffInDays));
						dateHours.put(auction, Long.valueOf(diffInHours));
					}else {
						dateDays.put(auction,Long.valueOf(0));
						dateHours.put(auction,Long.valueOf(0));
					}
				}
				
				itemsOfAuctions.addAll(itemDAO.getWonItemsByAuctionIds(ids_auction));
			}
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
		}
		try {
			notAuctionedItems = itemDAO.getNotAuctionedItemById(user.getId_user());
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
		}
		
		request.setAttribute("openedAuctions", openedAuctions);
		request.setAttribute("closedAuctions", closedAuctions);
		request.setAttribute("notAuctionedItems", notAuctionedItems);
		request.setAttribute("itemsOfAuctions", itemsOfAuctions);
		request.setAttribute("maxBids", maxBids);
		request.setAttribute("dateDays", dateDays);
		request.setAttribute("dateHours", dateHours);
		
		Methods.forward(templateEngine, request, response, Path.pathToSell);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
