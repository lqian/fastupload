package net.sourceforge.fastupload.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fastupload.DiskFileFactory;
import net.sourceforge.fastupload.HttpFileUploadParser;

/**
 * Servlet for FileUpload
 */
@WebServlet("/FileUpload")
public class FileUpload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		DiskFileFactory dff = new DiskFileFactory(System.getProperty("user.home") + "/", "utf-8");
		long s = System.currentTimeMillis();
		HttpFileUploadParser parser = new HttpFileUploadParser(req, dff);
		parser.parse();
		System.out.format("cost: %d %n", System.currentTimeMillis() - s);
	}
}
