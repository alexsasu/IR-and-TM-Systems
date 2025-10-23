package org.example.customAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.tartarus.snowball.ext.RomanianStemmer;
import org.apache.lucene.analysis.CharArraySet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CustomRomanianAnalyzer extends Analyzer {

    private final CharArraySet stopWords;

    public CustomRomanianAnalyzer() throws IOException {
        String stopWordsFilePath = "src/main/java/org/example/utils/stopwords.txt";

        // Reading the stop words from file
        Integer initialCapacity = 16;
        Boolean ignoreCase = true;
        CharArraySet stopWords = new CharArraySet(initialCapacity, ignoreCase);
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    stopWords.add(line);
                }
            }
        }

        this.stopWords = stopWords;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
        tokenStream = new StopFilter(tokenStream, stopWords);
        tokenStream = new SnowballFilter(tokenStream, new RomanianStemmer());
        tokenStream = new CustomRomanianDiacriticFilter(tokenStream);
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}
