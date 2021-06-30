import java.util.*;

/**
 * Clase que representa/almacena las horas de una actividad dentro de un horario
 * @author David de León Rodríguez
 */
public class Time {
	
	private TreeSet<Hour> monday    = new TreeSet<Hour>(new HourComp());
	private TreeSet<Hour> tuesday   = new TreeSet<Hour>(new HourComp());
	private TreeSet<Hour> wednesday = new TreeSet<Hour>(new HourComp());
	private TreeSet<Hour> thursday  = new TreeSet<Hour>(new HourComp());
	private TreeSet<Hour> friday    = new TreeSet<Hour>(new HourComp());
	private TreeSet<Hour> saturday  = new TreeSet<Hour>(new HourComp());
	
	/*________________________________________________________________*/

	public Time() {}
	
	/**
	 * Constructor que añade en el día indicado por parámetro
	 * una hora que empieza por la hora pasada por parámetro
	 */
	public Time(int day, Hour hour) {
		switch(day) {
			case 0: monday.addAll(Hour.createHour(hour)); break;
			case 1: tuesday.addAll(Hour.createHour(hour)); break;
			case 2: wednesday.addAll(Hour.createHour(hour)); break;
			case 3: thursday.addAll(Hour.createHour(hour)); break;
			case 4: friday.addAll(Hour.createHour(hour)); break;
			case 5: saturday.addAll(Hour.createHour(hour)); break;
		}
	}
		
	public TreeSet<Hour> getMonday() { return monday; }
	public TreeSet<Hour> getTuesday() { return tuesday; }
	public TreeSet<Hour> getWednesday() { return wednesday; }
	public TreeSet<Hour> getThursday() { return thursday; }
	public TreeSet<Hour> getFriday() { return friday; }
	public TreeSet<Hour> getSaturday() { return saturday; }

	public void setMonday   (TreeSet<Hour> monday) { this.monday = monday; }
	public void setTuesday  (TreeSet<Hour> tuesday) { this.tuesday = tuesday; }
	public void setWednesday(TreeSet<Hour> wednesday) { this.wednesday = wednesday; }
	public void setThursday (TreeSet<Hour> thursday) { this.thursday = thursday; }
	public void setFriday   (TreeSet<Hour> friday) { this.friday = friday; }
	public void setSaturday (TreeSet<Hour> saturday) { this.saturday = saturday; }

	/** Añade los tiempos de param a los del horario del objeto (this) */
	public void add(Time param) {
		monday.addAll(param.getMonday());
		tuesday.addAll(param.getTuesday());
		wednesday.addAll(param.getWednesday());
		thursday.addAll(param.getThursday());
		friday.addAll(param.getFriday());
		saturday.addAll(param.getSaturday());
	}
	
	/** Quita los intervalos de tiempo que tiene en común con param */
	public void remove(Time param) {
		monday.removeAll(param.getMonday());
		tuesday.removeAll(param.getTuesday());
		wednesday.removeAll(param.getWednesday());
		thursday.removeAll(param.getThursday());
		friday.removeAll(param.getFriday());
		saturday.removeAll(param.getSaturday());
	}
	
	/** @return minutos a la semana */
	public int getTotalMins() {
		return (monday.size() 	 + tuesday.size()
			  + wednesday.size() + thursday.size()
			  + friday.size()	 + saturday.size())
				* Hour.INTERVAL;
	}

	/** @return horas a la semana */
	public int getTotalHours() {
		return getTotalMins() / 60;
	}
	
	/** @return días con alguna hora a la semana */
	public int getDays() {
		int tmp = 0;
		if(!monday.isEmpty()) tmp++;
		if(!tuesday.isEmpty()) tmp++;
		if(!wednesday.isEmpty()) tmp++;
		if(!thursday.isEmpty()) tmp++;
		if(!friday.isEmpty()) tmp++;
		if(!saturday.isEmpty()) tmp++;
		return tmp;
	}
    
	public static boolean isDay(int day) {
		if(day >= 0 && day < 6) return true;
		else return false;
	}

	/**************************************************
	 ****************** Objetivos *********************
	 **************************************************/

	/** @return cantidad de intervalos de tiempo que tienen en común param y this */
	public int overlapping(Time param) {
		TreeSet<Hour> day;
		int overlap = 0;
		if(!param.getMonday().isEmpty()) {
			day = new TreeSet<Hour>(param.getMonday());
			for(Hour h : this.monday) day.remove(h);
			overlap += (param.getMonday().size() - day.size());
		}
		if(!param.getTuesday().isEmpty()) {
			day = new TreeSet<Hour>(param.getTuesday());
			for(Hour h : this.tuesday) day.remove(h);
			overlap += (param.getTuesday().size() - day.size());
		}
		if(!param.getWednesday().isEmpty()) {
			day = new TreeSet<Hour>(param.getWednesday());
			for(Hour h : this.wednesday) day.remove(h);
			overlap += (param.getWednesday().size() - day.size());
		}
		if(!param.getThursday().isEmpty()) {
			day = new TreeSet<Hour>(param.getThursday());
			for(Hour h : this.thursday) day.remove(h);
			overlap += (param.getThursday().size() - day.size());
		}
		if(!param.getFriday().isEmpty()) {
			day = new TreeSet<Hour>(param.getFriday());
			for(Hour h : this.friday) day.remove(h);
			overlap += (param.getFriday().size() - day.size());
		}
		if(!param.getSaturday().isEmpty()) {
			day = new TreeSet<Hour>(param.getSaturday());
			for(Hour h : this.saturday) day.remove(h);
			overlap += (param.getSaturday().size() - day.size());
		}
		return overlap;
	}
    
    /** @return cantidad de intervalos de tiempo que se pasan de 6 horas continuas */ 
	public int over6Hours() {
		int value = 0, cont = 0, sixH = 360 / Hour.INTERVAL;
		if(monday.size() > sixH) {
			cont = 0;
			TreeSet<Hour> tmp = new TreeSet<Hour>(monday);
			Hour last = null;
			for(Hour actual : tmp) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(actualLast.getHour().equals(last.getHour())
					&& actualLast.getMin().equals(last.getMin())) {
						cont++;
					}
					else {
						if(cont > sixH)
							value += (cont - sixH);
						cont = 0;
					}
				}
				last = actual;
			}
			if(cont > sixH)
				value += (cont - sixH);
		}
		if(tuesday.size() > sixH) {
			cont = 0;
			TreeSet<Hour> tmp = new TreeSet<Hour>(tuesday);
			Hour last = null;
			for(Hour actual : tmp) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(actualLast.getHour().equals(last.getHour())
					&& actualLast.getMin().equals(last.getMin())) {
						cont++;
					}
					else {
						if(cont > sixH)
							value += (cont - sixH);
						cont = 0;
					}
				}
				last = actual;
			}
			if(cont > sixH)
				value += (cont - sixH);
		}
		if(wednesday.size() > sixH) {
			cont = 0;
			TreeSet<Hour> tmp = new TreeSet<Hour>(wednesday);
			Hour last = null;
			for(Hour actual : tmp) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(actualLast.getHour().equals(last.getHour())
					&& actualLast.getMin().equals(last.getMin())) {
						cont++;
					}
					else {
						if(cont > sixH)
							value += (cont - sixH);
						cont = 0;
					}
				}
				last = actual;
			}
			if(cont > sixH)
				value += (cont - sixH);
		}
		if(thursday.size() > sixH) {
			cont = 0;
			TreeSet<Hour> tmp = new TreeSet<Hour>(thursday);
			Hour last = null;
			for(Hour actual : tmp) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(actualLast.getHour().equals(last.getHour())
					&& actualLast.getMin().equals(last.getMin())) {
						cont++;
					}
					else {
						if(cont > sixH)
							value += (cont - sixH);
						cont = 0;
					}
				}
				last = actual;
			}
			if(cont > sixH)
				value += (cont - sixH);
		}
		if(friday.size() > sixH) {
			cont = 0;
			TreeSet<Hour> tmp = new TreeSet<Hour>(friday);
			Hour last = null;
			for(Hour actual : tmp) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(actualLast.getHour().equals(last.getHour())
					&& actualLast.getMin().equals(last.getMin())) {
						cont++;
					}
					else {
						if(cont > sixH)
							value += (cont - sixH);
						cont = 0;
					}
				}
				last = actual;
			}
			if(cont > sixH)
				value += (cont - sixH);
		}
		if(saturday.size() > sixH) {
			cont = 0;
			TreeSet<Hour> tmp = new TreeSet<Hour>(saturday);
			Hour last = null;
			for(Hour actual : tmp) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(actualLast.getHour().equals(last.getHour())
					&& actualLast.getMin().equals(last.getMin())) {
						cont++;
					}
					else {
						if(cont > sixH)
							value += (cont - sixH);
						cont = 0;
					}
				}
				last = actual;
			}
			if(cont > sixH)
				value += (cont - sixH);
		}
		return value;
	}
    
    /** @return cantidad de intervalos de tiempo que desde la primera hora ocupada hasta
	 * la última ocupan más de ocho horas en un mismo día */
	public int over8Hours() {
		int value = 0;
		if(!monday.isEmpty()) {
			Hour ini = monday.first();
			Hour fin = monday.last();
			Hour tmp = fin.sub(ini);
			if(tmp.getHour() >= 8) {
				value += (tmp.getHour() - 8) * 60 / Hour.INTERVAL;
				value += tmp.getMin() / Hour.INTERVAL;
			}
		}
		if(!tuesday.isEmpty()) {
			Hour ini = tuesday.first();
			Hour fin = tuesday.last();
			Hour tmp = fin.sub(ini);
			if(tmp.getHour() >= 8) {
				value += (tmp.getHour() - 8) * 60 / Hour.INTERVAL;
				value += tmp.getMin() / Hour.INTERVAL;
			}
		}
		if(!wednesday.isEmpty()) {
			Hour ini = wednesday.first();
			Hour fin = wednesday.last();
			Hour tmp = fin.sub(ini);
			if(tmp.getHour() >= 8) {
				value += (tmp.getHour() - 8) * 60 / Hour.INTERVAL;
				value += tmp.getMin() / Hour.INTERVAL;
			}
		}
		if(!thursday.isEmpty()) {
			Hour ini = thursday.first();
			Hour fin = thursday.last();
			Hour tmp = fin.sub(ini);
			if(tmp.getHour() >= 8) {
				value += (tmp.getHour() - 8) * 60 / Hour.INTERVAL;
				value += tmp.getMin() / Hour.INTERVAL;
			}
		}
		if(!friday.isEmpty()) {
			Hour ini = friday.first();
			Hour fin = friday.last();
			Hour tmp = fin.sub(ini);
			if(tmp.getHour() >= 8) {
				value += (tmp.getHour() - 8) * 60 / Hour.INTERVAL;
				value += tmp.getMin() / Hour.INTERVAL;
			}
		}
		if(!saturday.isEmpty()) {
			Hour ini = saturday.first();
			Hour fin = saturday.last();
			Hour tmp = fin.sub(ini);
			if(tmp.getHour() >= 8) {
				value += (tmp.getHour() - 8) * 60 / Hour.INTERVAL;
				value += tmp.getMin() / Hour.INTERVAL;
			}
		}
		return value;
	}
    
	/** @return cantidad de intervalos desocupados entre dos intervalos ocupados que
	 * sean más de 30 minutos. Además inhabilita que se escojan asignaturas de mañana
	 * y tarde con mucho tiempo entre ellas */
	public int deadTime() {
		int value = 0;
		if(monday.size() > 2) {
			Hour last = null;
			for(Hour actual : monday) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(!actualLast.equals(last)) {
						Hour deadTime = actual.sub(last);
						if(deadTime.getHour() > 0) {
							value += deadTime.getHour() * 60 / Hour.INTERVAL;
							value += deadTime.getMin() / Hour.INTERVAL;
						}
						else if(deadTime.getMin() > 35)
							value += deadTime.getMin() / Hour.INTERVAL;
					}
				}
				last = actual;
			}
		}
		if(tuesday.size() > 2) {
			Hour last = null;
			for(Hour actual : tuesday) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(!actualLast.equals(last)) {
						Hour deadTime = actual.sub(last);
						if(deadTime.getHour() > 0) {
							value += deadTime.getHour() * 60 / Hour.INTERVAL;
							value += deadTime.getMin() / Hour.INTERVAL;
						}
						else if(deadTime.getMin() > 35)
							value += deadTime.getMin() / Hour.INTERVAL;
					}
				}
				last = actual;
			}
		}
		if(wednesday.size() > 2) {
			Hour last = null;
			for(Hour actual : wednesday) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(!actualLast.equals(last)) {
						Hour deadTime = actual.sub(last);
						if(deadTime.getHour() > 0) {
							value += deadTime.getHour() * 60 / Hour.INTERVAL;
							value += deadTime.getMin() / Hour.INTERVAL;
						}
						else if(deadTime.getMin() > 35)
							value += deadTime.getMin() / Hour.INTERVAL;
					}
				}
				last = actual;
			}
		}
		if(thursday.size() > 2) {
			Hour last = null;
			for(Hour actual : thursday) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(!actualLast.equals(last)) {
						Hour deadTime = actual.sub(last);
						if(deadTime.getHour() > 0) {
							value += deadTime.getHour() * 60 / Hour.INTERVAL;
							value += deadTime.getMin() / Hour.INTERVAL;
						}
						else if(deadTime.getMin() > 35)
							value += deadTime.getMin() / Hour.INTERVAL;
					}
				}
				last = actual;
			}
		}
		if(friday.size() > 2) {
			Hour last = null;
			for(Hour actual : friday) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(!actualLast.equals(last)) {
						Hour deadTime = actual.sub(last);
						if(deadTime.getHour() > 0) {
							value += deadTime.getHour() * 60 / Hour.INTERVAL;
							value += deadTime.getMin() / Hour.INTERVAL;
						}
						else if(deadTime.getMin() > 35)
							value += deadTime.getMin() / Hour.INTERVAL;
					}
				}
				last = actual;
			}
		}
		if(saturday.size() > 2) {
			Hour last = null;
			for(Hour actual : saturday) {
				if(last != null) {
					Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
					if(!actualLast.equals(last)) {
						Hour deadTime = actual.sub(last);
						if(deadTime.getHour() > 0) {
							value += deadTime.getHour() * 60 / Hour.INTERVAL;
							value += deadTime.getMin() / Hour.INTERVAL;
						}
						else if(deadTime.getMin() > 35)
							value += deadTime.getMin() / Hour.INTERVAL;
					}
				}
				last = actual;
			}
		}
		return value;
    }
    
    public String printDay(TreeSet<Hour> day) {
        String tmp = "";
        Hour last = null;
        
		for(Hour actual: day) {
			if(last == null)
				tmp += actual;
			
			else {
				Hour actualLast = actual.sub(new Hour(0, Hour.INTERVAL));
				if(!actualLast.equals(last))
					tmp += "-" + last + ' ' + actual;
			}
			
			if(actual.equals(day.last()))
				tmp += "-" + actual;

			last = actual;
		}
        return tmp;
    }
	
	public String toString() {
        String tmp = "";
        if(!monday.isEmpty()) {
            tmp += " l." + printDay(monday);
        }
        if(!tuesday.isEmpty()) {
            tmp += " m." + printDay(tuesday);
        }
        if(!wednesday.isEmpty())  {
            tmp += " x." + printDay(wednesday);
        }
        if(!thursday.isEmpty()) {
            tmp += " j." + printDay(thursday);
        }
        if(!friday.isEmpty()) {
            tmp += " v." + printDay(friday);
        }
        if(!saturday.isEmpty()) {
            tmp += " s." + printDay(saturday);
        }
		return tmp;
	}

}
