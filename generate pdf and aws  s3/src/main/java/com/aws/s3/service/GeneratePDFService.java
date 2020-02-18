package com.aws.s3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class GeneratePDFService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(GeneratePDFService.class);

	@Value("${spring.datasource.url}") // Taking URL from application.properties file
	private String springdatasourceurl;

	@Value("${spring.datasource.username}") // Taking UserName from application.properties file
	private String springdatasourceusername;

	@Value("${spring.datasource.password}") // Taking Password from application.properties file
	private String springdatasourcepassword;

	@Value("${spring.datasource.driverClassName}") // Taking DriverClass from application.properties file
	private String springdatasourcedriverClassName;

	public String generatePDFFromDB(String name) {

		File file = new File(name + ".pdf");
		if (!file.exists()) {

			Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 100f, 0f);
			try {
				PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(name + ".pdf"));
				document.open();
				Class.forName(springdatasourcedriverClassName);
				Connection connection = DriverManager.getConnection(springdatasourceurl, springdatasourceusername,
						springdatasourcepassword);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(
						"SELECT Sr_no, communicationType, createdBy, fromEmail, fromPassword, toEmail, `subject`, message, attachment, createdDate, `status`  from communicationenginehistory");
				java.sql.ResultSetMetaData data = resultSet.getMetaData();

				int columnCount = data.getColumnCount();

				PdfPTable table = new PdfPTable(columnCount);

				for (int i = 1; i <= columnCount; i++) {
					String columnName = data.getColumnName(i);
					table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
					PdfPCell col = new PdfPCell(new Paragraph(columnName));
					col.setBackgroundColor(BaseColor.GRAY);
					col.setPaddingLeft(2);
					col.setPaddingRight(2);
					table.setSpacingBefore(0f);
					table.setSpacingAfter(0f);
					table.addCell(col);
				}
				while (resultSet.next()) {
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("Sr_no").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("communicationType").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("createdBy").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("fromEmail").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("fromPassword").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("toEmail").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("subject").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("message").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("attachment").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("createdDate").toString())));
					table.addCell(new PdfPCell(new Paragraph(resultSet.getString("status").toString())));
				}
				document.add(table);
				document.close();
				pdfWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return "{\"status\": \"Success\",\"reason\": \"SuccesFully Generate PDF\"}";

		} else {
			return "{\"status\": \"Success\",\"reason\": \"File Already Exist\"}";
		}

	}

}
