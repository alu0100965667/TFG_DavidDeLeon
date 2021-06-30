import java.io.*;
import java.util.*;

public class GRASP {
	
	// Almacén de asignaturas
	private ArrayList<SubjectInfo> subjects1C = new ArrayList<SubjectInfo>();
	private ArrayList<SubjectInfo> subjects2C = new ArrayList<SubjectInfo>();

	private Solution solution;
	private Solution bestSolution;
	private int bestIteration;
	private Integer bestF;
	private ArrayList<Solution> solutions;
	private ArrayList<Solution> allSolutions;
	
	public int MAX_ITERATIONS = 20;
	public boolean SEMIGREEDY_FLAG = false;		// Best/First
	public int RCL_SIZE = 4;
	public double RCL_PARAM = 0.5;

	public long execTime = 0;
	public long constructionTime = 0;
	public long searchTime = 0;

	String instanceName = "";
	
	public GRASP(String dirName) throws FileNotFoundException, IOException, Exception {
		Instance instance = new Instance(dirName);
		subjects1C = instance.subjects1C;
		subjects2C = instance.subjects2C;
		solution = new Solution(subjects1C, subjects2C);
		solutions = new ArrayList<Solution>();
		allSolutions = new ArrayList<Solution>();
	}

	public GRASP(String dirName, String instanceName) throws FileNotFoundException, IOException, Exception {
		Instance instance = new Instance(dirName);
		this.instanceName = instanceName;
		subjects1C = instance.subjects1C;
		subjects2C = instance.subjects2C;
		solution = new Solution(subjects1C, subjects2C);
		solutions = new ArrayList<Solution>();
		allSolutions = new ArrayList<Solution>();
	}
	
	public void exec_SingleObjetive_First() {
		SEMIGREEDY_FLAG = false;
		bestF = Integer.MAX_VALUE;
		boolean stop = false;
		int it = 0;
		bestIteration = -1;
		solutions.clear();
		allSolutions.clear();
		resetTracking();
		while(!stop) {
			long time = System.currentTimeMillis();
			semiGreedy(); addConstructionTime(System.currentTimeMillis() - time);

			time = System.currentTimeMillis();
			localSearchFirst(); addSearchTime(System.currentTimeMillis() - time);

			int value = solution.getValue();
			// Si el valor de la sol generada es mejor que el mejor valor registrado
			// actualizamos Best y borramos las soluciones almacenadas
			if(value < bestF) {
				bestSolution = new Solution(solution);
				bestF = value;
				bestIteration = it + 1;
				solutions.clear();
				solutions.add(new Solution(solution));
			}
			// Si el valor de la sol generada es igual que el mayor valor reg
			// almacenamos la solución por si es de utilidad para el usuario
			else if(value == bestF) solutions.add(new Solution(solution));

			
			allSolutions.add(new Solution(solution));
			// Comprobar condición de parada
			it++;
			if(it > MAX_ITERATIONS) stop = true;
		}
		stopTracking();
	}
	
	public void exec_SingleObjetive_Best() {
		SEMIGREEDY_FLAG = true;
		bestF = Integer.MAX_VALUE;
		boolean stop = false;
		int it = 0;
		bestIteration = -1;
		solutions.clear();
		allSolutions.clear();
		resetTracking();
		while(!stop) {
			long time = System.currentTimeMillis();
			semiGreedy(); addConstructionTime(System.currentTimeMillis() - time);

			time = System.currentTimeMillis();
			localSearchBest(); addSearchTime(System.currentTimeMillis() - time);

			int value = solution.getValue();
			// Si el valor de la sol generada es mejor que el mejor valor registrado
			// actualizamos Best y borramos las soluciones almacenadas
			if(value < bestF) {
				bestSolution = new Solution(solution);
				bestF = value;
				bestIteration = it + 1;
				solutions.clear();
				solutions.add(new Solution(solution));
			}
			// Si el valor de la sol generada es igual que el mayor valor reg
			// almacenamos la solución por si es de utilidad para el usuario
			else if(value == bestF)	solutions.add(new Solution(solution));
			allSolutions.add(new Solution(solution));
			// Comprobar condición de parada
			it++;
			if(it > MAX_ITERATIONS) stop = true;
		}
		stopTracking();
	}
	
	public void exec_MultiObjetive_First() {
		SEMIGREEDY_FLAG = false;
		bestF = Integer.MAX_VALUE;
		boolean stop = false;
		int it = 0;
		bestIteration = -1;
		solutions.clear();
		allSolutions.clear();
		resetTracking();
		while(!stop) {
			long time = System.currentTimeMillis();
			semiGreedyMO(); addConstructionTime(System.currentTimeMillis() - time);

			time = System.currentTimeMillis();
			localSearchMultiObjectiveFirst2(); addSearchTime(System.currentTimeMillis() - time);

			int value = solution.getValue();
			// Si el valor de la sol generada es mejor que el mejor valor registrado
			// actualizamos Best
			if(value < bestF) {
				bestSolution = new Solution(solution);
				bestF = value;
				bestIteration = it + 1;
			}
			solutions.add(new Solution(solution));
			allSolutions.add(new Solution(solution));
			// Comprobar condición de parada
			it++;
			if(it > MAX_ITERATIONS) stop = true;
		}
		stopTracking();
	}
	
	/**************************************************
	 *************** Single-objective *****************
	 **************************************************/

	/** Función que construye una solución utilizando un procedimiento voraz adaptativo */
	public void semiGreedy() {
		solution.reset();
		while(!solution.isFull()) {
			ArrayList<Subject> rcL = makeCandidateList(solution);	// Creo la lista de candidatos
			Random rnd = new Random(); 
			int i = rnd.nextInt(rcL.size()); // Selecciono uno aleatoriamente
			solution.setSubject(rcL.get(i)); // y lo añado
		}
	}

	public void semiGreedy_NoMethod() {
		solution.reset();
		while(!solution.isFull()) {
			ArrayList<Subject> rcL = new ArrayList<Subject>();
			for(int i = 0; i < solution.get1CSize(); i++)
				if(solution.get1C().get(i).getRight().get(0) < 0)
					rcL.addAll(solution.get1C().get(i).getLeft().getSubjects());
			for(int i = 0; i < solution.get2CSize(); i++)
				if(solution.get2C().get(i).getRight().get(0) < 0)
					rcL.addAll(solution.get2C().get(i).getLeft().getSubjects());
			ArrayList<Pair<Subject, Integer>> values = new ArrayList<Pair<Subject, Integer>>();
			for(int i = 0; i < rcL.size(); i++) {
				Solution aux = new Solution(this.solution);
				aux.setSubject(rcL.get(i));
				values.add(new Pair<Subject, Integer>(rcL.get(i), aux.getValue()));
			}
			values.sort(new PairRightComp());

			rcL = this.getCandidateList(values);

			Random rnd = new Random(); 
			int i = rnd.nextInt(rcL.size()); // Selecciono uno aleatoriamente
			solution.setSubject(rcL.get(i)); // y lo añado
		}
	}

	/**
	 * Genera una lista de obj. Subject (con su CPL) que pueden añadirse a una solución
	 * @param sol
	 * @return
	 */
	public ArrayList<Subject> makeCandidateList(Solution sol) {
		ArrayList<Subject> rcl = new ArrayList<Subject>();
		// Si el cuatrimestre está vacío añade las opciones por defecto
		// Si no, añade las asiganturas según las asignaturas ya matriculadas
		if(!sol.is1CFull()) {
			if(!sol.is1CEmpty()) 
				rcl.addAll(getCandidates(sol.get1C()));
			else
				for(int i = 0; i < this.subjects1C.size(); i++)
					rcl.addAll(subjects1C.get(i).getFirsts());
		}
		if(!sol.is2CFull()) {
			if(sol.is2CEmpty())
				for(int i = 0; i < this.subjects2C.size(); i++)
					rcl.addAll(subjects2C.get(i).getFirsts());
			else
				rcl.addAll(getCandidates(sol.get2C()));
		}
		
		// Filtrado para que el SemiGreedy tenga sentido
		ArrayList<Pair<Subject, Integer>> values = new ArrayList<Pair<Subject, Integer>>();
		for(int i = 0; i < rcl.size(); i++) {
			Solution aux = new Solution(this.solution);
			aux.setSubject(rcl.get(i));
			values.add(new Pair<Subject, Integer>(rcl.get(i), aux.getValue()));
		}
		values.sort(new PairRightComp());
		
		return getCandidateList(values);
	}

	public ArrayList<Subject> getCandidateList(ArrayList<Pair<Subject, Integer>> orderedList) {
		ArrayList<Subject> rcl = new ArrayList<Subject>();
		ArrayList<Pair<Subject, Integer>> newlist = new ArrayList<Pair<Subject, Integer>>();
		
		if(SEMIGREEDY_FLAG) {
			// BEST
			int bestValue = orderedList.get(0).getRight();
			int peorValue = orderedList.get(orderedList.size()-1).getRight();
			for(Pair<Subject, Integer> i : orderedList)
				if(i.getRight() <= bestValue + RCL_PARAM * (peorValue - bestValue)) {
					rcl.add(i.getLeft());
					newlist.add(i);
				}
		}
		else {
			// FIRST
			for(int i = 0; i < orderedList.size() && i < RCL_SIZE; i++) {
				rcl.add(orderedList.get(i).getLeft());
				newlist.add(orderedList.get(i));
			}
		}

		return rcl;
	}

	public ArrayList<Subject> getCandidates(ArrayList<Pair<SubjectInfo, ArrayList<Integer>>> cuatri) {
		ArrayList<Subject> list = new ArrayList<Subject>();

		// Almacena los indices en el cuatri de las asignaturas matriculadas por año
		ArrayList<ArrayList<Integer>> subjectsIncl = new ArrayList<ArrayList<Integer>>();

		// Almacena las que no están matriculadas
		ArrayList<ArrayList<Integer>> subjectsNotI = new ArrayList<ArrayList<Integer>>();

		for(int i = 0; i < 4; i++) {
			subjectsIncl.add(new ArrayList<Integer>());
			subjectsNotI.add(new ArrayList<Integer>());
		}
		for(int i = 0; i < cuatri.size(); i++) {
			if(cuatri.get(i).getRight().get(0) >= 0) // Comprueba que está incluida en la sol
				subjectsIncl.get(cuatri.get(i).getLeft().getYear()).add(i);
			else {
				subjectsNotI.get(cuatri.get(i).getLeft().getYear()).add(i);
			}
		}

		// Poner las asignaturas que no están matriculadas con los grupos de las asignaturas que
		// ya están matriculadas y sean del mismo año
		for(int y = 0; y < subjectsNotI.size(); y++) {
			if(!subjectsNotI.get(y).isEmpty()) {
				if(!subjectsIncl.get(y).isEmpty()) {
					TreeSet<Integer> cGroups = new TreeSet<Integer>();
					TreeSet<Integer> pGroups = new TreeSet<Integer>();
					TreeSet<Integer> lGroups = new TreeSet<Integer>();
					// Recorro las asignaturas del año que estoy mirando
					// almacenando los grupos que les han sido asignados
					for(int s = 0; s < subjectsIncl.get(y).size(); s++) {
						cGroups.add(cuatri.get(subjectsIncl.get(y).get(s)).getRight().get(0));
						int p = cuatri.get(subjectsIncl.get(y).get(s)).getRight().get(1);
						if(p >= 0) pGroups.add(p);
						int l = cuatri.get(subjectsIncl.get(y).get(s)).getRight().get(2);
						if(l >= 0) lGroups.add(l);
					}
					// Almaceno las asignaturas sin matricular con los grupos de las asignaturas matriculadas de su mismo año
					for(int s = 0; s < subjectsNotI.get(y).size(); s++) {
						SubjectInfo subject = cuatri.get(subjectsNotI.get(y).get(s)).getLeft();

						if(subject.getPSize() > 0 && subject.getLSize() > 0 && pGroups.size() > 0 && lGroups.size() > 0) {
							for(int c = 0; c < cGroups.size() && c < subject.getCSize(); c++) {
								for(int p = 0; p < pGroups.size() && p < subject.getPSize(); p++) {
									for(int l = 0; l < lGroups.size() && l < subject.getLSize(); l++) {
										Subject tmpSubject = subject.getSubject(c, p, l);
										if(tmpSubject != null)
											list.add(tmpSubject);
									}
								}
							}
						}
						// LSize == 0
						else if(subject.getLSize() == 0 && subject.getPSize() > 0 && pGroups.size() > 0) {
							for(int c = 0; c < cGroups.size() && c < subject.getCSize(); c++) {
								for(int p = 0; p < pGroups.size() && p < subject.getPSize(); p++) {
									Subject tmpSubject = subject.getSubject(c, p, -1);
									if(tmpSubject != null)
										list.add(tmpSubject);
								}
							}
						}
						// PSize == 0
						else if(subject.getPSize() == 0 && subject.getLSize() > 0 && lGroups.size() > 0) {
							for(int c = 0; c < cGroups.size() && c < subject.getCSize(); c++) {
								for(int l = 0; l < lGroups.size() && l < subject.getLSize(); l++) {
									Subject tmpSubject = subject.getSubject(c, -1, l);
									if(tmpSubject != null)
										list.add(tmpSubject);
								}
							}
						}
						
						else if(subject.getPSize() > 0 && subject.getLSize() > 0 && pGroups.size() == 0 && lGroups.size() > 0) {
							for(int c = 0; c < cGroups.size() && c < subject.getCSize(); c++) {
								for(int p = 0; p < subject.getPSize(); p++) {
									for(int l = 0; l < lGroups.size() && l < subject.getLSize(); l++) {
										Subject tmpSubject = subject.getSubject(c, p, l);
										if(tmpSubject != null)
											list.add(tmpSubject);
									}
								}
							}
						}

						else if(subject.getPSize() > 0 && subject.getLSize() > 0 && pGroups.size() > 0 && lGroups.size() == 0) {
							for(int c = 0; c < cGroups.size() && c < subject.getCSize(); c++) {
								for(int p = 0; p < pGroups.size() && p < subject.getPSize(); p++) {
									for(int l = 0; l < subject.getLSize(); l++) {
										Subject tmpSubject = subject.getSubject(c, p, l);
										if(tmpSubject != null)
											list.add(tmpSubject);
									}
								}
							}
						}

						else if(subject.getPSize() > 0 && subject.getLSize() > 0 && pGroups.size() == 0 && lGroups.size() == 0) {
							for(int c = 0; c < cGroups.size() && c < subject.getCSize(); c++) {
								for(int p = 0; p < subject.getPSize(); p++) {
									for(int l = 0; l < subject.getLSize(); l++) {
										Subject tmpSubject = subject.getSubject(c, p, l);
										if(tmpSubject != null)
											list.add(tmpSubject);
									}
								}
							}
						}
					}
				}
				else {
					for(int s = 0; s < subjectsNotI.get(y).size(); s++) {
						SubjectInfo subject = cuatri.get(subjectsNotI.get(y).get(s)).getLeft();
						list.addAll(subject.getFirsts());
					}
				}
			}
		}
		return list;
	}
	
	/** Función que lleva a cabo un algoritmo de búsqueda local primer vecino */
	public void localSearchFirst() {
		boolean improvement = true;
		while(improvement) {
			improvement = false;
			Solution sol = solution.getNeighborFirstBest();
			if(sol.getValue() < solution.getValue()) {
				solution = new Solution(sol);
				improvement = true;
			}
		}
	}

	/** Función que lleva a cabo un algoritmo de búsqueda local mejor vecino */
	public void localSearchBest() {
		boolean improvement = true;
		while(improvement) {
			improvement = false;
			Solution best = solution.getNeighborBest();
			if(best.getValue() < solution.getValue()) {
				solution = new Solution(best);
				improvement = true;
			}
		}
	}
	

	/**************************************************
	 **************** Multiobjective ******************
	 **************************************************/

	/** Función que construye una solución utilizando un procedimiento voraz adaptativo
	 * según el objetivo pasado por parámetro */
	public void semiGreedyMO() {
		solution.reset();
		while(!solution.isFull()) {
			Random rnd = new Random();
			int objective = rnd.nextInt(solution.NOBJECTIVES);
			ArrayList<Subject> rcL = makeCandidateList(solution, objective); // Creo la lista de candidatos
			int i = rnd.nextInt(rcL.size()); // Selecciono uno aleatoriamente
			solution.setSubject(rcL.get(i)); // y lo añado
		}
	}

	/**
	 * 
	 * @param sol
	 * @param objective
	 * @return
	 */
	public ArrayList<Subject> makeCandidateList(Solution sol, int objective) {
		ArrayList<Subject> rcl = new ArrayList<Subject>();
		
		// Si el cuatrimestre está vacío añade las opciones por defecto
		// Si no, añade las asiganturas según las asignaturas ya matriculadas
		if(!sol.is1CFull()) {
			if(!sol.is1CEmpty()) 
				rcl.addAll(getCandidates(sol.get1C()));
			else
				for(int i = 0; i < this.subjects1C.size(); i++)
					rcl.addAll(subjects1C.get(i).getFirsts());
		}

		if(!sol.is2CFull()) {
			if(sol.is2CEmpty())
				for(int i = 0; i < this.subjects2C.size(); i++)
					rcl.addAll(subjects2C.get(i).getFirsts());
			else
				rcl.addAll(getCandidates(sol.get2C()));
		}

		// Filtrado para que el SemiGreedy tenga sentido
		ArrayList<Pair<Subject, Integer>> values = new ArrayList<Pair<Subject, Integer>>();
		for(int i = 0; i < rcl.size(); i++) {
			Solution aux = new Solution(this.solution);
			aux.setSubject(rcl.get(i));
			values.add(new Pair<Subject, Integer>(rcl.get(i), aux.getObjective(objective)));
		}
		values.sort(new PairRightComp());
		
		return getCandidateList(values);
	}
	
	public void localSearchMultiObjectiveFirst2() {
		int objective = 0;
		while(objective < solution.NOBJECTIVES) {
			Solution sol = solution.getNeighborFirst(objective);
			if(solution.dominates(sol)) {
				solution = new Solution(sol);
				objective = 0;
			}
			else objective++;
		}
	}

	
	public Solution getSolution() { return solution; }
	public Solution getBestSolution() { return bestSolution; }

	public void resetTracking() {
		execTime = System.currentTimeMillis();
		constructionTime = 0;
		searchTime = 0;
	}

	public void addConstructionTime(long time) { constructionTime += time; }
	public void addSearchTime(long time) { searchTime += time; }

	public void stopTracking() {
		execTime = System.currentTimeMillis() - execTime;
		constructionTime = constructionTime / MAX_ITERATIONS;
		searchTime = searchTime / MAX_ITERATIONS;
	}

	public String export() {
		int overlapValue = 0;
		int over8hValue = 0;
		int over6hValue = 0;
		int deadTimeValue = 0;
		int creditsValue = 0;
		int totalValue = 0;
		for(int i = 0; i < allSolutions.size(); i++) {
			overlapValue += allSolutions.get(i).overlapping();
			over8hValue += allSolutions.get(i).over8Hours();
			over6hValue += allSolutions.get(i).over6Hours();
			deadTimeValue += allSolutions.get(i).deadTime();
			creditsValue += allSolutions.get(i).getValueCredits();
			totalValue += allSolutions.get(i).getValue();
		}
		String text = "" + instanceName + "," + System.currentTimeMillis() + ',' + MAX_ITERATIONS + ",";

		if(SEMIGREEDY_FLAG) text += RCL_PARAM + ",";
		else text += RCL_SIZE + ",";

		text += execTime + ",";
		text += constructionTime + ",";
		text += searchTime + ",";

		text += totalValue / MAX_ITERATIONS + ",";
		text += overlapValue / MAX_ITERATIONS + ",";
		text += over8hValue / MAX_ITERATIONS + ",";
		text += over6hValue / MAX_ITERATIONS + ",";
		text += deadTimeValue / MAX_ITERATIONS + ",";
		text += creditsValue / MAX_ITERATIONS + ",";
		text += bestSolution.getValue() + ",";
		text += bestSolution.overlapping() + ",";
		text += bestSolution.over8Hours() + ",";
		text += bestSolution.over6Hours() + ",";
		text += bestSolution.deadTime() + ",";
		text += bestSolution.getValueCredits() + ",";
		text += bestIteration + "\n";

		return text;
	}
}
