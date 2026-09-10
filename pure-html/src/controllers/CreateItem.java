package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.servlet.http.Part;

import org.thymeleaf.TemplateEngine;

import beans.User;
import dao.ItemDAO;
import utils.ConnectionHandler;
import utils.Methods;
import utils.Path;
import utils.TemplateHandler;

/**
 * Servlet implementation class CreateItem
 */
@MultipartConfig
@WebServlet("/CreateItem")
public class CreateItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateItem() {
        super();
    }
    
    @Override
    public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	this.templateEngine  = TemplateHandler.getEngine(servletContext, ".html");
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
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String priceString =  request.getParameter("price");
		String nameString = request.getParameter("name");
		String descriptionString = request.getParameter("description");
		Part filePart = request.getPart("image"); 
		
		if(priceString == null || nameString == null || descriptionString == null || filePart == null || priceString.isEmpty() || nameString.isEmpty() || descriptionString.isEmpty()) {
			Methods.forwardToErrorPage(templateEngine, request, response, "Data missing");
			return;
		}
		
		int price;
		
		try {
			price = Integer.parseInt(priceString);
		} catch (NumberFormatException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, "Data type not numbers");
			return;
		}

		InputStream fileContent = filePart.getInputStream();
		
		String img_name = filePart.getSubmittedFileName();
		String uploadPath = "C:\\Users\\Ablerto Sbidfi\\eclipse-workspace\\TIW_Project_Biffi_Firenze_2023_Pure_HTML\\WebContent\\ImageFolder\\";
		
		File uploadDir = new File(uploadPath);
		if(!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		
		File file = new File(uploadPath + File.separator + img_name);
		
		try (OutputStream outputStream = new FileOutputStream(file)){
			int read;
			final byte[] bytes = new byte[1024];
			
			while((read = fileContent.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		ItemDAO itemDAO = new ItemDAO(conn);
		
		try {
			itemDAO.publishItem(user.getId_user(), price, nameString, descriptionString, img_name);
		} catch(SQLException e) {
			Methods.forwardToErrorPage(templateEngine, request, response, e.getMessage());
		}
		
		response.sendRedirect(getServletContext().getContextPath() + Path.goToSell);
		return;
	}

}
