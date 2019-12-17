package pkg2019.lapr1_1dk_mafia;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import javafx.print.PaperSource;
import static pkg2019.lapr1_1dk_mafia.LAPR1_1DK_Mafia.OUTPUT_FILE;

public class LAPR1_1DK_Mafia {

    static Scanner sc = new Scanner(System.in);
    static final int MAX_OBSERVATIONS = 26280;
    static final int NUM_HOURS_IN_STAGE = 6;
    static final int NUM_STAGES = 4;
    static final int NUM_HOURS = 24;
    static final int NUM_DAYS_IN_YEAR = 365;
    static final String OUTPUT_FILE = "Output.txt";

    public static void main(String[] args) throws FileNotFoundException {
        //if (args.length == 1) {
        int[] consumptionMW = new int[MAX_OBSERVATIONS];
        LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];
        int size = readFile(consumptionMW, dateTime, args);
        int start = 0;
        int[] auxConsumptionMW = Arrays.copyOf(consumptionMW, size);
        int auxSize = definePeriod(auxConsumptionMW, dateTime, size, start);
//        }
//        if (args.length == 6) {
//            int[] consumptionMW = new int[MAX_OBSERVATIONS];
//            LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];
//            int size = readFile(consumptionMW, dateTime, args);
//            int start = 0;
//            int[] auxConsumptionMW = Arrays.copyOf(consumptionMW, size);
//            int auxSize = DefinePeriodNonInteractive(auxConsumptionMW, dateTime, size, start, args);
//        }
//        if (args.length != 1 && args.length != 6) {
//            System.out.println("Parâmetros inválidos");
//        }
    }

    //lê ficheiro .csv
    public static int readFile(int[] consumptionMW, LocalDateTime[] dateTime, String[] args) throws FileNotFoundException {
        Scanner fileScan = new Scanner(new File("DAYTON (2).csv"));
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

    //menu interativo para escolher a resolução temporal
    public static int definePeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size, int start) throws FileNotFoundException {
        System.out.printf("Que resolução temporal deseja? %n"
                + "1. Períodos do dia; %n"
                + "2. Diário; %n"
                + "3. Mensal; %n"
                + "4. Anual. %n"
                + "5. Média Móvel Pesada");
        int resolution = sc.nextInt();
        switch (resolution) {
            case 1:
                System.out.printf("Que periodo do dia deseja? %n"
                        + "1. Madrugada; %n"
                        + "2. Manhã; %n"
                        + "3. Tarde; %n"
                        + "4. Noite. %n");
                int period = sc.nextInt();
                switch (period) {
                    case 1:
                        dayPeriod(consumptionMW, dateTime, size, 0); //TODO: alterar números para constantes
                        size = exchangeInfoDayPeriods(consumptionMW, dateTime, size, 0);
                        criarGrafico(consumptionMW, size);
                        averages(consumptionMW, dateTime, size);
                        defineOrder(consumptionMW, start, size);
                        MediaMovelSimples(consumptionMW);
                        break;
                    case 2:
                        dayPeriod(consumptionMW, dateTime, size, 6);
                        size = exchangeInfoDayPeriods(consumptionMW, dateTime, size, 6);
                        criarGrafico(consumptionMW, size);
                        averages(consumptionMW, dateTime, size);
                        defineOrder(consumptionMW, start, size);
                        break;
                    case 3:
                        dayPeriod(consumptionMW, dateTime, size, 12);
                        size = exchangeInfoDayPeriods(consumptionMW, dateTime, size, 12);
                        criarGrafico(consumptionMW, size);
                        averages(consumptionMW, dateTime, size);
                        defineOrder(consumptionMW, start, size);
                        break;
                    case 4:
                        dayPeriod(consumptionMW, dateTime, size, 18);
                        size = exchangeInfoDayPeriods(consumptionMW, dateTime, size, 18);
                        criarGrafico(consumptionMW, size);
                        averages(consumptionMW, dateTime, size);
                        defineOrder(consumptionMW, start, size);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                        break;
                }
                break;
            case 2:
                size = dailyPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, dateTime, size);
                defineOrder(consumptionMW, start, size);
                break;
            case 3:
                size = monthlyPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, dateTime, size);
                defineOrder(consumptionMW, start, size);
                break;
            case 4:
                size = annualPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, dateTime, size);
                defineOrder(consumptionMW, start, size);
                break;
            case 5:
                MediaMovelPesada(consumptionMW, size);
                break;
            default:
                System.out.println("Opção inválida. ");
                break;
        }
        return size;
    }

    //ordena de forma crescente ou decrescente conforme escolha do utilizador
    public static void defineOrder(int[] consumptionMW, int start, int size) {
        System.out.printf("De que forma pretende ordenar? %n"
                + "1. Crescente; %n"
                + "2. Decrescente. %n");
        int order = sc.nextInt();
        switch (order) {
            case 1:
                mergeSort(consumptionMW, start, size);
                break;
            case 2:
                inverseMergeSort(consumptionMW, start, size);
                break;
            default:
                System.out.println("Opção inválida.");
                break;
        }
    }

    //calcula consumos de um dado periodo do dia - manhã, tarde, noite ou madrugada
    public static void dayPeriod(int[] consuptionMW, LocalDateTime[] dateTime, int size, int startPeriod) throws FileNotFoundException {
        int endPeriod = startPeriod + NUM_HOURS_IN_STAGE;
        while (endPeriod < size) {
            for (int i = startPeriod + 1; i < endPeriod; i++) {
                consuptionMW[startPeriod] += consuptionMW[i];
            }
            startPeriod += NUM_HOURS_IN_STAGE * NUM_STAGES;
            endPeriod = startPeriod + NUM_HOURS_IN_STAGE;
        }
    }

    public static void averages(int[] consumptionMW, LocalDateTime[] dateTime, int size) {
        int consumptionSum = 0, averageValues = 0, aboveAverageValues = 0, belowAverageValues = 0;
        for (int i = 0; i < size; i++) {
            consumptionSum += consumptionMW[i];
        }
        double averageConsumption = consumptionSum / size;
        double upperLimit = averageConsumption + (averageConsumption * 0.2);
        double lowerLimit = averageConsumption - (averageConsumption * 0.2);
        for (int i = 0; i < size; i++) {
            if (consumptionMW[i] >= lowerLimit && consumptionMW[i] < upperLimit) {
                averageValues++;
            }
            if (consumptionMW[i] < lowerLimit) {
                belowAverageValues++;
            }
            if (consumptionMW[i] >= upperLimit) {
                aboveAverageValues++;
            }
        }
        System.out.println("Média : " + (consumptionSum / size) + " " + "MW");
        System.out.println("Quantidade de valores próximos da média: " + averageValues);     
        System.out.println("Quantidade de valores acima da média: " + aboveAverageValues);      
        System.out.println("Quantidade de valores abaixo da média: " + belowAverageValues);      
    }
    

    //calcula consumos diários
    public static int dailyPeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size) throws FileNotFoundException {
        int startPeriod = 0, endPeriod = NUM_HOURS;
        boolean leftovers = false;
        while (endPeriod < size) {
            for (int i = startPeriod + 1; i < endPeriod; i++) {
                consumptionMW[startPeriod] += consumptionMW[i];
            }
            startPeriod = endPeriod;
            endPeriod = endPeriod + NUM_HOURS;
        }
        int leftoverHours = endPeriod - size;
        if (leftoverHours < NUM_HOURS) {
            for (int i = startPeriod + 1; i < size; i++) {
                consumptionMW[startPeriod] += consumptionMW[i];
                leftovers = true;
            }
        }

        //return size = exchangeInfoDays(consumptionMW, dateTime, size, NUM_HOURS);
        size = exchangeInfoDays(consumptionMW, dateTime, size, NUM_HOURS);
        return size;

    }

    //calcula consumos mensais
    public static int monthlyPeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size) {
        int startPeriod = 0, endPeriod = getMonthLength(dateTime, startPeriod) * NUM_HOURS, numMonths = 0;
        while (endPeriod < size) {
            for (int i = startPeriod + 1; i < endPeriod; i++) {
                consumptionMW[startPeriod] += consumptionMW[i];
            }
            numMonths++;
            startPeriod = endPeriod;
            endPeriod += getMonthLength(dateTime, startPeriod) * NUM_HOURS;
        }
        int leftoverDays = endPeriod - size;
        if (leftoverDays < getMonthLength(dateTime, startPeriod) * NUM_HOURS) {
            for (int i = startPeriod; i < size; i++) {
                consumptionMW[startPeriod] += consumptionMW[i];
            }
            numMonths++;
        }
        return size = exchangeInfoMonthsYears(consumptionMW, dateTime, size, numMonths, true);
    }

    //retorna o nº de dias de um dado mês
    public static int getMonthLength(LocalDateTime[] dateTime, int index) {
        int length = YearMonth.of(dateTime[index].getYear(), dateTime[index].getMonthValue()).lengthOfMonth();
        return length;
    }

    //calcula consumos anuais
    public static int annualPeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size) {
        int startPeriod = 0, i = 0, numYears = 0, year = dateTime[0].getYear();
        while (startPeriod < size) {
            while (i < size && dateTime[i].getYear() == year) {
                consumptionMW[startPeriod] += consumptionMW[i];
                i++;
            }
            startPeriod = i;
            year++;
            numYears++;
        }
        return size = exchangeInfoMonthsYears(consumptionMW, dateTime, size, numYears, false);
    }

    public static int exchangeInfoDayPeriods(int[] consumptionMW, LocalDateTime[] dateTime, int size, int start) {
        int i, idx2 = start;
        for (i = 0; i < size / NUM_HOURS; i++) {
            //trocar datas
            dateTime[i] = dateTime[idx2];
            //trocar consumos
            consumptionMW[i] = consumptionMW[idx2];
            idx2 += NUM_HOURS;
        }
        size = i;
        return size;
    }

    //troca informação dos dias
    public static int exchangeInfoDays(int[] consumptionMW, LocalDateTime[] dateTime, int size, int period) {
        int i;
        for (i = 1; i < size / period; i++) {
            int idx2 = i * period;
            //trocar datas
            dateTime[i] = dateTime[idx2];
            //trocar consumos
            consumptionMW[i] = consumptionMW[idx2];
        }
        size = i;
        //System.out.println(size);
        //System.out.println(dateTime[size - 2]);
        return size;
    }

    //troca a informação dos meses ou dos anos
    public static int exchangeInfoMonthsYears(int[] consumptionMW, LocalDateTime[] dateTime, int size, int numOccorrences, boolean isMonth) {
        int i, idx2 = 0;
        for (i = 1; i < numOccorrences; i++) {
            if (isMonth) {
                idx2 += getMonthLength(dateTime, idx2) * NUM_HOURS;
            } else {
                if (Year.isLeap(dateTime[idx2].getYear())) {
                    idx2 += (NUM_DAYS_IN_YEAR + 1) * NUM_HOURS;
                } else {
                    idx2 += NUM_DAYS_IN_YEAR * NUM_HOURS;
                }
            }
            dateTime[i] = dateTime[idx2];
            consumptionMW[i] = consumptionMW[idx2];
        }
        size = i;
        //System.out.println(size);
        //System.out.println(dateTime[size - 1]);
        //System.out.println(Arrays.toString(consumptionMW));
        return size;
    }

//    private static void averageConsumption(int[] auxConsumptionMW, int size) throws FileNotFoundException {
//        int consumptionSum = 0, averageValues = 0, aboveAverageValues = 0, belowAverageValues = 0;
//        for (int i = 0; i < size; i++) {
//            consumptionSum += auxConsumptionMW[i];
//        }
//        double averageConsumption = consumptionSum / size;
//        double upperLimit = averageConsumption + (averageConsumption * 0.2);
//        double lowerLimit = averageConsumption - (averageConsumption * 0.2);
//        for (int i = 0; i < size; i++) {
//            if (auxConsumptionMW[i] >= lowerLimit && auxConsumptionMW[i] < upperLimit) {
//                averageValues++;
//            }
//            if (auxConsumptionMW[i] < lowerLimit) {
//                belowAverageValues++;
//            }
//            if (auxConsumptionMW[i] >= upperLimit) {
//                aboveAverageValues++;
//            }
//        }
//        System.out.println("Quantidade de valores dentro da média: " + averageValues);
//        System.out.println("Quantidade de valores acima da média: " + aboveAverageValues);
//        System.out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
//    }
    public static void merge(int consumption[], int start, int middle, int end) {

        // create a temp array
        int temp[] = new int[end - start + 1];

        // crawlers for both intervals and for temp
        int i = start, j = middle + 1, k = 0;

        // traverse both arrays and in each iteration add smaller of both elements in temp 
        while (i <= middle && j <= end) {
            if (consumption[i] <= consumption[j]) {                           //trocar o sinal para decrescente
                temp[k] = consumption[i];
                k += 1;
                i += 1;
            } else {
                temp[k] = consumption[j];
                k += 1;
                j += 1;
            }
        }

        // add elements left in the first interval 
        while (i <= middle) {
            temp[k] = consumption[i];
            k += 1;
            i += 1;
        }

        // add elements left in the second interval 
        while (j <= end) {
            temp[k] = consumption[j];
            k += 1;
            j += 1;
        }

        // copy temp to original interval
        for (i = start; i <= end; i += 1) {
            consumption[i] = temp[i - start];
        }
    }

    public static void inverseMerge(int consumption[], int start, int middle, int end) {
        // create a temp array
        int temp[] = new int[end - start + 1];

        // crawlers for both intervals and for temp
        int i = start, j = middle + 1, k = 0;

        // traverse both arrays and in each iteration add smaller of both elements in temp 
        while (i <= middle && j <= end) {
            if (consumption[i] >= consumption[j]) {                           //trocar o sinal para decrescente
                temp[k] = consumption[i];
                k += 1;
                i += 1;
            } else {
                temp[k] = consumption[j];
                k += 1;
                j += 1;
            }
        }

        // add elements left in the first interval 
        while (i <= middle) {
            temp[k] = consumption[i];
            k += 1;
            i += 1;
        }

        // add elements left in the second interval 
        while (j <= end) {
            temp[k] = consumption[j];
            k += 1;
            j += 1;
        }

        // copy temp to original interval
        for (i = start; i <= end; i += 1) {
            consumption[i] = temp[i - start];
        }
    }

    public static void inverseMergeSort(int consumption[], int start, int end) {
        if (start < end) {
            int mid = (start + end) / 2;
            inverseMergeSort(consumption, start, mid);
            inverseMergeSort(consumption, mid + 1, end);
            inverseMerge(consumption, start, mid, end);
        }
    }

    public static void mergeSort(int consumption[], int start, int end) {

        if (start < end) {
            int mid = (start + end) / 2;
            mergeSort(consumption, start, mid);
            mergeSort(consumption, mid + 1, end);
            merge(consumption, start, mid, end);
        }
    }

    private static void MediaMovelSimples(int[] auxConsumptionMW) throws FileNotFoundException {
        System.out.println("Defina o parâmetro n: ");
        int n = sc.nextInt();
        while (n <= 0 || n > auxConsumptionMW.length) {
            System.out.println("O valor introduzido é inválido. Por favor introduza um valor entre 0 e " + auxConsumptionMW.length + ".");
            n = sc.nextInt();
        }
        double sum = 0, finalSum = 0;
        for (int k = 0; k <= n - 1; k++) { //vai até n-1, conforme o que está na fórmula
            sum = auxConsumptionMW[k] + sum; //está a somar bem
            finalSum = sum - k; //é o xi-k(k é o indice)
        }
        System.out.print(((1 / n) * finalSum) + "MW. ");
    }

    private static void MediaMovelPesada(int[] consumptionMW, int size) throws FileNotFoundException {

        double[] consumptionNewMW = new double[size];
        System.out.println("Insira o valor de α (entre 0 e 1): ");
        double alpha = sc.nextDouble();

        if (alpha < 0 || alpha > 1) {
            do {
                System.out.println("Valor errado. Insira novo valor de α entre 0 e 1: ");
                alpha = sc.nextDouble();
            } while (alpha < 0 || alpha > 1);
        }

        for (int i = 1; i < size; i++) {
            consumptionNewMW[0] = consumptionMW[0];
            consumptionNewMW[i] = (alpha * consumptionMW[i]) + ((1 - alpha) * consumptionNewMW[i - 1]);
            consumptionNewMW[size - 1] = consumptionMW[size - 1];
        }

        // criar 1 gráfico com os valores inicias e o valor de α
        criarGraficoPesada(consumptionMW, consumptionNewMW, size);

    }

    public static void absoluteError(int[] consumptionMW, int[] arrayY, int size) {
        int sum = 0;
        for (int i = 0; i < size - 1; i++) {
            sum = sum + Math.abs(arrayY[i] - consumptionMW[i]);
        }
        double absoluteError = sum / size;
    }

    private static void criarGrafico(int[] grafico, int size) {

        JavaPlot p = new JavaPlot();

        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.LINES);
        myPlotStyle.setLineWidth(1);
        myPlotStyle.setLineType(NamedPlotColor.BLUE);

        int tab[][];
        tab = new int[size][2];
        for (int i = 0; i < size; i++) {
            tab[i][0] = i;
            tab[i][1] = grafico[i];
        }

        DataSetPlot s = new DataSetPlot(tab);
        s.setTitle("Teste");
        s.setPlotStyle(myPlotStyle);

        //p.newGraph();
        p.addPlot(s);

        p.newGraph();
        p.plot();
    }

    private static void criarGraficoPesada(int[] grafico1, double[] grafico2, int size) {
        JavaPlot p = new JavaPlot();

        PlotStyle myPlotStyle = new PlotStyle();
        PlotStyle myPlotStyle2 = new PlotStyle();
        myPlotStyle.setStyle(Style.LINES);
        myPlotStyle.setLineWidth(1);
        myPlotStyle2.setStyle(Style.LINES);
        myPlotStyle2.setLineWidth(1);
        myPlotStyle.setLineType(NamedPlotColor.BLUE);
        myPlotStyle2.setLineType(NamedPlotColor.ORANGE);
        p.set("xrange", "[0:200]");

        int tab1[][];
        double tab2[][];

        tab1 = new int[size][2];
        tab2 = new double[size][2];

        for (int i = 0; i < size; i++) {
            tab1[i][0] = i;
            tab1[i][1] = grafico1[i];

            tab2[i][0] = i;
            tab2[i][1] = grafico2[i];
        }

        DataSetPlot s = new DataSetPlot(tab1);
        DataSetPlot t = new DataSetPlot(tab2);

        s.setTitle("ATUAL");
        t.setTitle("ALPHA");
        s.setPlotStyle(myPlotStyle);
        t.setPlotStyle(myPlotStyle2);

        //p.newGraph();
        p.addPlot(s);
        p.addPlot(t);

        p.newGraph();
        p.plot();

    }

    private static int DefinePeriodNonInteractive(int[] consumptionMW, LocalDateTime[] dateTime, int size, int start, String[] args) throws FileNotFoundException {
        switch (args[1]) {
            case "11":
                dayPeriod(consumptionMW, dateTime, size, 0); //TODO: alterar números para constantes
                size = exchangeInfoDayPeriods(consumptionMW, dateTime, size, 0);
                criarGrafico(consumptionMW, size);
                averagesNonInteractive(consumptionMW, dateTime, size);
                DefineOrderNonInteractive(consumptionMW, start, size, args);
                DefineModel(consumptionMW, size, args);
                //falta previsão
                break;

            case "12":
                dayPeriod(consumptionMW, dateTime, size, 6);
                size = exchangeInfoDayPeriods(consumptionMW, dateTime, size, 6);
                criarGrafico(consumptionMW, size);
                averagesNonInteractive(consumptionMW, dateTime, size);
                DefineOrderNonInteractive(consumptionMW, start, size, args);
                DefineModel(consumptionMW, size, args);
                //falta previsão
                break;
            case "13":
                dayPeriod(consumptionMW, dateTime, size, 12);
                size = exchangeInfoDayPeriods(consumptionMW, dateTime, size, 12);
                criarGrafico(consumptionMW, size);
                averagesNonInteractive(consumptionMW, dateTime, size);
                DefineOrderNonInteractive(consumptionMW, start, size, args);
                DefineModel(consumptionMW, size, args);
                //falta previsão
                break;
            case "14":
                dayPeriod(consumptionMW, dateTime, size, 18);
                size = exchangeInfoDayPeriods(consumptionMW, dateTime, size, 18);
                criarGrafico(consumptionMW, size);
                averagesNonInteractive(consumptionMW, dateTime, size);
                DefineOrderNonInteractive(consumptionMW, start, size, args);
                DefineModel(consumptionMW, size, args);
                //falta previsão
                break;
            case "2":
                size = dailyPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                averagesNonInteractive(consumptionMW, dateTime, size);
                DefineOrderNonInteractive(consumptionMW, start, size, args);
                DefineModel(consumptionMW, size, args);
                //falta previsão
                break;
            case "3":
                size = monthlyPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                averagesNonInteractive(consumptionMW, dateTime, size);
                DefineOrderNonInteractive(consumptionMW, start, size, args);
                DefineModel(consumptionMW, size, args);
                //falta previsão
                break;
            case "4":
                size = annualPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                averagesNonInteractive(consumptionMW, dateTime, size);
                DefineOrderNonInteractive(consumptionMW, start, size, args);
                DefineModel(consumptionMW, size, args);
                //falta previsão
                break;

        }
        return size;
    }

    public static void DefineOrderNonInteractive(int[] consumptionMW, int start, int size, String[] args) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
        switch (args[3]) {
            case "1":
                mergeSort(consumptionMW, start, size);
                break;
            case "2":
                inverseMergeSort(consumptionMW, start, size);
                break;
            default:
                System.out.println("Parâmetro de ordenação inválido.");
                out.println("Parâmetro de ordenação inválido.");
                break;
        }
    }

    public static void DefineModel(int[] consumptionMW, int size, String[] args) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
        switch (args[2]) {
            case "1":
                MediaMovelSimplesNonInteractive(consumptionMW, size, args);
                criarGrafico(consumptionMW, size);
                break;
            case "2":
                double []consumptionNewMW=MediaMovelPesadaNonInteractive(consumptionMW, size, args);
                criarGraficoPesada(consumptionMW, consumptionNewMW, size);
                break;
            default:
                System.out.println("Parâmetro de modelo inválido.");
                out.println("Parâmetro de modelo inválido.");
                break;
        }
    }

    private static void MediaMovelSimplesNonInteractive(int[] consumptionMW, int size, String[] args) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
        int n = Integer.parseInt(args[4]);
        if (n <= 0 || n > size) {
            System.out.println("O parâmetro que toma o valor númerico n é inválido ");
            out.println("O parâmetro que toma o valor númerico n é inválido ");
        } else {
            double sum = 0, finalSum = 0;
            for (int k = 0; k <= n - 1; k++) { //vai até n-1, conforme o que está na fórmula
                sum = consumptionMW[k] + sum; //está a somar bem
                finalSum = sum - k; //é o xi-k(k é o indice)
            }
            System.out.println(((1 / n) * finalSum) + "MW. "); //é necessário?
        }
    }

    public static void averagesNonInteractive(int[] consumptionMW, LocalDateTime[] dateTime, int size) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
        int consumptionSum = 0, averageValues = 0, aboveAverageValues = 0, belowAverageValues = 0;
        for (int i = 0; i < size; i++) {
            consumptionSum += consumptionMW[i];
        }
        double averageConsumption = consumptionSum / size;
        double upperLimit = averageConsumption + (averageConsumption * 0.2);
        double lowerLimit = averageConsumption - (averageConsumption * 0.2);
        for (int i = 0; i < size; i++) {
            if (consumptionMW[i] >= lowerLimit && consumptionMW[i] < upperLimit) {
                averageValues++;
            }
            if (consumptionMW[i] < lowerLimit) {
                belowAverageValues++;
            }
            if (consumptionMW[i] >= upperLimit) {
                aboveAverageValues++;
            }
        }

        System.out.println("Média : " + (consumptionSum / size) + " " + "MW");
        out.println("Média : " + (consumptionSum / size) + " " + "MW");
        System.out.println("Quantidade de valores próximos da média: " + averageValues);
        out.println("Quantidade de valores dentro da média: " + averageValues);
        System.out.println("Quantidade de valores acima da média: " + aboveAverageValues);
        out.println("Quantidade de valores acima da média: " + aboveAverageValues);
        System.out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
        out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
    }

    private static double [] MediaMovelPesadaNonInteractive(int[] consumptionMW, int size, String[] args) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
        double[] consumptionNewMW = new double[size];
        double alpha = Integer.parseInt(args[4]);

        if (alpha < 0 || alpha > 1) {
            System.out.println("O parâmetro que toma o valor númerico alpha é inválido ");
            out.println("O parâmetro que toma o valor númerico n é inválido ");
        }

        for (int i = 1; i < size; i++) {
            consumptionNewMW[0] = consumptionMW[0];
            consumptionNewMW[i] = (alpha * consumptionMW[i]) + ((1 - alpha) * consumptionNewMW[i - 1]);
            consumptionNewMW[size - 1] = consumptionMW[size - 1];
        }
        return consumptionNewMW;
    }
}

