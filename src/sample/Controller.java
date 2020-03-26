package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public class Controller implements Initializable {

    Timer temporizador = new Timer();
    TimerTask task = new FuncionARepetir(0);

    boolean marca = true;

    private Model modelo = new Model();

    @FXML public TextField ipField;
    @FXML public TextField statusField;
    @FXML public TextField leerField;
    @FXML public TextField escribirField;
    @FXML public Circle ledContar;
    @FXML public Circle ledDecontar;
    @FXML public Circle ledReset;

    //Método de inicialización, se puede asemejar al setup del arduino
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Inicializamos los circulos de los led en un color determinado
        ledReset.setFill(Color.GRAY);
        ledDecontar.setFill(Color.GRAY);
        ledContar.setFill(Color.GRAY);
    }

    //Continuar con las acciones de los listeners
    //Métodos de accción de comunicación
    @FXML public void clickConectar(ActionEvent event){
        String mensajeConectar = modelo.Conectar("192.168.0.1");
        statusField.setText(mensajeConectar);
    }
    @FXML public void clickDesconectar(ActionEvent event){
        String mensajeDesconexion = modelo.Desconectar();
        statusField.setText(mensajeDesconexion);
    }

    //Métodos de los botones de interacción con el contador
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
        /*
        String[] readData = modelo.LeerDB(ledContar, ledDecontar, ledReset);

        String _personas = readData[0];
        String _colorContar = readData[1];
        String _colorDecontar = readData[2];
        String _colorReset = readData[3];

        leerField.setText(_personas);
        ledContar.setFill(Paint.valueOf(_colorContar));
        ledDecontar.setFill(Paint.valueOf(_colorDecontar));
        ledReset.setFill(Paint.valueOf(_colorReset));
        */
    }

    //Métodos de arranque y paro de la lectura
    @FXML public void run(ActionEvent event){
        if (marca){
            temporizador.schedule(task, 0, 1000);
            task.run();
        }else {
            TimerTask taskAux = new FuncionARepetir(0);
            temporizador.schedule(taskAux,0, 1000);
        }
    }
    @FXML public void stop(ActionEvent event){
        temporizador.cancel();
        temporizador = new Timer();
        marca = false;
    }
}

class FuncionARepetir extends TimerTask {

    int contador;

    public FuncionARepetir(int contador_param){
        contador = contador_param;
    }

    public void run() {
        contador++;
        System.out.println("toc toc" + contador);
    }
}

