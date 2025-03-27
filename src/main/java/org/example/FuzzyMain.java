package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FuzzyMain {

	static final int GENRE_IDX = 2;

	static final    int POP_IDX = 9;
	static final double POP_MAX = 900;
	static final double POP_MIN = 0.0;

	static final int TITLE_IDX  = 18;

	static final    int VOTE_AVG_IDX = 19;
	static final double VOTE_AVG_MAX = 10.0;
	static final double VOTE_AVG_MIN = 0.0;

	static final int VOTE_CT_IDX  = 20;
	static final int VOTE_CT_MAX = 13752;
	static final int VOTE_CT_MIN = 0;

	public static void main(String[] args) {

		var gVoteAvg = new FuzzyVar();
		gVoteAvg.add(new Trapezoid("gVa_Ruim", VOTE_AVG_MIN, VOTE_AVG_MIN, 3.5, 6.25));
		gVoteAvg.add(new Trapezoid("gVa_Passável", 1, 3.75, 4.5, 9));
		gVoteAvg.add(new Trapezoid("gVa_Bom", 2.0, 5.75, 7.75, VOTE_AVG_MAX));
		gVoteAvg.add(new Trapezoid("gVa_Ótimo", 5.0, 8, VOTE_AVG_MAX, VOTE_AVG_MAX));

		var gVoteCt  = new FuzzyVar();
		gVoteCt.add(new Trapezoid("gVc_NãoConfiável", VOTE_CT_MIN, VOTE_CT_MIN, 10, 40));
		gVoteCt.add(new Trapezoid("gVc_ParcialmenteConfiável", 8, 65, 175, 200));
		gVoteCt.add(new Trapezoid("gVc_Confiável", 15, 220, 800,  1000));
		gVoteCt.add(new Trapezoid("gVc_MuitoConfiável", 20, 1000, VOTE_CT_MAX, VOTE_CT_MAX));

		var gPop     = new FuzzyVar();
		gPop.add(new Trapezoid("gPop_Obscuro", POP_MIN, POP_MIN, 2, 65));
		gPop.add(new Trapezoid("gPop_Conhecido", 7, 15, 95, 130));
		gPop.add(new Trapezoid("gPop_Icônico", 15, 130, POP_MAX, POP_MAX));

		var gGenre = new FuzzyVar();
		gGenre.add(new Trapezoid("gGenre_Péssimo", 0,0,0.1,0.35));
		gGenre.add(new Trapezoid("gGenre_Ruim", 0,0.25,0.4,0.75));
		gGenre.add(new Trapezoid("gGenre_Mediano", 0.2,0.5,0.6,0.85));
		gGenre.add(new Trapezoid("gGenre_Bom", 0.5,0.6,0.75,1.0));
		gGenre.add(new Trapezoid("gGenre_Ótimo", 0.5,0.75,1.0,1.0));

		var gCredibilidade = new FuzzyVar();
		gCredibilidade.add(new Trapezoid("NC", 0, 0, 3, 6));
		gCredibilidade.add(new Trapezoid("C", 5, 7, 8, 10));
		gCredibilidade.add(new Trapezoid("MC", 7, 9, 10, 10));

		var gAtratividade = new FuzzyVar();
		gAtratividade.add(new Trapezoid("NA", 0, 0, 3, 6));
		gAtratividade.add(new Trapezoid("A", 5, 7, 8, 10));
		gAtratividade.add(new Trapezoid("MA", 7, 9, 10, 10));

		var gRecomenditividade = new FuzzyVar();
		gRecomenditividade.add(new Trapezoid("NR", 0, 0, 3, 6));
		gRecomenditividade.add(new Trapezoid("R", 5, 7, 8, 10));
		gRecomenditividade.add(new Trapezoid("MR", 7, 9, 10, 10));

		List<Pair<Double, String>> results = new ArrayList<>();

		try {

			BufferedReader bfr = new BufferedReader(new FileReader("movie_dataset.csv"));

			for (Iterator<CSVRecord> it = CSVFormat.DEFAULT.parse(bfr).stream().skip(1).iterator(); it.hasNext(); ) {
				var record = it.next();
				var store = new HashMap<String,Double>();

				var voteavg    = Double.parseDouble(record.get(VOTE_AVG_IDX));
				var votect     = Double.parseDouble(record.get(VOTE_CT_IDX));
				var popularity = Double.parseDouble(record.get(POP_IDX));
				var title      = record.get(TITLE_IDX);
				var genres     = List.of(record.get(GENRE_IDX).split(" "));

				gVoteAvg.fuzz(voteavg,           store);
				gVoteCt .fuzz(votect,            store);
				gPop    .fuzz(popularity,        store);
				gGenre  .fuzz(generoAvg(genres), store);

				String[] ctRows = { "gVa_Ruim", "gVa_Passável", "gVa_Bom", "gVa_Ótimo" };
				String[] ctCols = { "gVc_NãoConfiável", "gVc_ParcialmenteConfiável", "gVc_Confiável", "gVc_MuitoConfiável" };
				String[][] credibilityTable = {
						// Ruim   Passável Bom   Ótimo
						{  "NC",  "NC",    "NC", "NC", }, // NãoConfiável
						{  "NC",  "NC",    "C",  "C", },  // ParcialmenteConfiavel
						{  "NC",  "C",     "C",  "MC", }, // Confiável
						{  "NC",  "C",     "MC", "MC" },  // MuitoConfiável
				};

				for (int y = 0; y < 4; y++) for (int x = 0; x < 4; x++)
					rodaRegraE(store, ctRows[y], ctCols[x], credibilityTable[y][x]);

				// NC e gPop_Icônico   -> NA
				// C  e gPop_Icônico   -> A
				// MC e gPop_Icônico   -> MA
				// NC e gPop_Conhecido -> NA
				// C  e gPop_Conhecido -> A
				// MC e gPop_Conhecido -> MA
				// NC e gPop_Obscuro   -> NA
				// C  e gPop_Obscuro   -> NA
				// MC e gPop_Obscuro   -> A

				rodaRegraE(store, "NC", "gPop_Icônico", "NA");
				rodaRegraE(store, "C", "gPop_Icônico", "A");
				rodaRegraE(store, "MC", "gPop_Icônico", "MA");
				rodaRegraE(store, "NC", "gPop_Conhecido", "NA");
				rodaRegraE(store, "C", "gPop_Conhecido", "A");
				rodaRegraE(store, "MC", "gPop_Conhecido", "MA");
				rodaRegraE(store, "NC", "gPop_Obscuro", "NA");
				rodaRegraE(store, "C", "gPop_Obscuro", "NA");
				rodaRegraE(store, "MC", "gPop_Obscuro", "A");

				// NA e gGenre_Ótimo   -> R
				// A  e gGenre_Ótimo   -> MR
				// MA e gGenre_Ótimo   -> MR
				// NA e gGenre_Bom     -> R
				// A  e gGenre_Bom     -> R
				// MA e gGenre_Bom     -> MR
				// NA e gGenre_Mediano -> NR
				// A  e gGenre_Mediano -> R
				// MA e gGenre_Mediano -> MR
				// NA e gGenre_Ruim    -> NR
				// A  e gGenre_Ruim    -> NR
				// MA e gGenre_Ruim    -> R
				// NA e gGenre_Péssimo -> NR
				// A  e gGenre_Péssimo -> NR
				// MA e gGenre_Péssimo -> NR

				rodaRegraE(store, "NA", "gGenre_Ótimo",  "R");
				rodaRegraE(store, "A",  "gGenre_Ótimo",  "MR");
				rodaRegraE(store, "MA", "gGenre_Ótimo",  "MR");
				rodaRegraE(store, "NA", "gGenre_Bom",    "R");
				rodaRegraE(store, "A",  "gGenre_Bom",    "R");
				rodaRegraE(store, "MA", "gGenre_Bom",    "MR");
				rodaRegraE(store, "NA", "gGenre_Mediano","NR");
				rodaRegraE(store, "A",  "gGenre_Mediano","R");
				rodaRegraE(store, "MA", "gGenre_Mediano","MR");
				rodaRegraE(store, "NA", "gGenre_Ruim","NR");
				rodaRegraE(store, "A",  "gGenre_Ruim","NR");
				rodaRegraE(store, "MA", "gGenre_Ruim","R");
				rodaRegraE(store, "NA", "gGenre_Péssimo","NR");
				rodaRegraE(store, "A",  "gGenre_Péssimo","NR");
				rodaRegraE(store, "MA", "gGenre_Péssimo","NR");

				double NR = store.get("NR");
				double R  = store.get("R");
				double MR = store.get("MR");

				double Rscore = (NR*1.5f+R*7.0f+MR*9.5f)/(NR+R+MR);

				results.add(Pair.of(Rscore, title));

			}

		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}

		results.sort(Comparator.comparing(Pair::getLeft, Comparator.reverseOrder()));
		results.forEach(System.out::println);
	}

	private static void rodaRegraE(HashMap<String, Double> asVariaveis,String var1,String var2,String varr) {
		double v = Math.min(asVariaveis.get(var1), asVariaveis.get(var2));
		asVariaveis.compute(varr, (k, vatual) -> vatual == null ? v : Math.max(vatual, v));
	}

	private static void rodaRegraOU(HashMap<String, Double> asVariaveis,String var1,String var2,String varr) {
		double v = Math.max(asVariaveis.get(var1),asVariaveis.get(var2));
		asVariaveis.compute(varr, (k, vatual) -> vatual == null ? v : Math.max(vatual, v));
	}

	private static double generoAvg(List<String> genres) {
		if (genres.isEmpty()) return 0.0;
		else
			return genres.stream().map(FuzzyMain::valorGenero).reduce(0.0, Double::sum) / genres.size();
	}

	private static double valorGenero(String genero) throws IllegalArgumentException {
		return switch (genero) {
			case "Action" -> 0.8;
			case "Adventure" -> 0.75;
			case "Animation" -> 0.8;
			case "Comedy" -> 0.8;
			case "Crime" -> 0.45;
			case "Documentary" -> 1.0;
			case "Drama" -> 0.2;
			case "Family" -> 0.1;
			case "Fantasy" -> 0.5;
			case "Fiction" -> 0.8;
			case "Foreign" -> 1.0;
			case "History" -> 1.0;
			case "Horror" -> 0.5;
			case "Music" -> 0.0;
			case "Mystery" -> 0.75;
			case "Movie" -> 0.95;
			case "Romance" -> 0.0;
			case "Science" -> 0.8;
			case "Thriller" -> 0.75;
			case "TV" -> 0.3;
			case "War" -> 0.9;
			case "Western" -> 0.4;
			// Workaround
			case " " -> 0.0;
			case ""  -> 0.0;
			default -> throw new IllegalArgumentException(genero);
		};
	}
}