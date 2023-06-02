package gov.doc.isu.shamen.util;

import static gov.doc.isu.dwarf.resources.Constants.EMPTY_SPACE;
import static gov.doc.isu.dwarf.resources.Constants.EMPTY_STRING;
import static gov.doc.isu.dwarf.resources.Constants.HYPHEN;
import static gov.doc.isu.dwarf.resources.Constants.NULL_AS_STRING;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * PDFReportUtil.java Utility object for Reports
 *
 * @author nehas Jun 3, 2005
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class ReportUtil {

    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.util.ReportUtil");
    public static final String NINE_DIGIT_FORMAT = "000000000";
    public static final String THREE_DIGIT_FORMAT = "000";
    public static final String FOUR_DIGIT_FORMAT = "0000";
    public static final String TEN_DIGIT_FORMAT = "00000000.00";
    public static final float MARGIN_LEFT = 20;
    public static final float MARGIN_RIGHT = 20;
    public static final float MARGIN_TOP = 0;
    public static final float MARGIN_BOTTOM = 10;
    public static final int CELL_HEIGHT = 16;
    public static final int MAX_LINE_COUNT_LANDSCAPE = 27;
    public static final int MAX_LINE_COUNT_PORTRAIT = 37;
    public static final int DATA_TABLE_CELL_HEIGHT_LANDSCAPE = 450;
    public static final int DATA_TABLE_CELL_HEIGHT_PORTRAIT = 630;
    public static final Font TEXT_FONT = FontFactory.getFont(FontFactory.TIMES, 8f, 0, new Color(0, 0, 0));
    public static final Font NO_DATA_FONT = FontFactory.getFont(FontFactory.TIMES_BOLD, 11f, 0, new Color(0, 0, 0));

    /**
     * Added by smcafee Method used to return a right aligned cell
     *
     * @param string
     *        string
     * @param fontSize
     *        fontSize
     * @param isBold
     *        isBold
     * @param border
     *        border
     * @param iPad
     *        iPad
     * @return PdfPCell
     */
    public static synchronized PdfPCell getFormattedCellRight(String string, int fontSize, boolean isBold, int border, int iPad) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getFormattedCellRight.");
        log.debug("Parameters are: string=" + String.valueOf(string) + ", fontSize=" + String.valueOf(fontSize) + ", isBold=" + String.valueOf(isBold) + ", border=" + String.valueOf(border) + ", iPad=" + String.valueOf(iPad));
        PdfPCell ppcWork = new PdfPCell(getFormattedCell(string, fontSize, isBold, border));
        ppcWork.setHorizontalAlignment(2);
        if(iPad != 0){
            ppcWork.setPaddingRight(iPad);
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getFormattedCellRight.");
        return ppcWork;
    }// end method

    /**
     * Method used to return a center aligned cell Added by smcafee
     *
     * @param string
     *        string
     * @param fontSize
     *        fontSize
     * @param isBold
     *        isBold
     * @param border
     *        border
     * @return PdfPCell
     */
    public static synchronized PdfPCell getFormattedCellCenter(String string, int fontSize, boolean isBold, int border) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getFormattedCellCenter.");
        log.debug("Parameters are: string=" + String.valueOf(string) + ", fontSize=" + String.valueOf(fontSize) + ", isBold=" + String.valueOf(isBold) + ", border=" + String.valueOf(border));
        PdfPCell ppcWork = new PdfPCell(getFormattedCell(string, fontSize, isBold, border));
        ppcWork.setHorizontalAlignment(1);
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getFormattedCellCenter.");
        return ppcWork;
    }// end method

    /**
     * Method used to set the font of the string parameter
     *
     * @param string
     *        string
     * @param fontSize
     *        fontSize
     * @param isBold
     *        isBold
     * @param border
     *        border
     * @return PdfPCell
     */
    public static synchronized PdfPCell getFormattedCell(String string, int fontSize, boolean isBold, int border) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getFormattedCell.");
        log.debug("Parameters are: string=" + String.valueOf(string) + ", fontSize=" + String.valueOf(fontSize) + ", isBold=" + String.valueOf(isBold) + ", border=" + String.valueOf(border));
        if(fontSize == 0){
            fontSize = 10;
        }// end if
        Phrase phrase = null;
        if(string != null){
            if(isBold){
                phrase = new Phrase(string, FontFactory.getFont(FontFactory.HELVETICA, fontSize, Font.BOLD, new Color(0, 0, 0)));
            }else{
                phrase = new Phrase(string, FontFactory.getFont(FontFactory.HELVETICA, fontSize, Font.NORMAL, new Color(0, 0, 0)));
            }// end else
        }else{
            phrase = new Phrase(EMPTY_STRING, FontFactory.getFont(FontFactory.HELVETICA, fontSize, Font.NORMAL, new Color(0, 0, 0)));
        }// end else

        // return phrase;
        PdfPCell newCell = new PdfPCell(phrase);
        newCell.setBorder(border);
        newCell.setLeading(1f, 1f);
        newCell.setPadding(1f);
        newCell.setNoWrap(false);
        newCell.setColspan(1);
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getFormattedCell.");
        return newCell;
    }// end method

    /**
     * Added by smcafee Method used to determine the cell alignment
     *
     * @param sAlignmentValue
     *        The alignment string
     * @param sCellValue
     *        The cell value string
     * @return PdfPCell The properly formatted cell
     */
    public static PdfPCell getAlignedCell(String sAlignmentValue, String sCellValue) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getAlignedCell.");
        log.debug("Parameters are: sAlignmentValue=" + String.valueOf(sAlignmentValue) + ", sCellValue=" + String.valueOf(sCellValue));
        PdfPCell ppcReturn;
        if(sAlignmentValue.startsWith("R")){
            int iPad = 0;
            if(sAlignmentValue.length() > 1){
                iPad = Integer.valueOf(sAlignmentValue.substring(1));
            }// end if
            ppcReturn = getFormattedCellRight(sCellValue, 10, false, 0, iPad);
        }else if("C".equals(sAlignmentValue)){
            ppcReturn = ReportUtil.getFormattedCellCenter(sCellValue, 10, false, 0);
        }else{
            ppcReturn = getFormattedCell(sCellValue, 10, false, 0);
        }// end else
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getFormattedCell.");
        return ppcReturn;
    }// end method

    /**
     * border int BOTTOM 2 border int BOX 15 border int LEFT 4 border int NO_BORDER 0 border int RIGHT 8 border int TOP 1 border int UNDEFINED -1 Method used wen the label and value of the field are to be inserted in the same Cell. Label is to be of the Normal style while Value has to be in Bold
     *
     * @param text
     *        text
     * @param value
     *        value
     * @param style
     *        style
     * @param fontSize
     *        fontSize
     * @param border
     *        border
     * @return PdfPCell
     * @throws BadElementException
     *         if an exception occurred
     */
    public static synchronized PdfPCell getMergedDataPdfPCell(String text, String value, int style, int fontSize, int border) throws BadElementException {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getMergedDataPdfPCell.");
        log.debug("Parameters are: text=" + String.valueOf(text) + ", value=" + String.valueOf(value) + ", style=" + String.valueOf(style) + ", fontSize=" + String.valueOf(fontSize) + ", border=" + String.valueOf(border));
        String newText = text;
        String newValue = value;
        if((text == null) || NULL_AS_STRING.equalsIgnoreCase(text)){
            newText = EMPTY_SPACE;
        }// end if

        if((value == null) || NULL_AS_STRING.equalsIgnoreCase(value)){
            newValue = EMPTY_SPACE;
        }// end if

        Chunk chunk1 = new Chunk(newText, FontFactory.getFont(FontFactory.HELVETICA, fontSize, style, new Color(0, 0, 0)));

        Chunk chunk = new Chunk(newValue, FontFactory.getFont(FontFactory.HELVETICA, fontSize, 0, new Color(0, 0, 0)));

        Paragraph paragraph = new Paragraph(chunk1);
        paragraph.add(chunk);

        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(border);
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getMergedDataPdfPCell.");
        return cell;
    }// end method

    /**
     * This method returns a formatted PdfPCell object with the given text, font size, fontType, border.
     *
     * @author Ravi Reddy
     * @param string
     *        string
     * @param fontSize
     *        fontSize
     * @param fontType
     *        fontType
     * @param isBold
     *        isBold
     * @param border
     *        border
     * @return PdfPCell
     */
    public static synchronized PdfPCell getFormattedCell(String string, int fontSize, String fontType, boolean isBold, int border) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getFormattedCell.");
        log.debug("Parameters are: string=" + String.valueOf(string) + ", fontSize=" + String.valueOf(fontSize) + ", fontType=" + String.valueOf(fontType) + ", isBold=" + String.valueOf(isBold) + ", border=" + String.valueOf(border));
        if(fontSize == 0){
            fontSize = 10;
        }// end if

        Phrase phrase = null;

        if(string != null){
            if(isBold){
                phrase = new Phrase(string, FontFactory.getFont(fontType, fontSize, Font.BOLD, new Color(0, 0, 0)));
            }else{
                phrase = new Phrase(string, FontFactory.getFont(fontType, fontSize, Font.NORMAL, new Color(0, 0, 0)));
            }// end else
        }else{
            phrase = new Phrase(EMPTY_STRING, FontFactory.getFont(fontType, fontSize, Font.NORMAL, new Color(0, 0, 0)));
        }// end else

        PdfPCell newCell = new PdfPCell(phrase);
        newCell.setBorder(border);
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getFormattedCell.");
        return newCell;
    }// end method

    /**
     * @param value
     *        the double value
     * @return String
     * @screen: <JSP Name> This method returns the value of double passed in as parameter in '##,###,###.##' format. It returns the full string with lenght size as 13.For example if you pass 123 you will get #######123.00 where '#' means space character.
     */
    public static String getNumberInFormat(double value) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getNumberInFormat.");
        log.debug("Parameters are: value=" + String.valueOf(value));
        DecimalFormat format = new DecimalFormat("00,000,000.00");

        String text = format.format(value);
        Boolean isNegative = text.startsWith(HYPHEN);
        Integer i = isNegative ? 1 : 0;

        char[] array = text.toCharArray();
        Boolean checkDecimal = false;

        for(;i < array.length;i++){
            if((array[i] == '0') || (array[i] == ',')){
                array[i] = ' ';
            }else{
                if(array[i] != '.'){
                    break;
                }else{
                    checkDecimal = true;
                }// end else
            }// end else
        }// end for

        if(checkDecimal){
            int a = text.indexOf('.') + 1;
            int b = text.indexOf('.') + 2;
            int c = text.indexOf('.');

            if((array[a] == ' ') && (array[b] == ' ')){
                array[c] = ' ';
            }else if((array[a] == ' ') && (array[b] != ' ')){
                array[a] = '0';
            }// end else
        }// end if

        if(isNegative){
            if(i == 1){
                array[0] = '-';
            }else{
                array[0] = ' ';

                if(array[i - 1] == ' '){
                    array[i - 1] = '-';
                }else if(array[i - 2] == ' '){
                    array[i - 2] = '-';
                }else if(array[i - 3] == ' '){
                    array[i - 3] = '-';
                }// end else if
            }// end else
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getNumberInFormat.");
        return new String(array);
    }// end method

    /**
     * @author Ravi Reddy
     * @param text
     *        text
     * @param value
     *        value
     * @param fontType
     *        fontType
     * @param style
     *        style
     * @param fontSize
     *        fontSize
     * @param border
     *        border
     * @return PdfPCell
     * @throws BadElementException
     *         if an exception occurred
     * @screen Name: <The detailed purpose of this method> This method returns merged cell of both Label and value.
     */
    public static synchronized PdfPCell getMergedDataPdfPCell(String text, String value, String fontType, int style, int fontSize, int border) throws BadElementException {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getMergedDataPdfPCell.");
        log.debug("Parameters are: text=" + String.valueOf(text) + ", value=" + String.valueOf(value) + ", fontType=" + String.valueOf(fontType) + ", style=" + String.valueOf(style) + ", fontSize=" + String.valueOf(fontSize) + ", border=" + String.valueOf(border));
        String newText = text;
        String newValue = value;
        if((text == null) || NULL_AS_STRING.equalsIgnoreCase(text)){
            newText = EMPTY_SPACE;
        }// end if

        if((value == null) || NULL_AS_STRING.equalsIgnoreCase(value)){
            newValue = EMPTY_SPACE;
        }// end if

        Chunk chunk1 = new Chunk(newText, FontFactory.getFont(fontType, fontSize, style, new Color(0, 0, 0)));

        Chunk chunk = new Chunk(newValue, FontFactory.getFont(fontType, fontSize, 0, new Color(0, 0, 0)));

        Paragraph paragraph = new Paragraph(chunk1);
        paragraph.add(chunk);

        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(border);
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getMergedDataPdfPCell.");
        return cell;
    }// end method

    /**
     * @param columnNos
     *        columnNos
     * @param border
     *        border
     * @param widthsInFloat
     *        widthsInFloat
     * @param widthsInInt
     *        widthsInInt
     * @param widthPercent
     *        widthPercent
     * @param alignment
     *        alignment
     * @return PdfPTable
     * @throws DocumentException
     *         if an exception occurred
     * @screen: <JSP Name> This method returns the Table for a Document.
     */
    public static PdfPTable getTable(int columnNos, int border, float[] widthsInFloat, int[] widthsInInt, float widthPercent, int alignment) throws DocumentException {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getTable.");
        log.debug("Parameters are: columnNos=" + String.valueOf(columnNos) + ", border=" + String.valueOf(border) + ", widthsInFloat=" + String.valueOf(widthsInFloat) + ", widthsInInt=" + String.valueOf(widthsInInt) + ", widthPercent=" + String.valueOf(widthPercent) + ", alignment=" + String.valueOf(alignment));
        PdfPTable table = new PdfPTable(columnNos);
        table.getDefaultCell().setBorder(border);

        if(widthsInFloat != null){
            table.setWidths(widthsInFloat);
        }else if(widthsInInt != null){
            table.setWidths(widthsInInt);
        }// end else if

        table.setWidthPercentage(widthPercent);
        table.setHorizontalAlignment(alignment);
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getTable.");
        return table;
    }// end method

    /**
     * @param table
     *        table
     * @param cells
     *        cells
     * @screen: <JSP Name> This method adds cells(PdfPCell) contained in List to PdfPTable
     */
    public static synchronized void addCellsToTable(PdfPTable table, List<?> cells) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method addCellsToTable.");
        if((table != null) && (cells != null)){
            for(Iterator<?> iter = cells.iterator();iter.hasNext();){
                table.addCell((PdfPCell) iter.next());
            }// end for
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method addCellsToTable.");
    }// end method

    /**
     * @param value
     *        Long value
     * @param digitFormat
     *        digitFormat
     * @return <The detailed purpose of this method> This method formats a number to the given number of digits and returns the formatted number as String. For example. If you pass a number 100 and the format as ReportUtil._4_DIGIT_FORMAT, it will return you #100 number as String.
     */
    public static String getNumberInFormat(long value, String digitFormat) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getNumberInFormat.");
        log.debug("Parameters are: value=" + String.valueOf(value) + ", digitFormat=" + String.valueOf(digitFormat));
        DecimalFormat format = new DecimalFormat(digitFormat);

        String text = format.format(value);

        char[] array = text.toCharArray();

        for(int i = 0;i < array.length;i++){
            if(array[i] == '0'){
                array[i] = ' ';
            }else{
                break;
            }// end else
        }// end for
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getNumberInFormat.");
        return new String(array);
    }// end method

    /**
     * @param value
     *        double value
     * @param digitFormat
     *        digitFormat
     * @return String
     * @author Ashish Arya
     * @date 5-sep-2005 <The detailed purpose of this method> This method formats a number to the given number of digits and returns the formatted number as String. For example. If you pass a number 100 and the format as ReportUtil._10_DIGIT_FORMAT, it will return you #100 number as String.
     */
    public static String getNumberInFormat(double value, String digitFormat) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getNumberInFormat.");
        log.debug("Parameters are: value=" + String.valueOf(value) + ", digitFormat=" + String.valueOf(digitFormat));
        DecimalFormat format = new DecimalFormat(digitFormat);

        String text = format.format(value);

        char[] array = text.toCharArray();

        for(int i = 0;i < array.length;i++){
            if(i == 0 && array[i] == '-'){
                continue;
            }// end if
            if(array[i] == '0'){
                array[i] = ' ';
            }else{
                break;
            }// end else
        }// end for
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getNumberInFormat.");
        return new String(array);
    }// end method

    /**
     * @param value
     *        double value
     * @param format
     *        format
     * @return String
     * @author Ashish Arya
     * @date 5-sep-2005 <The detailed purpose of this method> This method formats a decimal number according to decimal format object passed in.This method removes all the 0(zeros) which are not required in the number for example 0001000 will become 1000 and 00012345 will become 12345.
     */
    public static String getNumberInFormat(double value, DecimalFormat format) {
        log.debug("Entering gov.doc.isu.shamen.util.ReportUtil - method getNumberInFormat.");
        log.debug("Parameters are: value=" + String.valueOf(value) + ", format=" + String.valueOf(format));
        String result = String.valueOf(value);
        if(format != null){
            String text = format.format(value);

            char[] array = text.toCharArray();

            for(int i = 0;i < array.length;i++){
                if(i == 0 && array[i] == '-'){
                    continue;
                }// end if
                if(array[i] == '0'){
                    array[i] = ' ';
                }else{
                    break;
                }// end else
            }// end for
            result = new String(array);
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ReportUtil - method getNumberInFormat.");
        return result;
    }// end method

    /**
     * This method gets the header for the table
     *
     * @param data
     *        the string array of data
     * @return PdfPTable as a caption
     * @throws Exception
     *         Exception
     */
    public static PdfPTable getData(String[] data) throws Exception {
        log.debug("Entering getData");
        log.debug("This method gets the header for the table");
        log.debug("Entry parameters are: data=" + Arrays.toString(data));
        // Set up the table to display the data
        final PdfPTable datatable = new PdfPTable(1);
        datatable.setWidthPercentage(100); // percentage
        if(data != null){
            if(data.length == 1 && EMPTY_STRING.equalsIgnoreCase(data[0])){
                datatable.addCell(getFormattedCellCenter("NO RESULT DETAIL FOUND", 11, true, 0));
            }else{
                for(int i = 0;i < data.length;i++){
                    datatable.addCell(getFormattedCell(data[i], 8, false, 0));
                }// end for
            }// end if/else
        }// end if
        log.debug("Return value is: datatable=" + String.valueOf(datatable));
        log.debug("Exiting getData");
        return datatable;
    }// end getData

    /**
     * Gets the DOC Image
     *
     * @param photoPath
     *        the path of the photo
     * @param scale
     *        the scale of the photo
     * @return Image image
     */
    public static Image getDOCImage(String photoPath, int scale) {
        log.debug("Entering getDOCImage");
        log.debug("Gets the DOC Image");
        log.debug("Entry parameters are: photoPath=" + String.valueOf(photoPath) + ", scale=" + String.valueOf(scale));
        Image img = null;
        try{
            java.awt.Image awtImg = new ImageIcon(ReportUtil.class.getResource(photoPath)).getImage();
            img = Image.getInstance(awtImg, null);
            img.scalePercent(scale);
            if(scale == 0){
                img.scalePercent(46);
            }// end if
        }catch(Exception e){
            log.error("Exception loading image: " + e.getMessage(), e);
        }// end try/catch
        log.debug("Return value is: img=" + String.valueOf(img));
        log.debug("Exiting getDOCImage");
        return img;
    }// end getDOCImage

    /**
     * This method gets the header for the table
     *
     * @param type
     *        The type of the table
     * @param tableName
     *        The name of the table
     * @return PdfPTable as a caption
     * @throws Exception
     *         Exception
     */
    public static PdfPTable getDocumentHeader(String type, String tableName) throws Exception {
        log.debug("Entering getDocumentHeader");
        log.debug("This method gets the header for the table");
        log.debug("Entry parameters are: type=" + String.valueOf(type) + ", tableName=" + String.valueOf(tableName));
        // Set up the table to display the data
        final PdfPTable datatable = new PdfPTable(4);
        int[] headerwidths = {60, 10, 10, 20}; // percentage
        datatable.setWidthPercentage(100); // percentage
        datatable.setWidths(headerwidths);
        // Put in caption
        final Paragraph p1 = new Paragraph();
        p1.add(new Chunk(StringUtils.upperCase(type) + "\n", FontFactory.getFont(FontFactory.COURIER_BOLD, 12f, 0, new Color(0, 0, 0))));
        p1.add(new Chunk(tableName + "\n", FontFactory.getFont(FontFactory.COURIER_BOLD, 10f, 0, new Color(0, 0, 0))));
        PdfPCell captionCell = new PdfPCell(p1);
        captionCell.setPadding(2.0f);
        captionCell.setBorder(Rectangle.BOX);
        captionCell.setBorderColor(Color.BLACK);
        captionCell.setBorderWidth(1.0f);
        captionCell.setBackgroundColor(new Color(255, 255, 255));
        captionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        captionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        captionCell.setLeading(2f, 1f);
        captionCell.setColspan(1);
        datatable.addCell(captionCell);
        // Put in image
        final Paragraph p = new Paragraph();
        p.add(new Chunk(getDOCImage("DOCLogo.gif", 0), 0, 0));
        PdfPCell imageCell = new PdfPCell(p);
        imageCell.setPadding(0);
        imageCell.setBorder(0);
        imageCell.setLeading(2f, 1f);
        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        imageCell.setColspan(1);
        datatable.addCell(imageCell);
        // Put in second logo
        final Paragraph p3 = new Paragraph();
        p3.add(new Chunk(getDOCImage("Standing_Wizard.png", 0), 0, 0));
        PdfPCell imageCell2 = new PdfPCell(p3);
        imageCell2.setPadding(0);
        imageCell2.setBorder(0);
        imageCell2.setLeading(2f, 1f);
        imageCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        imageCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        imageCell2.setColspan(1);
        datatable.addCell(imageCell2);
        // Put in sheild logo
        final Paragraph p4 = new Paragraph();
        p4.add(new Chunk(getDOCImage("ISU_JCCC_Dk.png", 40), 0, 0));
        PdfPCell imageCell3 = new PdfPCell(p4);
        imageCell3.setPadding(0);
        imageCell3.setBorder(0);
        imageCell3.setLeading(2f, 1f);
        imageCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        imageCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        imageCell3.setColspan(1);
        datatable.addCell(imageCell3);
        log.debug("Return value is: datatable=" + String.valueOf(datatable));
        log.debug("Exiting getDocumentHeader");
        return datatable;
    }// end getDocumentHeader

}// end class
