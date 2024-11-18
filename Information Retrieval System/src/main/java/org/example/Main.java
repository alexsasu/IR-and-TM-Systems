package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.example.customAnalyzer.CustomRomanianAnalyzer;
import org.example.utils.PDFParser;
import org.example.utils.WordParser;

public class Main {
    private static String directoryPath;
    private static String query;

    private static final String INDEX_DIR = "src/main/java/org/example/indexDir";

    private static Directory index;
    private static Analyzer analyzer;
    private static IndexWriterConfig config;

    public static void main(String[] args) {
        // Parsing the program arguments
        if (args.length != 3) {
            System.out.println("Error: Invalid arguments. Choose either \"-index -directory <path to docs>\" or \"-search -query <keyword>\".");
        } else {
            String mode = args[0];
            String modeArgument = args[1];
            if (!(mode + " " + modeArgument).equals("-index -directory") && !(mode + " " + modeArgument).equals("-search -query")) {
                System.out.println("Error: Invalid arguments. Choose either \"-index -directory <path to docs>\" or \"-search -query <keyword>\".");
            } else {
                if (Objects.equals(mode, "-index")) {
                    directoryPath = args[2];
                } else {
                    query = args[2];
                }

                // Based on the inputted mode, initiate indexing or search
                try {
                    setupIRSystem();
                    if (mode.equals("-index")) {
                        indexDocuments(directoryPath);
                    } else {
                        searchDocuments(query);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void setupIRSystem() throws IOException {
        index = FSDirectory.open(Paths.get(INDEX_DIR));
        analyzer = new CustomRomanianAnalyzer();
        config = new IndexWriterConfig(analyzer);
    }

    private static void indexDocuments(String directoryPath) throws IOException {
        File directory = new File(directoryPath);

        IndexWriter writer = new IndexWriter(index, config);
        for (File file : directory.listFiles()) {
                if (file.isFile() &&
                        (file.getName().endsWith(".txt") || file.getName().endsWith(".pdf") || file.getName().endsWith(".doc") || file.getName().endsWith(".docx"))) {
                    // If the file is already indexed, we want to remove it from the index and then re-add
                    // so that we make sure our file's indexed content is up-to-date. We recognize a file
                    // by its absolute path
                    writer.deleteDocuments(new Term("path", file.getAbsolutePath()));

                    indexFile(writer, file);
                }
        }
        writer.close();
    }

    private static void searchDocuments(String query) throws ParseException, IOException {
        // Searcher setup
        Query q = new QueryParser("content", analyzer).parse(query);
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Execute the search
        int maxSearches = 5; // We want at most 5 documents
        TopDocs docs = searcher.search(q, maxSearches);
        ScoreDoc[] hits = docs.scoreDocs;

        // Display the results found
        for (ScoreDoc scoreDoc : hits) {
            Document doc = searcher.doc(scoreDoc.doc);
            String fileName = doc.get("filename");
            System.out.println(fileName);
        }
    }

    private static void indexFile(IndexWriter writer, File file) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("filename", file.getName(), Field.Store.YES));
        doc.add(new StringField("path", file.getAbsolutePath(), Field.Store.YES));
        doc.add(new TextField("content", extractFileContent(file), Field.Store.YES));
        writer.addDocument(doc);
    }

    private static String extractFileContent(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".txt")) {
            return Files.readString(file.toPath());
        } else if (fileName.endsWith(".pdf")) {
            return PDFParser.parsePDF(file);
        } else if (fileName.endsWith(".doc")) {
            return WordParser.parseDOC(file);
        } else {
            return WordParser.parseDOCX(file);
        }
    }
}
