package edu.upc.dsa;

import edu.upc.dsa.models.FAQ;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FAQResource {

    public List<FAQ> getFAQs() {
        List<FAQ> faqs = new ArrayList<>();
        faqs.add(new FAQ("¿Cómo puedo hacer esto?", "Esto se hace así.", "Alex", new Date()));
        faqs.add(new FAQ("¿Qué has cambiado en esta pantalla?", "Nada.", "Matías", new Date()));
        faqs.add(new FAQ("¿Funciona bien?", "A veces sí.", "Pol", new Date()));
        faqs.add(new FAQ("¿Por qué no funciona?", "Porque no.", "Jordi", new Date()));
        faqs.add(new FAQ("¿Qué es esto?", "Esto es un FAQ.", "Pau", new Date()));
        return faqs;
    }
}