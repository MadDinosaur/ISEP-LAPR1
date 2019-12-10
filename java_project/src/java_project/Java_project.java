package Java_project;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Java_project {

    static final int MAX_OBSERVATIONS = 26280;

    static void main(String[] args) throws FileNotFoundException {
        int[] consumptionMW = new int[MAX_OBSERVATIONS];
        LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];
        int size = readFile(consumptionMW, dateTime);
    } 
    
    
    //método para ler ficheiro
    public static int readFile(int[] consumptionMW, LocalDateTime[] dateTime) throws FileNotFoundException {
        Scanner fileScan = new Scanner(new File("DAYTON.csv"));
        fileScan.nextLine(); //descarta a linha do cabeçalho
        int numLines = 0; //conta as linhas do documento
        while (fileScan.hasNextLine()) {
            String[] line = fileScan.nextLine().split(","); //separa em Datetime e Comsumption
            //guarda data e hora no array dateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateTime[numLines] = LocalDateTime.parse(line[0], formatter);
            //guarda consumo no array consumptionMW
            consumptionMW[numLines] = Integer.parseInt(line[1]);
            numLines++;
        }
        fileScan.close();
        //retorna para entrar como comprimento do array
        return numLines;
    }
    
    //escolha do periodo do dia
    //temporário, ver amanhã com o professor
    public static void getHour (int []consuptionMW, LocalDateTime[] dateTime) throws FileNotFoundException{
        double [] Morning = new double [MAX_OBSERVATIONS];
        
        for (int i=0;i<dateTime.length;i++){
            if (dateTime[i].getHour()>=6 && dateTime[i].getHour()<12){
                    //passar informação toda da linha para array morning
                
            }else{
                 if (dateTime[i].getHour()>=12 && dateTime[i].getHour()<18){
                    //passar informação toda da linha para array afternoon
                 
                 } else {
                      if (dateTime[i].getHour()>=18&&dateTime[i].getHour()<24){
                    //passar informação toda da linha para array night
                
                 }else{
                         if (dateTime[i].getHour()>=0&& dateTime[i].getHour()<6){
                    //passar informação toda da linha para array dawn
                }
                         }
                      }
            }
        }
    }
}

