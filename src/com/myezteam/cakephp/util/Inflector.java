package com.myezteam.cakephp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert word between singular and plural
 * 
 * @author Darcy Young
 */
public abstract class Inflector {

	private static class Replacement {
		private Pattern rule;

		private String replacement;

		Replacement(Pattern rule, String replacement) {
			this.rule = rule;
			this.replacement = replacement;
		}
	}

	private static List<Replacement> plurals = new ArrayList<Replacement>();
	private static List<Replacement> singulars = new ArrayList<Replacement>();
	private static List<String> uncountables = new ArrayList<String>();
	private static Replacement underscore = null;

	public static String singularize(String plural) {
		if (uncountables.contains(plural.toLowerCase())) {
			return plural;
		}
		for (Replacement replacement : singulars) {
			Matcher matcher = replacement.rule.matcher(plural);
			if (matcher.find()) {
				return matcher.replaceFirst(replacement.replacement);
			}
		}
		return plural;
	}

	public static String pluralize(String singular) {
		if (uncountables.contains(singular.toLowerCase())) {
			return singular;
		}
		for (Replacement replacement : plurals) {
			Matcher matcher = replacement.rule.matcher(singular);
			if (matcher.find()) {
				return matcher.replaceFirst(replacement.replacement);
			}
		}
		return singular;
	}

	public static String capitalize(String str) {
		return capitalize(str, null);
	}

	public static String capitalize(String str, char[] delimiters) {
		if (str == null || str.length() == 0) {
			return str;
		}
		int strLen = str.length();
		StringBuffer buffer = new StringBuffer(strLen);

		int delimitersLen = 0;
		if (delimiters != null) {
			delimitersLen = delimiters.length;
		}

		boolean capitalizeNext = true;
		for (int i = 0; i < strLen; i++) {
			char ch = str.charAt(i);

			boolean isDelimiter = false;
			if (delimiters == null) {
				isDelimiter = Character.isWhitespace(ch);
			} else {
				for (int j = 0; j < delimitersLen; j++) {
					if (ch == delimiters[j]) {
						isDelimiter = true;
						break;
					}
				}
			}

			if (isDelimiter) {
				buffer.append(ch);
				capitalizeNext = true;
			} else if (capitalizeNext) {
				buffer.append(Character.toTitleCase(ch));
				capitalizeNext = false;
			} else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}

	public static String camelize(String lowerCaseAndUnderscoredWord) {
		String replace = lowerCaseAndUnderscoredWord.replace("_", " ");
		replace = capitalize(replace);
		replace = replace.replace(" ", "");
		return replace;
	}

	public static String underscorize(String camelCasedWord) {
		Matcher matcher = underscore.rule.matcher(camelCasedWord);
		if (matcher.find()) {
			return matcher.replaceAll(underscore.replacement).toLowerCase();
		}
		return camelCasedWord;
	}

	public static String humanize(String lowerCaseAndUnderscoredWord) {
		String replace = lowerCaseAndUnderscoredWord.replace("_", " ");
		replace = capitalize(replace);
		return replace;
	}

	public static String tableize(String className) {
		String replace = underscorize(className);
		replace = pluralize(replace);
		return replace;
	}

	public static String classify(String tableName) {
		String replace = singularize(tableName);
		replace = camelize(replace);
		return replace;
	}

	public static String variable(String string) {
		String replace = underscorize(string);
		replace = camelize(replace);
		replace = replace.trim();
		if (replace.length() < 1) {
			return "";
		}
		return replace.substring(0, 1).toLowerCase() + replace.substring(1);
	}

	public static void plural(Pattern rule, String replacement) {
		plurals.add(0, new Replacement(rule, replacement));
	}

	public static void singular(Pattern rule, String replacement) {
		singulars.add(0, new Replacement(rule, replacement));
	}

	public static void irregular(String singular, String plural) {
		plural(Pattern.compile(
				"(" + singular.charAt(0) + ")" + singular.substring(1) + "$",
				Pattern.CASE_INSENSITIVE), "$1" + plural.substring(1));
		singular(
				Pattern.compile(
						"(" + plural.charAt(0) + ")" + plural.substring(1)
								+ "$", Pattern.CASE_INSENSITIVE), "$1"
						+ singular.substring(1));
	}

	public static void uncountable(String[] words) {
		for (int i = 0; i < words.length; i++) {
			uncountables.add(words[i]);
		}
	}

	static {
		Inflector.underscore = new Replacement(
				Pattern.compile("(?<=\\w)([A-Z])"), "_$1");
		Inflector.plural(Pattern.compile("$"), "s");
		Inflector.plural(Pattern.compile("s$", Pattern.CASE_INSENSITIVE), "s");
		Inflector.plural(
				Pattern.compile("(ax|test)is$", Pattern.CASE_INSENSITIVE),
				"$1es");
		Inflector.plural(
				Pattern.compile("(octop|vir)us$", Pattern.CASE_INSENSITIVE),
				"$1i");
		Inflector.plural(
				Pattern.compile("(alias|status)$", Pattern.CASE_INSENSITIVE),
				"$1es");
		Inflector.plural(Pattern.compile("(bu)s$", Pattern.CASE_INSENSITIVE),
				"$1ses");
		Inflector.plural(
				Pattern.compile("(buffal|tomat)o$", Pattern.CASE_INSENSITIVE),
				"$1oes");
		Inflector.plural(
				Pattern.compile("([ti])um$", Pattern.CASE_INSENSITIVE), "$1a");
		Inflector.plural(Pattern.compile("sis$", Pattern.CASE_INSENSITIVE),
				"ses");
		Inflector.plural(Pattern.compile("(?:([^f])fe|([lr])f)$",
				Pattern.CASE_INSENSITIVE), "$1$2ves");
		Inflector.plural(Pattern.compile("(hive)$", Pattern.CASE_INSENSITIVE),
				"$1s");
		Inflector.plural(
				Pattern.compile("([^aeiouy]|qu)y$", Pattern.CASE_INSENSITIVE),
				"$1ies");
		Inflector.plural(
				Pattern.compile("(x|ch|ss|sh)$", Pattern.CASE_INSENSITIVE),
				"$1es");
		Inflector.plural(Pattern.compile("(matr|vert|ind)ix|ex$",
				Pattern.CASE_INSENSITIVE), "$1ices");
		Inflector.plural(
				Pattern.compile("([m|l])ouse$", Pattern.CASE_INSENSITIVE),
				"$1ice");
		Inflector.plural(Pattern.compile("^(ox)$", Pattern.CASE_INSENSITIVE),
				"$1en");
		Inflector.plural(Pattern.compile("(quiz)$", Pattern.CASE_INSENSITIVE),
				"$1zes");

		Inflector.singular(Pattern.compile("s$", Pattern.CASE_INSENSITIVE), "");
		Inflector.singular(
				Pattern.compile("(n)ews$", Pattern.CASE_INSENSITIVE), "$1ews");
		Inflector.singular(
				Pattern.compile("([ti])a$", Pattern.CASE_INSENSITIVE), "$1um");
		Inflector
				.singular(
						Pattern.compile(
								"((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$",
								Pattern.CASE_INSENSITIVE), "$1$2sis");
		Inflector.singular(
				Pattern.compile("(^analy)ses$", Pattern.CASE_INSENSITIVE),
				"$1sis");
		Inflector
				.singular(
						Pattern.compile("([^f])ves$", Pattern.CASE_INSENSITIVE),
						"$1fe");
		Inflector.singular(
				Pattern.compile("(hive)s$", Pattern.CASE_INSENSITIVE), "$1");
		Inflector.singular(
				Pattern.compile("(tive)s$", Pattern.CASE_INSENSITIVE), "$1");
		Inflector.singular(
				Pattern.compile("([lr])ves$", Pattern.CASE_INSENSITIVE), "$1f");
		Inflector
				.singular(Pattern.compile("([^aeiouy]|qu)ies$",
						Pattern.CASE_INSENSITIVE), "$1y");
		Inflector.singular(
				Pattern.compile("(s)eries$", Pattern.CASE_INSENSITIVE),
				"$1eries");
		Inflector.singular(
				Pattern.compile("(m)ovies$", Pattern.CASE_INSENSITIVE),
				"$1ovie");
		Inflector.singular(
				Pattern.compile("(x|ch|ss|sh)es$", Pattern.CASE_INSENSITIVE),
				"$1");
		Inflector.singular(
				Pattern.compile("([m|l])ice$", Pattern.CASE_INSENSITIVE),
				"$1ouse");
		Inflector.singular(
				Pattern.compile("(bus)es$", Pattern.CASE_INSENSITIVE), "$1");
		Inflector.singular(Pattern.compile("(o)es$", Pattern.CASE_INSENSITIVE),
				"$1");
		Inflector.singular(
				Pattern.compile("(shoe)s$", Pattern.CASE_INSENSITIVE), "$1");
		Inflector.singular(
				Pattern.compile("(cris|ax|test)es$", Pattern.CASE_INSENSITIVE),
				"$1is");
		Inflector.singular(
				Pattern.compile("(octop|vir)i$", Pattern.CASE_INSENSITIVE),
				"$1us");
		Inflector.singular(
				Pattern.compile("(alias|status)es$", Pattern.CASE_INSENSITIVE),
				"$1");
		Inflector.singular(
				Pattern.compile("^(ox)en", Pattern.CASE_INSENSITIVE), "$1");
		Inflector.singular(
				Pattern.compile("(vert|ind)ices$", Pattern.CASE_INSENSITIVE),
				"$1ex");
		Inflector.singular(
				Pattern.compile("(matr)ices$", Pattern.CASE_INSENSITIVE),
				"$1ix");
		Inflector.singular(
				Pattern.compile("(quiz)zes$", Pattern.CASE_INSENSITIVE), "$1");

		Inflector.irregular("person", "people");
		Inflector.irregular("man", "men");
		Inflector.irregular("child", "children");
		Inflector.irregular("sex", "sexes");
		Inflector.irregular("move", "moves");

		Inflector.uncountable(new String[] { "equipment", "information",
				"rice", "money", "species", "series", "fish", "sheep" });
	}
}
