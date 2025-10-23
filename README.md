# Uni-IR-and-RAG-Systems

Individual projects made during the Information Retrieval & Text Mining course taken in the 2nd year of the Artificial Intelligence master program at the Faculty of Mathematics and Computer Science, University of Bucharest.

## Information Retrieval (IR) System

The main idea of the project was to develop, in the programming language of our choice, an indexer as well as a searcher, for Romanian texts written in different file formats (.txt, .pdf, .doc, .docx), and use them in order to query the indexed documents.

#### Indexer Component

In order to implement the indexer, I chose to use functionalities from the Apache ecosystem. Thus, for the parsing of .pdf, .doc, and .docx files, I made use of the PDFBox library, and the HWPF and XWPF APIs.

#### Searcher Component

For the implementation of the searcher, I created a custom analyzer that utilizes elements from the **Apache Lucene** library, in the following order: a tokenizer, a filter for converting text to lower case, a filter for stop words removal, and a filter for stemming. For the removal of stop words, the original [stop words list](https://github.com/apache/lucene/blob/main/lucene/analysis/common/src/resources/org/apache/lucene/analysis/ro/stopwords.txt) used in the implementation of the **RomanianAnalyzer** tool from Lucene was utilized, and in addition to the listed stop words, their non-diacritics versions were also manually added. Moreover, right after the stemming operation, the searcher also performs diacritical removal, as the requirements mentioned the possibility of inputting in the query string a word/sentence without diacritics and still finding the documents that contain the diacritics version of the given word/sentence.

#### Other Details

Between multiple runs of the application, indexed files are saved and loaded to and from a local directory managed automatically by the program.

The program assures that if the same file (each file is identified by their absolute path) is attempted to be indexed twice, only the current content will be indexed, so as not to have multiple versions of the same file indexed.

The searcher returns only the top 5 documents containing the queried information, in descending order of the number of apparitions.

#### How to Run the Application

```
mvn package
java -jar target/docsearch-1.0-SNAPSHOT.jar -index -directory <path to docs>
java -jar target/docsearch-1.0-SNAPSHOT.jar -search -query <keyword>
```

## Retrieval Augmented Generation (RAG) System

For this project, the requirement was to improve an already existing information retrieval system by integrating it into a RAG pipeline. The information retrieval and **LLM** components had to be accustomed with the Romanian language. The RAG system had to be tested on a few chosen Romanian texts, based on which it would return a sorted list of documents that were relevant for the inputted query.

#### Document Preparation and Preprocessing

The texts used were prose literary works written by Romanian authors (.txt, .doc, .docx, and .pdf files), and they were chosen because they captured the nuances and semantics of Romanian in a suitable manner. Furthermore, they were read and split into chunks with the help of the LangChain library.

The only preprocessing realized consisted of replacing certain unusual and possibly confusing characters for the system, with their normal counterparts, namely: “, ”, ş, Ş, ţ, Ţ; which were replaced with: ", ș, Ș, ț, Ț. Other preprocessing means were not explored so as not to exclude important information for the embeddings model associated with the IR component and the LLM.

#### Information Retrieval Component

In order to achieve fast semantic search and retrieval, the **ChromaDB** vector database was chosen. Moreover, while feeding text to the database, their embeddings were also stored with the help of the [all-MiniLM-L6-v2](https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2) pretrained sentence transformer from Hugging Face.

#### Augmentation Component

Augmenting the query inputted by the user was realized through multiple means. First, the relevant content retrieved by ChromaDB was merged with query. Then, the following **prompt engineering** strategies were employed: instructions, specially formatted tokens that facilitated the recognition of the LLM, and few shot prompting.

#### Generative Component

Finally, the augmented query was sent to a pretrained LLM, namely the [RoLlama2-7b-Instruct](https://huggingface.co/OpenLLM-Ro/RoLlama2-7b-Instruct) created by OpenLLM-Ro. Due to hardware constraints, the model went through 4-bit quantization before being utilized, which was realized by Kaggle user [gpreda](https://www.kaggle.com/gpreda).

> Note: for further details about the project solution, please consult [this path](https://github.com/alexsasu/Uni-IR-and-RAG-Systems/tree/main/Retrieval%20Augmented%20Generation%20System/Documentation) inside the repository
