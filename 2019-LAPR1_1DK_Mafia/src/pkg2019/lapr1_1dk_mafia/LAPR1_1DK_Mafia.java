package pkg2019.lapr1_1dk_mafia;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import javafx.print.PaperSource;

public class LAPR1_1DK_Mafia {

    static Scanner sc = new Scanner(System.in);
    static final int MAX_OBSERVATIONS = 26280;
    static final int NUM_DAY_STAGES = 6;
    static final int NUM_HOURS = 24;

    public static void main(String[] args) throws FileNotFoundException {
        int[] consumptionMW = new int[MAX_OBSERVATIONS];
        LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];

        int size = readFile(consumptionMW, dateTime);
        int middle=size/2;
        int start=0;
        int[] auxConsumptionMW = definePeriod(consumptionMW, dateTime, size, middle, start);
        AverageConsumption(auxConsumptionMW, size);

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

    public static void higherConsumption(int[] auxConsumptionMW, int size) throws FileNotFoundException {
        int higher = auxConsumptionMW[0];
        for (int i = 1; i < size; i++) {
            if (auxConsumptionMW[i] > higher) {
                higher = auxConsumptionMW[i];
            }
        }
        System.out.println("O maior valor de consumo registado foi: " + higher);
    }

    public static void lowerConsumption(int[] auxConsumptionMW, int size) throws FileNotFoundException {
        int lower = auxConsumptionMW[0];
        for (int i = 1; i < size; i++) {
            if (auxConsumptionMW[i] < lower) {
                lower = auxConsumptionMW[i];
            }
        }
        System.out.println("O menor valor de consumo registado foi: " + lower);
    }

    public static int[] definePeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size, int middle, int start) throws FileNotFoundException {
        int[] auxConsumptionMW = new int[MAX_OBSERVATIONS];
        for (int i = 0; i < size; i++) {
            auxConsumptionMW[i] = consumptionMW[i];
        }
        System.out.printf("Que resolução temporal deseja? %n"
                + "1. Períodos do dia; %n"
                + "2. Diário; %n"
                + "3. Mensal; %n"
                + "4. Anual. %n");
        int resolution = sc.nextInt();
        switch (resolution) {
            case 1:
                dayPeriod(consumptionMW, dateTime, size);
                mergeSort(consumptionMW, start, size);
                break;
            case 2:
                daily(consumptionMW, dateTime, size);
                periodAverages(consumptionMW, dateTime, size);
                break;
            case 3:
                monthlyPeriod(consumptionMW, dateTime, size);
                break;
            case 4:
                annualPeriod(consumptionMW, dateTime, size);
                break;
            default:
                System.out.println("Opção inválida. ");
        }
        return auxConsumptionMW;
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
        exchangeInfoDays(consuptionMW, dateTime, size, NUM_DAY_STAGES);
    }

    public static void periodAverages(int[] consumptionMW, LocalDateTime[] dateTime, int size) {
        int consumptionSum = 0;
        for (int i = 0; i < size; i++) {
            consumptionSum += consumptionMW[i];
        }
        System.out.println("Média : " + (consumptionSum / (size / 4)) + " " + "MW");
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
        exchangeInfoDays(consuptionMW, dateTime, size, NUM_HOURS);
    }
  
    public static void monthlyPeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size) {
        int startPeriod = 0;
        int numYears = Math.abs(dateTime[0].getYear() - dateTime[size - 1].getYear());
        for (int years = 0; years < numYears + 1; years++) {
            int month = 1;
            while (month < 13) {
                while (dateTime[startPeriod].getMonthValue() == month) {
                    consumptionMW[startPeriod] += consumptionMW[startPeriod + 1];
                    startPeriod++;
                    month++;
                }
                //exchangeInfoMonthsYears(consumptionMW, dateTime, size, startPeriod);
            }
        }
        System.out.println(consumptionMW[size - 1]);
    }

    public static void annualPeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size) {
        int startPeriod = 0, year = dateTime[0].getYear();
        while (startPeriod < size) {
            while (dateTime[startPeriod].getYear() == year) {
                consumptionMW[startPeriod] += consumptionMW[startPeriod + 1];
                startPeriod++;
            }
            //exchangeInfoMonthsYears(consumptionMW, dateTime, size, size);
            year++;
        }
        System.out.println(consumptionMW[size - 1]);
    }

    //troca informação das partes do dia ou dos dias
    public static void exchangeInfoDays(int[] consumptionMW, LocalDateTime[] dateTime, int size, int period) {
        int i;
        for (i = 1; i < size / period; i++) {
            int idx2 = i * period;
            //trocar datas
            dateTime[i] = dateTime[idx2];
            //trocar consumos
            consumptionMW[i] = consumptionMW[idx2];
        }
        size = i;
        System.out.println(size);
        System.out.println(dateTime[size - 1]);
    }

    //troca a informação dos meses ou dos anos
    public static void exchangeInfoMonthsYears(int[] consumptionMW, LocalDateTime[] dateTime, int size, int index) {

        System.out.println(dateTime[size - 2]);
    }

    private static void AverageConsumption(int[] auxConsumptionMW, int size) throws FileNotFoundException {
        int consumptionSum = 0, averageValues = 0, aboveAverageValues = 0, belowAverageValues = 0;
        for (int i = 0; i < size; i++) {
            consumptionSum += auxConsumptionMW[i];
        }
        double averageConsumption = consumptionSum / size;
        double upperLimit = averageConsumption + (averageConsumption * 0.2);
        double lowerLimit = averageConsumption - (averageConsumption * 0.2);
        for (int i = 0; i < size; i++) {
            if (auxConsumptionMW[i] >= lowerLimit && auxConsumptionMW[i] < upperLimit) {
                averageValues++;
            }
            if (auxConsumptionMW[i] < lowerLimit) {
                belowAverageValues++;
            }
            if (auxConsumptionMW[i] >= upperLimit) {
                aboveAverageValues++;
            }
        }
        System.out.println("Quantidade de valores dentro da média: " + averageValues);
        System.out.println("Quantidade de valores acima da média: " + aboveAverageValues);
        System.out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
        lowerConsumption(auxConsumptionMW, size);
        higherConsumption(auxConsumptionMW, size);
    }

    public static void merge(int consumption[], int end, int start, int middle) {

	// create a temp array
	int temp[] = new int[end - start + 1];

	// crawlers for both intervals and for temp
	int i = start, j = middle+1, k = 0;

	// traverse both arrays and in each iteration add smaller of both elements in temp 
	while(i <= middle && j <= end) {
		if(consumption[i] <=consumption[j]) {
			temp[k] = consumption[i];
			k += 1; i += 1;
		}
		else {
			temp[k] = consumption[j];
			k += 1; j += 1;
		}
	}

	// add elements left in the first interval 
	while(i <= middle) {
		temp[k] = consumption[i];
		k += 1; i += 1;
	}

	// add elements left in the second interval 
	while(j <= end) {
		temp[k] = consumption[j];
		k += 1; j += 1;
	}

	// copy temp to original interval
	for(i = start; i <= end; i += 1) {
		consumption[i] = temp[i - start];
	}
}


public static void mergeSort(int consumption[], int start, int end) {

	if(start < end) {
		int mid = (start + end) / 2;
		mergeSort(consumption, start, mid);
		mergeSort(consumption, mid+1, end);
		merge(consumption, start, mid, end);
	}
        for (int m=0; m<500; m++){
            System.out.println(consumption[m]);
        }
}

    private static void MediaMovelSimples(int[] auxConsumptionMW) throws FileNotFoundException {
        System.out.println("Insira a ordem da média móvel(n): ");
        int n = sc.nextInt();

        while (n <= 0 || n > auxConsumptionMW.length) {
            System.out.println("Valor está errado: n > 0 e <= " + auxConsumptionMW.length);
            n = sc.nextInt();
        }

        double somatorio = 0;
        for (int i = 0; i < n; i++) {
            somatorio = auxConsumptionMW[i] + somatorio;
        }

        double media = 0;
        media = somatorio / n;

        System.out.println(media);
    }

    private static void MediaMovelPesada(int[] auxConsumptionMW) throws FileNotFoundException {

        // criar o gráfico base 
        System.out.println("Insira o valor de α: [0:1]");
        double alpha = sc.nextDouble();

        while (alpha <= 0 || alpha > 1) {
            System.out.println("Valor errado α[0:1]");
            alpha = sc.nextDouble();
        }

        // criar o gráfico com o alpha implementado
    }

}
