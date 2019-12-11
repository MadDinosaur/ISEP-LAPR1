package pkg2019.lapr1_1dk_mafia;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class LAPR1_1DK_Mafia {

    /**
     * @param args the command line arguments
     */
    static Scanner sc = new Scanner(System.in);
    static final int MAX_OBSERVATIONS = 26280;
    public static void main(String[] args) throws FileNotFoundException {
        int[] consumptionMW = new int[MAX_OBSERVATIONS];
        LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];
        
        int size = readFile(consumptionMW, dateTime);
    }
    public static int readFile(int[] consumptionMW, LocalDateTime[] dateTime) throws FileNotFoundException {
        Scanner fileScan = new Scanner (new File("DAYTON.csv"));
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
    public static void definePeriod(int[] consuptionMW, LocalDateTime[] dateTime) throws FileNotFoundException {
        System.out.printf("Que resolução temporal deseja?/n"
                + "1. Períodos do dia;/n"
                + "2. Diário;/n"
                + "3. Mensal;/n"
                + "4. Anual./n");
            int resolution = sc.nextInt();
            switch (resolution){
                case 1:
                    dayPeriod(consuptionMW, dateTime, MAX_OBSERVATIONS);
                    break;
                case 2:
                    //metodo day
                    break;
                case 3:
                    //metodo mensal
                    break;
                case 4:
                    //metodo anual
                    break;
                    default:
                        System.out.println("Opção inválida. ");
            }           
    }
    
   public static void dayPeriod (int[] consuptionMW, LocalDateTime[] dateTime, int size) throws FileNotFoundException {
       int startPeriod=0, endPeriod=6;
       while (endPeriod<size){
       for (int i =startPeriod+1; i<endPeriod; i++){
          consuptionMW[startPeriod]+=consuptionMW[i];
       }
       startPeriod=endPeriod;
       endPeriod=endPeriod+6;
   }
       System.out.println(consuptionMW[0]);
   }
}
