package pkg2019.lapr1_1dk_mafia;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

public class LAPR1_1DK_Mafia {
    static Scanner sc = new Scanner(System.in);
    static final int MAX_OBSERVATIONS = 26280;
    static final int NUM_DAY_STAGES=6;
    static final int NUM_HOURS=24;

    public static void main(String[] args) throws FileNotFoundException {
        int[] consumptionMW = new int[MAX_OBSERVATIONS];
        LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];

        int size = readFile(consumptionMW, dateTime);
        definePeriod(consumptionMW, dateTime, size);
    }

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

    public static void definePeriod(int[] consuptionMW, LocalDateTime[] dateTime, int size) throws FileNotFoundException {
        System.out.printf("Que resolução temporal deseja? %n"
                + "1. Períodos do dia; %n"
                + "2. Diário; %n"
                + "3. Mensal; %n"
                + "4. Anual. %n");
        int resolution = sc.nextInt();
        switch (resolution) {
            case 1:
                dayPeriod(consuptionMW, dateTime, size);
                break;
            case 2:
                daily(consuptionMW, dateTime, size);
                break;
            case 3:
                monthlyPeriod(consuptionMW, dateTime, size);
                break;
            case 4:
                annualPeriod(consuptionMW, dateTime, size);
                break;
            default:
                System.out.println("Opção inválida. ");
        }
    }

    public static void dayPeriod(int[] consuptionMW, LocalDateTime[] dateTime, int size) throws FileNotFoundException {
        int startPeriod = 0, endPeriod = 6;
        while (endPeriod < size) {
            for (int i = startPeriod + 1; i < endPeriod; i++) {
                consuptionMW[startPeriod] += consuptionMW[i];
            }
            startPeriod = endPeriod;
            endPeriod = endPeriod + 6;
        }
        System.out.println(consuptionMW[0]);
        exchangeInfo(consuptionMW, dateTime, size, NUM_DAY_STAGES);
    }

    public static void daily(int[] consuptionMW, LocalDateTime[] dateTime, int size) throws FileNotFoundException {
        int startPeriod = 0, endPeriod = 24;
        while (endPeriod < size) {
            for (int i = startPeriod + 1; i < endPeriod; i++) {
                consuptionMW[startPeriod] += consuptionMW[i];
            }
        startPeriod = endPeriod;
        endPeriod = endPeriod + 24;
        }
        exchangeInfo(consuptionMW, dateTime, size, NUM_HOURS);
    }

    public static void monthlyPeriod(int[] consuptionMW, LocalDateTime[] dateTime, int size) {
        int startPeriod = 0, month = 1;
        while (month < 13) {
            while (dateTime[startPeriod].getMonthValue() == month) {
                consuptionMW[startPeriod] += consuptionMW[startPeriod + 1];
                startPeriod++;
            }
            month++;
        }
        System.out.println(consuptionMW[0]);
    }

    public static void annualPeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size) {
        int startPeriod = 0, year = dateTime[0].getYear();
        while (startPeriod < size) {
            while (dateTime[startPeriod].getYear() == year) {
                consumptionMW[startPeriod] += consumptionMW[startPeriod + 1];
                startPeriod++;
            }
            year++;
        }
    }

    public static void exchangeInfo(int[] consumptionMW, LocalDateTime[] dateTime, int size, int period) {
        int i;
        for (i=1; i<size/period;i++){ 
            int idx2 = i*period;
            //trocar datas
            dateTime[i] = dateTime[idx2];
            //trocar consumos
            consumptionMW[i] = consumptionMW[idx2];
        }
        size=i;
        System.out.println(size);
        System.out.println(dateTime[size-2]);
    }
}
