package org.example.customAnalyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.text.Normalizer;

public class CustomRomanianDiacriticFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    public CustomRomanianDiacriticFilter(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            String term = termAtt.toString();
            term = removeDiacritics(term);
            termAtt.setEmpty().append(term);

            return true;
        } else {
            return false;
        }
    }

    private String removeDiacritics(String input) {
        String normalized = Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return normalized;
    }
}
