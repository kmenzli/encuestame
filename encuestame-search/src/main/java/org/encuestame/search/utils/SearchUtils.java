/*
 ************************************************************************************
 * Copyright (C) 2001-2011 encuestame: system online surveys Copyright (C) 2009
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.search.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

/**
 * Search Utils.
 * @author Morales, Diana Paola paolaATencuestame.org
 * @since Mar 23, 2011
 */
public class SearchUtils {

    /**
    * Log
    */
    private static final Log log = LogFactory.getLog(SearchUtils.class);

    /**
    * Get Filename extension.
    * @param path fullname file
    * @return
    */
    public static String getExtension(final String path) {
       final String ext = path.substring(path.lastIndexOf('.') + 1);
       log.debug("Path file " + path);
       log.debug("Ext file " + ext);
       return ext;
   }

   /**
    * PDF Document content parser.
    * @param is Document content
    * @return
    * @throws IOException
    */
    public static COSDocument parseDocument(final InputStream is) throws IOException {
       PDFParser parser = null;
       parser = new PDFParser(is);
       parser.parse();
       return parser.getDocument();
   }

   /**
    * Create PDF Document.
    * @param file {@link File}
    * @return {@link Document}
    * @throws Exception
    */
    public static Document createPdfDocument(final File file) throws Exception {
       InputStream is = new FileInputStream(file);
       COSDocument cosDoc = null;
       cosDoc = parseDocument(is);
       PDDocument pdDoc = new PDDocument(cosDoc);
       String docText = "";
       PDFTextStripper stripper = new PDFTextStripper();
       docText = stripper.getText(pdDoc);
       log.debug("PDF Doc Text "+docText.length());
       Document doc = new Document();
       if (StringUtils.isNotEmpty(docText)) {
           doc.add(new Field("content", docText, Field.Store.NO,
                   Field.Index.ANALYZED));
           doc.add(new Field("fullpath", file.getCanonicalPath(),
                   Field.Store.YES, Field.Index.NOT_ANALYZED)); // Index Full
                                                                   // Path.
           doc.add(new Field("filename", file.getName(), Field.Store.YES,
                   Field.Index.NOT_ANALYZED));
       }
       // extract PDF document's meta-data
       PDDocumentInformation docInfo = pdDoc.getDocumentInformation();
       String author = docInfo.getAuthor();
       String title = docInfo.getTitle();
       String keywords = docInfo.getKeywords();
       String summary = docInfo.getSubject();
       if (StringUtils.isNotEmpty(author)) {
           doc.add(new Field("author", author, Field.Store.YES,
                   Field.Index.NOT_ANALYZED));
       }
       if (StringUtils.isNotEmpty(title)) {
           doc.add(new Field("title", title, Field.Store.YES,
                   Field.Index.ANALYZED));
       }
       if (StringUtils.isNotEmpty(keywords)) {
           doc.add(new Field("keywords", keywords, Field.Store.YES,
                   Field.Index.ANALYZED));
       }
       if (StringUtils.isNotEmpty(summary)) {
           doc.add(new Field("summary", summary, Field.Store.YES,
                   Field.Index.ANALYZED));
       }
       return doc;
   }

    /**
    * Create Document Word.
    * @param file {@link File}
    * @return {@link Document}
    * @throws POIXMLException
    * @throws Exception
    */
    public static Document createWordDocument(final File file) throws POIXMLException,
           Exception {
       InputStream is = new FileInputStream(file);
       String bodyText = null;
       StringBuilder content = new StringBuilder();
       try {
           XWPFDocument wd = new XWPFDocument(is);
           XWPFWordExtractor wde = new XWPFWordExtractor(wd);
           bodyText = wde.getText();
       } catch (Exception e) {
           e.printStackTrace();
       }
       Document doc = new Document();
       if (!bodyText.equals("") && bodyText != null) {
           doc.add(new Field("content", bodyText, Field.Store.NO,
                   Field.Index.ANALYZED));
           doc.add(new Field("filename", file.getName(), Field.Store.NO,
                   Field.Index.ANALYZED));
           doc.add(new Field("fullpath", file.getCanonicalPath(), Field.Store.NO,
                   Field.Index.ANALYZED));
       }
       return doc;
   }

    /**
    * Create Spreadsheets Document.
    * @param file Spreadsheet {@link File}.
    * @return {@link Document}
    * @throws FileNotFoundException
    */
    public static Document createSpreadsheetsDocument(final File file) throws Exception {
       log.debug("FileName Excel: " + file.getName());
       InputStream is = new FileInputStream(file);
       StringBuilder contents = new StringBuilder();
       POIFSFileSystem fileSystem = new POIFSFileSystem(is);
       HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
       for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
           HSSFSheet sheet = workBook.getSheetAt(i);
           Iterator<Row> rows = sheet.rowIterator();
           while (rows.hasNext()) {
               HSSFRow row = (HSSFRow) rows.next();
               // Display the row number
               System.out.println(row.getRowNum());
               Iterator<Cell> cells = row.cellIterator();
               while (cells.hasNext()) {
                   HSSFCell cell = (HSSFCell) cells.next();
                   // Display the cell number of the current Row
                   switch (cell.getCellType()) {
                   case HSSFCell.CELL_TYPE_NUMERIC: {
                       System.out.println(String.valueOf(cell
                               .getNumericCellValue()));
                       contents.append(
                               String.valueOf(cell.getNumericCellValue()))
                               .append(" ");
                       break;
                   }

                   case HSSFCell.CELL_TYPE_STRING: {
                       HSSFRichTextString richTextString = cell
                               .getRichStringCellValue();
                       System.out.println(richTextString.toString());
                       contents.append(richTextString.toString()).append(" ");
                       break;
                   }

                   case HSSFCell.CELL_TYPE_BOOLEAN: {
                       contents.append(
                               String.valueOf(cell.getBooleanCellValue()))
                               .append(" ");
                       break;
                   }
                   }
               }
           }
       }
       Document doc = new Document();
       doc.add(new Field("content", contents.toString(), Field.Store.YES,
               Field.Index.ANALYZED));
       log.debug("Content Spreadsheets " + contents.toString());
       return doc;
   }

    /**
    * Create Text Document.
    * @param file Text File.
    * @return {@link Document}
    * @throws Exception
    */
    public static Document createTextDocument(final File file) throws Exception {
       Document doc = new Document();
       //Index file contents
       doc.add(new Field("contents", new FileReader(file)));
       // Index File Name.
       doc.add(new Field("filename", file.getName(), Field.Store.YES,
               Field.Index.NOT_ANALYZED));
       // Index Full Path.
       doc.add(new Field("fullpath", file.getCanonicalPath(), Field.Store.YES,
               Field.Index.NOT_ANALYZED));
       return doc;
   }
}
