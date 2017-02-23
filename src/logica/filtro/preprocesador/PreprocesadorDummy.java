package logica.filtro.preprocesador;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * Implementación de un preprocesador de texto de un tweet que convierte texto plano
 * en una lista de caracteristicas para un modelo de aprendizaje de máquina.
 * 
 * 1) Tokenización y lematización: librerias de NLP de Stanford
 * 		http://nlp.stanford.edu/software/corenlp.shtml
 * 		Librerias: stanford-corenlp.jar stanfor-corenlp-models.jar xom.jar joda-time.jar jollyday.jar
 * 2) Stop words: 
 * 		Function words tomadas de: http://www.sequencepublishing.com/academic.html
 * 
 * @author Eddie Contreras
 */
public class PreprocesadorDummy {
	StanfordCoreNLP stanfordNLP;
	HashSet<String> stopWords;

	/**
	 * Creador
	 */
	public PreprocesadorDummy() {
		//Crea un objeto StanfordCoreNLP con las operaciones [tokenización, partición de oraciones, POS, lematizacion]
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		stanfordNLP = new StanfordCoreNLP(props);
		
		//Crea el conjunto de stop words
		stopWords = getStopWords();
	}

	/**
	 * Preprocesa el texto del tweet
	 * @param texto a preprocesar
	 * @return lista de características
	 */
	public HashSet<String> preprocesar(String texto) {
		HashSet<String> caracteristicas = new HashSet<String>();
		
		// Remover menciones, urls y hashtags. 
		// Remover el caracter '#' de los hashTags 
		String regexMenciones = "(@[\\w]*)";
		String regexUrls = "(htt[^\\s]*)";
		String regexHashTags = "#";
		String regexARemover = String.format("%s|%s|%s", regexMenciones, regexUrls, regexHashTags);
		texto = texto.replaceAll(regexARemover, "");
		
		// Separar palabras unidas por símbolos (e.g. pal~pal)
		String regexPalabrasUnidas = "(?<=[\\w])[-~_.,:+*^><=/]+(?=[\\w])";
		texto = texto.replaceAll(regexPalabrasUnidas, " ");
		
		// Separar hashTags en estilo 'CamelCase'.
		String regexCamelCase = String.format("%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[a-z])(?=[A-Z])"); 
		texto = texto.replaceAll(regexCamelCase, " ");
		
		// Aplica operaciones de NLP sobre el texto 
		Annotation document = new Annotation(texto);
		stanfordNLP.annotate(document);

		//Recorre las oraciones
		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			//Recorre los tokens en cada oración
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String palabra = token.get(TextAnnotation.class);	//texto del token
				String lemma = token.get(LemmaAnnotation.class); 	//lemma

				//Evalua varias condiciones sobre la palabra
				revisarPalabra(palabra, lemma, caracteristicas);
			}
		}
		
		return caracteristicas;
	}
	
	/**
	 * Evalua varias condiciones sobre la palabra, las aceptadas son 
	 * agregadas a la lista de caracteristicas
	 * @param palabra palabra a evaluar
	 * @param lemma lemma de la palabra
	 * @param caracteristicas lista de palabras aceptadas
	 */
	private void revisarPalabra(String palabra, String lemma, HashSet<String> caracteristicas) {
		
		//Palabras de menos de dos caracteres: ignorar 
		if (palabra.length() >= 2) {
			
			//Acrónimos de dos caracteres: usar palabra no lemma
			if (esAcronimo2(palabra)) {
				palabra = palabra.toLowerCase();	//Normalización a minúsculas
				
				//Stopping
				if (!stopWords.contains(palabra)) {
					caracteristicas.add(palabra);
				}		
			//Números: convertir a "num"
			} else if (esNumero(palabra)) {
				palabra = "num";
				caracteristicas.add(palabra);

			} else {			
				lemma = lemma.toLowerCase(); 	//Normalización a minúsculas
				
				//Stopping
				if (!stopWords.contains(lemma)) {
					caracteristicas.add(lemma);
				}	
			}
		}
	}

	/**
	 * Evalua si una palabra de dos caracteres es acrónimo
	 * @param palabra
	 * @return esAcronimo
	 */
	private boolean esAcronimo2(String palabra) {
		boolean esAcronimo = false;

		if (palabra.length() == 2) {
			char[] palabraChar = palabra.toCharArray();
			if (Character.isUpperCase(palabraChar[0]) && Character.isUpperCase(palabraChar[1])) {
				esAcronimo = true;
			}
		}
		return esAcronimo;
	}
	
	/**
	 * Evalua si una palabra es un numero
	 * @param palabra
	 * @return esNumero
	 */
	private boolean esNumero(String palabra) {
		boolean esNumero = false;
		
		try {
			Double.parseDouble(palabra);
			esNumero = true;
		} catch (Exception e) {
			esNumero = false;
		}	
		return esNumero;
	}

	/**
	 * Lista de stop words
	 * Function words tomadas de: http://www.sequencepublishing.com/academic.html
	 * [REV] http://dev.mysql.com/doc/refman/5.7/en/fulltext-stopwords.html
	 * [REV] http://www.lextek.com/manuals/onix/stopwords1.html
	 * [REV] http://norm.al/2009/04/14/list-of-english-stop-words/
	 */
	private HashSet<String> getStopWords() {
		HashSet<String> stopWordsSet = new HashSet<String>();
		
		/* Verbos auxiliares: 
		 * [compuestos] be able to, had better, have to, need to, ought to, used to */
		String[] englishAuxiliaryVerbs = {"can", "could", "dare", "may", "might", "must", "ought", "shall", "should", "will", "would"};

		/* Conjunciones*/
		String[] englishConjunctions = {"accordingly", "after", "albeit", "although", "and", "as", "because", "before", "both", "but", "consequently", "either", "for", "hence", "however", "if", "neither", "nevertheless", "nor", "once", "or", "since", "so", "than", "that", "then", "thence", "therefore", "tho", "though", "thus", "till", "unless", "until", "when", "whenever", "where", "whereas", "wherever", "whether", "while", "whilst", "yet"};

		/* Determinantes*/
		String[] englishDeterminers = {"a", "all", "an", "another", "any", "both", "each", "either", "every", "her", "his", "its", "my", "neither", "no", "other", "our", "per", "some", "that", "the", "their", "these", "this", "those", "whatever", "whichever", "your"};

		/* Preposiciones	
		 * [compuestos]: according to, ahead of, all over, as of, as to, away from, because of, by the time of, close by, close to, due to, except for, for all, in between, in front of, in keeping with, in place of, in spite of, in view of, instead of, near to, next to, on top of, other than, out of, pertaining to, similar to, thanks to, up to */
		String[] englishPrepositions = {"aboard", "about", "above", "absent", "across", "after", "against", "ahead", "along", "alongside", "amid", "amidst", "among", "amongst", "anti", "around", "as", "aside", "astraddle", "astride", "at", "bar", "barring", "before", "behind", "below", "beneath", "beside", "besides", "between", "beyond", "but", "by", "circa", "concerning", "considering", "despite", "down", "during", "except", "excepting", "excluding", "failing", "following", "for", "from", "given", "in", "including", "inside", "into", "less", "like", "minus", "near", "notwithstanding", "of", "off", "on", "onto", "opposite", "out", "outside", "over", "past", "pending", "per", "plus", "regarding", "respecting", "round", "save", "saving", "since", "than", "through", "throughout", "thru", "till", "to", "toward", "towards", "under", "underneath", "unlike", "until", "unto", "up", "upon", "versus", "via", "wanting", "with", "within", "without"};

		/* Pronombres 
		 * [compuestos]: each other, no one, one another */
		String[] englishPronouns = {"all", "another", "any", "anybody", "anyone", "anything", "both", "each", "either", "everybody", "everyone", "everything", "few", "he", "her", "hers", "herself", "him", "himself", "his", "i", "it", "its", "itself", "many", "me", "mine", "myself", "neither", "nobody", "none", "nothing", "one", "other", "ours", "ourselves", "several", "she", "some", "somebody", "someone", "something", "such", "that", "theirs", "them", "themselves", "these", "they", "this", "those", "us", "we", "what", "whatever", "which", "whichever", "who", "whoever", "whom", "whomever", "whose", "you", "yours", "yourself", "yourselves"};

		/* Cuantificadores
		 * [compuestos]: a couple of, a few, a good deal of, a good many, a great deal of, a great many, a lack of, a little, a little bit of, a majority of, a minority of, a number of, a plethora of, a quantity of, an amount of, heaps of, masses of, numbers of, plenty of, quantities of, the lack of, the majority of, the minority of, the number of, the plethora of, the remainder of, the rest of, the whole, tons of, {one half, one third, one fourth, one quarter, one fifth, etc.}, {one, two, three, four, etc.} */
		String[] englishQuantifiers = {"all", "another", "any", "both", "certain", "each", "either", "enough", "few", "fewer", "less", "little", "loads", "lots", "many", "more", "most", "much", "neither", "no", "none", "part", "several", "some", "various"};
	
		/* Caracteres especiales */
		String[] stopWords1 = {"'s", "'ll", "...", "''", "``", "'", "--", "\"" ,"\\*", "\\/", "-lsb-", "-rsb-", "-lrb-", "-rrb-", "-lcb-", "-rcb-"};
		
		/* Otras palabras */
		String[] stopWords2 = {"rt", "via", "cc", "be", "not", "do"};
		
		stopWordsSet.addAll(Arrays.asList(englishAuxiliaryVerbs));
		stopWordsSet.addAll(Arrays.asList(englishConjunctions));
		stopWordsSet.addAll(Arrays.asList(englishDeterminers));
		stopWordsSet.addAll(Arrays.asList(englishPrepositions));
		stopWordsSet.addAll(Arrays.asList(englishPronouns));
		stopWordsSet.addAll(Arrays.asList(englishQuantifiers));
		stopWordsSet.addAll(Arrays.asList(stopWords1));
		stopWordsSet.addAll(Arrays.asList(stopWords2));
		
		return stopWordsSet;
	}
}
