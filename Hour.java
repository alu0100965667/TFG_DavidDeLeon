import java.util.*;

public class Hour {

    public static final int INTERVAL = 5;

    private Integer hour;
    private Integer min;

    //////////////////////////////////////////////////

    public Hour(Integer hour, Integer min) {
        this.hour = hour;
        this.min = min;
    }
    /** Crea objeto hora a partir de un entero (ej: 1030 -> 10:30) */
    public Hour(int param) {
        min = param % 100;
        hour = (param - min) / 100;
    }

    public Integer getHour() { return hour; }
    public Integer getMin() { return min; }
    public void setHour(Integer hour) { this.hour = hour; }
    public void setMin(Integer min) { this.min = min; }

    /** @return Resultado de this + param */
    public Hour add(Hour param) {
        int hours = 0;
        Integer tmpHour, tmpMin;
        Integer tmp = this.min + param.min;
        if(tmp >= 60) { hours++; tmpMin = tmp - 60; }
        else tmpMin = tmp;
        tmp = this.hour + param.hour + hours;
        
        tmpHour = tmp;
        Hour resultado = new Hour(tmpHour, tmpMin);
        return resultado;
    }

    /** @return Resultado de this - param */
    public Hour sub(Hour param) {
        int hours = 0;
        Integer tmpHour, tmpMin;
        Integer tmp = this.min - param.min;
        if(tmp < 0) { hours++; tmpMin = tmp + 60; }
        else tmpMin = tmp;
        tmp = this.hour - param.hour - hours;
        tmpHour = tmp;
        Hour resultado = new Hour(tmpHour, tmpMin);
        return resultado;
    }

    @Override
    public boolean equals(Object param) {
        if(param instanceof Hour) {
            Hour tmp = (Hour) param;
            if(this.hour.equals(tmp.hour) && this.min.equals(tmp.min))
                return true;
        }
        return false;
    }

    /** @return Set de Hour que representan un intervalo de 60 minutos */
    public static TreeSet<Hour> createHour(Hour ini) {
		TreeSet<Hour> set = new TreeSet<Hour>(new HourComp());
        Hour tmp = new Hour(ini.hour, ini.min);
        for(Integer h = 0; h < 60 / INTERVAL; h++) {
            set.add(tmp);
            tmp = tmp.add(new Hour(0, INTERVAL));
        }
		return set;
	} 

    /** @return true si el número representa una hora */
	public static boolean isHour(int hour) {
		if(hour < 0 || hour > 2359) return false;
		if((hour / 10) % 10 <= 5) return true;
		return false;
	}

    public String toString() {
        String tmp = "" + hour + ":";
        if(min < 10) tmp += "0";
        tmp += min;
        return tmp;
    }

    
}

class HourComp implements Comparator<Hour> {
    @Override
    public int compare(Hour h1, Hour h2) {
        if(h1.getHour() > h2.getHour()) return 1;
        else if(h1.getHour().equals(h2.getHour())) {
            if(h1.getMin() > h2.getMin()) return 1;
            else if(h1.getMin() < h2.getMin()) return -1;
            else return 0;
        }
        else return -1;
    }
}