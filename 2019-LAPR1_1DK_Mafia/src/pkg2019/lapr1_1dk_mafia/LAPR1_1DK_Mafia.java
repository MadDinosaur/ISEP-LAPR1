package pkg2019.lapr1_1dk_mafia;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.FileTerminal;
import com.panayotis.gnuplot.terminal.GNUPlotTerminal;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import com.sun.glass.ui.Size;
import com.sun.jndi.url.iiop.iiopURLContext;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.dateTime;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import static java.lang.System.out;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LAPR1_1DK_Mafia {

    static Scanner sc = new Scanner(System.in);
    static final int MAX_OBSERVATIONS = 26280;
    static final int NUM_HOURS_IN_STAGE = 6;
    static final int NUM_STAGES = 4;
    static final int NUM_HOURS = 24;
    static final int NUM_DAYS_IN_YEAR = 365;
    static final String OUTPUT_FILE = "Output.txt";
    static final String nome = "Consumos.csv";

    public static void main(String[] args) throws FileNotFoundException {
        int[] consumptionMW = new int[MAX_OBSERVATIONS];
        LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];
        int size = readFile(consumptionMW, dateTime, args);
        //if (args.length == 2) {
        PrintWriter out = null;
        //menu interativo
        int option;
        do {
            int[] auxConsumptionMW = Arrays.copyOf(consumptionMW, size);
            option = menu(auxConsumptionMW, dateTime, size, args, out);
        } while (option != 7);
//        } else {
//         PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
//        if (args.length == 12) {
//          int[] auxConsumptionMW = Arrays.copyOf(consumptionMW, size);
//        DefinePeriodNonInteractive(auxConsumptionMW, dateTime, size, args, out);
//        } else {
//          System.out.println("Parâmetros inválidos");
//        }
//       out.close();
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

    //menu interativo geral
    public static int menu(int[] consumptionMW, LocalDateTime[] dateTime, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        System.out.printf("Indique a opção que pretende:%n"
                + "1. Visualizar gráfico de consumos;%n"
                + "2. Visualizar média global e distribuição de observações;%n"
                + "3. Calcular Média Móvel Simples;%n"
                + "4. Calcular Média Móvel Pesada;%n"
                + "5. Ordenar valores;%n"
                + "6. Efetuar uma previsão;%n"
                + "7. Sair.%n");
        int option = sc.nextInt();
        switch (option) {
            case 1:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                criarGrafico(consumptionMW, size, args,out);
                break;
            case 2:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                averages(consumptionMW, size, args,out);
                break;
            case 3:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                MediaMovelSimples(consumptionMW, size, args, out);
                break;
            case 4:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                MediaMovelPesada(consumptionMW, size, args, out);
                break;
            case 5:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                defineOrder(consumptionMW, size, args, out);
                break;
            case 6:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                definePrevision(consumptionMW, dateTime, size, option);
                break;
        }
        return option;
    }

    public static int exchange(int[] consumptionMW, LocalDateTime[] dateTime, int size, int option) throws FileNotFoundException {
        switch (option) {
            case 1:
                size = exchangeInfoDayPeriods(consumptionMW, size, 0, dateTime);
                break;
            case 2:
                size = exchangeInfoDayPeriods(consumptionMW, size, 6, dateTime);
                break;
            case 3:
                size = exchangeInfoDayPeriods(consumptionMW, size, 12, dateTime);
                break;
            case 4:
                size = exchangeInfoDayPeriods(consumptionMW, size, 18, dateTime);
                break;
            case 5:
                size = dailyPeriod(consumptionMW, size, dateTime);
                break;
            case 6:
                size = monthlyPeriod(consumptionMW, dateTime, size);
                break;
            case 7:
                size = annualPeriod(consumptionMW, dateTime, size);
                break;
        }
        return size;
    }

    //menu para escolher a resolução temporal
    public static int definePeriod(int[] consumptionMW, LocalDateTime[] dateTime, int size) throws FileNotFoundException {
        System.out.printf("Que resolução temporal deseja %n"
                + "1. Madrugadas;%n"
                + "2. Manhãs;%n"
                + "3. Tardes;%n"
                + "4. Noites;%n"
                + "5. Diário; %n"
                + "6. Mensal; %n"
                + "7. Anual; %n");
        int option = sc.nextInt();
        switch (option) {
            case 1:
                dayPeriod(consumptionMW, size, 0); //TODO: alterar números para constantes
                break;
            case 2:
                dayPeriod(consumptionMW, size, 6);
                break;
            case 3:
                dayPeriod(consumptionMW, size, 12);
                break;
            case 4:
                dayPeriod(consumptionMW, size, 18);
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            default:
                System.out.println("Opção inválida. ");
                break;
        }
        return option;
    }

    public static void definePrevision(int[] consumptionMW, LocalDateTime[] dateTime, int size, int option) throws FileNotFoundException {
        boolean flag, mark;
        switch (option) {
            case 1:
                //madrugadas
                flag = true; //esta flag serve para dizer aos métodos se hão-de chamar a previsão ou verificar o critério mensal
                mark = true;   //este mark serve para dizer aos métodos se hão-de chamar a previsão ou verificar o critério diário
                verifyYear(consumptionMW, dateTime, size, flag, mark);
                break;
            case 2:
                //manhãs
                flag = true;
                mark = true;
                verifyYear(consumptionMW, dateTime, size, flag, mark);
                break;
            case 3:
                //tardes
                flag = true;
                mark = true;
                verifyYear(consumptionMW, dateTime, size, flag, mark);
                break;
            case 4:
                //noites
                flag = true;
                mark = true;
                verifyYear(consumptionMW, dateTime, size, flag, mark);
                break;
            case 5:
                //dias
                flag = true;
                mark = true;
                verifyYear(consumptionMW, dateTime, size, flag, mark);
                break;
            case 6:
                //meses
                flag = true;
                mark = false;
                verifyYear(consumptionMW, dateTime, size, flag, mark);
                break;
            case 7:
                //anos
                flag = false;
                mark = false;
                verifyYear(consumptionMW, dateTime, size, flag, mark);
                break;
            default:
                System.out.println("Opção inválida. ");
                break;
        }
    }

    //método de verificação do dia
    public static void verifyDay(int[] consumption, LocalDateTime[] dateTime, int size, int year, int monthsNumber, int daysNumber) throws FileNotFoundException {
        System.out.println("Insira o dia pretendido. ");
        int day = sc.nextInt();
        if (day > 0 && day <= daysNumber) {
            previsionType(consumption, dateTime, size);
        } else {
            System.out.println("O dia introduzido não existe nos registos. ");
        }
    }

    //método de verificação do mês
    public static void verifyMonth(int[] consumption, LocalDateTime[] dateTime, int size, int year, int monthsNumber, boolean mark) throws FileNotFoundException {
        System.out.println("Insira o mês pretendido. ");
        int month = sc.nextInt();
        if (month < 01 || month > monthsNumber) {
            System.out.println("O mês introduzido não existe nos registos. ");
        } else {
            if (mark == true) { //caso de ser necessária a verificação dos dias
                int daysNumber = YearMonth.of(year, month).lengthOfMonth(); //achar o número de dias que o mês inserido tem
                verifyDay(consumption, dateTime, size, year, monthsNumber, daysNumber);
            } else {
                if (mark == false) {
                    previsionType(consumption, dateTime, size);
                }
            }
        }
    }

    //método de verificação do ano  //Os anos já estão a funcionar bem!
    public static void verifyYear(int[] consumption, LocalDateTime[] dateTime, int size, boolean flag, boolean mark) throws FileNotFoundException {
        System.out.println("Insira o ano. ");
        int year = sc.nextInt();
        if (year < dateTime[0].getYear() || year > dateTime[size - 1].getYear()) {
            System.out.println("O ano introduzido não existe nos registos. ");
        } else {
            if (year == dateTime[size - 1].getYear()) { //caso de ser o último ano
                if (flag == true) {
                    int monthsNumber = dateTime[size - 1].getMonthValue(); //ver quantos meses tem o úlimo ano
                    verifyMonth(consumption, dateTime, size, year, monthsNumber, mark);
                } else {
                    if (flag == false) {
                        previsionType(consumption, dateTime, size);
                    }
                }
            } else {
                if (flag == true) {
                    int monthsNumber = 12;
                    verifyMonth(consumption, dateTime, size, year, monthsNumber, mark);
                } else {
                    previsionType(consumption, dateTime, size);
                }
            }
        }
    }

    public static void previsionType(int[] consumption, LocalDateTime[] dateTime, int size) throws FileNotFoundException {
        System.out.printf("Que tipo de previsão pretende? %n"
                + "1. Previsão a partir da média móvel simples;%n"
                + "2. Previsão a partir da média exponencialmente pesada.%n");
        int option = sc.nextInt();
        switch (option) {
            case 1:
                previsionMediaSimples(consumption, size);
                break;
            case 2: {
                double[] consumptionNewMW = null;
                previsionMediaMovelPesada(consumption, size);
            }
            break;
            default:
                System.out.println("Opção inválida. ");
                break;
        }
    }

    //ordena de forma crescente ou decrescente conforme escolha do utilizador
    public static void defineOrder(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        int start = 0, order;
        if (args.length == 12) {
            order = Integer.parseInt(args[7]);
        } else {
            System.out.printf("De que forma pretende ordenar? %n"
                    + "1. Crescente; %n"
                    + "2. Decrescente. %n");
            order = sc.nextInt();
        }

        switch (order) {
            case 1:
                mergeSort(consumptionMW, start, size-1);
                criarGrafico(consumptionMW, size, args,out);
                break;
            case 2:
                inverseMergeSort(consumptionMW, start, size-1);
                criarGrafico(consumptionMW, size, args,out);
                break;
            default:
                System.out.println("Parâmentro de ordenação inválido.");
                if (args.length == 12) {
                    out.println("Parâmetro de ordenação inválido.");
                    out.close();
                }
                break;
        }
    }

    //calcula consumos de um dado periodo do dia - manhã, tarde, noite ou madrugada
    public static void dayPeriod(int[] consuptionMW, int size, int startPeriod) throws FileNotFoundException {
        int endPeriod = startPeriod + NUM_HOURS_IN_STAGE;
        while (endPeriod < size) {
            for (int i = startPeriod + 1; i < endPeriod; i++) {
                consuptionMW[startPeriod] += consuptionMW[i];
            }
            startPeriod += NUM_HOURS_IN_STAGE * NUM_STAGES;
            endPeriod = startPeriod + NUM_HOURS_IN_STAGE;
        }
        int leftoverHours = endPeriod - size;
        if (leftoverHours < NUM_HOURS) {
            for (int i = startPeriod + 1; i < size; i++) {
                consuptionMW[startPeriod] += consuptionMW[i];
            }
        }
    }

    public static int averages(int[] consumptionMW, int size, String[] args,PrintWriter out) throws FileNotFoundException {
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
        if (args.length == 12) {
            out.println("Média : " + (consumptionSum / size) + " " + "MW");
            out.println("Quantidade de valores dentro da média: " + averageValues);
            out.println("Quantidade de valores acima da média: " + aboveAverageValues);
            out.println("Quantidade de valores abaixo da média: " + belowAverageValues);

        }

        imprimirGraficoBarras(belowAverageValues, averageValues, aboveAverageValues, args, out);

        System.out.println("Média : " + (consumptionSum / size) + " " + "MW");
        System.out.println("Quantidade de valores próximos da média: " + averageValues);
        System.out.println("Quantidade de valores acima da média: " + aboveAverageValues);
        System.out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
        return consumptionSum/size;
    }

    //calcula consumos diários
    public static int dailyPeriod(int[] consumptionMW, int size, LocalDateTime[] dateTime) throws FileNotFoundException {
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
        size = exchangeInfoDays(consumptionMW, size, NUM_HOURS, dateTime);
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
            for (int i = startPeriod + 1; i < size; i++) {
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
            i++;
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

    public static int exchangeInfoDayPeriods(int[] consumptionMW, int size, int start, LocalDateTime[] dateTime) {
        int i, idx2 = start;
        for (i = 0; i < size / NUM_HOURS; i++) {
            //trocar consumos
            consumptionMW[i] = consumptionMW[idx2];
            dateTime[i] = dateTime[idx2];
            idx2 += NUM_HOURS;
        }
        size = i;
        return size;
    }

    //troca informação dos dias
    public static int exchangeInfoDays(int[] consumptionMW, int size, int period, LocalDateTime[] dateTime) {
        int i;
        for (i = 1; i < size / period; i++) {
            int idx2 = i * period;
            //trocar consumos
            consumptionMW[i] = consumptionMW[idx2];
            dateTime[i] = dateTime[idx2];
        }
        size = i;
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
            consumptionMW[i] = consumptionMW[idx2];
            dateTime[i] = dateTime[idx2];
        }
        size = i;
        return size;
    }

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

    public static double[] MediaMovelSimples(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        int n;
        boolean nonInteractiveInvalidInput = false;
        if (args.length == 12) {
            n = Integer.parseInt(args[9]);
        } else {
            System.out.println("Defina o parâmetro n: ");
            n = sc.nextInt();
        }
        while (n <= 0 || n > consumptionMW.length) {
            if (args.length == 12) {
                System.out.println("O parâmetro que toma o valor númerico n é inválido");
                out.println("O parâmetro que toma o valor númerico n é inválido ");
                nonInteractiveInvalidInput = true;
                break;
            } else {
                System.out.println("O valor introduzido é inválido. Por favor introduza um valor entre 0 e " + consumptionMW.length + ".");
                n = sc.nextInt();
            }
        }
        double[] mediaMovelSimples = new double[size - n];
        int total = 0, i;
        if (nonInteractiveInvalidInput == false) {
            for (i = n - 1; i <= size; i++) {
                for (int j = i - n + 1; j <= i; j++) {
                    total += consumptionMW[i];
                }
                mediaMovelSimples[i] = (total / n);
                total = 0;
            }
            criarGraficoMediaSimples(consumptionMW, mediaMovelSimples, mediaMovelSimples.length, n, out,args);
            absoluteError(consumptionMW, mediaMovelSimples, mediaMovelSimples.length);
        }
        return mediaMovelSimples;
    }

    public static double[] MediaMovelPesada(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        double alpha;
        double[] consumptionNewMW = new double[size];
        boolean nonInteractiveInvalidInput = false;
        if (args.length == 12) {
            alpha = Double.parseDouble(args[9]);
        } else {
            System.out.println("Insira o valor de α (entre 0 e 1): ");
            alpha = sc.nextDouble();
        }

        while (alpha < 0 || alpha > 1) {
            if (args.length == 12) {
                System.out.println("O parâmetro que toma o valor númerico α é inválido");
                out.println("O parâmetro que toma o valor númerico α é inválido ");
                nonInteractiveInvalidInput = true;
                break;
            } else {
                System.out.println("Valor errado. Insira novo valor de α entre 0 e 1: ");
                alpha = sc.nextDouble();
            }
        }
        if (nonInteractiveInvalidInput == false) {
            for (int i = 1; i < size - 1; i++) {
                consumptionNewMW[0] = consumptionMW[0];
                consumptionNewMW[i] = (alpha * consumptionMW[i]) + ((1 - alpha) * consumptionNewMW[i - 1]);
                consumptionNewMW[size - 1] = consumptionMW[size - 1];
            }
            // criar 1 gráfico com os valores inicias e o valor de α
            criarGraficoMediaPesada(consumptionMW, consumptionNewMW, size, alpha,args,out);
            absoluteError(consumptionMW, consumptionNewMW, consumptionNewMW.length);
        }
        return consumptionNewMW;
    }

    public static double absoluteError(int[] consumptionMW, double[] arrayY, int size) {
        int sum = 0;
        for (int i = 0; i < size - 1; i++) {
            sum = (int) (sum + Math.abs(arrayY[i] - consumptionMW[i]));
        }
        double absoluteError = sum / size;
        System.out.println("Erro absoluto: " + absoluteError);
        return absoluteError;
    }

    public static void criarGrafico(int[] grafico, int size, String[] args, PrintWriter out) throws FileNotFoundException {

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
        s.setTitle("GRAFICO");
        s.setPlotStyle(myPlotStyle);

        p.addPlot(s);

        p.newGraph();
        p.plot();
//        if (args.length == 2) {
            System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
            int op = sc.nextInt();

            if (op != 1 && op != 2 && op != 3 && op != 4) {
                do {
                    System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
                    op = sc.nextInt();
                } while (op == 1 || op == 2 || op == 3 || op == 4);
//            }

            // ainda a desenvolver a parte de guardar em png.
            if (op == 1) {

                String title = "Consumo de energia";
                //Genera um file em .png
                File file = new File("statistics_" + title + ".png");
                //Cria um novo plot
                JavaPlot plot = new JavaPlot();
                //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
                GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
                plot.setTerminal(terminal);
                //Configuração dos labels
                plot.set("xlabel", "\"Observações\"");
                plot.set("ylabel", "\"" + title + "\"");
                plot.addPlot(s);
                //Define o estilo do gráfico
                PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
                stl.setStyle(Style.LINES);
                plot.setKey(JavaPlot.Key.OFF);
                plot.plot();

                System.out.println("Ficheiro guardado em PNG.");
            }

            if (op == 2) {
                csvWriteGrafico(grafico, size);
            }
            if (op == 3) {
                String title = "Consumo de energia";
                //Genera um file em .png
                File file = new File("statistics_" + title + ".png");
                //Cria um novo plot
                JavaPlot plot = new JavaPlot();
                //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
                GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
                plot.setTerminal(terminal);
                //Configuração dos labels
                plot.set("xlabel", "\"Observações\"");
                plot.set("ylabel", "\"" + title + "\"");
                plot.addPlot(s);
                //Define o estilo do gráfico
                PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
                stl.setStyle(Style.LINES);
                plot.setKey(JavaPlot.Key.OFF);
                plot.plot();
                csvWriteGrafico(grafico, size);
                System.out.println("Ficheiros guardados em PNG e CSV.");
            }

            if (op == 4) {
                System.out.println("Nenhum ficheiro guardado.");
            }
//        } else {
//            String title = "Consumo de energia";
//                //Genera um file em .png
//                File file = new File("statistics_" + title + ".png");
//                //Cria um novo plot
//                JavaPlot plot = new JavaPlot();
//                //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
//                GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
//                plot.setTerminal(terminal);
//                //Configuração dos labels
//                plot.set("xlabel", "\"Observações\"");
//                plot.set("ylabel", "\"" + title + "\"");
//                plot.addPlot(s);
//                //Define o estilo do gráfico
//                PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
//                stl.setStyle(Style.LINES);
//                plot.setKey(JavaPlot.Key.OFF);
//                plot.plot();
//
//            System.out.println("Ficheiro guardado em PNG.");
//            out.println("Ficheiro guardado em PNG.");
//
//        }
            }
    }

    public static void criarGraficoMediaPesada(int[] grafico1, double[] grafico2, int size, double alpha,String[]args,PrintWriter out) throws FileNotFoundException {
        JavaPlot p = new JavaPlot();

        PlotStyle myPlotStyle = new PlotStyle();
        PlotStyle myPlotStyle2 = new PlotStyle();
        myPlotStyle.setStyle(Style.LINES);
        myPlotStyle.setLineWidth(1);
        myPlotStyle2.setStyle(Style.LINES);
        myPlotStyle2.setLineWidth(1);
        myPlotStyle.setLineType(NamedPlotColor.BLUE);
        myPlotStyle2.setLineType(NamedPlotColor.ORANGE);

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
        t.setTitle("ALPHA" + alpha);
        s.setPlotStyle(myPlotStyle);
        t.setPlotStyle(myPlotStyle2);

        p.addPlot(s);
        p.addPlot(t);

        p.newGraph();
        p.plot();
        
// if (args.length==2){
        System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
        int op = sc.nextInt();

        if (op != 1 && op != 2 && op != 3 && op != 4) {
            do {
                System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
                op = sc.nextInt();
            } while (op == 1 || op == 2 || op == 3 || op == 4);
        }

        // ainda a desenvolver a parte de guardar em png.
       
        if (op == 1) {

            String title = "Consumo de energia no gráfico α = " + alpha;
            //Genera um file em .png
            File file = new File("statistics_" + title + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
            plot.setTerminal(terminal);
            //Configuração dos labels
            plot.set("xlabel", "\"Observações\"");
            plot.set("ylabel", "\"" + title + "\"");
            plot.addPlot(s);
            plot.addPlot(t);
            //Define o estilo do gráfico
            PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
            stl.setStyle(Style.LINES);
            plot.setKey(JavaPlot.Key.OFF);
            plot.plot();

            System.out.println("Ficheiro guardado em PNG.");
        }

        if (op == 2) {
            csvWriteMedias(grafico1, grafico2, size);
            System.out.println("Ficheiro guardado em CSV.");
        }
        if(op==3){
            String title = "Consumo de energia no gráfico α = " + alpha;
            //Genera um file em .png
            File file = new File("statistics_" + title + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
            plot.setTerminal(terminal);
            //Configuração dos labels
            plot.set("xlabel", "\"Observações\"");
            plot.set("ylabel", "\"" + title + "\"");
            plot.addPlot(s);
            plot.addPlot(t);
            //Define o estilo do gráfico
            PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
            stl.setStyle(Style.LINES);
            plot.setKey(JavaPlot.Key.OFF);
            plot.plot();
            csvWriteMedias(grafico1, grafico2, size);
            System.out.println("Ficheiros guardados em PNG e CSV.");
            
        }

        if (op == 4) {
            System.out.println("Nenhum ficheiro guardado.");
        }
//        }
//        else{String title = "Consumo de energia no gráfico α = " + alpha;
//            //Genera um file em .png
//            File file = new File("statistics_" + title + ".png");
//            //Cria um novo plot
//            JavaPlot plot = new JavaPlot();
//            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
//            GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
//            plot.setTerminal(terminal);
//            //Configuração dos labels
//            plot.set("xlabel", "\"Observações\"");
//            plot.set("ylabel", "\"" + title + "\"");
//            plot.addPlot(s);
//            plot.addPlot(t);
//            //Define o estilo do gráfico
//            PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
//            stl.setStyle(Style.LINES);
//            plot.setKey(JavaPlot.Key.OFF);
//            plot.plot();
//            System.out.println("Ficheiro guardado em PNG.");
//            out.println("Ficheiro guardado em PNG.");
//        }

    }

    public static void criarGraficoMediaSimples(int[] grafico1, double[] grafico2, int length, int n, PrintWriter out,String[]args) throws FileNotFoundException {
        JavaPlot p = new JavaPlot();

        PlotStyle myPlotStyle = new PlotStyle();
        PlotStyle myPlotStyle2 = new PlotStyle();
        myPlotStyle.setStyle(Style.LINES);
        myPlotStyle.setLineWidth(1);
        myPlotStyle2.setStyle(Style.LINES);
        myPlotStyle2.setLineWidth(1);
        myPlotStyle.setLineType(NamedPlotColor.BLUE);
        myPlotStyle2.setLineType(NamedPlotColor.ORANGE);

        int tab1[][];
        double tab2[][];

        tab1 = new int[length][2];
        tab2 = new double[length - n + 1][2];

        for (int i = 0; i < length; i++) {
            tab1[i][0] = i;
            tab1[i][1] = grafico1[i];
        }
        for (int i = 0; i < length - n + 1; i++) {
            tab2[i - n + 1][0] = i;
            tab2[i - n + 1][1] = grafico2[i];
        }

        DataSetPlot s = new DataSetPlot(tab1);
        DataSetPlot t = new DataSetPlot(tab2);

        s.setTitle("ATUAL");
        t.setTitle("N = " + n);
        s.setPlotStyle(myPlotStyle);
        t.setPlotStyle(myPlotStyle2);

        p.addPlot(s);
        p.addPlot(t);

        p.newGraph();
        p.plot();

        System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
        int op = sc.nextInt();
//if(args.length==2){
        if (op != 1 && op != 2 && op != 3 && op != 4) {
            do {
                System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
                op = sc.nextInt();
            } while (op == 1 || op == 2 || op == 3 || op == 4);
        }

        // ainda a desenvolver a parte de guardar em png.
        if (op == 1) {

            String title = "Consumo de energia no gráfico n = " + n;
            //Genera um file em .png
            File file = new File("statistics_" + title + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
            plot.setTerminal(terminal);
            //Configuração dos labels
            plot.set("xlabel", "\"Observações\"");
            plot.set("ylabel", "\"" + title + "\"");
            plot.addPlot(s);
            plot.addPlot(t);
            //Define o estilo do gráfico
            PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
            stl.setStyle(Style.LINES);
            plot.setKey(JavaPlot.Key.OFF);
            plot.plot();

            System.out.println("Items guardados.");
        }

        if (op == 2) {
            csvWriteMedias(grafico1, grafico2, length);
            System.out.println("Ficheiro guardado em CSV.");
        }
        if(op==3){
             String title = "Consumo de energia no gráfico n = " + n;
            //Genera um file em .png
            File file = new File("statistics_" + title + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
            plot.setTerminal(terminal);
            //Configuração dos labels
            plot.set("xlabel", "\"Observações\"");
            plot.set("ylabel", "\"" + title + "\"");
            plot.addPlot(s);
            plot.addPlot(t);
            //Define o estilo do gráfico
            PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
            stl.setStyle(Style.LINES);
            plot.setKey(JavaPlot.Key.OFF);
            plot.plot();

            System.out.println("Ficheiros guardados em CSV e PNG.");
        }

        if (op == 4) {
            System.out.println("Nenhum ficheiro guardado.");
        } 
//    }else {
//            String title = "Grafico de Barras";
//            //Gera um file em .png
//            File file = new File("statistics_" + title + ".png");
//            //Cria um novo plot
//            JavaPlot plot = new JavaPlot();
//            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
//            GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
//            plot.setTerminal(terminal);
//            //Configuração dos labels
//            plot.set("xlabel", "\"Observações\"");
//            plot.set("ylabel", "\"" + title + "\"");
//            plot.addPlot(s);
//            //Define o estilo do gráfico
//            PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
//            stl.setStyle(Style.LINES);
//            plot.setKey(JavaPlot.Key.OFF);
//            plot.plot();
//
//            System.out.println("Ficheiro guardado em PNG.");
//            out.println("Ficheiro guardado em PNG.");
//
//        }

    }

    private static void DefinePeriodNonInteractive(int[] consumptionMW, LocalDateTime[] dateTime, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        switch (args[3]) {
            case "11":
                dayPeriod(consumptionMW, size, 0); //TODO: alterar números para constantes
                size = exchangeInfoDayPeriods(consumptionMW, size, 0, dateTime);
                criarGrafico(consumptionMW, size, args,out);
                averages(consumptionMW, size, args,out);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out);
                
                //falta previsão
                break;

            case "12":
                dayPeriod(consumptionMW, size, 6);
                size = exchangeInfoDayPeriods(consumptionMW, size, 6, dateTime);
                criarGrafico(consumptionMW, size, args,out);
                averages(consumptionMW, size, args,out);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out);
                
                //falta previsão
                break;
            case "13":
                dayPeriod(consumptionMW, size, 12);
                size = exchangeInfoDayPeriods(consumptionMW, size, 12, dateTime);
                criarGrafico(consumptionMW, size, args,out);
                averages(consumptionMW, size, args,out);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out);
                
                //falta previsão
                break;
            case "14":
                dayPeriod(consumptionMW, size, 18);
                size = exchangeInfoDayPeriods(consumptionMW, size, 18, dateTime);
                criarGrafico(consumptionMW, size, args,out);
                averages(consumptionMW, size, args,out);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out);
                
                //falta previsão
                break;
            case "2":
                size = dailyPeriod(consumptionMW, size, dateTime);
                criarGrafico(consumptionMW, size, args,out);
                averages(consumptionMW, size, args,out);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out);
                
                //falta previsão
                break;
            case "3":
                size = monthlyPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size, args,out);
                averages(consumptionMW, size, args,out);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out);
                
                //falta previsão
                break;
            case "4":
                size = annualPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size, args,out);
                averages(consumptionMW, size, args,out);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out);
                
                //falta previsão
                break;

        }
    }

    public static void DefineModel(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        switch (args[5]) {
            case "1":
                MediaMovelSimples(consumptionMW, size, args, out);
                break;
            case "2":
                MediaMovelPesada(consumptionMW, size, args, out);
                break;
            default:
                System.out.println("Parâmetro de modelo inválido.");
                out.println("Parâmetro de modelo inválido.");
                break;
        }
    }

    public static void previsionMediaSimples(int[] consumptionMW, int size) throws FileNotFoundException {
        System.out.println("Introduza o valor da ordem. ");
        int n = sc.nextInt();
        double sum = 0, finalSum = 0;
        for (int k = 1; k <= n - 1; k++) {
            sum = consumptionMW[k - 1] + sum;
            finalSum = sum - k;
        }
        System.out.print(finalSum * (1) / (n) + " MW. ");
    }

    public static double previsionMediaMovelPesada(int[] consumptionMW, int size) {
        double[] consumptionNewMW = new double[size];
        System.out.println("Insira o alfa. ");
        double alpha = sc.nextDouble();
        while (alpha <= 0 || alpha >= 1) {
            System.out.println("Valor errado. Insira novo valor de α entre 0 e 1: ");
            alpha = sc.nextFloat();
        }
        for (int i = size - 2; i < 0; i++) {
            consumptionNewMW[0] = consumptionMW[0];
            consumptionNewMW[i] = (alpha * consumptionMW[i]) + ((1 - alpha) * consumptionNewMW[i - 1]);
            consumptionNewMW[size - 1] = consumptionMW[size - 1];
            System.out.print(consumptionNewMW[0]);
        }
        double sum = 0;
        for (int k = 0; k < size - 1; k++) {
            sum = sum + consumptionNewMW[k];
        }
        double x = (sum) / (size - 2);
        System.out.print(x + " MW. %n ");
        return x;
    }

    public static void imprimirGraficoBarras(int belowAverageValues, int averageValues, int aboveAverageValues, String[] args, PrintWriter out) throws FileNotFoundException {
        JavaPlot p = new JavaPlot();
        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.BOXES);
        myPlotStyle.setLineWidth(1);
        myPlotStyle.setLineType(NamedPlotColor.BLUE);
        myPlotStyle.setPointType(7);
        myPlotStyle.setPointSize(1);

        int tab[][] = new int[7][1];

        //tab[0][0] = 0;
        tab[1][0] = belowAverageValues;
        //tab[2][0] = 0;
        tab[2][0] = averageValues;
        //tab[4][0] = 0;
        tab[3][0] = aboveAverageValues;
        //tab[6][0] = 0;

        DataSetPlot s = new DataSetPlot(tab);
        s.setTitle("Consumo de Energia");
        s.setPlotStyle(myPlotStyle);

        p.addPlot(s);
        p.newGraph();
        p.plot();
//        if (args.length == 2) {
            System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
            int op = sc.nextInt();

            if (op != 1 && op != 2 && op != 3 && op != 4) {
                do {
                    System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
                    op = sc.nextInt();
                } while (op == 1 || op == 2 || op == 3 || op == 4);
            }

            // ainda a desenvolver a parte de guardar em png.
            if (op == 1) {

                String title = "Grafico de Barras";
                //Gera um file em .png
                File file = new File("statistics_" + title + ".png");
                //Cria um novo plot
                JavaPlot plot = new JavaPlot();
                //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
                GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
                plot.setTerminal(terminal);
                //Configuração dos labels
                plot.set("xlabel", "\"Observações\"");
                plot.set("ylabel", "\"" + title + "\"");
                plot.addPlot(s);
                //Define o estilo do gráfico
                PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
                stl.setStyle(Style.BOXES);
                plot.setKey(JavaPlot.Key.OFF);
                plot.plot();

                System.out.println("Ficheiro guardado em PNG.");
            }

            if (op == 2) {
                csvWriteBarras(belowAverageValues, averageValues, aboveAverageValues);
                System.out.println("Ficheiro guardado em CSV");
            }
            if (op == 3) {
                String title = "Grafico de Barras";
                //Gera um file em .png
                File file = new File("statistics_" + title + ".png");
                //Cria um novo plot
                JavaPlot plot = new JavaPlot();
                //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
                GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
                plot.setTerminal(terminal);
                //Configuração dos labels
                plot.set("xlabel", "\"Observações\"");
                plot.set("ylabel", "\"" + title + "\"");
                plot.addPlot(s);
                //Define o estilo do gráfico
                PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
                stl.setStyle(Style.BOXES);
                plot.setKey(JavaPlot.Key.OFF);
                plot.plot();
                csvWriteBarras(belowAverageValues, averageValues, aboveAverageValues);
                System.out.println("Ficheiros guardados em PNG e CSV");
            }

            if (op == 4) {
                System.out.println("Nenhum ficheiro guardado.");
            }
//        } else {
//            String title = "Grafico de Barras";
//            //Gera um file em .png
//            File file = new File("statistics_" + title + ".png");
//            //Cria um novo plot
//            JavaPlot plot = new JavaPlot();
//            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
//            GNUPlotTerminal terminal = new FileTerminal("png", "statistics_" + title + ".png");
//            plot.setTerminal(terminal);
//            //Configuração dos labels
//            plot.set("xlabel", "\"Observações\"");
//            plot.set("ylabel", "\"" + title + "\"");
//            plot.addPlot(s);
//            //Define o estilo do gráfico
//            PlotStyle stl = ((AbstractPlot) plot.getPlots().get(0)).getPlotStyle();
//            stl.setStyle(Style.BOXES);
//            plot.setKey(JavaPlot.Key.OFF);
//            plot.plot();
//
//            System.out.println("Ficheiro guardado em PNG.");
//            out.println("Ficheiro guardado em PNG.");
//
//        }
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy  HH,mm,ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void csvWriteMedias(int consumptionMW[], double consumptionNewMW[], int size) throws FileNotFoundException {
        String hora;
        hora = getDateTime();

        PrintWriter out = new PrintWriter(new File(nome + "," + hora + ".csv"));
        for (int i = 0; i < size; i++) {
            out.println(consumptionMW[i] + " (original)");
            out.printf("%.1f(filtrada)\n", consumptionNewMW[i]);
        }

        out.close();
    }

    public static void csvWriteGrafico(int consumptionMW[], int size) throws FileNotFoundException {
        String hora;
        hora = getDateTime();

        PrintWriter out = new PrintWriter(new File(nome + "," + hora + ".csv"));
        for (int i = 0; i < size; i++) {
            out.println(consumptionMW[i]);
        }

        out.close();
    }

    public static void csvWriteBarras(int belowAverageValues, int averageValues, int aboveAverageValues) throws FileNotFoundException {
        String hora;
        hora = getDateTime();

        PrintWriter out = new PrintWriter(new File(nome + "," + hora + ".csv"));
        out.println("Quantidade de valores dentro da média: " + averageValues);
        out.println("Quantidade de valores acima da média: " + aboveAverageValues);
        out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
        out.close();
    }
}
