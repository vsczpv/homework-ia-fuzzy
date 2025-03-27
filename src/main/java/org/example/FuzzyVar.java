package org.example;

import java.util.ArrayList;
import java.util.HashMap;

/// Variável fuzzy
public class FuzzyVar {

	ArrayList<Trapezoid> enums;
	public FuzzyVar() {
		enums = new ArrayList<>();
	}

	public void add(Trapezoid var) {
		enums.add(var);
	}

	/**
	 * Classifica o valor v em relação a todas as variáveis contidas no grupo
	 * @param v O valor a ser fuzzificado
	 * @param store [out] Lista de categorias × proporção
	 */
	public void fuzz(double v, HashMap<String,Double> store) {
		for (Trapezoid var : enums) {
			double val = var.proportion(v);
			store.put(var.getName(), val);
		}
	}

	public String unfuzz(HashMap<String,Double> store) {
		double max = Double.MIN_VALUE;
		Trapezoid res = null;
		for (var e : enums) {
			var x = store.get(e.getName());
			if (x > max) {
				max = x;
				res = e;
			}
		}
		assert res != null;
		return res.getName();
	}
}
