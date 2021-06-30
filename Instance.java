import java.io.*;
import java.util.*;

/**
 * Clase encargada de leer los archivos con las asignaturas codificadas 
 * y generar los objetos para el funcionamiento de los algoritmos
 * @author David de León Rodríguez
 */
public class Instance {

	public final int INTERVAL = 5;

	public ArrayList<SubjectInfo> subjects1C = new ArrayList<SubjectInfo>();
	public ArrayList<SubjectInfo> subjects2C = new ArrayList<SubjectInfo>();
    
    /**
	 * Constructor que lee los ficheros .subject del directorio pasado
	 * por parámetro y crea los objetos SubjectInfo correspondientes
	 * @param dirName Directorio de trabajo
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public Instance(String absolutePath) throws FileNotFoundException, IOException, Exception {		
		File folder = new File(absolutePath);
		File[] listOfFiles = folder.listFiles();
		
		ArrayList<String> fileNames = new ArrayList<String>();
        
        // Almacena los nombres de los ficheros
		for(int i = 0; i < listOfFiles.length; i++)
			if(listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".subject"))
				fileNames.add(listOfFiles[i].getName());
        
        // Para cada fichero de una asignatura
		for(int i = 0; i < fileNames.size(); i++) {
            BufferedReader br = new BufferedReader(new FileReader(absolutePath + "\\" + fileNames.get(i)));
			
			String name = br.readLine();
			long id = Long.parseLong(br.readLine());
			int credit = Integer.parseInt(br.readLine());
			int year = Integer.parseInt(br.readLine());
			int semester = Integer.parseInt(br.readLine());
			boolean optativa = Boolean.parseBoolean(br.readLine());
			int itinerario = Integer.parseInt(br.readLine());
            			
			ArrayList<Pair<Integer, Time>> 
							 cGroups = new ArrayList<Pair<Integer, Time>>(),
							 pGroups = new ArrayList<Pair<Integer, Time>>(),
							 lGroups = new ArrayList<Pair<Integer, Time>>();
            
            String line = br.readLine();

			if(line.charAt(0) == 'C') {
                line = br.readLine();
                // Mientras se lean números
				while(Character.isDigit(line.charAt(0))) {
                    String[] tokens = line.split(" ");
                    
					if(!Time.isDay(Integer.parseInt(tokens[2]))) {
						br.close();
						throw new Exception("Error en formato día: C '" + line + "'");
					}
					if(!Hour.isHour(Integer.parseInt(tokens[1]))) {
						br.close();
						throw new Exception("Error en formato hora: C '" + line + "'");
					}
					
					Time time = new Time(
							Integer.parseInt(tokens[2]),
							new Hour(Integer.parseInt(tokens[1])));
					
					// Buscar si ya se ha puesto ese grupo en la lista
					// - si: mete el horario leído en el grupo encontrado
					// - no: mete un nuevo grupo con el horario leído
					int group = Integer.parseInt(tokens[0]);
					boolean found = false;
					int j = 0;
					while(j < cGroups.size() && !found) {
						if(cGroups.get(j).getLeft() == group)
							found = true;
						else j++;
					}
					
					if(found) cGroups.get(j).getRight().add(time);
					else cGroups.add(new Pair<Integer, Time>(group, time));
					
					line = br.readLine();
				}
			}
			else { br.close(); throw new Exception("Fallo en lectura de grupos C"); }
			
			if(line.charAt(0) == 'P') {
				line = br.readLine();
				while(Character.isDigit(line.charAt(0))) {
					String[] tokens = line.split(" ");
					
					if(!Time.isDay(Integer.parseInt(tokens[2]))) {
						br.close();
						throw new Exception("Error en formato día: P '" + line + "'");
					}
					if(!Hour.isHour(Integer.parseInt(tokens[1]))) {
						br.close();
						throw new Exception("Error en formato hora: P '" + line + "'");
					}
					
					Time time = new Time(
							Integer.parseInt(tokens[2]),
							new Hour(Integer.parseInt(tokens[1])));
					
					// Buscar si ya se ha puesto ese grupo en la lista
					int group = Integer.parseInt(tokens[0]);
					boolean found = false;
					int j = 0;
					while(j < pGroups.size() && !found) {
						if(pGroups.get(j).getLeft() == group)
							found = true;
						else j++;
					}
					
					if(found) pGroups.get(j).getRight().add(time);
					else pGroups.add(new Pair<Integer, Time>(group, time));
					
					line = br.readLine();
				}
			}
			else { br.close(); throw new Exception("Fallo en lectura de grupos P"); }
			
			if(line.charAt(0) == 'L') {
				line = br.readLine();
				while(line != null && Character.isDigit(line.charAt(0))) {
					String[] tokens = line.split(" ");

					if(!Time.isDay(Integer.parseInt(tokens[2]))) {
						br.close();
						throw new Exception("Error en formato día: L '" + line + "'");
					}
					if(!Hour.isHour(Integer.parseInt(tokens[1]))) {
						br.close();
						throw new Exception("Error en formato hora: L '" + line + "'");
					}
					
					Time time = new Time(
							Integer.parseInt(tokens[2]),
							new Hour(Integer.parseInt(tokens[1])));
					
					// Buscar si ya se ha puesto ese grupo en la lista
					int group = Integer.parseInt(tokens[0]);
					boolean found = false;
					int j = 0;
					while(j < lGroups.size() && !found) {
						if(lGroups.get(j).getLeft() == group)
							found = true;
						else j++;
					}
					
					if(found) lGroups.get(j).getRight().add(time);
					else lGroups.add(new Pair<Integer, Time>(group, time));
					
					line = br.readLine();
				}
			}
			else { br.close(); throw new Exception("Fallo en lectura de grupos L"); }
			
			br.close();
			
			ArrayList<Time> cTmp = new ArrayList<Time>(),
							pTmp = new ArrayList<Time>(),
							lTmp = new ArrayList<Time>();
			
			// Crea los arrays de grupos para crear el objeto SubjectInfo
			for(int x = 0; x < cGroups.size(); x++)
				cTmp.add(cGroups.get(x).getRight());
			for(int x = 0; x < pGroups.size(); x++)
				pTmp.add(pGroups.get(x).getRight());
			for(int x = 0; x < lGroups.size(); x++)
				lTmp.add(lGroups.get(x).getRight());
			
			SubjectInfo subject = new SubjectInfo(name, id, credit, semester, year, optativa, itinerario, cTmp, pTmp, lTmp);
			if(subject.getSemester() == 0)      subjects1C.add(subject);
			else if(subject.getSemester() == 1) subjects2C.add(subject);
			else throw new Exception("Fallo en lectura de asignatura: " + subject);
		}
	}
    
}
