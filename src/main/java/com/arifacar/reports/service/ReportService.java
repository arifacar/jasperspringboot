package com.arifacar.reports.service;

import com.arifacar.reports.constant.DocumentType;
import com.arifacar.reports.constant.ReportEntity;
import com.arifacar.reports.utils.FileStreamConverter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by arifacar on 7/12/16.
 */

@Service
public class ReportService {

    @Autowired
    FileStreamConverter fileStreamConverter;

    @Autowired
    DataSource dataSource;

    @Value("${mapUrl}")
    private String mapUrl;

    public ResponseEntity<InputStreamResource> prepareResponse(File reportDocument, DocumentType documentType) {
        FileInputStream fileInputStream = fileStreamConverter.getFileInputStream(reportDocument);

        return ResponseEntity
                .ok()
                .contentLength(reportDocument.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "filename=report." + documentType.toString().toLowerCase())
                .body(new InputStreamResource(fileInputStream));
    }

    public File createReport(DocumentType documentType, JasperPrint jasperPrint) {
        File reportDocument = null;

        try {
            switch (documentType) {
                case HTML:
                    reportDocument = File.createTempFile("output.", ".html");
                    JasperExportManager.exportReportToHtmlFile(jasperPrint, reportDocument.getName());
                    break;

                case PDF:
                    reportDocument = File.createTempFile("output.", ".pdf");
                    JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(reportDocument));
                    break;

                case XLSX:
                    reportDocument = File.createTempFile("output.", ".xlsx");
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE, reportDocument);
                    exporter.exportReport();
                    break;

                case CSV:
                    reportDocument = File.createTempFile("output.", ".csv");
                    JRCsvExporter cvsExporter = new JRCsvExporter();
                    cvsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    cvsExporter.setParameter(JRExporterParameter.OUTPUT_FILE, reportDocument);
                    cvsExporter.exportReport();
                    break;

                default:
                    reportDocument = File.createTempFile("output.", ".pdf");
                    JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(reportDocument));
                    break;
            }
        } catch (IOException | JRException e) {
            e.printStackTrace();
        }

        return reportDocument;
    }


    public JasperPrint preparePrint(ReportEntity reportBaseEntity, String jrxmlName, Long entityId) {
        JasperPrint jasperPrint;
        JasperReport jasperReport;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try (InputStream inputStream = JRLoader.getResourceInputStream("jrxml/ireport/" + jrxmlName + ".jrxml")) {
            jasperReport = JasperCompileManager.compileReport(JRXmlLoader.load(inputStream));

            HashMap<String, Object> parameters = new HashMap<>();
            String query;

            switch (reportBaseEntity.toString()) {
                case "COUNTRY":
                    query = "SELECT * FROM " + jrxmlName + " WHERE COUNTRY_ID = ?";
                    break;

                default:
                    query = "SELECT * FROM " + jrxmlName + " WHERE CITY_ID = ?";
                    break;
            }

            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setLong(1, entityId);
            rs = stmt.executeQuery();

            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRResultSetDataSource(rs));

        } catch (IOException | JRException | SQLException e) {
            throw new RuntimeException("Failed to build report", e);
        } finally {
            try {
                if (rs != null)
                    rs.close();

                if (stmt != null)
                    stmt.close();

                if (conn != null)
                    conn.close();

            } catch (Exception ex) {
                Log.error("Resource release exception", ex);
            }
        }

        return jasperPrint;
    }


    public File crosstabReport(ReportEntity reportEntity, Long entityId, DocumentType documentType) {
        JasperPrint jasperPrint = preparePrint(reportEntity, "CROSSTAB_REPORT", entityId);
        return createReport(documentType, jasperPrint);
    }

    public File flatReport(ReportEntity reportEntity, Long entityId, DocumentType documentType) {
        JasperPrint jasperPrint = preparePrint(reportEntity, "FLAT_REPORT", entityId);
        return createReport(documentType, jasperPrint);
    }
}
