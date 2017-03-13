package crawling;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaTopics {
	static final String[] topics = { "java", "statements", "condition", "loop", "expression", "boolean", "datatype",
			"variable", "statements", "conditional blocks", "loop blocks", "boolean expressions", "variables",
			"primitive Types", "arithmetic expressions", "literals", "methods", "string", "objects", "packages",
			"arrays", "mathematical functions", "large numbers", "random numbers", "unicode", "comments", "keywords",
			"coding conventions", "networking", "database programming", "regular expressions", "library", "reflection",
			"applets", "graphics", "canvas", "javaBeans", "beans", "event handling", "streams", "basic I/O",
			"annotations", "concurrency", "threads", " runnables", "synchronization", "client", "server",
			"remote method invocation", "RMI", "stack trace", "stacktrace", "exceptions", "try", "catch", "throw",
			"checked", "uncheked", "collection", "arrayList", "map", "class", "object", "interfaces", "inheritance",
			"overloading", "constructors" };

	public static boolean ContainJavaTopic(String input) {
		System.out.println(input);
		Arrays.sort(topics);
		String pattern = "(https://en.wikibooks.org/wiki/Java_Programming/)(.*)";
		Pattern regex = Pattern.compile(pattern);
		Matcher m = regex.matcher(input);
		String result = new String();
		if (m.find()) {
			result = m.group(2);
			System.out.println("Found value: " + result);
			return (binarySearch(topics, result, 0, topics.length - 1) >= 0) ? true : false;
		}
		return false;
	}

	private static int binarySearch(String[] words, String value, int min, int max) {
		if (min > max) {
			return -1;
		}
		int mid = (max + min) / 2;
		if (words[mid].equals(value)) {
			return mid;
		} else if (words[mid].compareTo(value) > 0) {
			return binarySearch(words, value, min, mid - 1);
		} else {
			return binarySearch(words, value, mid + 1, max);
		}
	}

}
