package pkg2019.lapr1_1dk_mafia;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.FileTerminal;
import com.panayotis.gnuplot.terminal.GNUPlotTerminal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class LAPR1_1DK_Mafia {

    static Scanner sc = new Scanner(System.in);
    static final int MAX_OBSERVATIONS = 26280;
    static final int NUM_HOURS_IN_STAGE = 6;
    static final int NUM_STAGES = 4;
    static final int NUM_HOURS = 24;
    static final int NUM_DAYS_IN_YEAR = 365;
    static final int START=0;
    static final int END_DAWN=6;
    static final int END_MORNING=12;
    static final int END_AFTERNOON=18;
    static final String OUTPUT_FILE = "Output.txt";
    static final String nome = "Consumos ";
    static String agregacao = "";
    static String tipo = "";
    static String file = "";

    public static void main(String[] args) throws FileNotFoundException {
        int[] consumptionMW = new int[MAX_OBSERVATIONS];
        LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];
        //String file = args[1];
        String file = "";//só para os testes
        int size = readFile(consumptionMW, dateTime, file);

//        if (args.length == 2) {
        PrintWriter out = null;
        //menu interativo
        int option = 0;
        do {
//                if (option == 6) {
//                    file = changeFile();
//                    size = readFile(consumptionMW, dateTime, file);
//                }
            int[] auxConsumptionMW = Arrays.copyOf(consumptionMW, size);
            option = menu(auxConsumptionMW, dateTime, size, args, out);
        } while (option != 0);
//        } else {
//            PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
//            if (args.length == 12) {
//                int[] auxConsumptionMW = Arrays.copyOf(consumptionMW, size);
//                DefinePeriodNonInteractive(auxConsumptionMW, dateTime, size, args, out);
//            } else {
//                System.out.println("Parâmetros inválidos");
//            }
//            out.close();
//        }
    }

    //lê ficheiro .csv
    public static int readFile(int[] consumptionMW, LocalDateTime[] dateTime, String file) throws FileNotFoundException {
        Scanner fileScan = new Scanner(new File("DAYTON (2).csv"));//trocar Dayton(2) pelo file
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

    //muda o ficheiro .csv
    public static String changeFile() {
        Scanner novo = new Scanner(System.in);
        System.out.println("Introduza o nome do novo ficheiro com a respetiva extensão. ");
        String file = novo.nextLine();
        return file;
    }

    //menu interativo geral
    public static int menu(int[] consumptionMW, LocalDateTime[] dateTime, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        System.out.printf("Indique a opção que pretende:%n"
                + "1. Visualizar gráfico de consumos;%n"
                + "2. Visualizar média global e distribuição de observações;%n"
                + "3. Efetuar uma filtragem;%n"
                + "4. Ordenar valores;%n"
                + "5. Efetuar uma previsão;%n"
                + "6. Alterar o ficheiro;%n"
                + "0. Sair.%n");
        int option = sc.nextInt();
        switch (option) {
            case 1:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                switch (option) {
                    case 1:
                        agregacao = "Madrugadas";
                        break;
                    case 2:
                        agregacao = "Manhas";
                        break;
                    case 3:
                        agregacao = "Tardes";
                        break;
                    case 4:
                        agregacao = "Noites";
                        break;
                    case 5:
                        agregacao = "Diario";
                        break;
                    case 6:
                        agregacao = "Mensal";
                        break;
                    case 7:
                        agregacao = "Anual";
                        break;
                }
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                break;
            case 2:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                switch (option) {
                    case 1:
                        agregacao = "Madrugadas";
                        break;
                    case 2:
                        agregacao = "Manhas";
                        break;
                    case 3:
                        agregacao = "Tardes";
                        break;
                    case 4:
                        agregacao = "Noites";
                        break;
                    case 5:
                        agregacao = "Diario";
                        break;
                    case 6:
                        agregacao = "Mensal";
                        break;
                    case 7:
                        agregacao = "Anual";
                        break;
                }
                averages(consumptionMW, size, args, out, agregacao);
                break;
            case 3:
                System.out.printf("Que tipo de filtragem pretende?%n "
                        + "1. A partir da média móvel simples;%n "
                        + "2. A partir da média exponencialmente pesada. %n");
                int op = sc.nextInt();
                while (op != 1 && op != 2) {
                    System.out.println("Introduza uma opção válida. ");
                    op = sc.nextInt();
                }
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                switch (op) {
                    case 1:
                        switch (option) {
                            case 1:
                                agregacao = "Madrugadas";
                                break;
                            case 2:
                                agregacao = "Manhas";
                                break;
                            case 3:
                                agregacao = "Tardes";
                                break;
                            case 4:
                                agregacao = "Noites";
                                break;
                            case 5:
                                agregacao = "Diario";
                                break;
                            case 6:
                                agregacao = "Mensal";
                                break;
                            case 7:
                                agregacao = "Anual";
                                break;
                        }
                        MediaMovelSimples(consumptionMW, size, args, out, agregacao);
                        break;
                    case 2:
                        switch (option) {
                            case 1:
                                agregacao = "Madrugadas";
                                break;
                            case 2:
                                agregacao = "Manhas";
                                break;
                            case 3:
                                agregacao = "Tardes";
                                break;
                            case 4:
                                agregacao = "Noites";
                                break;
                            case 5:
                                agregacao = "Diario";
                                break;
                            case 6:
                                agregacao = "Mensal";
                                break;
                            case 7:
                                agregacao = "Anual";
                                break;
                        }
                        MediaMovelPesada(consumptionMW, size, args, out, agregacao);
                        break;
                }
                break;
            case 4:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                switch (option) {
                    case 1:
                        agregacao = "Madrugadas";
                        break;
                    case 2:
                        agregacao = "Manhas";
                        break;
                    case 3:
                        agregacao = "Tardes";
                        break;
                    case 4:
                        agregacao = "Noites";
                        break;
                    case 5:
                        agregacao = "Diario";
                        break;
                    case 6:
                        agregacao = "Mensal";
                        break;
                    case 7:
                        agregacao = "Anual";
                        break;
                }
                defineOrder(consumptionMW, size, args, out, agregacao);
                break;
            case 5:
                option = definePeriod(consumptionMW, dateTime, size);
                size = exchange(consumptionMW, dateTime, size, option);
                definePrevision(consumptionMW, dateTime, size, option, args, out);
                break;
            case 6:
                break;
            case 0:
                break;
            default:
                System.out.println("Opção inválida. ");
        }
        return option;
    }

    //--------------------------------------------PERIODICIDADE--------------------------------------------------------
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
        if (option < 1 || option > 7) {
            System.out.println("Opção inválida. ");
        }
        return option;
    }

    //chama os métodos de conversão de arrays de acordo com a resolução temporal
    public static int exchange(int[] consumptionMW, LocalDateTime[] dateTime, int size, int option) throws FileNotFoundException {
        switch (option) {
            case 1:
                dayPeriod(consumptionMW, size, START); 
                size = exchangeInfoDayPeriods(consumptionMW, size, START, dateTime);
                break;
            case 2:
                dayPeriod(consumptionMW, size, END_DAWN);
                size = exchangeInfoDayPeriods(consumptionMW, size, END_DAWN, dateTime);
                break;
            case 3:
                dayPeriod(consumptionMW, size, END_MORNING);
                size = exchangeInfoDayPeriods(consumptionMW, size, END_MORNING, dateTime);
                break;
            case 4:
                dayPeriod(consumptionMW, size, END_AFTERNOON);
                size = exchangeInfoDayPeriods(consumptionMW, size, END_AFTERNOON, dateTime);
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
                default:
                    System.out.println("Opção inválida. ");
                    break;
        }
        return size;
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

    //--------------------------------------------PREVISAO--------------------------------------------------------
    ///menu para escolher a previsão
    public static void definePrevision(int[] consumptionMW, LocalDateTime[] dateTime, int size, int option, String[] args, PrintWriter out) throws FileNotFoundException {
        sc.nextLine();
        String inputDate = "";
        if (args.length == 2) {
            switch (option) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    System.out.println("Insira a data pretendida no formato YYYYMMDD:");
                    inputDate = sc.nextLine() + " 00:00";
                    break;
                case 6:
                    System.out.println("Insira a data pretendida no formato YYYYMM:");
                    inputDate = sc.nextLine() + "01 00:00";
                    break;
                case 7:
                    System.out.println("Insira a data pretendida no formato YYYY:");
                    inputDate = sc.nextLine() + "0101 00:00";
                    break;
            }
        } else {
            int option2 = Integer.parseInt(args[3]);
            switch (option2) {
                case 11:
                case 12:
                case 13:
                case 14:
                case 2:
                    inputDate = args[11];
                    break;
                case 3:
                    inputDate = args[11];
                    break;
                case 4:
                    inputDate = args[11];
                    break;

            }
        }
        LocalDateTime date = verifyDate(inputDate, dateTime, size, option, args, out);
        if (date != null) {
            if (date.isAfter(dateTime[size - 1])) {
                double media = previsionType(consumptionMW, dateTime, size, size - 1, args, out);
                System.out.println("A previsão de consumo para a data " + inputDate + " é de " + media + " MW.");
                if (args.length == 12) {
                    out.println("A previsão de consumo para a data " + inputDate + " é de " + media + " MW.");
                }
            } else {
                long index = searchForDateIndex(dateTime, date, option,args);
                double media = previsionType(consumptionMW, dateTime, size, (int) index, args, out);
                System.out.println("A previsão de consumo para a data " + inputDate + " é de " + media + " MW.");
                if (args.length == 12) {
                    out.println("A previsão de consumo para a data " + inputDate + " é de " + media + " MW.");
                }
            }
        }

    }    //valida a data introduzida pelo utilizador e converte para LocalDateTime

    public static LocalDateTime verifyDate(String inputDate, LocalDateTime[] dateTime, int size, int option, String[] args, PrintWriter out) {
        LocalDateTime date = null;

        //valida a data introduzida de modo a ser aceite pelo DateTimeFormatter
        boolean dateExists = true;
        int year = Integer.parseInt(inputDate.substring(0, 4));
        int month = Integer.parseInt(inputDate.substring(4, 6));
        int day = Integer.parseInt(inputDate.substring(6, 8));
        if (year < 1000 || year > 9999 || month < 1 || month > 12 || day < 1 || day > YearMonth.of(year, month).lengthOfMonth()) {
            System.out.println("Data inválida");
            if (args.length == 12) {
                out.println("Data inválida");
            }
            dateExists = false;
        }
        //conversão da data para LocalDateTime
        if (dateExists) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
            date = LocalDateTime.parse(inputDate, formatter);

            //calcula a previsão mais recente que é possível obter
            LocalDateTime latestDate = null;
            switch (option) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    latestDate = dateTime[size - 1].plusDays(1);
                    break;
                case 6:
                    latestDate = dateTime[size - 1].plusMonths(1);
                    break;
                case 7:
                    latestDate = dateTime[size - 1].plusYears(1);
                    break;
            }
            //valida se a data está dentro do array dateTime ou é imediatamente a seguir
            if (date.isBefore(dateTime[0]) || date.isAfter(latestDate)) {
                System.out.println("Data inválida.");
                if (args.length == 12) {
                    out.println("Data inválida");
                }
                date = null;
            }
        }
        return date;
    }

    //retorna o índice no array dateTime da data introduzida
    public static long searchForDateIndex(LocalDateTime[] dateTime, LocalDateTime date, int option,String[]args) {
        long index = 0;
        if(args.length==2){
        switch (option) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                index = Duration.between(dateTime[0], date).toDays();
                break;
            case 6:
                index = ChronoUnit.MONTHS.between(dateTime[0], date);
                break;
            case 7:
                index = Math.abs(dateTime[0].getYear() - date.getYear());
        }
        }
        else {
            int option2 = Integer.parseInt(args[3]);
            switch (option2) {
                case 11:
                case 12:
                case 13:
                case 14:
                case 2:
                    index = Duration.between(dateTime[0], date).toDays();
                    break;
                case 3:
                    index = ChronoUnit.MONTHS.between(dateTime[0], date);

                    break;
                case 4:
                    index = Math.abs(dateTime[0].getYear() - date.getYear());
                    break;

            }
        }
        return index;
    }
    
    //seleciona previsão por média móvel simples ou pesada
    public static double previsionType(int[] consumptionMW, LocalDateTime[] dateTime, int size, int index, String[] args, PrintWriter out) throws FileNotFoundException {
        int option = 0;
        if (args.length == 2) {
            System.out.printf("Que tipo de previsão pretende?%n"
                    + "1. Previsão a partir da média móvel simples;%n"
                    + "2. Previsão a partir da média exponencialmente pesada.%n");

            option = sc.nextInt();
        } else {
            option = Integer.parseInt(args[5]);
        }
        switch (option) {
            case 1:
                //validar últimas e primeiras posições do array
                double[] mediaMovelSimples = MediaMovelSimples(consumptionMW, size, args, out, agregacao);
                return mediaMovelSimples[index];
            case 2: {
                double[] mediaMovelPesada = MediaMovelPesada(consumptionMW, size, args, out, agregacao);
                return mediaMovelPesada[index];
            }
            default:
                System.out.println("Opção inválida. ");
                break;
        }
        return 0;
    }
    //--------------------------------------------ORDENACAO--------------------------------------------------------

    //ordena de forma crescente ou decrescente conforme escolha do utilizador
    public static void defineOrder(int[] consumptionMW, int size, String[] args, PrintWriter out, String agregacao) throws FileNotFoundException {
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
                mergeSort(consumptionMW, start, size - 1);
                tipo = "Crescente";
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                break;
            case 2:
                inverseMergeSort(consumptionMW, start, size - 1);
                tipo = "Decrescente";
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
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

    //método de ordenação decrescente
    public static void inverseMergeSort(int consumption[], int start, int end) {
        if (start < end) {
            int mid = (start + end) / 2;
            inverseMergeSort(consumption, start, mid);
            inverseMergeSort(consumption, mid + 1, end);
            inverseMerge(consumption, start, mid, end);
        }
    }

    public static void inverseMerge(int consumption[], int start, int middle, int end) {
        int temp[] = new int[end - start + 1];

        int i = start, j = middle + 1, k = 0;

        while (i <= middle && j <= end) {
            if (consumption[i] >= consumption[j]) {                          
                temp[k] = consumption[i];
                k += 1;
                i += 1;
            } else {
                temp[k] = consumption[j];
                k += 1;
                j += 1;
            }
        }

        while (i <= middle) {
            temp[k] = consumption[i];
            k += 1;
            i += 1;
        }

        while (j <= end) {
            temp[k] = consumption[j];
            k += 1;
            j += 1;
        }

        for (i = start; i <= end; i += 1) {
            consumption[i] = temp[i - start];
        }
    }

    //método de ordenação crescente
    public static void mergeSort(int consumption[], int start, int end) {

        if (start < end) {
            int mid = (start + end) / 2;
            mergeSort(consumption, start, mid);
            mergeSort(consumption, mid + 1, end);
            merge(consumption, start, mid, end);
        }
    }

    public static void merge(int consumption[], int start, int middle, int end) {

        int temp[] = new int[end - start + 1];

        int i = start, j = middle + 1, k = 0;

        while (i <= middle && j <= end) {
            if (consumption[i] <= consumption[j]) {                           
                temp[k] = consumption[i];
                k += 1;
                i += 1;
            } else {
                temp[k] = consumption[j];
                k += 1;
                j += 1;
            }
        }

        while (i <= middle) {
            temp[k] = consumption[i];
            k += 1;
            i += 1;
        }

        while (j <= end) {
            temp[k] = consumption[j];
            k += 1;
            j += 1;
        }

        for (i = start; i <= end; i += 1) {
            consumption[i] = temp[i - start];
        }
    }
    //--------------------------------------------MEDIA E DISTRUBUIÇÃO--------------------------------------------------------
    public static int averages(int[] consumptionMW, int size, String[] args, PrintWriter out, String agregacao) throws FileNotFoundException {
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

        imprimirGraficoBarras(belowAverageValues, averageValues, aboveAverageValues, args, out, agregacao,file);

        System.out.println("Média : " + (consumptionSum / size) + " " + "MW");
        System.out.println("Quantidade de valores próximos da média: " + averageValues);
        System.out.println("Quantidade de valores acima da média: " + aboveAverageValues);
        System.out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
        return consumptionSum / size;
    }
    //--------------------------------------------FILTRAGEM--------------------------------------------------------

    public static double[] MediaMovelSimples(int[] consumptionMW, int size, String[] args, PrintWriter out, String agregacao) throws FileNotFoundException {
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
        double[] mediaMovelSimples = new double[size];
        int i;
        if (nonInteractiveInvalidInput == false) {
            for (i = n - 1; i < size; i++) {
                for (int k = 0; k < n; k++) {
                    mediaMovelSimples[i] += consumptionMW[i - k];
                }
                mediaMovelSimples[i] /= n;
            }

            criarGraficoMediaSimples(consumptionMW, mediaMovelSimples, mediaMovelSimples.length, n, out, args, agregacao,file);
            absoluteError(consumptionMW, mediaMovelSimples, mediaMovelSimples.length, out, args);
            //System.out.println(Arrays.toString(mediaMovelSimples));
        }
        return mediaMovelSimples;
    }

    public static double[] MediaMovelPesada(int[] consumptionMW, int size, String[] args, PrintWriter out, String agregacao) throws FileNotFoundException {
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
            for (int i = 1; i < size; i++) {
                consumptionNewMW[0] = consumptionMW[0];
                consumptionNewMW[i] = (alpha * consumptionMW[i]) + ((1 - alpha) * consumptionNewMW[i - 1]);
            }
            // criar 1 gráfico com os valores inicias e o valor de α
            criarGraficoMediaPesada(consumptionMW, consumptionNewMW, size, alpha, args, out,file);
            absoluteError(consumptionMW, consumptionNewMW, consumptionNewMW.length, out, args);
        }
        return consumptionNewMW;
    }

    public static double absoluteError(int[] consumptionMW, double[] arrayY, int size, PrintWriter out, String[] args) {
        double sum = 0.00;
        for (int i = 0; i < size; i++) {
            sum = sum + Math.abs(arrayY[i] - consumptionMW[i]);
        }
        double absoluteError = sum / size;
        System.out.printf("Erro absoluto: %.2f \n", absoluteError);
        if (args.length == 12) {
            out.printf("Erro absoluto: %.2f \n", absoluteError);
        }
        return absoluteError;
    }
    //--------------------------------------------GRAFICOS--------------------------------------------------------

    public static void criarGrafico(int[] grafico, int size, String[] args, PrintWriter out, String agregacao, String tipo, String cabecalho) throws FileNotFoundException {

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
            } while (op != 1 && op != 2 && op != 3 && op != 4);
        }

        // ainda a desenvolver a parte de guardar em png.
        if (op == 1) {

            String title = "Consumo de energia";
            //Genera um file em .png
            File file = new File("Grafico " + title + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png", "Grafico " + "(" + cabecalho + ") " + tipo + " " + agregacao + " " + title + ".png");
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
            csvWriteGrafico(grafico, size, agregacao, tipo, cabecalho);
            System.out.println("Ficheiro guardado em CSV.");
        }
        if (op == 3) {
            String title = "Consumo de energia";
            //Genera um file em .png
            File file = new File("Grafico " + title + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png", "Grafico " + "(" + cabecalho + ") " + tipo + " " + agregacao + " " + title + ".png");
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
            csvWriteGrafico(grafico, size, agregacao, tipo, cabecalho);
            System.out.println("Ficheiros guardados em PNG e CSV.");
        }

        if (op == 4) {
            System.out.println("Nenhum ficheiro guardado.");
        }
//        } else {
//            String title = "Consumo de energia";
//                //Genera um file em .png
//                File file = new File("Grafico" + title + ".png");
//                //Cria um novo plot
//                JavaPlot plot = new JavaPlot();
//                //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
//                GNUPlotTerminal terminal = new FileTerminal("png", "Grafico " + "(" + cabecalho + ") " + tipo + " " + agregacao + " " + title + ".png");
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

    public static void criarGraficoMediaPesada(int[] grafico1, double[] grafico2, int size, double alpha, String[] args, PrintWriter out, String cabecalho) throws FileNotFoundException {
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
            } while (op != 1 && op != 2 && op != 3 && op != 4);
        }

        // ainda a desenvolver a parte de guardar em png.
        if (op == 1) {

            String title = "Consumo de energia no grafico alpha = " + alpha;
            //Genera um file em .png
            File file = new File(title + " " + agregacao + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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
            csvWriteMediaPesada(grafico1, grafico2, size, agregacao,cabecalho);
            System.out.println("Ficheiro guardado em CSV.");
        }
        if (op == 3) {
            String title = "Consumo de energia no grafico alpha = " + alpha;
            //Genera um file em .png
            File file = new File(title + " " + agregacao + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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
            csvWriteMediaPesada(grafico1, grafico2, size, agregacao,cabecalho);
            System.out.println("Ficheiros guardados em PNG e CSV.");

        }

        if (op == 4) {
            System.out.println("Nenhum ficheiro guardado.");
        }
//        }
//        else{String title = "Consumo de energia no gráfico alpha = " + alpha;
//            //Genera um file em .png
//            File file = new File(title + " " + agregacao + ".png");
//            //Cria um novo plot
//            JavaPlot plot = new JavaPlot();
//            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
//            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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

    public static void criarGraficoMediaSimples(int[] grafico1, double[] grafico2, int length, int n, PrintWriter out, String[] args, String agregacao, String cabecalho) throws FileNotFoundException {
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
        for (int i = n - 1; i < length; i++) {
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
        //if(args.length==2){
        System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
        int op = sc.nextInt();
        
        if (op != 1 && op != 2 && op != 3 && op != 4) {
            do {
                System.out.println("Pretende gravar o gráfico? 1.PNG 2.CSV 3.PNG e CSV 4.Não");
                op = sc.nextInt();
            } while (op != 1 && op != 2 && op != 3 && op != 4);
        }

        // ainda a desenvolver a parte de guardar em png.
        if (op == 1) {

            String title = "Consumo de energia no gráfico n = " + n;
            //Genera um file em .png
            File file = new File(title + " " + agregacao + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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
            csvWriteMediaSimples(grafico1, grafico2, length, agregacao,cabecalho);
            System.out.println("Ficheiro guardado em CSV.");
        }
        if (op == 3) {
            String title = "Consumo de energia no grafico n = " + n;
            //Genera um file em .png
            File file = new File(title + " " + agregacao + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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
            csvWriteMediaSimples(grafico1, grafico2, length, agregacao,cabecalho);

            System.out.println("Ficheiros guardados em CSV e PNG.");
        }

        if (op == 4) {
            System.out.println("Nenhum ficheiro guardado.");
        }
//    }else {
//            String title = "Grafico de Barras";
//            //Gera um file em .png
//            File file = new File(title + " " + agregacao + ".png");
//            //Cria um novo plot
//            JavaPlot plot = new JavaPlot();
//            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
//            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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

    public static void imprimirGraficoBarras(int belowAverageValues, int averageValues, int aboveAverageValues, String[] args, PrintWriter out, String agregacao, String cabecalho) throws FileNotFoundException {
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
        s.setTitle("Número de Observações");
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
            } while (op != 1 && op != 2 && op != 3 && op != 4);
        }

        // ainda a desenvolver a parte de guardar em png.
        if (op == 1) {

            String title = "Grafico de Barras";
            //Gera um file em .png
            File file = new File(title + " " + agregacao + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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
            csvWriteBarras(belowAverageValues, averageValues, aboveAverageValues, agregacao,cabecalho);
            System.out.println("Ficheiro guardado em CSV");
        }
        if (op == 3) {
            String title = "Grafico de Barras";
            //Gera um file em .png
            File file = new File(title + " " + agregacao + ".png");
            //Cria um novo plot
            JavaPlot plot = new JavaPlot();
            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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
            csvWriteBarras(belowAverageValues, averageValues, aboveAverageValues, agregacao,cabecalho);
            System.out.println("Ficheiros guardados em PNG e CSV");
        }

        if (op == 4) {
            System.out.println("Nenhum ficheiro guardado.");
        }
//        } else {
//            String title = "Grafico de Barras";
//            //Gera um file em .png
//            File file = new File(title + " " + agregacao + ".png");
//            //Cria um novo plot
//            JavaPlot plot = new JavaPlot();
//            //Cria uma classe no terminal que interage com o Gnuplot sem mostrar o gráfico
//            GNUPlotTerminal terminal = new FileTerminal("png","(" + cabecalho + ") " + title + " " + agregacao + ".png");
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

    public static void csvWriteMediaSimples(int consumptionMW[], double consumptionNewMW[], int size, String agregacao, String cabecalho) throws FileNotFoundException {
        String hora;
        hora = getDateTime();

        PrintWriter out = new PrintWriter(new File(nome + "(" + cabecalho + ") " + "Media Simples" + " " + agregacao + "," + hora + ".csv"));
        for (int i = 0; i < size; i++) {
            out.println(consumptionMW[i] + " (original)");
            out.printf("%.1f(filtrada)\n", consumptionNewMW[i]);
        }

        out.close();
    }

    public static void csvWriteMediaPesada(int consumptionMW[], double consumptionNewMW[], int size, String agregacao, String cabecalho) throws FileNotFoundException {
        String hora;
        hora = getDateTime();

        PrintWriter out = new PrintWriter(new File(nome + "(" + cabecalho + ") " + "Media Pesada" + " " + agregacao + "," + hora + ".csv"));
        for (int i = 0; i < size; i++) {
            out.println(consumptionMW[i] + " (original)");
            out.printf("%.1f(filtrada)\n", consumptionNewMW[i]);
        }

        out.close();
    }

    public static void csvWriteGrafico(int consumptionMW[], int size, String agregacao, String tipo, String cabecalho) throws FileNotFoundException {
        String hora;
        hora = getDateTime();

        PrintWriter out = new PrintWriter(new File(nome + "(" + cabecalho + ") " + tipo + " " + agregacao + "," + hora + ".csv"));
        for (int i = 0; i < size; i++) {
            out.println(consumptionMW[i]);
        }

        out.close();
    }

    public static void csvWriteBarras(int belowAverageValues, int averageValues, int aboveAverageValues, String agregacao, String cabecalho) throws FileNotFoundException {
        String hora;
        hora = getDateTime();

        PrintWriter out = new PrintWriter(new File(nome + "(" + cabecalho + ") " + agregacao + " " + "," + hora + ".csv"));
        out.println("Quantidade de valores dentro da média: " + averageValues);
        out.println("Quantidade de valores acima da média: " + aboveAverageValues);
        out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
        out.close();
    }

    //--------------------------------------------NAO INTERATIVO--------------------------------------------------------
    private static void DefinePeriodNonInteractive(int[] consumptionMW, LocalDateTime[] dateTime, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        switch (args[3]) {
            case "11":
                dayPeriod(consumptionMW, size, START); 
                size = exchangeInfoDayPeriods(consumptionMW, size, START, dateTime);
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                averages(consumptionMW, size, args, out, agregacao);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out, agregacao);

                //falta previsão
                break;

            case "12":
                dayPeriod(consumptionMW, size, END_DAWN);
                size = exchangeInfoDayPeriods(consumptionMW, size, END_DAWN, dateTime);
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                averages(consumptionMW, size, args, out, agregacao);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out, agregacao);

                //falta previsão
                break;
            case "13":
                dayPeriod(consumptionMW, size, END_MORNING);
                size = exchangeInfoDayPeriods(consumptionMW, size, END_MORNING, dateTime);
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                averages(consumptionMW, size, args, out, agregacao);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out, agregacao);

                //falta previsão
                break;
            case "14":
                dayPeriod(consumptionMW, size, END_AFTERNOON);
                size = exchangeInfoDayPeriods(consumptionMW, size, END_AFTERNOON, dateTime);
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                averages(consumptionMW, size, args, out, agregacao);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out, agregacao);

                //falta previsão
                break;
            case "2":
                size = dailyPeriod(consumptionMW, size, dateTime);
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                averages(consumptionMW, size, args, out, agregacao);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out, agregacao);

                //falta previsão
                break;
            case "3":
                size = monthlyPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                averages(consumptionMW, size, args, out, agregacao);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out, agregacao);

                //falta previsão
                break;
            case "4":
                size = annualPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size, args, out, agregacao, tipo,file);
                averages(consumptionMW, size, args, out, agregacao);
                DefineModel(consumptionMW, size, args, out);
                defineOrder(consumptionMW, size, args, out, agregacao);

                //falta previsão
                break;

        }
    }

    public static void DefineModel(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        switch (args[5]) {
            case "1":
                MediaMovelSimples(consumptionMW, size, args, out, agregacao);
                break;
            case "2":
                MediaMovelPesada(consumptionMW, size, args, out, agregacao);
                break;
            default:
                System.out.println("Parâmetro de modelo inválido.");
                out.println("Parâmetro de modelo inválido.");
                break;
        }
    }

}
