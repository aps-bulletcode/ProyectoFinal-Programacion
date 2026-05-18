package service;

import model.Cliente;
import java.time.LocalDate;
import java.time.Period;

public class CoachService {

    /**
     * Devuelve una recomendación base/prompt estructurado para usar con una IA (ej. ChatGPT)
     * basándose en las características físicas y objetivos del cliente.
     */
    public String obtenerRecomendacion(Cliente c) {
        int edad = 0;
        
        // Calcular edad si la fecha de nacimiento no es nula
        if (c.getFechaNacimiento() != null) {
            edad = Period.between(c.getFechaNacimiento(), LocalDate.now()).getYears();
        }

        // Construcción del "prompt" profesional para generar la rutina
        return String.format(
            "Actuando como Entrenador Personal de IA para %s %s:\n" +
            "- Datos del cliente: Edad: %d años, Género: %s, Altura: %d cm, Peso actual: %.2f kg.\n" +
            "- Objetivo principal: %s.\n\n" +
            "Por favor, genera un plan de entrenamiento semanal adaptado estrictamente a sus características físicas y meta, " +
            "incluyendo recomendaciones de frecuencia cardíaca, grupos musculares por día y observaciones sobre su salud articular " +
            "considerando su relación peso/altura.",
            c.getNombre() != null ? c.getNombre() : "Cliente",
            c.getApellidos() != null ? c.getApellidos() : "",
            edad,
            c.getGenero() != null ? c.getGenero() : "No especificado",
            c.getAltura(),
            c.getPesoInicial(),
            c.getObjetivoFitness() != null ? c.getObjetivoFitness() : "Salud general"
        );
    }
}
