package application.validators;

import java.time.LocalDateTime;

public class ComentarioValidator {

    // ✅ MÉTODOS DE CONVENIENCIA
    public static boolean esReciente(LocalDateTime fechaCreacion) {
        if (fechaCreacion == null) return false;
        return fechaCreacion.isAfter(LocalDateTime.now().minusDays(7));
    }

    public static boolean esPositivo(Integer calificacion) {
        return calificacion != null && calificacion >= 4;
    }

    public static boolean esNegativo(Integer calificacion) {
        return calificacion != null && calificacion <= 2;
    }

    public static String getCalificacionEstrellas(Integer calificacion) {
        if (calificacion == null) return "Sin calificación";
        return "★".repeat(calificacion) + "☆".repeat(5 - calificacion);
    }

    // ✅ MÉTODO PARA CALCULAR TIEMPO TRANSCURRIDO
    public static String calcularTiempoTranscurrido(LocalDateTime fecha) {
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = java.time.Duration.between(fecha, ahora).toMinutes();
        long horas = java.time.Duration.between(fecha, ahora).toHours();
        long dias = java.time.Duration.between(fecha, ahora).toDays();

        if (minutos < 1) {
            return "Ahora mismo";
        } else if (minutos < 60) {
            return minutos + " minuto" + (minutos != 1 ? "s" : "");
        } else if (horas < 24) {
            return horas + " hora" + (horas != 1 ? "s" : "");
        } else if (dias < 30) {
            return dias + " día" + (dias != 1 ? "s" : "");
        } else {
            long meses = dias / 30;
            if (meses < 12) {
                return meses + " mes" + (meses != 1 ? "es" : "");
            } else {
                long años = meses / 12;
                return años + " año" + (años != 1 ? "s" : "");
            }
        }
    }

    // ✅ MÉTODO PARA VERIFICAR SI TIENE RESPUESTA
    public static boolean tieneRespuesta(String respuesta) {
        return respuesta != null && !respuesta.trim().isEmpty();
    }
}