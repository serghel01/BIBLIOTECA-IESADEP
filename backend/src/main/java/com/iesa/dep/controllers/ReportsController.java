
package com.iesa.dep.controllers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @GetMapping("/monthly")
    public ResponseEntity<byte[]> monthly(@RequestParam("month") String month){
        try{
            // Datos simulados (en producción calcular desde Firestore)
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(40, "Préstamos", "Semana 1");
            dataset.addValue(55, "Préstamos", "Semana 2");
            dataset.addValue(30, "Préstamos", "Semana 3");
            dataset.addValue(70, "Préstamos", "Semana 4");

            JFreeChart chart = ChartFactory.createBarChart("Préstamos del mes "+month, "Semana", "Cantidad", dataset);
            BufferedImage img = chart.createBufferedImage(600,400);

            ByteArrayOutputStream chartBaos = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(chartBaos, img);

            // Crear PDF con PDFBox
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, chartBaos.toByteArray(), "chart");
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.drawImage(pdImage, 50, 250, 500, 300);
            content.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            doc.close();

            byte[] bytes = baos.toByteArray();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="reporte-"+month+".pdf"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(bytes);
        }catch(Exception e){
            String txt = "Error generando reporte: " + e.getMessage();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="reporte-"+month+".pdf"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(txt.getBytes());
        }
    }
}
