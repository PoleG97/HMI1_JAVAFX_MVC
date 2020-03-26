package sample;

import Moka7.IntByRef;
import Moka7.S7;
import Moka7.S7Client;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.Timer;
import java.util.TimerTask;

public class Model{
    public static final S7Client Client = new S7Client();
    public static byte[] Buffer = new byte[6];
    public static int DBSample = 1;
    public static int Rack = 0;
    public static int Slot = 1;
    private static long Elapsed;
    private static int CurrentStatus = S7.S7CpuStatusUnknown;
    private static int ok=0;
    private static int ko=0;

    boolean Salida1;
    boolean Salida2;
    boolean Salida3;
    int Personas;

    public Paint BoolToBrush(boolean Salida, Circle circulo){
        if (Salida){
            circulo.setFill(Color.GREEN);
            return Color.GREEN;
        }else{
            circulo.setFill(Color.GRAY);
            return Color.GRAY;
        }
    }

    public String Conectar(String _ip){
        String _IP = _ip;
        Client.SetConnectionType(S7.OP);
        Client.ConnectTo(_IP, 0, 1);  //Me conecto
        if (Client.Connected){
            ShowStatus();
            return "Has sido conectado de forma satisfactoria";
        }else {
            System.out.println("Ocurrio un ERROR en el conexión");
            ShowStatus();
            return "Ocurrió un ERROR en la conexión";
        }
    }
    public String Desconectar(){
        Client.Disconnect();
        System.out.println("Desconectado");
        return "Desconectado";
    }

    public void ShowStatus() {
        IntByRef PlcStatus = new IntByRef(S7.S7CpuStatusUnknown);
        TestBegin("GetPlcStatus()");
        int Result = Client.GetPlcStatus(PlcStatus);
        if (Result==0)
        {
            System.out.print("PLC Status : ");
            switch (PlcStatus.Value)
            {
                case S7.S7CpuStatusRun :
                    System.out.println("RUN");
                    //txtStatus.setText("RUN");
                    break;
                case S7.S7CpuStatusStop :
                    System.out.println("STOP");
                    //txtStatus.setText("STOP");
                    break;
                default :
                    System.out.println("Unknown ("+PlcStatus.Value+")");
            }
        }
        CurrentStatus = PlcStatus.Value;
        TestEnd(Result);
    }
    static void Error(int Code) {
        System.out.println(S7Client.ErrorText(Code));
    }

    static void TestBegin(String FunctionName) {
        System.out.println();
        System.out.println("+================================================================");
        System.out.println("| "+FunctionName);
        System.out.println("+================================================================");
        Elapsed = System.currentTimeMillis();
    }
    static void TestEnd(int Result) {
        if (Result!=0)
        {
            ko++;
            Error(Result);
        }
        else
            ok++;
        System.out.println("Execution time "+(System.currentTimeMillis()-Elapsed)+" ms");
    }

    public String[] LeerDB(Circle circulo1, Circle circulo2, Circle circulo3){
        String personasEnviar = "";
        Paint colorSalida1 = Color.GRAY;
        Paint colorSalida2 = Color.GRAY;
        Paint colorSalida3 = Color.GRAY;

        if (Client.Connected){
            byte[] buffer = new byte[6];
            int result = Client.ReadArea(S7.S7AreaDB, DBSample, 0, 4, buffer);
            if (result == 0){

                //Almacenamos el estado de las salidas provenientes del PLC
                Salida1 = S7.GetBitAt(buffer, 0, 4);
                Salida2 = S7.GetBitAt(buffer, 0, 5);
                Salida3 = S7.GetBitAt(buffer, 0, 6);
                Personas = S7.GetDIntAt(buffer, 2);

                personasEnviar = Integer.toString(Personas);

                colorSalida1 = BoolToBrush(Salida1, circulo1);
                colorSalida2 = BoolToBrush(Salida2, circulo2);
                colorSalida3 = BoolToBrush(Salida3, circulo3);

            }
        }
        String[] dataLeerDB = {personasEnviar, String.valueOf(colorSalida1), String.valueOf(colorSalida2), String.valueOf(colorSalida3)};
        return dataLeerDB;
    }

    public void WritePulContar(){
        int writeResult = WriteBit("DB1.DBX0.0", true);
        if (writeResult != 0) {
            System.out.println(Client.ErrorText(writeResult));
        }
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writeResult = WriteBit("DB1.DBX0.0", false);
        if (writeResult != 0) {
            System.out.println(Client.ErrorText(writeResult));
        }
    }
    public void WritePulDecontar(){
        int writeResult = WriteBit("DB1.DBX0.1", true);
        if (writeResult != 0) {
            System.out.println(Client.ErrorText(writeResult));
        }
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writeResult = WriteBit("DB1.DBX0.1", false);
        if (writeResult != 0) {
            System.out.println(Client.ErrorText(writeResult));
        }
    }
    public void WritePulReset(){
        int writeResult = WriteBit("DB1.DBX0.3", true);
        if (writeResult != 0) {
            System.out.println(Client.ErrorText(writeResult));
        }
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writeResult = WriteBit("DB1.DBX0.3", false);
        if (writeResult != 0) {
            System.out.println(Client.ErrorText(writeResult));
        }
    }
    public void WritevalInt(String lecturaCampoEscritura){
        Client.WriteArea(S7.S7AreaDB, Integer.parseInt(lecturaCampoEscritura), 2, 4, Buffer);
        S7.SetWordAt(Buffer, 1, Integer.parseInt(lecturaCampoEscritura));
    }

    private int WriteBit(String address, boolean value) {
        String[] strings = address.split(".");
        int db = Integer.parseInt(strings[0].replace("DB", ""));
        int pos = Integer.parseInt(strings[1].replace("DBX", ""));
        int bit = Integer.parseInt(strings[2]);
        return WriteBit(db, pos, bit, value);
    }
    public int WriteBit(int db, int pos, int bit, boolean value) {
        byte[] buffer = new byte[1];
        S7.SetBitAt(buffer, 0, bit, value);
        return Client.WriteArea(S7.S7AreaDB, db, pos + bit, buffer.length, buffer);
    }

    //Metodos auxialiares de RUN/STOP

}


