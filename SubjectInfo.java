import java.util.*;

/**
 * Clase que almacena la información sobre las asignaturas
 * @author David de León Rodríguez
 */
public class SubjectInfo {

	private String name;
	private long id;
	private int credit;
	private int year;
	private int semester;

	private boolean optativa;
	private int itinerario;
	
	private ArrayList<Time> cGroups, pGroups, lGroups;
	private ArrayList<Subject> subjects;
	private ArrayList<Subject> firsts;

	/*________________________________________________________________*/
	
	public SubjectInfo(String name, long id, int credit, int semester, int year) {
		this.name = name;
		this.id = id;
		this.credit = credit;
		this.semester = semester;
		this.year = year;

		this.cGroups = new ArrayList<Time>();
		this.pGroups = new ArrayList<Time>();
		this.lGroups = new ArrayList<Time>();
	}
	
	public SubjectInfo(String name, long id, int credit, int semester, int year, boolean optativa, int itinerario,
			ArrayList<Time> cGroups, ArrayList<Time> pGroups, ArrayList<Time> lGroups) {
		this.name = name;
		this.id = id;
		this.credit = credit;
		this.semester = semester;
		this.year = year;
		
		this.cGroups = cGroups;
		this.pGroups = pGroups;
		this.lGroups = lGroups;

		createSubjects();
		setFirsts();
	}

	public ArrayList<Time> getcGroups() { return cGroups; }
	public ArrayList<Time> getpGroups() { return pGroups; }
	public ArrayList<Time> getlGroups() { return lGroups; }

	public void setcGroups(ArrayList<Time> cGroups) { this.cGroups = cGroups; }
	public void setpGroups(ArrayList<Time> pGroups) { this.pGroups = pGroups; }
	public void setlGroups(ArrayList<Time> lGroups) { this.lGroups = lGroups; }

	public int getCSize() { return this.cGroups.size(); }
	public int getPSize() { return this.pGroups.size(); }
	public int getLSize() { return this.lGroups.size(); }

	public String getName() { return name; }
	public Long getId() { return id; }
	public int getCredit() { return credit; }
	public int getSemester() { return semester; }
	public int getYear() { return year; }
	public boolean getOptativa() { return optativa; }
	public int getItinerario() { return itinerario; }

	public ArrayList<Subject> getSubjects() { return subjects; }
	/** @return objeto Subjecto con los tiempos de los grupos de los parámetros
	 * @param c Grupo de teoría
	 * @param p Grupo de problemas
	 * @param l Grupo de laboratorio
	*/
	public Subject getSubject(int c, int p, int l) {
		int cSize = cGroups.size();
		int pSize = pGroups.size();
		int lSize = lGroups.size();
		if(c < cSize && p < pSize && l < lSize) {
			if(lSize == 0) { lSize = 1; l = 0; }
			if(pSize == 0) { pSize = 1; p = 0; }
			return subjects.get(c*pSize*lSize + p*lSize + l);
		}
		return null;
	}

	/** @return objeto Subject con los grupos por defecto para la construcción de soluciones */
	public Subject getFirst() { return subjects.get(0); }

	public ArrayList<Subject> getFirsts() { return firsts; }
	public void setFirsts() {
		firsts = new ArrayList<Subject>();
		// Si solo hay un grupo de teoría (solo 1 turno)
		if(cGroups.size() == 1)
			for(int i = 0; i < subjects.size() / 2; i++)
				firsts.add(subjects.get(i));	// Añadimos todas las opciones
		// Si hay más de un grupo de teoría -> Hay dos o + turnos
		else {
			// Añadir las opciones de grupos dentro del primer turno
			if(pGroups.size() > 0 && lGroups.size() > 0) {
				for(int p = 0; p < pGroups.size() / 2; p++)
					for(int l = 0; l < lGroups.size() / 2; l++)
						firsts.add(getSubject(0, p, l));
			}
			else if(pGroups.size() < 0 && lGroups.size() > 0) {
				for(int l = 0; l < lGroups.size() / 2; l++)
					firsts.add(getSubject(0, -1, l));
			}
			else if(pGroups.size() > 0 && lGroups.size() < 0) {
				for(int p = 0; p < pGroups.size() / 2; p++)
					firsts.add(getSubject(0, p, -1));
			}
		}
	}	
    
	/** Crea el vector de asignaturas con todas las configuraciones de grupos posibles */
	private void createSubjects() {
        subjects = new ArrayList<Subject>();
		if(cGroups.size() > 0 && pGroups.size() > 0 && lGroups.size() > 0) {
			for(int c = 0; c < cGroups.size(); c++) {
				for(int p = 0; p < pGroups.size(); p++) {
					for(int l = 0; l < lGroups.size(); l++) {
						Time time = new Time();
						time.add(cGroups.get(c));
						time.add(pGroups.get(p));
						time.add(lGroups.get(l));
						Subject tmp = new Subject(id, semester, c, p, l, time);
						subjects.add(tmp);
					}
				}
			}
        }
        
		else if(pGroups.size() == 0) {
			for(int c = 0; c < cGroups.size(); c++) {
				for(int l = 0; l < lGroups.size(); l++) {
					Time time = new Time();
					time.add(cGroups.get(c));
					time.add(lGroups.get(l));
					Subject tmp = new Subject(id, semester, c, -1, l, time);
					subjects.add(tmp);
				}
			}
        }
        else if(lGroups.size() == 0) {
			for(int c = 0; c < cGroups.size(); c++) {
				for(int p = 0; p < pGroups.size(); p++) {
					Time time = new Time();
					time.add(cGroups.get(c));
					time.add(pGroups.get(p));
					Subject tmp = new Subject(id, semester, c, p, -1, time);
					subjects.add(tmp);
				}
			}
		}
		else if(pGroups.size() == 0 && lGroups.size() == 0) {
            for(int c = 0; c < cGroups.size(); c++) {
                Time time = new Time();
                time.add(cGroups.get(c));
                Subject tmp = new Subject(id, semester, c, -1, -1, time);
                subjects.add(tmp);
            }
        }
	}

	public boolean equals(SubjectInfo param) {
		return param.getId().equals(this.getId());
	}
	public boolean equals(long param) {
		return getId().equals(param);
	}

    public String toString() {
        String tmp = "";
        tmp += "Name: " + name + '\n';
        tmp += "Id:   " + id + '\n';
        tmp += "Cred: " + credit + '\n';
        tmp += "Seme: " + semester + '\n';
        tmp += "Year: " + year + '\n';
		tmp += "Optativa: "   + optativa + '\n';
		tmp += "Itinerario: " + itinerario + '\n';

        tmp += "C " + cGroups.size() + '\n';
        for(int i = 0; i < cGroups.size(); i++)
            tmp += i + " " + cGroups.get(i) + '\n';
        
        tmp += "P " + pGroups.size() + '\n';
        for(int i = 0; i < pGroups.size(); i++)
            tmp += i + " " + pGroups.get(i) + '\n';
        
        tmp += "L " + lGroups.size() + '\n';
        for(int i = 0; i < lGroups.size(); i++)
            tmp += i + " " + lGroups.get(i) + '\n';
        
        return tmp;
    }
}
