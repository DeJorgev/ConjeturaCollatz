package conjeturadecollatz;

public class HiloProcesador implements Runnable {
    //Variables regulares hilo
    private long minimo,maximo;
    private Datos datos;
    private boolean modoIntervalo,modoCompletado;
    
    //variables resultados más elevadas. 
    private long numeroMasAltoHilo;
    private String [] secuenciaMasLargaHilo = {};
    
    //Contructor para que el hilo trabaje con intervalos
    public HiloProcesador(long minimo, long maximo, Datos datos,boolean modoCompletado) {
        this.minimo = minimo;
        this.maximo = maximo;
        this.datos = datos;
        this.modoCompletado = modoCompletado;
        this.modoIntervalo = true;
    }
    
    //Constructor para que el hilo trabaje con 1 numero cada vez
    public HiloProcesador(Datos datos,boolean modoCompletado) {
        this.datos = datos;
        this.modoCompletado = modoCompletado;
        this.modoIntervalo = false;        
    }
    
    @Override
    public void run(){
       long valorEnPrueba = 0;  
       
        if (modoIntervalo) {
            for (long i = minimo; i <= maximo && i != Long.MIN_VALUE;++i) {
                valorEnPrueba = i;             
                gestionarPruebaNumero(valorEnPrueba);
            }
        }else
            //-1 es el valor que devuelve datos al acabar con el rango de numeros solicitado por el usuario
             while(valorEnPrueba != -1 && valorEnPrueba != Long.MIN_VALUE){                 
                valorEnPrueba = datos.getsetNumeroAProcesar();
                if(valorEnPrueba!=-1)
                    gestionarPruebaNumero(valorEnPrueba);
            }
        procesarDatosMasAltos();
    }
        
//Pila de metodos de prueba de conjetura en un numero    
    //Comprueba la conjetura de collatz de un numero provisto probando que no aparezcan otros ciclos y recogiendo los datos necesarios.
    private void gestionarPruebaNumero(long valorEnPrueba){
        long valorSecuencia = valorEnPrueba;
        String cadenaSecuencia = " " + valorEnPrueba + " ",valorInicioBucle = "";
        boolean cicloEnBucle = false, valorSecuenciaYaExplorado = false;
        try {   
            while (!cicloEnBucle && !valorSecuenciaYaExplorado) {
                
                valorSecuencia = realizarCalculoNumero(valorSecuencia);
                //Comprobacion de que entra en bucle
                if (cadenaSecuencia.contains(" " + valorSecuencia + " ")){ 
                    cicloEnBucle = true;
                    valorInicioBucle = String.valueOf(valorSecuencia);
                }else
                    cadenaSecuencia += valorSecuencia + " ";
                valorSecuenciaYaExplorado = estaEnMapa(valorSecuencia, modoCompletado);
            }
            cadenaSecuencia = gestionAutocompletadoHashMap(valorSecuenciaYaExplorado, cadenaSecuencia, valorSecuencia, valorInicioBucle);
            
            String[] secuenciaDividida = cadenaSecuencia.split(" ");
            esBucleAnomalo(secuenciaDividida, valorEnPrueba);
            esCadenaMasLarga(secuenciaDividida);
            
        } catch (ArithmeticException e) { datos.setNumerosNoAnalizados(String.valueOf(valorEnPrueba));}
    }
    
    //Realiza los calculos de la conjetura
    private long realizarCalculoNumero(long valorSecuencia){
                if(valorSecuencia % 2 == 0)
                    valorSecuencia /= 2;
                else {         
                    //Operacion x*3 + 1 controlando desbordamiento
                    valorSecuencia = Math.incrementExact(Math.multiplyExact(valorSecuencia, 3));
                    esNumeroMasAltoTotal(valorSecuencia);
                }   
        return valorSecuencia;
    }
    
    //Comprueba si el bucle cumple o no la conjetura y en caso negativo guarda el numero probado como Anomalo
    private void esBucleAnomalo(String [] secuenciaDividida, long valorEnPrueba){
        if(!secuenciaDividida[secuenciaDividida.length-1].equals("1"))
                datos.setNumerosAnomalos(String.valueOf(valorEnPrueba));
    }
    
    //Metodos de completado por hashmap
        //Si modo completado esta activado comprueba si el numero existe como clave en el mapa y devuelve true o false, en caso contrario siempre devuelve false.
    private boolean estaEnMapa(long valorAComprobar,boolean modoCompletado){
        if (modoCompletado) 
            return datos.comprobarKeyMap(valorAComprobar);
        else
            return false;
    }
        
        //Si la secuencia ya ha sido explorada la Completa, en caso contrario añade los valores que todavia no existen al hashmap de datos.
    private String gestionAutocompletadoHashMap(Boolean valorSecuenciaYaExplorado, String cadenaSecuencia, long valorSecuencia, String valorInicioBucle){
        if (valorSecuenciaYaExplorado)
                cadenaSecuencia = datos.autoCompletarSecuencia(valorSecuencia,cadenaSecuencia);  
            else 
                datos.aniadirSecuenciaAMapa(cadenaSecuencia.split(" "),valorInicioBucle);
        return cadenaSecuencia;
    }
    
//Pila de metodos de procesamiento de datos más altos    
    //Comprueba si el número en secuencia es el mas alto alcanzado por el hilo
    private void esNumeroMasAltoTotal(long valorAComprobar){
        if (numeroMasAltoHilo < valorAComprobar) 
            numeroMasAltoHilo = valorAComprobar;
    }
    
    //Comprueba si la cadena que contiene la secuencia actual es la que tiene mas numeros del hilo
    private void esCadenaMasLarga(String[] secuenciaDividida){
        if(secuenciaDividida.length > secuenciaMasLargaHilo.length)
            secuenciaMasLargaHilo = secuenciaDividida;               
    }
    
    //Comprueba si los datos mas altos de todo el hilo lo son a su vez del resto por el momento y de ser asi los sube a la clase Datos.
    private void procesarDatosMasAltos(){
        datos.setNumeroMasAlto(numeroMasAltoHilo);        
        datos.compararTamañoSecuencia(secuenciaMasLargaHilo);
        }
}