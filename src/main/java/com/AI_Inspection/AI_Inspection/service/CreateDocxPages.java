package com.AI_Inspection.AI_Inspection.service;
import jakarta.xml.bind.JAXBElement;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreateDocxPages {


    @Autowired
    private  ImageService imageService;

    @Autowired
    private CovePageService covePageService;

    @Autowired
    private  AudioService audioService;

    @Autowired
    private StringToJsonService stringToJsonService;

    private int captionNumber = 1;

    public File createdocx() {
        File tempFile = null;
        try {
            // Create a new WordprocessingMLPackage
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

            // Get the main document part
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

            mainDocumentPart.addObject(covePageService.createCustomTable(wordMLPackage));

            mainDocumentPart.addObject(createPageBreak());

            String audioString = audioService.openAISendRequest();
            List<String> stringList = stringToJsonService.audioString(audioString);
            System.out.println(stringList);

            Map<String, List<String>> categoryImages = imageService.getTop4ImagesPerCategory();
//            Map<String, List<String>> categoryImages = new HashMap<>();
//            categoryImages.put("SUMIDEROS", Arrays.asList("IMG_4376.jpeg", "IMG_4387.jpeg", "20240219_092647.jpg", "IMG_4094.JPEG"));

            mainDocumentPart.addObject(createHeading("4 INSPECCIÓN DE LA CUBIERTA", "Heading1"));
            mainDocumentPart.addObject(createHeading("En la inspección realizada, se verifican diferentes aspectos de la cubierta según la siguiente relación:  ", "Normal"));
            int subHeadingCounter = 0;
            for(Map.Entry<String, List<String>> entry : categoryImages.entrySet()) {

                subHeadingCounter +=1;
                P paragraph = createHeading("4."+ subHeadingCounter + " "  + entry.getKey(), "Heading2");
                mainDocumentPart.addObject(paragraph);

                Tbl descriptionTable = create1x1TableWithBorder(stringList.get(0));
                mainDocumentPart.addObject(descriptionTable);

                P emptyParagraph = Context.getWmlObjectFactory().createP();
                mainDocumentPart.addObject(emptyParagraph);

                Tbl table = createTableWithImages(wordMLPackage, entry.getValue().get(0), entry.getValue().get(1));
                mainDocumentPart.addObject(table);
                Tbl captiontable = createTableWithCenteredText(captionNumber, captionNumber + 1);
                mainDocumentPart.addObject(captiontable);

                mainDocumentPart.addObject(emptyParagraph);

                Tbl table2 = createTableWithImages(wordMLPackage, entry.getValue().get(2), entry.getValue().get(3));
                mainDocumentPart.addObject(table2);
                Tbl captiontable2 = createTableWithCenteredText(captionNumber + 2, captionNumber + 3);
                mainDocumentPart.addObject(captiontable2);
                captionNumber = captionNumber + 4;

                mainDocumentPart.addObject(emptyParagraph);

                Tbl recommendationTable = create1x1TableWithBorder(stringList.get(1));
                mainDocumentPart.addObject(recommendationTable);
            }

            tempFile = File.createTempFile("GeneratedDocument", ".docx");
            wordMLPackage.save(tempFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    private P createPageBreak() {
        P pageBreakParagraph = Context.getWmlObjectFactory().createP();
        R run = Context.getWmlObjectFactory().createR();
        Br breakObj = Context.getWmlObjectFactory().createBr();
        breakObj.setType(STBrType.PAGE); // Set break type to page
        run.getContent().add(breakObj);
        pageBreakParagraph.getContent().add(run);
        return pageBreakParagraph;
    }

    // Function to create a heading
    public static P createHeading(String headingText, String style) {
        P heading = Context.getWmlObjectFactory().createP();

        // Add the text to the paragraph
        R run = Context.getWmlObjectFactory().createR();
        Text text = Context.getWmlObjectFactory().createText();
        text.setValue(headingText);
        run.getContent().add(text);
        heading.getContent().add(run);

        // Set the heading style
        PPr paragraphProperties = Context.getWmlObjectFactory().createPPr();
        PPrBase.PStyle headingStyle = Context.getWmlObjectFactory().createPPrBasePStyle();
        headingStyle.setVal(style); // Use the "Heading1" style
        paragraphProperties.setPStyle(headingStyle);
        heading.setPPr(paragraphProperties);

        return heading;
    }

    // Function to create a 1x1 table with a border and text
    public static Tbl create1x1TableWithBorder(String cellText) {
        // Create the table
        Tbl table = Context.getWmlObjectFactory().createTbl();
        setTableBorders(table, true);

        // Create a row
        Tr row = Context.getWmlObjectFactory().createTr();

        // Create a cell with text
        Tc cell = Context.getWmlObjectFactory().createTc();
        P paragraph = Context.getWmlObjectFactory().createP();
        R run = Context.getWmlObjectFactory().createR();
        Text text = Context.getWmlObjectFactory().createText();
        text.setValue(cellText);
        run.getContent().add(text);
        paragraph.getContent().add(run);
        cell.getContent().add(paragraph);

        // Add the cell to the row
        row.getContent().add(cell);

        // Add the row to the table
        table.getContent().add(row);

        return table;
    }

    public static Tbl createTableWithImages(WordprocessingMLPackage wordMLPackage, String image1, String image2) throws Exception {
        // Create the table
        Tbl table = Context.getWmlObjectFactory().createTbl();
        String imageFolder = "src/main/resources/uploads/images/";

        // Add the table row
        Tr row = Context.getWmlObjectFactory().createTr();
        row.setTrPr(new TrPr());
        table.getContent().add(row);

        // Add first cell with image
        Tc cell1 = createImageCell(wordMLPackage, imageFolder+image1);
        row.getContent().add(cell1);

        // Set cell height (row height property)
        CTHeight rowHeight = Context.getWmlObjectFactory().createCTHeight();
        rowHeight.setHRule(STHeightRule.AT_LEAST);
        rowHeight.setVal(BigInteger.valueOf(3626)); // Height in twips (1/20th of a point)
        JAXBElement<CTHeight> jaxbElement = Context.getWmlObjectFactory().createCTTrPrBaseTrHeight(rowHeight);
        row.getTrPr().getCnfStyleOrDivIdOrGridBefore().add(jaxbElement);


        // Add second cell with image
        Tc cell2 = createImageCell(wordMLPackage,imageFolder+image2);
        row.getContent().add(cell2);

        // Set table borders
        setTableBorders(table, true);

        return table;
    }

    // Method to create a cell with an image
    public static Tc createImageCell(WordprocessingMLPackage wordMLPackage, String imagePath) throws Exception {
        // Create a new cell
        Tc cell = Context.getWmlObjectFactory().createTc();

        // Add the image to the cell
        P p = createImageParagraph(wordMLPackage, imagePath, 2772000, 2088000);
        cell.getContent().add(p);

        // Set vertical alignment for the table cell
        TcPr cellProperties = cellMargin(85, 0, 112, 112);
        CTVerticalJc verticalAlignment = Context.getWmlObjectFactory().createCTVerticalJc();
        verticalAlignment.setVal(STVerticalJc.CENTER);
        cellProperties.setVAlign(verticalAlignment);

        // Optional: Set the width of the cell
        TblWidth cellWidth = Context.getWmlObjectFactory().createTblWidth();
        cellWidth.setW(BigInteger.valueOf(4804)); // Width in twips (1/20th of a point)
        cellWidth.setType("dxa");
        cellProperties.setTcW(cellWidth);
        cell.setTcPr(cellProperties);

        return cell;
    }

    // Method to create a paragraph with an image
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

        // Center-align the paragraph
        paragraph.setPPr(new PPr());
        PPr paragraphProperties = Context.getWmlObjectFactory().createPPr();
        Jc alignment = Context.getWmlObjectFactory().createJc();
        alignment.setVal(JcEnumeration.CENTER);
        paragraphProperties.setJc(alignment);
        paragraph.setPPr(paragraphProperties);

        return paragraph;
    }

    // Set borders for the table
    public static void setTableBorders(Tbl table, boolean width_table) {
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

        if(width_table){
            TblWidth tblWidth = Context.getWmlObjectFactory().createTblWidth();
            tblWidth.setW(BigInteger.valueOf(10000)); // Full width (twips: 1/20 of a point)
            tblWidth.setType("dxa");
            table.getTblPr().setTblW(tblWidth);
        }
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

    public static TcPr cellMargin(int top, int bottom, int left, int right) {

        TcPr cellProperties = Context.getWmlObjectFactory().createTcPr();
        // Add cell margins
        TcMar cellMargins = Context.getWmlObjectFactory().createTcMar();
        TblWidth topMargin = Context.getWmlObjectFactory().createTblWidth();
        topMargin.setW(BigInteger.valueOf(top)); // Top margin in twips (10 points)
        topMargin.setType("dxa");

        TblWidth bottomMargin = Context.getWmlObjectFactory().createTblWidth();
        bottomMargin.setW(BigInteger.valueOf(bottom)); // Bottom margin in twips (10 points)
        bottomMargin.setType("dxa");

        TblWidth leftMargin = Context.getWmlObjectFactory().createTblWidth();
        leftMargin.setW(BigInteger.valueOf(left)); // Left margin in twips (10 points)
        leftMargin.setType("dxa");

        TblWidth rightMargin = Context.getWmlObjectFactory().createTblWidth();
        rightMargin.setW(BigInteger.valueOf(right)); // Right margin in twips (10 points)
        rightMargin.setType("dxa");

        // Apply margins to the cell
        cellMargins.setTop(topMargin);
        cellMargins.setBottom(bottomMargin);
        cellMargins.setLeft(leftMargin);
        cellMargins.setRight(rightMargin);
        cellProperties.setTcMar(cellMargins);


        return cellProperties;
    }
}

