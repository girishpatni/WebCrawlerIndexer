package crawling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

public class CrawlerClass {
	public static DataBaseHandler db = new DataBaseHandler();

	public static void main(String[] args) throws SQLException, IOException {
		db.runSql2("TRUNCATE Record;");
		processPage("https://en.wikibooks.org/wiki/Java_Programming");
		LuceneIndexer.indexDocument();
		LuceneIndexer.queryTopic("Null Pointer");
	}

	public static void processPage(String URL) throws SQLException, IOException {
		String sql = "select * from Record where URL = '" + URL + "'";
		ResultSet rs = db.runSql(sql);
		if (rs.next()) {

		} else {
			sql = "INSERT INTO  `Crawler`.`Record` " + "(`URL`) VALUES " + "(?);";
			PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, URL);
			stmt.execute();
			Document doc = Jsoup.connect(URL).get();
			writeDocumentToFile(doc);
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				if (link.attr("href").contains("Java") && hasJavaTopics(link.attr("abs:href"))) {
					processPage(link.attr("abs:href"));
				}
			}
		}
	}

	private static boolean hasJavaTopics(String url) {
		String pattern = "(https://en.wikibooks.org/wiki/Java_Programming/)(.*)";
		Pattern regex = Pattern.compile(pattern);
		Matcher m = regex.matcher(url);
		return (m.find()) ? true : false;
	}

	private static void writeDocumentToFile(Document doc) {
		File file = new File(doc.title() + ".txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				String text = stemProcessing(doc.text());	 //taking text data from doc and then stemming it			
				bw.write(text);
				bw.close();
			} catch (IOException e) {
				System.out.println("Unable to Write" + doc.title());
			}
		}
	}

	public static String stemProcessing(String input) {
		StringBuffer output = new StringBuffer();
		List<String> words = Arrays.asList(input.split(""));
		SnowballStemmer snowballStemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
		for (String word : words) {
			output.append(snowballStemmer.stem(word).toString());
		}
		return output.toString();
	}
}
