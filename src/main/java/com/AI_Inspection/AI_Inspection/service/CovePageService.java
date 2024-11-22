package com.AI_Inspection.AI_Inspection.service;

import com.AI_Inspection.AI_Inspection.entity.DocumentDetails;
import com.AI_Inspection.AI_Inspection.repo.DocumentDetailsRepo;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
public class CovePageService {

    @Autowired
    private DocumentDetailsRepo documentDetailsRepo;


    public Tbl createCustomTable(WordprocessingMLPackage wordMLPackage) throws Exception {

        DocumentDetails documentDetails = documentDetailsRepo.findTopByOrderByIdDesc();

        P image = createImageParagraph(wordMLPackage, "src/main/resources/static/coverPage.png", 4572000, 2708800);
        // Set alignment to the right
        PPr paragraphProperties = Context.getWmlObjectFactory().createPPr();
        Jc alignment = Context.getWmlObjectFactory().createJc();
        alignment.setVal(JcEnumeration.RIGHT);
        paragraphProperties.setJc(alignment);
        image.setPPr(paragraphProperties);
        wordMLPackage.getMainDocumentPart().getContent().add(image);

        // Create the table
        Tbl table = Context.getWmlObjectFactory().createTbl();

        // Set the table properties to add a border and make it full width
        table.setTblPr(new TblPr());
        TblWidth tblWidth = Context.getWmlObjectFactory().createTblWidth();
        tblWidth.setW(BigInteger.valueOf(10000)); // Full width (twips: 1/20 of a point)
        tblWidth.setType("dxa");
        table.getTblPr().setTblW(tblWidth);


        // First row: First column (spanning) and second column
        Tr firstRow = Context.getWmlObjectFactory().createTr();

        Tc firstCell = Context.getWmlObjectFactory().createTc();
        firstCell.getContent().add(createImageParagraph(wordMLPackage,"src/main/resources/static/logo.png",756000, 273600));

        firstRow.getContent().add(firstCell);
        addCellToRow(firstRow, documentDetails.getDocumentName(),"FF0000");
        table.getContent().add(firstRow);

        Tr row2 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row2, "", "");
        addCellToRow(row2, "Emplazamiento", "000000");
        table.getContent().add(row2);

        Tr row3 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row3, "", "");
        addCellToRow(row3, documentDetails.getAddress(), "FF0000");
        table.getContent().add(row3);


        Tr row4 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row4, "", "");
        addCellToRow(row4, "Cliente", "000000");
        table.getContent().add(row4);

        Tr row5 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row5, "", "");
        addCellToRow(row5, "PROLOGIS SPAIN XX", "FF0000");
        table.getContent().add(row5);

        Tr row6 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row6, "", "");
        addCellToRow(row6, "Autor", "000000");
        table.getContent().add(row6);

        Tr row7 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row7, "", "");
        addCellToRow(row7, "Emilio Fernández Cervilla - Arquitecto Colegiado COAC 32.932/0 – COAM 65.355\n", "FF0000");
        table.getContent().add(row7);

        Tr row8 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row8, "", "");
        addCellToRow(row8, "ZAGA Management SL. Av. Diagonal 484, 4º2ª B. 08006 Barcelona. www.zaga.pro", "FF0000");
        table.getContent().add(row8);

        Tr row9 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row9, "", "");
        addCellToRow(row9, "Fecha", "000000");
        table.getContent().add(row9);

        LocalDate currentDate = LocalDate.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        // Format the current date
        String formattedDate = currentDate.format(formatter);

        Tr row10 = Context.getWmlObjectFactory().createTr();
        addCellToRow(row10, "", "");
        addCellToRow(row10, formattedDate, "FF0000");
        table.getContent().add(row10);

        return table;
    }


    public static P createImageParagraph(WordprocessingMLPackage wordMLPackage, String imagePath, int width, int height) throws Exception {
        P paragraph = Context.getWmlObjectFactory().createP();

        // Load the image as a BinaryPartAbstractImage
        byte[] imageBytes = new FileInputStream(imagePath).readAllBytes();
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);

        // Image inline
        Inline inline = imagePart.createImageInline(null, null, 0, 1, width,height, false);

        // Add the image to the paragraph
        R run = Context.getWmlObjectFactory().createR();
        ObjectFactory factory = new ObjectFactory();
        org.docx4j.wml.Drawing drawing = factory.createDrawing();

        drawing.getAnchorOrInline().add(inline);
        run.getContent().add(drawing);
        paragraph.getContent().add(run);

        return paragraph;
    }

    private static void addCellToRow(Tr row, String content, String color) {
        Tc cell = Context.getWmlObjectFactory().createTc();
        P paragraph = Context.getWmlObjectFactory().createP();
        Text text = Context.getWmlObjectFactory().createText();
        text.setValue(content);
        R run = Context.getWmlObjectFactory().createR();
        run.getContent().add(text);
        run.setRPr(new RPr());
        setTextColor(run, color);
        paragraph.getContent().add(run);
        cell.getContent().add(paragraph);
        row.getContent().add(cell);
    }

    private static void setTextColor(R run, String colorHex) {
        // Create a color element for the run and set the text color
        Color color = Context.getWmlObjectFactory().createColor();
        color.setVal(colorHex); // Set color in hex (e.g., FF0000 for red)

        // Apply the color to the run
        run.getRPr().setColor(color);
    }


}
