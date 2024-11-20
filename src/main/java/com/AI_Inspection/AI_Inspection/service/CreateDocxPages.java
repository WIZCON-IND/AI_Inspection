package com.AI_Inspection.AI_Inspection.service;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

@Service
public class CreateDocxPages {
    private int captionNumber = 1;

    public File createdocx() {
        File tempFile = null;
        try {
            // Create a new WordprocessingMLPackage
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

            // Get the main document part
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

            // Add content for Page 1
//            addPageContent(mainDocumentPart, "Page 1 Content...");
            // Add images for Page 1
//            addImage(wordMLPackage, "src/main/resources/static/coverPage.png");
//
//            // Add a page break
//            mainDocumentPart.addObject(createPageBreak());

            // Add content for Page 11
//            addPageContent(mainDocumentPart, "Page 11 Content...");

            Tbl table = createTableWithImages(wordMLPackage);
            mainDocumentPart.addObject(table);
            Tbl captiontable = createTableWithCenteredText(captionNumber, captionNumber+1);
            mainDocumentPart.addObject(captiontable);

            P emptyParagraph = Context.getWmlObjectFactory().createP();
            mainDocumentPart.addObject(emptyParagraph);

            Tbl table2 = createTableWithImages(wordMLPackage);
            mainDocumentPart.addObject(table2);
            Tbl captiontable2 = createTableWithCenteredText(captionNumber+2, captionNumber+3);
            mainDocumentPart.addObject(captiontable2);
            captionNumber = captionNumber+4;

            // Add images for Page 11
//            addImage(wordMLPackage, "src/main/resources/static/logo.png");

            tempFile = File.createTempFile("GeneratedDocument", ".docx");
            wordMLPackage.save(tempFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    private static void addPageContent(MainDocumentPart mainDocumentPart, String content) {
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        Text text = factory.createText();
        text.setValue(content);
        run.getContent().add(text);
        paragraph.getContent().add(run);
        mainDocumentPart.addObject(paragraph);
    }

    private static P createPageBreak() {
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        paragraph.getContent().add(run);
        run.getContent().add(factory.createBr());
        return paragraph;
    }

    private static void addImage(WordprocessingMLPackage wordMLPackage, String imagePath) throws Exception {
        // Assuming imagePath contains the path to the image file
        File file = new File(imagePath);
        byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());

        org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage imagePart =
                org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

        int id1 = 1;
        int id2 = 2;
        org.docx4j.dml.wordprocessingDrawing.Inline inline = imagePart.createImageInline("Filename hint", "Alternative text", id1, id2, false);

        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        org.docx4j.wml.Drawing drawing = factory.createDrawing();
        drawing.getAnchorOrInline().add(inline);
        R run = factory.createR();
        run.getContent().add(drawing);
        paragraph.getContent().add(run);

        wordMLPackage.getMainDocumentPart().addObject(paragraph);
    }

    public static Tbl createTableWithImages(WordprocessingMLPackage wordMLPackage) throws Exception {
        // Create the table
        Tbl table = Context.getWmlObjectFactory().createTbl();

        // Add the table row
        Tr row = Context.getWmlObjectFactory().createTr();
        table.getContent().add(row);

        // Add first cell with image
        Tc cell1 = createImageCell(wordMLPackage, "src/main/resources/static/coverPage.png");
        row.getContent().add(cell1);

        // Add second cell with image
        Tc cell2 = createImageCell(wordMLPackage,"src/main/resources/static/coverPage.png");
        row.getContent().add(cell2);

        // Set table borders
        setTableBorders(table);

        return table;
    }

    // Method to create a cell with an image
    public static Tc createImageCell(WordprocessingMLPackage wordMLPackage, String imagePath) throws Exception {
        // Create a new cell
        Tc cell = Context.getWmlObjectFactory().createTc();

        // Add the image to the cell
        P p = createImageParagraph(wordMLPackage, imagePath);
        cell.getContent().add(p);

        return cell;
    }

    // Method to create a paragraph with an image
    public static P createImageParagraph(WordprocessingMLPackage wordMLPackage, String imagePath) throws Exception {
        P paragraph = Context.getWmlObjectFactory().createP();

        // Load the image as a BinaryPartAbstractImage
        byte[] imageBytes = new FileInputStream(imagePath).readAllBytes();
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);

        // Image inline
        Inline inline = imagePart.createImageInline(null, null, 0, 1, 2772000,2088000, false);

        // Add the image to the paragraph
        R run = Context.getWmlObjectFactory().createR();
        ObjectFactory factory = new ObjectFactory();
        org.docx4j.wml.Drawing drawing = factory.createDrawing();

        drawing.getAnchorOrInline().add(inline);
        run.getContent().add(drawing);
        paragraph.getContent().add(run);

        return paragraph;
    }

    // Set borders for the table
    public static void setTableBorders(Tbl table) {
        TblBorders tblBorders = Context.getWmlObjectFactory().createTblBorders();

        table.setTblPr(new TblPr());
        CTBorder border = Context.getWmlObjectFactory().createCTBorder();
        border.setVal(STBorder.SINGLE);
        border.setColor("00000");
        border.setSz(new BigInteger("4")); // border thickness
        border.setSpace(new BigInteger("0"));

        tblBorders.setTop(border);
        tblBorders.setBottom(border);
        tblBorders.setLeft(border);
        tblBorders.setRight(border);
        tblBorders.setInsideH(border);
        tblBorders.setInsideV(border);

        table.getTblPr().setTblBorders(tblBorders);
    }


    public static Tbl createTableWithCenteredText(int text1, int text2) {
        // Create the table
        Tbl table = Context.getWmlObjectFactory().createTbl();

        TblPr tblPr = Context.getWmlObjectFactory().createTblPr();
        TblWidth tblWidth = Context.getWmlObjectFactory().createTblWidth();
        tblWidth.setW(BigInteger.valueOf(10000)); // Full width (twips: 1/20 of a point)
        tblWidth.setType("dxa");
        tblPr.setTblW(tblWidth);
        table.setTblPr(tblPr);

        // Create table properties for alignment and borders

        // Create the table row
        Tr tableRow = Context.getWmlObjectFactory().createTr();

        // Add the first cell with centered text
        Tc cell1 = createCenteredTextCell(String.valueOf(text1));
        tableRow.getContent().add(cell1);

        // Add the second cell with centered text
        Tc cell2 = createCenteredTextCell(String.valueOf(text2));
        tableRow.getContent().add(cell2);

        // Add the row to the table
        table.getContent().add(tableRow);

        return table;
    }

    // Helper method to create a table cell with centered text
    public static Tc createCenteredTextCell(String text) {
        // Create the cell
        Tc cell = Context.getWmlObjectFactory().createTc();

        // Create a paragraph
        P paragraph = Context.getWmlObjectFactory().createP();

        // Set alignment to center
        PPr paragraphProperties = Context.getWmlObjectFactory().createPPr();
        Jc justification = Context.getWmlObjectFactory().createJc();
        justification.setVal(JcEnumeration.CENTER);
        paragraphProperties.setJc(justification);
        paragraph.setPPr(paragraphProperties);

        // Add text to the paragraph
        R run = Context.getWmlObjectFactory().createR();
        Text t = Context.getWmlObjectFactory().createText();
        t.setValue(text);
        run.getContent().add(t);
        paragraph.getContent().add(run);

        // Add the paragraph to the cell
        cell.getContent().add(paragraph);

        return cell;
    }
}

