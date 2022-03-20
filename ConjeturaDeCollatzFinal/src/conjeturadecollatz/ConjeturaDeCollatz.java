package conjeturadecollatz;
import java.util.Scanner;
public class ConjeturaDeCollatz {
    
    //Variables de control.
    static private long Minimo, Maximo;
    static private int nHilos;
    static private boolean modoIntervalos,modoCompletar;
    
    
    public static void main(String[] args) {
        obtenerValoresVariablesPrueba();
        if (modoIntervalos)
            lanzarHilosIntervalos();
        else
            lanzarHilosNumerosUnoAUno();
        
        System.out.println("Número inicio pruebas: " + Minimo + " Número final pruebas: " + Maximo);
        System.out.println("Número de hilos activos:" + nHilos);
        System.out.println("Modo Intervalos: " + modoIntervalos);
        System.out.println("Modo Completar: " + modoCompletar);
    }
    
    //Genera bajo gestion del usuario las variables iniciales de la prueba.
    static private void obtenerValoresVariablesPrueba(){
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Introduce el valor mínimo del intervalo");
        Minimo = Long.parseLong(scanner.nextLine());
                
        System.out.println("Introduce el valor máximo del intervalo");
        Maximo = Long.parseLong(scanner.nextLine());
        
        System.out.println("Introduce el número de hilos para el procesamiento del intervalo");
        nHilos = Integer.parseInt(scanner.nextLine());
                
        System.out.println("Desea usar el modo intervalo S/N (cualquier cosa excepto s/S iniciara el modo de uno en uno)");
            if (scanner.nextLine().toLowerCase().equals("s"))
                modoIntervalos=true;
            else
                modoIntervalos=false;
            
        System.out.println("Desea usar el modo Completado por HashMap S/N (cualquier cosa excepto s/S iniciara el modo sin completado)");
            if (scanner.nextLine().toLowerCase().equals("s"))
                modoCompletar=true;
            else
                modoCompletar=false;
    }
    
    //Genera y lanza hilos preparados para trabajar con intervalos de numeros recibidos una unica vez.
    static private void lanzarHilosIntervalos(){
       long [][] intervalos = calcularIntervalos(Minimo, Maximo, nHilos);
       Datos datos = new Datos();
       Thread [] hilos = new Thread [nHilos];
       for (int i = 0;i < hilos.length; ++i) {
            HiloProcesador h = new HiloProcesador(intervalos[i][0], intervalos[i][1], datos, modoCompletar);
            hilos[i]=new Thread(h);
            hilos[i].start();
        }
        joinArrayHilos(hilos);
        
        datos.sacarResultados();        
    }
    
    //Genera y lanza hilos preparados para trabajar con un unico numero cada vez y solicitar el siguiente a la clase Datos.
    static private void lanzarHilosNumerosUnoAUno(){
       Datos datos = new Datos(Minimo, Maximo);
       Thread [] hilos = new Thread [nHilos];
       
       for (int i = 0;i < hilos.length; ++i) {
            HiloProcesador h = new HiloProcesador(datos, modoCompletar);
            hilos[i]=new Thread(h);
            hilos[i].start();
        }
        joinArrayHilos(hilos);
        datos.sacarResultados();
    }
    
    //Simplemente lanza el for con join del array de hilos para evitar la repetición de codigo.
    static private void joinArrayHilos(Thread [] hilos ){
        for (int i=0;i<hilos.length;++i) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                System.err.println("error de interrupcion en lanzarHilosIntervalos(): " + e);
            }
        }
    }
    
    //Genera un array bidimensional tipo long con los intervalos de números que debera resolver cada hilo.
    static private long [][] calcularIntervalos(long min,long max,int nHilos){
        //[x][0]:Mínimo del intervalo [x][1]:Máximo del intervalo
        long[][] intervalos = new long [nHilos][2];
        long tamañoRegularIntervalo = (max-min)/nHilos;
        intervalos[0][0] = min;
        intervalos[0][1] = min + tamañoRegularIntervalo;
        
        for(int i = 1; i < intervalos.length; i++)
            for (int j = 0; j < intervalos[0].length; j++)
                //Si j marca el mínimo del intervalo, rellenarlo con el máximo de la posición anterior + 1.
                if(j==0)
                    intervalos[i][j] = intervalos[i - 1][1]+1;
                //Si j marca el máximo del intervalo, rellenarlo con el maximo de la posicion anterior más el tamaño regular del intervalo.
                else{
                    intervalos[i][j] = intervalos[i-1][1] + tamañoRegularIntervalo;
                    //En caso de que el intervalo sea de un unico numero por hilo lo sobreescribe para que todos tengan uno.
                    if(intervalos[i][0] > intervalos[i][1])
                        intervalos[i][j] = intervalos[i][0];
                }
        
        //Sobreescribimos el ultimo maximo del ultimo intervalo para evitar problemas con restos decimales.
        intervalos[nHilos-1][1] = max;
        return intervalos;
    }
}