package org.example;

/**
 * Uma instância de trapézio que representa uma enumeração
 * em uma variável fuzzy.
 */
public class Trapezoid {

	String name;
	double b1, t1, t2, b2;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param name Identificador da enumeração
	 * @param b1 Começo da parametrização
	 * @param t1 Começo do topo do trapézio
	 * @param t2 Fim do topo do trapézio
	 * @param b2 Fim da parametrização
	 */
	public Trapezoid(String name, double b1, double t1, double t2, double b2) {
		this.name = name;
		this.b1   = b1;
		this.t1   = t1;
		this.t2   = t2;
		this.b2   = b2;
	}

	/**
	 * Retorna a proporção de um valor em relação à categoria
	 * @param v Valor a ser fuzzificado
	 * @return A proporção de v em relação ao trapézio
	 */
	public double proportion(double v) {

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
