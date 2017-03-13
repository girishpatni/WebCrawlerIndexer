package crawling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

public class LuceneIndexer {
	private static Path currentRelativePath = Paths.get("");
	private static File dataDir = currentRelativePath.toAbsolutePath().toFile();
	private static Directory indexDir = new RAMDirectory();
	private static StandardAnalyzer analyzer = new StandardAnalyzer();

	public static void indexDirectory(IndexWriter writer, File dir) throws IOException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(writer, f); // recurse
			} else if (f.getName().endsWith(".txt")) {
				indexFile(writer, f);
			}
		}
	}

	public static void indexFile(IndexWriter writer, File f) throws IOException {
		System.out.println("Indexing " + f.getName());
		Document doc = new Document();
		doc.add(new TextField("filename", f.getName(), TextField.Store.YES));
		try {
			FileInputStream is = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line).append("\n");
			}
			reader.close();
			doc.add(new TextField("contents", stringBuffer.toString(), TextField.Store.YES));
		} catch (Exception e) {
			System.out.println("something wrong with indexing content of the files");
		}
		writer.addDocument(doc);
	}

	public static void indexDocument() throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(indexDir, config);
		indexDirectory(writer, dataDir);
		writer.close();
	}

	public static void queryTopic(String topic) {
		String querystr = "contents:" + topic;
		Query query;
		try {
			query = new QueryParser("contents", analyzer).parse(querystr);
			int hitsPerPage = 10;
			IndexReader reader = null;
			TopScoreDocCollector collector = null;
			IndexSearcher searcher = null;
			try {
				reader = DirectoryReader.open(indexDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
			searcher = new IndexSearcher(reader);
			collector = TopScoreDocCollector.create(hitsPerPage);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			System.out.println("Found " + hits.length + " hits.");
			System.out.println();

			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d;
				d = searcher.doc(docId);
				System.out.println((i + 1) + ". " + d.get("filename"));
			}
			reader.close();
		} catch (ParseException | IOException e1) {
			e1.printStackTrace();
		}
	}

	public List<String> stem(List<String> input) {
		List<String> output = new ArrayList<>();
		SnowballStemmer snowballStemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
		for (String word : input) {
			output.add(snowballStemmer.stem(word).toString());
		}
		return output;
	}

}
