package com.darksoft.ournotes.model;

import java.util.concurrent.TimeUnit;

public class ConversionFecha {


    private static final int seconds_millis = 1;
    private static final int minute_millis = 60 * seconds_millis;
    private static final int hours_millis = 60 * minute_millis;
    private static final int day_millis = 24 * hours_millis;

    public String conversionFecha(int time){

        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        if (time > now || time <= 0){
            return "En el futuro";
        }

        long diff = now - time;

        if (diff < minute_millis)
            return "Justo ahora";
        if (diff < 2 * minute_millis)
            return "Hace un minuto";
        if (diff < 60 * minute_millis)
            return "Hace " + (diff / minute_millis) + " minutos";
        if (diff < 2 * hours_millis)
            return "Hace una hora";
        if (diff < 24 * hours_millis)
            return "Hace " + (diff / hours_millis) + " horas";
        if (diff < 48 * hours_millis)
            return "Ayer";

        return "Hace " + (diff / day_millis) + " dias";
    }
}
