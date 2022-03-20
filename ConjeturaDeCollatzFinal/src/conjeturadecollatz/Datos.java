package conjeturadecollatz;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Datos {
    //Variables para calcular el tiempo de ejecucion
    private long tiempoInicio = System.currentTimeMillis(); 
    
    //Variables con resultados.
    private String numerosAnomalos  = "" , numerosNoAnalizados = "";
    private long numeroMasAlto;
    private ArrayList<String[]> secuenciasMasLargas = new ArrayList<>();
    
    //Variables números de uno en uno.
    private long maximo,numeroAProcesar;    
    
    //HashMap numeros comprobados.
    private ConcurrentHashMap<Long,Long> mapaNumerosComprobados = new ConcurrentHashMap<>();
        
    //Constructor para intervalos.
    public Datos() {
    }    
    
    //Constructor para números de uno en uno
    public Datos(long minimo,long maximo) {
        this.maximo = maximo;
        numeroAProcesar = minimo;
    }
    
//Pila de metodos get/set sencillos.    
    
    public synchronized void setNumerosAnomalos(String numerosAnomalos) {
        this.numerosAnomalos += numerosAnomalos + " ";
    }

    public synchronized void setNumerosNoAnalizados(String numerosNoAnalizados) {
        this.numerosNoAnalizados += numerosNoAnalizados + " ";
    }
    
    public synchronized void setNumeroMasAlto(long numeroAComprobar) {
        if(numeroMasAlto<numeroAComprobar)
            this.numeroMasAlto = numeroAComprobar;
    } 
    
//Pila de metodos get y/o set.   
 //Metodo de numeros uno a uno
    //Si el numero a procesar alcanza el maximo o el valor maximo de Long devuelve -1 para señalar el fin a los hilos
    public synchronized long getsetNumeroAProcesar() {
        long NumeroAEnviar = numeroAProcesar;
        numeroAProcesar++;
        
         if(NumeroAEnviar <= maximo && NumeroAEnviar != Long.MIN_VALUE)
            return NumeroAEnviar;
        else 
            return -1;
    }
    
  //Pila de metodos para secuencias mas largas. 
    //Comprueba el valor de la secuencia enviada por el hilo y llama a un metodo en consecuencia
    public synchronized void compararTamañoSecuencia(String [] secuenciaHilo){
        if (secuenciasMasLargas.isEmpty()) 
            setNewSecuenciaMasLarga(secuenciaHilo);
        else{
            if(secuenciasMasLargas.get(0).length == secuenciaHilo.length)
                addSecuenciasMasLargas(secuenciaHilo);
            if(secuenciasMasLargas.get(0).length < secuenciaHilo.length)
                setNewSecuenciaMasLarga(secuenciaHilo);
        }
    }
    
    //Si el string con la secuencia es mas largo que los que ya existen se borran todos y se añade este.
    private void setNewSecuenciaMasLarga(String [] secuenciaMasLarga){
       secuenciasMasLargas.clear();
       secuenciasMasLargas.add(secuenciaMasLarga);
    }
    
    //Recibe un string con la secuencia igual de larga que los que ya existen en el arrayList y si no existe un string igual, se añade este al arrayList.
    private void addSecuenciasMasLargas(String[] secuenciaIgualDeLarga) {
            secuenciasMasLargas.add(secuenciaIgualDeLarga);
    }

 //Pila de metodos para el HashMap.
    //Comprueba si el numero esta en el mapa y actua en consideración.
    public boolean comprobarKeyMap(long valorAComprobar){
        return mapaNumerosComprobados.containsKey(valorAComprobar);
    }
    
    //Añade una secuencia entera dividida en un array de strings.
    public synchronized void aniadirSecuenciaAMapa(String[] secuenciaAAniadir,String valorInicioBucle){
        for(int i = 2; i<secuenciaAAniadir.length; i++)
            mapaNumerosComprobados.putIfAbsent(Long.parseLong(secuenciaAAniadir[i-1]),Long.parseLong(secuenciaAAniadir[i]));
        //Añade si fuera necesario el valor con el que empezaria a iniciarse el bucle.   
        mapaNumerosComprobados.putIfAbsent(Long.parseLong(secuenciaAAniadir[secuenciaAAniadir.length-1]),Long.parseLong(valorInicioBucle));
    }
    
    //Dado un número guardado en el mapa, devuelve el resto de su secuencia hasta entrar en un bucle. Es una zona crítica con un indice de error muy bajo
    public synchronized String autoCompletarSecuencia(long claveASacar,String secuenciaACompletar){
        String secuenciaTotal = secuenciaACompletar;
        boolean entraEnBucle = false;
    
            while(!entraEnBucle){
                claveASacar = mapaNumerosComprobados.get(claveASacar);
                if(secuenciaTotal.contains(" " + claveASacar + " "))
                    entraEnBucle = true;
                else
                    secuenciaTotal += claveASacar + " ";
            }
         
         return secuenciaTotal;
    }
    
//Metodos varios.    
    //Envia los resultados procesados al proceso principal.
    public void sacarResultados() {
        long tiempoFin = System.currentTimeMillis(), tiempoTotal = tiempoFin - tiempoInicio;
        
        System.out.println("Tiempo de proceso: " + tiempoTotal + "ms\n"
             + "Número más alto alcanzado: " + numeroMasAlto + "\n" + "Secuencias más largas: ");
       
        for (int i = 0; i < secuenciasMasLargas.size(); i++)
            for (int j = 1; j < secuenciasMasLargas.get(i).length; j++)             
                System.out.print(secuenciasMasLargas.get(i)[j] + " "); 
        System.out.println("");
        
        System.out.println("Longitud total de la cadena: " + secuenciasMasLargas.get(0).length + " Números \n" 
             +   "Números no analizados: " + numerosNoAnalizados + "\n"
             + "Números que no cumplen la conjetura: " + numerosAnomalos + "\n");
    }    
}