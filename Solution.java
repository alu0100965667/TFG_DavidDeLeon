import java.util.*;

/**
 * Clase que almacena las asignaturas que representan una solución
 * Posee métodos para generar objetos solución vecinos del objeto solución actual
 * @author David de León Rodríguez
 *
 */
public class Solution {
	
	private ArrayList<Pair<SubjectInfo, ArrayList<Integer>>> solution1C;
	private ArrayList<Pair<SubjectInfo, ArrayList<Integer>>> solution2C;

	private ArrayList<SubjectInfo> c1;
	private ArrayList<SubjectInfo> c2;

	private int CREDITOS_MATRICULA = 60;

	private int value1C;
	private int value2C;
	private int credit1C;
	private int credit2C;

	public int overlappingNumber = 1;
	public int deadTimeNumber = 1;
	public int over6HoursNumber = 1;
	public int over8HoursNumber = 1;
	public int creditsNumber = 2;

	public final int NOBJECTIVES = 6;

	private boolean best1C;
	private boolean best2C;
	
	/*________________________________________________________________*/

	public Solution(ArrayList<SubjectInfo> param1C, ArrayList<SubjectInfo> param2C) {
		c1 = param1C;
		c2 = param2C;
		solution1C = new ArrayList<Pair<SubjectInfo, ArrayList<Integer>>>();
		solution2C = new ArrayList<Pair<SubjectInfo, ArrayList<Integer>>>();
		for(int i = 0; i < param1C.size(); i++) solution1C.add(new Pair<SubjectInfo, ArrayList<Integer>>(param1C.get(i), new ArrayList<Integer>()));
		for(int i = 0; i < param2C.size(); i++) solution2C.add(new Pair<SubjectInfo, ArrayList<Integer>>(param2C.get(i), new ArrayList<Integer>()));

		best1C = false;
		best2C = false;
		reset();
	}
	public Solution(Solution copy) {
		c1 = copy.c1;
		solution1C = new ArrayList<Pair<SubjectInfo, ArrayList<Integer>>>();
		for(int i = 0; i < copy.get1C().size(); i++)
			solution1C.add(new Pair<SubjectInfo, ArrayList<Integer>>(copy.get1C().get(i)));
		value1C = copy.value1C;
		best1C = copy.best1C;

		c2 = copy.c2;
		solution2C = new ArrayList<Pair<SubjectInfo, ArrayList<Integer>>>();
		for(int i = 0; i < copy.get2C().size(); i++)
			solution2C.add(new Pair<SubjectInfo, ArrayList<Integer>>(copy.get2C().get(i)));
		value2C = copy.value2C;
		best2C = copy.best2C;
	}

	/** Función que resetea la solución poniendo que ninguna asignatura ha sido elegida */
	public void reset() {
		for(int i = 0; i < solution1C.size(); i++)
			solution1C.get(i).setRight(new ArrayList<Integer>(Arrays.asList(-1, -1, -1)));
		best1C = false;
		value1C = Integer.MAX_VALUE;
		for(int i = 0; i < solution2C.size(); i++)
			solution2C.get(i).setRight(new ArrayList<Integer>(Arrays.asList(-1, -1, -1)));
		best2C = false;
		value2C = Integer.MAX_VALUE;
	}

	public ArrayList<Pair<SubjectInfo, ArrayList<Integer>>> get1C() { return solution1C; }
	public ArrayList<Pair<SubjectInfo, ArrayList<Integer>>> get2C() { return solution2C; }

	public ArrayList<SubjectInfo> getc1() { return c1; }
	public ArrayList<SubjectInfo> getc2() { return c2; }

	public int getSize() { return solution1C.size() + solution2C.size(); }
	public int get1CSize() { return solution1C.size(); }
	public int get2CSize() { return solution2C.size(); }

	public void setSubject(int semester, int index, int c, int p, int l) {
		if(semester == 0) {
			solution1C.get(index).setRight(new ArrayList<Integer>(Arrays.asList(c, p, l)));
			best1C = false;
		}
		else if(semester == 1) {
			solution2C.get(index).setRight(new ArrayList<Integer>(Arrays.asList(c, p, l)));
			best2C = false;
		}
	}
	public void setSubject(Subject subject) {
		// Buscamos la asignatura pasada por parámetro y actualizamos índices de grupos
		if(subject.getSemester() == 0) {
			int i = 0; boolean found = false;
			while(!found && i < solution1C.size()) {
				if(subject.getid().equals(solution1C.get(i).getLeft().getId()))
					found = true;
				else i++;
			}
			solution1C.get(i).setRight(new ArrayList<Integer>(
				Arrays.asList(subject.getcGroup(), subject.getpGroup(), subject.getlGroup())));
			best1C = false; // Puede dar problemas
		}
		// Igual para el segundo cuatrimestre
		else if(subject.getSemester() == 1) {
			int i = 0; boolean found = false;
			while(!found && i < solution2C.size()) {
				if(subject.getid().equals(solution2C.get(i).getLeft().getId()))
					found = true;
				else i++;
			}
			solution2C.get(i).setRight(new ArrayList<Integer>(
				Arrays.asList(subject.getcGroup(), subject.getpGroup(), subject.getlGroup())));
			best2C = false; // Puede dar problemas
		}
	}
	public void setSubject(int semester, long id, int c, int p , int l) {
		// Buscamos la asignatura pasada por parámetro y actualizamos índices de grupos
		if(semester == 0) {
			int i = 0; boolean found = false;
			while(!found && i < solution1C.size()) {
				if(solution1C.get(i).getLeft().equals(id))
					found = true;
				else i++;
			}
			if(found)
				solution1C.get(i).setRight(new ArrayList<Integer>(
					Arrays.asList(c, p, l)));
			best1C = false;
		}
		// Igual para el segundo cuatrimestre
		else if(semester == 1) {
			int i = 0; boolean found = false;
			while(!found && i < solution2C.size()) {
				if(solution2C.get(i).getLeft().equals(id))
					found = true;
				else i++;
			}
			if(found)
				solution2C.get(i).setRight(new ArrayList<Integer>(
					Arrays.asList(c, p, l)));
			best2C = false;
		}
	}

	/** Devuelve true si hay 60 créditos matriculados o si
	 * todas las asignaturas disponibles están matriculadas
	 */
	public boolean isFull() {
		int nC = 0; // Número de créditos matriculados
		int nS = 0; // Número de asignaturas matriculadas
		for(int i = 0; i < solution1C.size(); i++) {
			if(!solution1C.get(i).getRight().get(0).equals(-1)) {
				nS++;
				nC += solution1C.get(i).getLeft().getCredit();
			}
		}
		for(int i = 0; i < solution2C.size(); i++) {
			if(!solution2C.get(i).getRight().get(0).equals(-1)) {
				nS++;
				nC += solution2C.get(i).getLeft().getCredit();
			}
		}
		return nC >= CREDITOS_MATRICULA || nS == solution1C.size() + solution2C.size();
	}
	/** @return true si hay 5 o más asignaturas matriculadas o todas las disponibles */
	public boolean is1CFull() {
		int n = 0;
		for(int i = 0; i < solution1C.size(); i++)
			if(!solution1C.get(i).getRight().get(0).equals(-1)) n++;
		return n >= 5 || n == solution1C.size();
	}
	/** @return true si hay 5 o más asignaturas matriculadas o todas las disponibles */
	public boolean is2CFull() {
		int n = 0;
		for(int i = 0; i < solution2C.size(); i++)
			if(!solution2C.get(i).getRight().get(0).equals(-1)) n++;
		return n >= 5 || n == solution2C.size();
	}

	public boolean isEmpty() {
		for(int i = 0; i < solution1C.size(); i++)
			if(!solution1C.get(i).getRight().get(0).equals(-1))
				return false;
		for(int i = 0; i < solution2C.size(); i++)
			if(!solution2C.get(i).getRight().get(0).equals(-1))
				return false;
		return true;
	}
	public boolean is1CEmpty() {
		for(int i = 0; i < solution1C.size(); i++)
			if(!solution1C.get(i).getRight().get(0).equals(-1))
				return false;
		return true;
	}
	public boolean is2CEmpty() {
		for(int i = 0; i < solution2C.size(); i++)
			if(!solution2C.get(i).getRight().get(0).equals(-1))
				return false;
		return true;
	}

	/**************************************************
	 ************* Funciones Objetivos ****************
	 **************************************************/

	public int getValue() {
		value1C = getValue1C();
		value2C = getValue2C();			
		return value1C + value2C + creditsNumber * getValueCredits();
	}
	public int getValue1C() {
		int val = 0;
		Time total = new Time();
		// Bucle para obtener el tiempo total del cuatrimestre
		for(int i = 0; i < solution1C.size(); i++) {
			int c = solution1C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution1C.get(i).getRight().get(1);
				int l = solution1C.get(i).getRight().get(2);
				Time time = solution1C.get(i).getLeft().getSubject(c, p, l).getTime();
				total.add(time);
				
				// Calcula los solpamientos
				for(int j = i + 1; j < solution1C.size(); j++) {
					int c_ = solution1C.get(j).getRight().get(0);
					if(c_ >= 0) {
						int p_ = solution1C.get(j).getRight().get(1);
						int l_ = solution1C.get(j).getRight().get(2);
						Time time_ = solution1C.get(j).getLeft().getSubject(c_, p_, l_).getTime();
						val += time.overlapping(time_);
					}
				}
			}
		}
		val = overlappingNumber * val;
		val += deadTimeNumber * total.deadTime();
		val += over6HoursNumber * total.over6Hours();
		val += over8HoursNumber * total.over8Hours();
		return val;
	}
	public int getValue2C() {
		int val = 0;
		Time total = new Time();
		// Bucle para obtener el tiempo total del cuatrimestre
		for(int i = 0; i < solution2C.size(); i++) {
			int c = solution2C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution2C.get(i).getRight().get(1);
				int l = solution2C.get(i).getRight().get(2);
				Time time = solution2C.get(i).getLeft().getSubject(c, p, l).getTime();
				total.add(time);
			
				for(int j = i + 1; j < solution2C.size(); j++) {
					int c_ = solution2C.get(j).getRight().get(0);
					if(c_ >= 0) {
						int p_ = solution2C.get(j).getRight().get(1);
						int l_ = solution2C.get(j).getRight().get(2);
						Time time_ = solution2C.get(j).getLeft().getSubject(c_, p_, l_).getTime();
						val += time.overlapping(time_);
					}
				}
			}
		}
		val = overlappingNumber * val;
		val += deadTimeNumber * total.deadTime();
		val += over6HoursNumber * total.over6Hours();
		val += over8HoursNumber * total.over8Hours();
		return val;
	}

	public int getObjective(int objective) {
		switch(objective) {
			case 0: return overlapping();
			case 1: return deadTime();
			case 2: return over8Hours();
			case 3: return over6Hours();
			case 4: return getValueCredits();
			default: return getValue();
		}
	}

	public int overlapping() { return overlappingNumber * (overlapping1C() + overlapping2C()); }
	public int overlapping1C() {
		int val = 0;
		for(int i = 0; i < solution1C.size(); i++) {
			int c = solution1C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution1C.get(i).getRight().get(1);
				int l = solution1C.get(i).getRight().get(2);
				Time time = solution1C.get(i).getLeft().getSubject(c, p, l).getTime();
				
				// Calcula los solpamientos
				for(int j = i + 1; j < solution1C.size(); j++) {
					int c_ = solution1C.get(j).getRight().get(0);
					if(c_ >= 0) {
						int p_ = solution1C.get(j).getRight().get(1);
						int l_ = solution1C.get(j).getRight().get(2);
						Time time_ = solution1C.get(j).getLeft().getSubject(c_, p_, l_).getTime();
						val += time.overlapping(time_);
					}
				}
			}
		}
		return val;
	}
	public int overlapping2C() {
		int val = 0;
		for(int i = 0; i < solution2C.size(); i++) {
			int c = solution2C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution2C.get(i).getRight().get(1);
				int l = solution2C.get(i).getRight().get(2);
				Time time = solution2C.get(i).getLeft().getSubject(c, p, l).getTime();
			
				for(int j = i + 1; j < solution2C.size(); j++) {
					int c_ = solution2C.get(j).getRight().get(0);
					if(c_ >= 0) {
						int p_ = solution2C.get(j).getRight().get(1);
						int l_ = solution2C.get(j).getRight().get(2);
						Time time_ = solution2C.get(j).getLeft().getSubject(c_, p_, l_).getTime();
						val += time.overlapping(time_);
					}
				}
			}
		}
		return val;
	}

	public int deadTime() { return deadTimeNumber * (deadTime1C() + deadTime2C()); }
	public int deadTime1C() {
		int val = 0;
		Time total = new Time();
		// Bucle para obtener el tiempo total del cuatrimestre
		for(int i = 0; i < solution1C.size(); i++) {
			int c = solution1C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution1C.get(i).getRight().get(1);
				int l = solution1C.get(i).getRight().get(2);
				Time time = solution1C.get(i).getLeft().getSubject(c, p, l).getTime();
				total.add(time);
			}
		}
		val += total.deadTime();
		return val;
	}
	public int deadTime2C() {
		int val = 0;
		Time total = new Time();
		// Bucle para obtener el tiempo total del cuatrimestre
		for(int i = 0; i < solution2C.size(); i++) {
			int c = solution2C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution2C.get(i).getRight().get(1);
				int l = solution2C.get(i).getRight().get(2);
				Time time = solution2C.get(i).getLeft().getSubject(c, p, l).getTime();
				total.add(time);
			}
		}
		val += total.deadTime();
		return val;
	}

	public int over8Hours() { return over8HoursNumber * (over8Hours1C() + over8Hours2C()); }
	public int over8Hours1C() {
		int val = 0;
		Time total = new Time();
		// Bucle para obtener el tiempo total del cuatrimestre
		for(int i = 0; i < solution1C.size(); i++) {
			int c = solution1C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution1C.get(i).getRight().get(1);
				int l = solution1C.get(i).getRight().get(2);
				Time time = solution1C.get(i).getLeft().getSubject(c, p, l).getTime();
				total.add(time);
			}
		}
		val += total.over8Hours();
		return val;
	}
	public int over8Hours2C() {
		int val = 0;
		Time total = new Time();
		// Bucle para obtener el tiempo total del cuatrimestre
		for(int i = 0; i < solution2C.size(); i++) {
			int c = solution2C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution2C.get(i).getRight().get(1);
				int l = solution2C.get(i).getRight().get(2);
				Time time = solution2C.get(i).getLeft().getSubject(c, p, l).getTime();
				total.add(time);
			}
		}
		val += total.over8Hours();
		return val;
	}

	public int over6Hours() { return over6HoursNumber * (over6Hours1C() + over6Hours2C()); }
	public int over6Hours1C() {
		int val = 0;
		Time total = new Time();
		// Bucle para obtener el tiempo total del cuatrimestre
		for(int i = 0; i < solution1C.size(); i++) {
			int c = solution1C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution1C.get(i).getRight().get(1);
				int l = solution1C.get(i).getRight().get(2);
				Time time = solution1C.get(i).getLeft().getSubject(c, p, l).getTime();
				total.add(time);
			}
		}
		val += total.over6Hours();
		return val;
	}
	public int over6Hours2C() {
		int val = 0;
		Time total = new Time();
		// Bucle para obtener el tiempo total del cuatrimestre
		for(int i = 0; i < solution2C.size(); i++) {
			int c = solution2C.get(i).getRight().get(0);
			if(c >= 0) {
				int p = solution2C.get(i).getRight().get(1);
				int l = solution2C.get(i).getRight().get(2);
				Time time = solution2C.get(i).getLeft().getSubject(c, p, l).getTime();
				total.add(time);
			}
		}
		val += total.over6Hours();
		return val;
	}

	/** @return Número de créditos matriculados en el primer cuatrimestre */
	public int get1CCredits() {
		credit1C = 0;
		for(int i = 0; i < solution1C.size(); i++)
			if(solution1C.get(i).getRight().get(0) > -1)
				credit1C += solution1C.get(i).getLeft().getCredit();
		return credit1C;
	}
	/** @return Número de créditos matriculados en el segundo cuatrimestre */
	public int get2CCredits() {
		credit2C = 0;
		for(int i = 0; i < solution2C.size(); i++)
			if(solution2C.get(i).getRight().get(0) > -1)
				credit2C += solution2C.get(i).getLeft().getCredit();
		return credit2C;
	}

	/** @return Valor de la característica de créditos*/
	public int getValueCredits() {
		int credit = get1CCredits() + get2CCredits();
		if(credit <= CREDITOS_MATRICULA)
			credit = CREDITOS_MATRICULA - credit;
		return credit;
	}

	/**************************************************
	 ***************** Neighborhood *******************
	 **************************************************/
	
	public Solution getNeighborFirstBest() {
		Solution neighbor1C = getNeighborFirstBest1C();
		Solution neighbor2C = getNeighborFirstBest2C();

		if(neighbor1C.getValue() < neighbor2C.getValue())
		return neighbor1C;
		return neighbor2C;
	}
	/** @return Primera solución vecina encontrada que sea mejor que la actual con el cambio en el 1er cuatrimestre*/
	public Solution getNeighborFirstBest1C() {
		Solution aux = new Solution(this);
		// Si el entorno de la solución actual no
		// ha sido recorrido sin hacer ningún cambio
		if(!best1C) {
			best1C = true;
			for(int i = 0; i < solution1C.size(); i++) {
				SubjectInfo subject = solution1C.get(i).getLeft();
				if(solution1C.get(i).getRight().get(0) > -1) {
					// Si la asignatura está matriculada...
					int C = solution1C.get(i).getRight().get(0);
					int P = solution1C.get(i).getRight().get(1);
					int L = solution1C.get(i).getRight().get(2);
					// C - Probamos los cambios en el grupo de teoría
					for(int c = 0; c < subject.getCSize(); c++) {
						if(c != C) {
							aux.setSubject(subject.getSubject(c, P, L));
							if(aux.getValue() < getValue()) {
								best1C = false;
								return aux;
							}
							aux = new Solution(this);
						}
					}
					// P - Probamos los cambios en el grupo de problemas
					for(int p = 0; p < subject.getPSize(); p++) {
						if(p != P) {
							aux.setSubject(subject.getSubject(C, p, L));
							if(aux.getValue() < getValue()) {
								best1C = false;
								return aux;
							}
							aux = new Solution(this);
						}
					}
					// L - Probamos los cambios en el grupo de prácticas
					for(int l = 0; l < subject.getLSize(); l++) {
						if(l != L) {
							aux.setSubject(subject.getSubject(C, P, l));
							if(aux.getValue() < getValue()) {
								best1C = false;
								return aux;
							}
							aux = new Solution(this);
						}
					}
					// C -> -1 - Probamos quitando la asignatura de la matrícula
					aux.setSubject(0, subject.getId(), -1, P, L);
					if(aux.getValue() < getValue()) {
						best1C = false;
						return aux;
					}
					aux = new Solution(this);
				}
				// C == -1
				else {
					// Si la asignatura no está matriculada probamos sus opciones
					for(int s = 0; s < subject.getSubjects().size(); s++) {
						aux.setSubject(subject.getSubjects().get(s));
						if(aux.getValue() < getValue()) {
							best1C = false;
							return aux;
						}
						aux = new Solution(this);
					}
				}
			}
		}

		return aux;
	}
	/** @return Primera solución vecina encontrada que sea mejor que la actual con el cambio en el 2o cuatrimestre*/
	public Solution getNeighborFirstBest2C() {
		Solution aux = new Solution(this);
		// best2C = false;
		if(!best2C) {
			best2C = true;
			for(int i = 0; i < solution2C.size(); i++) {
				SubjectInfo subject = solution2C.get(i).getLeft();
				if(solution2C.get(i).getRight().get(0) > -1) {
					int C = solution2C.get(i).getRight().get(0);
					int P = solution2C.get(i).getRight().get(1);
					int L = solution2C.get(i).getRight().get(2);
					// C
					for(int c = 0; c < subject.getCSize(); c++) {
						if(c != C) {
							aux.setSubject(subject.getSubject(c, P, L));
							if(aux.getValue() < getValue()) {
								best2C = false;
								return aux;
							}
							aux = new Solution(this);
						}
					}
					// P
					for(int p = 0; p < subject.getPSize(); p++) {
						if(p != P) {
							aux.setSubject(subject.getSubject(C, p, L));
							if(aux.getValue() < getValue()) {
								best2C = false;
								return aux;
							}
							aux = new Solution(this);
						}
					}
					// L
					for(int l = 0; l < subject.getLSize(); l++) {
						if(l != L) {
							aux.setSubject(subject.getSubject(C, P, l));
							if(aux.getValue() < getValue()) {
								best2C = false;
								return aux;
							}
							aux = new Solution(this);
						}
					}
					// C -> -1
					aux.setSubject(0, subject.getId(), -1, P, L);
					if(aux.getValue() < getValue()) {
						best2C = false;
						return aux;
					}
					aux = new Solution(this);
				}
				// C == -1
				else {
					for(int s = 0; s < subject.getSubjects().size(); s++) {
						aux.setSubject(subject.getSubjects().get(s));
						if(aux.getValue() < getValue()) {
							best2C = false;
							return aux;
						}
						aux = new Solution(this);
					}
				}
			}
		}
		return aux;
	}

	
	public Solution getNeighborBest() {
		Solution bestSol1C = getNeighbor1CBest();
		Solution bestSol2C = getNeighbor2CBest();
		if(bestSol1C.getValue() < bestSol2C.getValue()) 
		return bestSol1C;
		return bestSol2C;
	}
	/** @return Mejor solución vecina encontrada que sea mejor que la actual con el cambio en el 1er cuatrimestre*/
	public Solution getNeighbor1CBest() {
		Solution best = new Solution(this);
		Solution aux = new Solution(this);
		int value = getValue();
		for(int i = 0; i < get1CSize(); i++) {
			
			SubjectInfo subject = aux.get1C().get(i).getLeft();
			int C = aux.get1C().get(i).getRight().get(0);
			int P = aux.get1C().get(i).getRight().get(1);
			int L = aux.get1C().get(i).getRight().get(2);
			
			// C > -1 Asignatura matriculada
			if(C >= 0) {
				// Probar C == -1
				aux.setSubject(0, subject.getId(), -1, P, L);
				if(aux.getValue() < value) {
					/// System.out.println("mejora C -> -1");
					value = aux.getValue();
					best = new Solution(aux);
				}
				aux = new Solution(this);

				// L - Probar cambios en el grupo de laboratorio
				for(int l = 0; l < subject.getLSize(); l++) {
					if(l != L) {
						aux.setSubject(0, subject.getId(), C, P, l);
						if(aux.getValue() < value) {
							value = aux.getValue();
							best = new Solution(aux);
						}
						aux = new Solution(this);
					}
				}

				// P - Probar cambios en el grupo de problemas
				for(int p = 0; p < subject.getPSize(); p++) {
					if(p != P) {
						aux.setSubject(0, subject.getId(), C, p, L);
						if(aux.getValue() < value) {
							value = aux.getValue();
							best = new Solution(aux);
						}
						aux = new Solution(this);
					}
				}

				// C - Probar cambios en el grupo de teoría
				for(int c = 0; c < subject.getCSize(); c++) {
					if(c != C) {
						aux.setSubject(0, subject.getId(), c, P, L);
						if(aux.getValue() < value) {
							value = aux.getValue();
							best = new Solution(aux);
						}
						aux = new Solution(this);
					}
				}
			}
			// C == -1 Asignatura no matriculada
			else {
				ArrayList<Subject> tmpSubjects = subject.getFirsts();
				for(int j = 0; j < tmpSubjects.size(); j++) {
					aux.setSubject(tmpSubjects.get(j));
					if(aux.getValue() < value) {
						value = aux.getValue();
						best = new Solution(aux);
					}
					aux = new Solution(this);
				}
			}
		}
		return best;
	}
	/** @return Mejor solución vecina encontrada que sea mejor que la actual con el cambio en el 2o cuatrimestre*/
	public Solution getNeighbor2CBest() {
		Solution best = new Solution(this);
		Solution aux = new Solution(this);
		int value = getValue();
		for(int i = 0; i < get2CSize(); i++) {
			SubjectInfo subject = aux.get2C().get(i).getLeft();
			int C = aux.get2C().get(i).getRight().get(0);
			int P = aux.get2C().get(i).getRight().get(1);
			int L = aux.get2C().get(i).getRight().get(2);
			// C > -1 Asignatura matriculada
			if(C >= 0) {
				// Probar C == -1 (TODO Cuánto solapamiento debe haber para quitar la asignatura)
				aux.setSubject(0, subject.getId(), -1, P, L);
				if(aux.getValue() < value) {
					value = aux.getValue();
					best = new Solution(aux);
				}
				aux = new Solution(this);

				// Probar configuraciones L
				for(int l = 0; l < subject.getLSize(); l++) {
					if(l != L) {
						aux.setSubject(0, subject.getId(), C, P, l);
						if(aux.getValue() < value) {
							value = aux.getValue();
							best = new Solution(aux);
						}
						aux = new Solution(this);
					}
				}

				// Probar configuraciones P
				for(int p = 0; p < subject.getLSize(); p++) {
					if(p != P) {
						aux.setSubject(0, subject.getId(), C, p, L);
						if(aux.getValue() < value) {
							value = aux.getValue();
							best = new Solution(aux);
						}
						aux = new Solution(this);
					}
				}

				// Probar configuraciones C
				for(int c = 0; c < subject.getCSize(); c++) {
					if(c != C) {
						aux.setSubject(0, subject.getId(), c, P, L);
						if(aux.getValue() < value) {
							value = aux.getValue();
							best = new Solution(aux);
						}
						aux = new Solution(this);
					}
				}
			}
			// C == -1 Asignatura no matriculada
			else {
				ArrayList<Subject> tmpSubjects = subject.getFirsts();
				for(int j = 0; j < tmpSubjects.size(); j++) {
					aux.setSubject(tmpSubjects.get(j));
					if(aux.getValue() < value) {
						value = aux.getValue();
						best = new Solution(aux);
					}
					aux = new Solution(this);
				}
			}
		}
		return best;
	}

	/**************************************************
	 **************** Multiobjective ******************
	 **************************************************/

	/** Pasa el primer vecino que domine a la solución actual independientemente del objetivo */
	public Solution getNeighborFirst(int objective) {
		Solution neighbor1C = getNeighborFirstBestMO1C();
		Solution neighbor2C = getNeighborFirstBestMO2C();

		if(neighbor1C.getObjective(objective) < neighbor2C.getObjective(objective))
		return neighbor1C;
		return neighbor2C;
	}
	/** @return Primera solución vecina encontrada que sea mejor que la actual con el cambio en el 1er cuatrimestre*/
	public Solution getNeighborFirstBestMO1C() {
		Solution aux = new Solution(this);
		// Si el entorno de la solución actual no
		// ha sido recorrido sin hacer ningún cambio
		for(int i = 0; i < solution1C.size(); i++) {
				SubjectInfo subject = solution1C.get(i).getLeft();
				if(solution1C.get(i).getRight().get(0) > -1) {
					// Si la asignatura está matriculada...
					int C = solution1C.get(i).getRight().get(0);
					int P = solution1C.get(i).getRight().get(1);
					int L = solution1C.get(i).getRight().get(2);
					// C - Probamos los cambios en el grupo de teoría
					for(int c = 0; c < subject.getCSize(); c++) {
						if(c != C) {
							aux.setSubject(subject.getSubject(c, P, L));
							if(dominates(aux))
								return aux;
							aux = new Solution(this);
						}
					}
					// P - Probamos los cambios en el grupo de problemas
					for(int p = 0; p < subject.getPSize(); p++) {
						if(p != P) {
							aux.setSubject(subject.getSubject(C, p, L));
							if(dominates(aux))
								return aux;
							aux = new Solution(this);
						}
					}
					// L - Probamos los cambios en el grupo de prácticas
					for(int l = 0; l < subject.getLSize(); l++) {
						if(l != L) {
							aux.setSubject(subject.getSubject(C, P, l));
							if(dominates(aux))
								return aux;
							aux = new Solution(this);
						}
					}
					// C -> -1 - Probamos quitando la asignatura de la matrícula
					aux.setSubject(0, subject.getId(), -1, P, L);
					if(dominates(aux))
						return aux;
					aux = new Solution(this);
				}
				// C == -1
				else {
					// Si la asignatura no está matriculada probamos sus opciones
					for(int s = 0; s < subject.getSubjects().size(); s++) {
						aux.setSubject(subject.getSubjects().get(s));
						if(dominates(aux))
							return aux;
						aux = new Solution(this);
					}
				}
			}
		return aux;
	}
	/** @return Primera solución vecina encontrada que sea mejor que la actual con el cambio en el 2o cuatrimestre*/
	public Solution getNeighborFirstBestMO2C() {
		Solution aux = new Solution(this);
			for(int i = 0; i < solution2C.size(); i++) {
				SubjectInfo subject = solution2C.get(i).getLeft();
				if(solution2C.get(i).getRight().get(0) > -1) {
					int C = solution2C.get(i).getRight().get(0);
					int P = solution2C.get(i).getRight().get(1);
					int L = solution2C.get(i).getRight().get(2);
					// C
					for(int c = 0; c < subject.getCSize(); c++) {
						if(c != C) {
							aux.setSubject(subject.getSubject(c, P, L));
							if(dominates(aux))
								return aux;
							aux = new Solution(this);
						}
					}
					// P
					for(int p = 0; p < subject.getPSize(); p++) {
						if(p != P) {
							aux.setSubject(subject.getSubject(C, p, L));
							if(dominates(aux))
								return aux;
							aux = new Solution(this);
						}
					}
					// L
					for(int l = 0; l < subject.getLSize(); l++) {
						if(l != L) {
							aux.setSubject(subject.getSubject(C, P, l));
							if(dominates(aux))
								return aux;
							aux = new Solution(this);
						}
					}
					// C -> -1
					aux.setSubject(0, subject.getId(), -1, P, L);
					if(dominates(aux))
						return aux;
					aux = new Solution(this);
				}
				// C == -1
				else {
					for(int s = 0; s < subject.getSubjects().size(); s++) {
						aux.setSubject(subject.getSubjects().get(s));
						if(dominates(aux))
							return aux;
						aux = new Solution(this);
					}
				}
			}
		return aux;
	}

	/** Función que comprueba si la solución pasada por parámetro mejora algún objetivo sin deteriorar ninguno de los demás 
	 * @param newSol	Solución a comprobar
	 * @return	true si se mejora algún objetivo y no se deteriora ningún otro
	 */
	public boolean dominates(Solution newSol) {
		/*System.out.println("\nnonDOM:");
		System.out.println("newSol:\n" + newSol);
		System.out.println("thisss:\n" + this);*/

		int o = 0;
		boolean worse = false;
		boolean better = false;
		while(o < NOBJECTIVES && !worse) {
			int newSolObjective = newSol.getObjective(o);
			int thisSolObjective = getObjective(o);
			if(newSolObjective > thisSolObjective) worse = true;
			else if(newSolObjective < thisSolObjective) better = true;
			o++;
		}
		//System.out.println("return:" + (!worse && better));
		return !worse && better;
	}

	/** Función que comprueba si la solución pasada por parámetro mejora algún objetivo sin deteriorar ninguno de los demás 
	 * @param newSol	Solución a comprobar
	 * @param objective Objetivo a mejorar
	 * @return	true si se mejora el objetivo y no se deteriora ningún otro
	 */
	public boolean dominates(Solution newSol, int objective) {
		/*System.out.println("\nnonDOM:");
		System.out.println("newSol:\n" + newSol);
		System.out.println("thisss:\n" + this);*/
		if(newSol.getObjective(objective) > getObjective(objective)) return false;
		int o = 0;
		boolean worse = false;
		while(o < NOBJECTIVES && !worse) {
			if(o != objective) {
				int newSolObjective = newSol.getObjective(o);
				int thisSolObjective = getObjective(o);
				if(newSolObjective > thisSolObjective) worse = true;
			}
			o++;
		}
		//System.out.println("return:" + (!worse && better));
		return !worse;
	}

	public String toString() {
		String tmp = "1C:\n";
		for(int i = 0; i < solution1C.size(); i++) {
			tmp += solution1C.get(i).getLeft().getId() + ": "
				+ solution1C.get(i).getRight().get(0) + " "
				+ solution1C.get(i).getRight().get(1) + " "
				+ solution1C.get(i).getRight().get(2) + "\n";
		}
		tmp += "2C:\n";
		for(int i = 0; i < solution2C.size(); i++) {
			tmp += solution2C.get(i).getLeft().getId() + ": "
				+ solution2C.get(i).getRight().get(0) + " "
				+ solution2C.get(i).getRight().get(1) + " "
				+ solution2C.get(i).getRight().get(2) + "\n";
		}
		tmp += "total: " + getValue() + '\n';
		tmp += "overlapping: " + overlapping() + '\n';
		tmp += "deadtime:    " + deadTime() + '\n';
		tmp += "over8hours:  " + over8Hours() + '\n';
		tmp += "over6hours:  " + over6Hours() + '\n';
		tmp += "credit: " + getValueCredits() + '\n';
		return tmp;
	}
}

class SolutionComp implements Comparator<Solution> {
    @Override
    public int compare(Solution s1, Solution s2) {
        if(s1.getValue() < s2.getValue()) return 1;
        else if(s1.getValue() == s2.getValue()) return 0;
        else return -1;
    }
}