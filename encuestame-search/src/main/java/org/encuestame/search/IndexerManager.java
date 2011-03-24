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
package org.encuestame.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.apache.poi.POIXMLException;
import org.encuestame.search.utils.SearchUtils;

/**
 * Indexer Manager.
 * @author Morales, Diana Paola paolaATencuestame.org
 * @since Mar 23, 2011
 */
public class IndexerManager {

    /** Log. **/
    private static final Log log = LogFactory.getLog(IndexerManager.class);

    /** Directory file list to index. **/
    private List<File> directoriesToIndex = new ArrayList<File>();

    /** Directory index path. **/
    private String indexesLocation;

    /** Index writer. **/
    private IndexWriter indexWriter;

    /**
    * Indexer files.
    * @param files
    * @throws Exception
    */
    public IndexerManager(final List<File> files, final String indexesLocation) {
        this.indexesLocation = indexesLocation;
        this.directoriesToIndex = files;
    }

    /** Constructor. **/
    public IndexerManager() {
    }

    /**
     * Initialize index process.
     * @throws Exception
     */
     public void initializeIndex() throws Exception {
         log.debug("Initialize");
         this.startIndexWriter();
         for (File file : this.directoriesToIndex) {
             long start = System.currentTimeMillis();
             int numIndexed;
             try {
                 numIndexed = this.index(file);
             } finally {
                 this.close();
             }
             long end = System.currentTimeMillis();
             log.debug("Indexing " + numIndexed + " files took " + (end - start)
                     + " milliseconds");
         }
     }

    /**
    * Index Writer.
    * @throws IOException
    * @throws LockObtainFailedException
    * @throws CorruptIndexException
    *
    */
    private void startIndexWriter() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        final Directory dir = FSDirectory.open(new java.io.File(
                this.indexesLocation));
        this.indexWriter = new IndexWriter(dir, new StandardAnalyzer(
                Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED);
    }

    /**
     * Read Files in Attachment Directory.
     * @param dataDir Attachment Directory
     * @return
     * @throws Exception
     */
    public int index(final File dataDir) throws Exception {
        log.debug("Index file is directory: " + dataDir.isDirectory());
        File[] files = dataDir.listFiles();
        for (File f : files) {
            if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead()) {
                indexFile(f); // Write documents in Index
            }
        }
        return this.indexWriter.numDocs();
    }


   /**
    * Retrieve Document to Index.
    * @param file {@link File}
    * @param ext Filename extension
    * @return {@link Document}
    * @throws POIXMLException
    * @throws Exception
    */
    public Document getDocument(final File file, final String ext)
            throws POIXMLException, Exception {
        Document doc = null;
        log.debug("get Document extension " + ext);
        if ("docx".equals(ext)) {
            doc = SearchUtils.createWordDocument(file);
        } else if ("xls".equals(ext)) {
            doc = SearchUtils.createSpreadsheetsDocument(file);
        } else if ("pdf".equals(ext)) {
            doc = SearchUtils.createPdfDocument(file);
        } else if ("txt".equals(ext)) {
            doc = SearchUtils.createTextDocument(file);
        }
        return doc;
    }

    /**
    * Adding Document to Index Directory.
    * @param file {@link File}
    * @throws Exception
    */
    private void indexFile(final File file) throws Exception {
        log.debug("Indexing " + file.getCanonicalPath());
        final String pathFileName = file.getName().toString();
        final String ext = SearchUtils.getExtension(pathFileName);
        final Document doc = this.getDocument(file, ext);
        log.debug("Adding document..." + doc);
        if (doc == null) {
            log.warn("Document is null for this file: "+file.getAbsolutePath());
        } else {
            // Add Document to Lucene Index.
            this.indexWriter.addDocument(doc);
        }
    }

    /**
    * Close Lucene IndexWriter.
    * @throws IOException
    */
    public void close() throws IOException {
        this.indexWriter.close();
    }
}