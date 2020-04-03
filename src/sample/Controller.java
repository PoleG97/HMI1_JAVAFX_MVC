package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public class Controller implements Initializable {

    // ---------- DECLARACIONES DE ELEMENTOS NECESARIOS ---------- //

    /*
     * Declaramos todos los elementos que no sean botones de la
     * interfaz gráfica con su id de code
     */
    @FXML public TextField ipField;
    @FXML public TextField statusField;
    @FXML public TextField leerField;
    @FXML public TextField escribirField;
    @FXML public Circle ledContar;
    @FXML public Circle ledDecontar;
    @FXML public Circle ledReset;

    /*
     * Declaramos e instaciamos las varibles para los objetos necesarios
     * en la tarea programada.
     *
     * La TimerTask es la instanciación de la clase anidada del mismo nombre
     * que hereda de TimerTask su void run{}
     */
    Timer temporizador = new Timer();
    TimerTask task = new FuncionARepetir();

    /*
     * Declaramos la variable booleana que usaremos para poder hace ON/OFF
     * a la tarea programada y que pueda empezar de nuevo y no continuar
     */
    boolean marca = true;

    /*
     * Instanciamos la clase Model() para poder acceder así a sus elementos
     * y a sus métodos, sirviendo así Controller de puente entre Model
     * y View
     */
    private Model modelo = new Model();

    /*
     * Método de inicialización de elemento gráficos, puede asemejarse
     * al setup de arduino, también funcionaría para crear elementos
     * gráficos, vamos cosas que se hagan 1 vez
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /*
         * Inicializamos los circulos de los led en un color determinado
         */
        ledReset.setFill(Color.GRAY);
        ledDecontar.setFill(Color.GRAY);
        ledContar.setFill(Color.GRAY);
    }


    // ---------- MÉTODOS PARA LOS LISTENERS ---------- //


    /*
     * Métodos para las acciones de conectarse con el PLC, Conectar y Desconectar
     */
    @FXML public void clickConectar(ActionEvent event){
        String mensajeConectar = modelo.Conectar("192.168.0.1");
        statusField.setText(mensajeConectar);
    }
    @FXML public void clickDesconectar(ActionEvent event){
        String mensajeDesconexion = modelo.Desconectar();
        statusField.setText(mensajeDesconexion);
    }

    /*
     * Métodos de los botones de acción para el contador
     */
    @FXML public void clickContar(ActionEvent event){
        modelo.WritePulContar();
    }
    @FXML public void clickReset(ActionEvent event){
        modelo.WritePulReset();
    }
    @FXML public void clickDecontar(ActionEvent event){
        modelo.WritePulDecontar();
    }
    @FXML public void clickEscribir(ActionEvent event){
        String valorLeer = escribirField.getText();
        modelo.WritevalInt(valorLeer);
    }
    @FXML public void clickLeer(ActionEvent event){

    }

    /*
     * Declaramos los métodos de RUN/STOP de la tarea programada para poder hacerlo
     * desde un par de botones.
     * El reinicio de la tarea es total, se crea una nueva tras haber parado, lo que
     * significa que no tendrá memoria
     */
    @FXML public void run(ActionEvent event){

        /*
         * La variable "marca" sirve para decidir que tipo de trabajo estamos haciendo.
         * Esto quiere de decir que como vemos, este método se ejecutará con con RUN,
         * por lo tanto, "marca" será capaz de decidir si tiene que crear una nueva tarea
         * desde el principio (en el caso de que venga del botón STOP) o bien, crearma
         * a partir de los elementos Timer y TimerTask creados con anteorioridad
         */
        if (marca){
            temporizador.schedule(task, 0, 1000);
            task.run();
        }else {
            TimerTask taskAux = new FuncionARepetir();
            temporizador.schedule(taskAux,0, 1000);
        }
    }
    @FXML public void stop(ActionEvent event){
        temporizador.cancel();
        temporizador = new Timer();
        marca = false;
    }

    /*
     * Esta clase es una clase anidada en Controller para poder acceder
     * a los elementos gráficos que se definen en ella. Esto habría sido
     * tan simple como hacer que heredad de Controller, pero claro, para
     * que pueda hacer la tarea de las repeticiones necesitamos que herede
     * de TimerTask, y anidar la clase en Controller nos permite "saltarnos"
     * el problema de la herencia múltiple
     */
    class FuncionARepetir extends TimerTask {

        public void run() {
            /*
             * Almacenamos en la variable readData la salida de la ejecución de
             * la función LeerDB() alojada en Model.java
             */
            String[] readData = modelo.LeerDB();

            /*
             * Almacenamos en variables locales las variables de LeerDB con las
             * que construimos la salida de la propia función en Model(). Cada
             * variable local almacena una variable alojada en una posición
             * distinta del Array
             */
            String _personas = readData[0];
            String _colorContar = readData[1];
            String _colorDecontar = readData[2];
            String _colorReset = readData[3];

            /*
             * Actuamos sobre los elementos de la GUI creados en Controller con
             * nuestros valores de LeerDB obtenidos en el anterior paso
             */
            leerField.setText(_personas);
            ledContar.setFill(Paint.valueOf(_colorContar));
            ledDecontar.setFill(Paint.valueOf(_colorDecontar));
            ledReset.setFill(Paint.valueOf(_colorReset));
        }
    }
}

/*
            contador++;
            System.out.println("Este es el radio: " + ledReset.getRadius());
 */