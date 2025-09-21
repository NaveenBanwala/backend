package com.social_portfolio_db.demo.naveen.ServicesImp;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.borders.Border;

@Service
public class ResumeService {

    public byte[] generateResume(String username, String bio, java.util.List<String> skills) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Resume of " + username).setBold().setFontSize(20));
            document.add(new Paragraph("Bio: " + bio));
            document.add(new Paragraph("Skills:"));
            for (String skill : skills) {
                document.add(new Paragraph("- " + skill));
            }

            document.close();

            // Add a thin black border around the page
            // (Must be done after closing the document to ensure all content is laid out)
            pdfDoc.addEventHandler(com.itextpdf.kernel.events.PdfDocumentEvent.END_PAGE, new com.itextpdf.kernel.events.IEventHandler() {
                @Override
                public void handleEvent(com.itextpdf.kernel.events.Event event) {
                    com.itextpdf.kernel.events.PdfDocumentEvent docEvent = (com.itextpdf.kernel.events.PdfDocumentEvent) event;
                    com.itextpdf.kernel.pdf.PdfPage page = docEvent.getPage();
                    com.itextpdf.kernel.pdf.canvas.PdfCanvas canvas = new com.itextpdf.kernel.pdf.canvas.PdfCanvas(page);
                    float llx = document.getLeftMargin() / 2;
                    float lly = document.getBottomMargin() / 2;
                    float urx = page.getPageSize().getWidth() - document.getRightMargin() / 2;
                    float ury = page.getPageSize().getHeight() - document.getTopMargin() / 2;
                    canvas.setLineWidth(1f);
                    canvas.rectangle(llx, lly, urx - llx, ury - lly);
                    canvas.stroke();
                }
            });

            pdfDoc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating resume", e);
        }
    }

    public byte[] generateResume(String username, String bio, java.util.List<String> skills, String email, String location, java.util.List<com.social_portfolio_db.demo.naveen.Entity.Projects> projects) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Compact header: Name centered, bold, large, no color
            Paragraph nameHeader = new Paragraph(username)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2f)
                    .setMarginTop(0f);
            document.add(nameHeader);

            // Contact info: inline, centered, small font
            StringBuilder contactLine = new StringBuilder();
            if (email != null && !email.isEmpty()) contactLine.append(email);
            if (location != null && !location.isEmpty()) {
                if (contactLine.length() > 0) contactLine.append(" | ");
                contactLine.append(location);
            }
            if (contactLine.length() > 0) {
                document.add(new Paragraph(contactLine.toString())
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(8f)
                        .setMarginTop(0f));
            }

            // Profile/Bio section
            document.add(new Paragraph("Profile").setBold().setFontSize(12).setMarginBottom(1f).setMarginTop(6f));
            document.add(new Paragraph(bio != null ? bio : "N/A")
                    .setFontSize(10)
                    .setMarginBottom(8f)
                    .setMarginTop(0f));

            // Skills section
            document.add(new Paragraph("Skills").setBold().setFontSize(12).setMarginBottom(1f).setMarginTop(6f));
            if (skills != null && !skills.isEmpty()) {
                int numCols = Math.min(3, skills.size());
                Table skillsTable = new Table(UnitValue.createPercentArray(numCols)).useAllAvailableWidth();
                skillsTable.setBorder(Border.NO_BORDER);
                for (int i = 0; i < skills.size(); i++) {
                    skillsTable.addCell(new Cell().add(new Paragraph(skills.get(i)).setFontSize(10))
                        .setBorder(Border.NO_BORDER)
                        .setPadding(2f)
                        .setTextAlignment(TextAlignment.LEFT));
                }
                // Fill last row if needed
                int remainder = skills.size() % numCols;
                if (remainder != 0) {
                    for (int i = 0; i < numCols - remainder; i++) {
                        skillsTable.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(2f));
                    }
                }
                document.add(skillsTable.setMarginBottom(8f));
            } else {
                document.add(new Paragraph("No skills listed.").setFontSize(10).setMarginBottom(8f));
            }

            // Projects section
            document.add(new Paragraph("Projects").setBold().setFontSize(12).setMarginBottom(1f).setMarginTop(6f));
            if (projects != null && !projects.isEmpty()) {
                for (com.social_portfolio_db.demo.naveen.Entity.Projects project : projects) {
                    Paragraph projectTitle = new Paragraph(project.getTitle() != null ? project.getTitle() : "Untitled")
                        .setBold().setFontSize(11).setMarginBottom(0f).setMarginTop(2f);
                    document.add(projectTitle);
                    if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                        document.add(new Paragraph(project.getDescription()).setFontSize(10).setMarginBottom(0f).setMarginTop(0f));
                    }
                    if (project.getCreatedAt() != null) {
                        document.add(new Paragraph("Created: " + project.getCreatedAt().toString())
                            .setFontSize(9).setMarginBottom(2f).setMarginTop(0f));
                    }
                }
            } else {
                document.add(new Paragraph("No projects listed.").setFontSize(10).setMarginBottom(0f));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating detailed resume", e);
        }
    }
}
