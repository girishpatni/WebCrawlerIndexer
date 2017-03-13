package crawling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

public class Hello {

	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<>();
		list.add("car");
		list.add("cars");
		list.add("car's");
		list.add("cars");
		System.out.println(stem(list));
	}

	public static Set<String> stem(List<String> input) {
		Set<String> output = new HashSet<>();
		SnowballStemmer snowballStemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
		for (String word : input) {
			output.add(snowballStemmer.stem(word).toString());
		}
		return output;
	}

}