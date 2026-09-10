package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.AuctionDAO;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GetStillOpenedAuctions
 */
@WebServlet("/GetStillOpenedAuctions")
public class GetStillOpenedAuctions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStillOpenedAuctions() {
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        reader.close();
        
        String ids_auctionString = requestBody.toString();
        
        String[] id_auctionArray = ids_auctionString.split(",");
        
        int[] ids_auction = new int[id_auctionArray.length];
        if(id_auctionArray.length > 0) {
        	for(int i = 0; i < id_auctionArray.length; i++) {
            	try {
                	ids_auction[i] = Integer.parseInt(id_auctionArray[i]);
            	} catch(NumberFormatException e) {
            		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
        			response.getWriter().println("Id not number");
        			return;
            	}
            }
        }
        
        AuctionDAO auctionDAO = new AuctionDAO(conn);
        
        String stillOpenedAuction = null;
        String new_ids_auctionString = null;
        
        try {
        	stillOpenedAuction = auctionDAO.getStillOpenedAuction(ids_auction);
        } catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
        
        if(stillOpenedAuction == null) {
        	new_ids_auctionString = "";
        } else {
        	String[] stillOpenedArray = stillOpenedAuction.split(",");
        	boolean primo = true;
        	for(int i = 0; i < id_auctionArray.length; i++) {
        		for(int j = 0; j < stillOpenedArray.length; j++) {
        			if(id_auctionArray[i].equals(stillOpenedArray[j])) {
        				if(primo) {
        					new_ids_auctionString = id_auctionArray[i];
        					primo = false;
        				} else {
        					new_ids_auctionString += "," + id_auctionArray[i];
        				}
        			}
        		}
        	}
        }
        
        response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(new_ids_auctionString);
        
	}

}
