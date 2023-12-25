/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.Utiles;

import Controlador.Dao.Modelo.personaDao;
import Controlador.TDA.ListaDinamica.ListaDinamica;
import Controlador.TDA.listas.Exepciones.ListaVacia;
import Controlador.TDA.listas.Exepciones.PosicionNoEncontrada;
import Modelo.Persona;
import java.lang.reflect.Field;

/**
 *
 * @author Victor
 */
public class UtilesControlador {

    public static Field getField(Class clazz, String atribute) {
//        if (clazz == null || atribute == null) {
//            return null;
//        }
        Field field = null;
        for (Field f : clazz.getSuperclass().getDeclaredFields()) {
            if (f.getName().equalsIgnoreCase(atribute)) {
                field = f;
                break;
            }
        }
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().equalsIgnoreCase(atribute)) {
                field = f;
                break;
            }
        }
        return field;
    }
    
    public static ListaDinamica<Persona> Ordenar(ListaDinamica<Persona> lista, Integer tipo, String field) throws ListaVacia, Exception {
        Integer n = lista.getLongitud();
        Persona[] personas = lista.toArray();
        Field atribute = UtilesControlador.getField(Persona.class, field);
        if (atribute != null) {
            for (int i = 1; i < n - 1; i++) {
                int k = i;
                Persona personaOrden = personas[i];
                for (int j = i + 1; j < n; j++) {
                    if (personas[j].comparar(personaOrden, field, tipo)) {
                        personaOrden = personas[j];
                        k = j;
                    }
                }
                personas[k] = personas[i];
                personas[i] = personaOrden;
            }
        }
        else{
            throw new Exception("No existe el criterio de busqueda");
        }

        return lista.toList(personas);
    }

    public static ListaDinamica<Persona> ShellSort(ListaDinamica<Persona> lista, Integer tipo, String field) {
        int n = lista.getLongitud();
        Persona[] personas = lista.toArray();

        for (int intervalo = n / 2; intervalo > 0; intervalo /= 2) {
            for (int i = intervalo; i < n; i++) {
                Persona ayuda = personas[i];
                int j;
                for (j = i; j >= intervalo && ayuda.comparar(personas[j - intervalo], field, tipo); j -= intervalo) {
                    personas[j] = personas[j - intervalo];
                }
                personas[j] = ayuda;
            }
        }
        return lista.toList(personas);
    }
    
    public static ListaDinamica<Persona> QuickSort(ListaDinamica<Persona> listaPersonas, Integer Orden, String Campo) throws ListaVacia, PosicionNoEncontrada {
        if (listaPersonas == null || listaPersonas.getLongitud() <= 1) {
            return listaPersonas;
        }
        quicksortRecursivo(listaPersonas, 0, listaPersonas.getLongitud() - 1, Orden, Campo);
        return listaPersonas;
    }

    private static void quicksortRecursivo(ListaDinamica<Persona> listaPersonas, int inicio, int fin, Integer orden, String Campo) throws ListaVacia, PosicionNoEncontrada {
        if (inicio < fin) {
            int indiceParticion = Particionar(listaPersonas, inicio, fin, orden, Campo);
            quicksortRecursivo(listaPersonas, inicio, indiceParticion - 1, orden, Campo);
            quicksortRecursivo(listaPersonas, indiceParticion + 1, fin, orden, Campo);
        }
    }

    private static int Particionar(ListaDinamica<Persona> listaPersonas, int inicio, int fin, Integer orden, String Campo) throws ListaVacia, PosicionNoEncontrada {
        Persona pivote = listaPersonas.getInfo(fin);
        int i = inicio - 1;

        for (int j = inicio; j < fin; j++) {
            if (pivote.comparar(listaPersonas.getInfo(j), Campo, orden)) {
                i++;
                Intercambiar(listaPersonas, i, j);
            }
        }
        Intercambiar(listaPersonas, i + 1, fin);
        return i + 1;
    }

    private static void Intercambiar(ListaDinamica<Persona> listaPersonas, int i, int j) throws ListaVacia, PosicionNoEncontrada {
        Persona ayuda = listaPersonas.getInfo(i);
        listaPersonas.modificarPosicion(listaPersonas.getInfo(j), i);
        listaPersonas.modificarPosicion(ayuda, j);
    }
    
    public static ListaDinamica<Persona> BusquedaBinaria(ListaDinamica<Persona> lista, String query, String field) throws ListaVacia, PosicionNoEncontrada {
        ListaDinamica<Persona> listaOrdenada = QuickSort(lista, 1, field);
        ListaDinamica<Persona> resultado = new ListaDinamica<>();

        boolean encontrado = false;
        int inicio = 0;
        int fin = listaOrdenada.getLongitud() - 1;

        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            Persona Mitad = listaOrdenada.getInfo(medio);
            Field campo = getField(Persona.class, field);

            try {
                campo.setAccessible(true);
                Object valorMedioObj = campo.get(Mitad);
                String valorMedio = (valorMedioObj != null) ? valorMedioObj.toString() : "";

                if (valorMedio.toLowerCase().contains(query.trim().toLowerCase())) {
                    encontrado = true;
                    resultado.AgregarFinal(Mitad);
                    int j = medio - 1;
                    while (j >= 0) {
                        Persona personaAnterior = listaOrdenada.getInfo(j);
                        Object valorAnteriorObj = campo.get(personaAnterior);
                        String valorAnterior = (valorAnteriorObj != null) ? valorAnteriorObj.toString() : "";

                        if (valorAnterior.toLowerCase().contains(query.trim().toLowerCase())) {
                            resultado.AgregarFinal(personaAnterior);
                            j--;
                        } 
                        else {
                            break;
                        }
                    }
                    int i = medio + 1;
                    while (i < listaOrdenada.getLongitud()) {
                        Persona personaSiguiente = listaOrdenada.getInfo(i);
                        Object valorSiguienteObj = campo.get(personaSiguiente);
                        String valorSiguiente = (valorSiguienteObj != null) ? valorSiguienteObj.toString() : "";

                        if (valorSiguiente.toLowerCase().contains(query.trim().toLowerCase())) {
                            resultado.AgregarFinal(personaSiguiente);
                            i++;
                        } 
                        else {
                            break;
                        }
                    }
                    break;
                } 
                else {
                    String valorQuery = query;
                    if (valorMedio.compareToIgnoreCase(valorQuery) > 0) {
                        fin = medio - 1;
                    } 
                    else {
                        inicio = medio + 1;
                    }
                }
            } 
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return resultado;
    }

    public static ListaDinamica<Persona> BusquedaLineal(ListaDinamica<Persona> lista, String query, String field) throws ListaVacia, PosicionNoEncontrada {
        ListaDinamica<Persona> result = new ListaDinamica<>();
        Integer ultimaPosicionOcupada = lista.getLongitud();

        for (int i = 0; i < ultimaPosicionOcupada; i++) {
            Persona persona = lista.getInfo(i);
            Field campo = getField(Persona.class, field);

            try {
                campo.setAccessible(true);
                Object valorObj = campo.get(persona);
                String valor = (valorObj != null) ? valorObj.toString() : "";
                if (valor.toLowerCase().startsWith(query.trim().toLowerCase())) {
                    result.AgregarFinal(persona);
                }
            } 
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static ListaDinamica<Persona> linealSearch(ListaDinamica<Persona> lista, String query, String field) throws ListaVacia, PosicionNoEncontrada {
        ListaDinamica<Persona> resultado = new ListaDinamica<>();
        Integer ultimaPosicionOcupada = lista.getLongitud();

        for (int i = 0; i < ultimaPosicionOcupada; i++) {
            Persona persona = lista.getInfo(i);
            Field campo = getField(Persona.class, field);

            try {
                campo.setAccessible(true);
                Object valorObj = campo.get(persona);
                String valor = (valorObj != null) ? valorObj.toString() : "";

                if (valor.toLowerCase().contains(query.trim().toLowerCase())) {
                    resultado.AgregarFinal(persona);
                }
            } 
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return resultado;
    }
    
//        public static void main(String[] args) {
//        try {
//            personaDao pc = new personaDao();
//            ListaDinamica a = pc.all();
////            System.out.println(pc.ordenar(a, 0, "Nombre").toString());
//            System.out.println(BusquedaLineal(a, "b", "nombre"));
////            System.out.println(QuickSort(a, 1, "Genero"));
////            System.out.println(pc.all());
////            System.out.println("---------------------------");
////            System.out.println(pc.ordenar(pc.all(), 0, "Nombre"));
//        } 
//        catch (Exception e) {
//        }
//        
//    }
//    
}
