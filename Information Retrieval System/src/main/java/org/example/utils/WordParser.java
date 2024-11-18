package org.example.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WordParser {
    public static String parseDOC(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        HWPFDocument document = new HWPFDocument(fis);
        WordExtractor extractor = new WordExtractor(document);
        return extractor.getText();
    }

    public static String parseDOCX(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        XWPFDocument document = new XWPFDocument(fis);
        StringBuilder text = new StringBuilder();
        document.getParagraphs().forEach(paragraph -> text.append(paragraph.getText()).append("\n"));
        return text.toString();
    }
}
