package org.example;

import org.example.BetterFuzz.Trapezoid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FuzzyMain {

	public static void main(String[] args) {

		var grupoPreco = new FuzzyVar();
		grupoPreco.add(new Trapezoid("Muito Barato", 0, 0, 10, 20));
		grupoPreco.add(new Trapezoid("Barato", 10, 20, 30, 60));
		grupoPreco.add(new Trapezoid("Custo Medio", 20, 40, 50, 70));
		grupoPreco.add(new Trapezoid("Caro", 40, 60, 70, 120));
		grupoPreco.add(new Trapezoid("Muito Caro", 70, 110, 500, 500));

		var grupoRating = new FuzzyVar();
		grupoRating.add(new Trapezoid("MR",0,0,10,20));
		grupoRating.add(new Trapezoid("R",10,20,30,40));
		grupoRating.add(new Trapezoid("B",20,40,45,50));
		grupoRating.add(new Trapezoid("MB",40,48,50,50));

		var grupoAtratividade = new FuzzyVar();
		grupoRating.add(new Trapezoid("NA",0,0,3,6));
		grupoRating.add(new Trapezoid("A",5,7,8,10));
		grupoRating.add(new Trapezoid("MA",7,9,10,10));

		try {
			BufferedReader bfr = new BufferedReader(new FileReader("restaurantes_filtrados.csv"));

			String header = bfr.readLine();
			String[] splitheader = header.split(";");

			for (int i = 0; i < splitheader.length;i++) {
				System.out.println(i + " " + splitheader[i]);
			}

			String line = "";

			while ((line = bfr.readLine()) != null) {
				String[] spl = line.split(";");
				HashMap<String,Double> asVariaveis = new HashMap<>();

				float custodinheiro = Float.parseFloat(spl[3]);
				grupoPreco.fuzz(custodinheiro, asVariaveis);

				float rating = Float.parseFloat(spl[5]);
				grupoRating.fuzz(rating, asVariaveis);

				System.out.println(spl[2]+" custodinheiro "+custodinheiro+" rating "+rating);
				//System.out.println("rating "+rating+" -> "+asVariaveis);

				// Barato e B -> A
				// Muito Barato e B -> A
				// Muito Barato e MB -> MA
				// Barato e MB -> MA
				// Barato e R -> NA
				// Muito Barato e R -> A
				// Muito Barato e MR -> NA

				rodaRegraE(asVariaveis,"Barato","B","A");
				rodaRegraE(asVariaveis,"Muito Barato","B","A");
				rodaRegraE(asVariaveis,"Muito Barato","MB","MA");
				rodaRegraE(asVariaveis,"Barato","MB","MA");
				rodaRegraE(asVariaveis,"Barato","R","NA");
				rodaRegraE(asVariaveis,"Muito Barato","R","A");
				rodaRegraE(asVariaveis,"Muito Barato","MR","NA");
				rodaRegraE(asVariaveis,"Muito Caro","MR","NA");
				rodaRegraE(asVariaveis,"Muito Caro","R","NA");
				rodaRegraE(asVariaveis,"Muito Caro","B","NA");
				rodaRegraE(asVariaveis,"Muito Caro","MB","A");

				double NA = asVariaveis.get("NA");
				double A = asVariaveis.get("A");
				double MA = asVariaveis.get("MA");

				double score = (NA*1.5f+A*7.0f+MA*9.5f)/(NA+A+MA);

				System.out.println("NA "+NA+" A "+A +" MA "+MA);
				System.out.println(" "+custodinheiro+" "+rating +"-> "+score);
			}

		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}

	}

	private static void rodaRegraE(HashMap<String, Double> asVariaveis,String var1,String var2,String varr) {
		double v = Math.min(asVariaveis.get(var1), asVariaveis.get(var2));
		asVariaveis.compute(varr, (k, vatual) -> vatual == null ? v : Math.max(vatual, v));
	}

	private static void rodaRegraOU(HashMap<String, Double> asVariaveis,String var1,String var2,String varr) {
		double v = Math.max(asVariaveis.get(var1),asVariaveis.get(var2));
		asVariaveis.compute(varr, (k, vatual) -> vatual == null ? v : Math.max(vatual, v));
	}
}
