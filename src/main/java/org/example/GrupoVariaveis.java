package org.example;

import java.util.ArrayList;
import java.util.HashMap;

/// Grupo de variáveis fuzzy que representam uma categoria
public class GrupoVariaveis {
	ArrayList<VariavelFuzzy> listaDeVariaveis;
	public GrupoVariaveis() {
		listaDeVariaveis = new ArrayList<>();
	}

	public void add(VariavelFuzzy var) {
		listaDeVariaveis.add(var);
	}

	/**
	 * Classifica o valor v em relação a todas as variáveis contidas no grupo
	 * @param v O valor a ser fuzzificado
	 * @param variaveisfuzzy [out] Lista de categorias × proporção
	 */
	public void fuzzifica(float v, HashMap<String,Float> variaveisfuzzy) {
		for (VariavelFuzzy var : listaDeVariaveis) {
			float val = var.fuzzifica(v);
			variaveisfuzzy.put(var.nome, val);
		}
	}
}
