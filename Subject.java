/**
 * Clase asignatura con la que trabajan los algoritmos
 * @author David de León Rodríguez
 *
 * Almacena los índices de los grupos que tiene asignados
 * y el tiempo que ocupa, pues el algoritmo maneja tal...
 */
public class Subject {
	
	private Long id;
	private Integer semester;

	private Integer cGroup = -1,
					pGroup = -1,
					lGroup = -1;
	
	private Time time = new Time();
	
	//////////////////////////////////////////////////
	
	public Subject(Long id, Integer semester, Integer cGroup, Integer pGroup, Integer lGroup, Time time) {
		this.id = id;
		this.cGroup = cGroup;
		this.pGroup = pGroup;
		this.lGroup = lGroup;
		this.semester = semester;
		this.time = time;
	}

	public Long getid() { return id; }
	public Integer getSemester() { return semester; }
	
	public Integer getcGroup() { return cGroup; }
	public Integer getpGroup() { return pGroup; }
	public Integer getlGroup() { return lGroup; }

	public Time getTime() { return time; }
	public void setTime(Time time) { this.time = time; }
	
	public String toString() {
		String tmp = "" + id
			+ " C:" + cGroup
			+ " P:" + pGroup
			+ " L:" + lGroup;
		tmp += time;

		return tmp;
	}
}
