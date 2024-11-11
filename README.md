# IR-and-TM-Systems

Individual projects made during the Information Retrieval & Text Mining course taken in the 2nd year of the Artificial Intelligence master program at the Faculty of Mathematics and Computer Science, University of Bucharest.

## Information Retrieval System

The main idea of the project was to develop, in the programming language of our choice, an indexer as well as a searcher, for Romanian texts written in different file formats (.txt, .pdf, .doc, .docx), and use them in order to query the indexed documents. 

#### The Indexer

In order to implement the indexer, I chose to use functionalities from the Apache ecosystem. Thus, for the parsing of .pdf, .doc, and .docx files, I made use of the PDFBox library, and the HWPF and XWPF APIs.

#### The Searcher

For the implementation of the searcher, I created a custom analyzer that utilizes elements from the **Apache Lucene** library, in the following order: a tokenizer, a filter for converting text to lower case, a filter for stop words removal, and a filter for stemming. For the removal of stop words, the original [stop words list](https://github.com/apache/lucene/blob/main/lucene/analysis/common/src/resources/org/apache/lucene/analysis/ro/stopwords.txt) used in the implementation of the **RomanianAnalyzer** tool from Lucene was utilized, and in addition to the listed stop words, their non-diacritics versions were also manually added. Moreover, right after the stemming operation, the searcher also performs diacritical removal, as we wanted to be able to input in the query string a word/sentence without diacritics and still find the documents that contain the diacritics version of the given word/sentence.

#### Other Details

Between multiple runs of the application, indexed files are saved and loaded to and from a local directory managed automatically by the program, while the program also assures that the same file isn't indexed twice by keeping track of a serialized file containing the absolute path of each indexed file.

#### How to Run the Application

```
mvn package
java -jar target/docsearch-1.0-SNAPSHOT.jar -index -directory <path to docs>
java -jar target/docsearch-1.0-SNAPSHOT.jar -search -directory <path to docs> -query <keyword>
```
