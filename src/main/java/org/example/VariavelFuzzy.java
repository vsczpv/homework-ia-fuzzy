package org.example;

/// Implementação de Variável Fuzzy
///
/// Representa um trapézio de seleção de uma variável fuzzy
public class VariavelFuzzy {
	String nome;
	float b1,t1,t2,b2;

	/**
	 * @param nome Nome da categoria
	 * @param b1 Começo da parametrização
	 * @param t1 Começo do topo do trapézio
	 * @param t2 Fim do topo do trapézio
	 * @param b2 Fim da parametrização
	 */
	public VariavelFuzzy(String nome, float b1, float t1, float t2, float b2) {
//		super();
		this.nome = nome;
		this.b1 = b1;
		this.t1 = t1;
		this.t2 = t2;
		this.b2 = b2;
	}

	/**
	 * Retorna a proporção de um valor em relação à categoria
	 * @param v Valor a ser fuzzificado
	 * @return A proporção de v em relação ao trapézio
	 */
	public float fuzzifica(float v) {
		if(v<b1) {
			return 0;
		}
		if(v>b2) {
			return 0;
		}

		if(v>=t1&&v<=t2) {
			return 1;
		}

		if(v>b1&&v<t1) {
			return (v-b1)/(t1-b1);
		}

		if(v>t2&&v<b2) {
			return 1.0f-((v-t2)/(b2-t2));
		}

		return 0;
	}

}
