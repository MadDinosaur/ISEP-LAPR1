package pkg2019.lapr1_1dk_mafia;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
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
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import com.panayotis.gnuplot.GNUPlotException;
import static javafx.beans.binding.Bindings.size;

public class LAPR1_1DK_Mafia {

    static Scanner sc = new Scanner(System.in);
    static final int MAX_OBSERVATIONS = 26280;
    static final int NUM_HOURS_IN_STAGE = 6;
    static final int NUM_STAGES = 4;
    static final int NUM_HOURS = 24;
    static final int NUM_DAYS_IN_YEAR = 365;
    static final String OUTPUT_FILE = "Output.txt";

    public static void main(String[] args) throws FileNotFoundException {
        int[] consumptionMW = new int[MAX_OBSERVATIONS];
        LocalDateTime[] dateTime = new LocalDateTime[MAX_OBSERVATIONS];
        int size = readFile(consumptionMW, dateTime, args);
        if (args.length == 1) {
            PrintWriter out = null;
            //menu interativo
            int option;
            do {
                int[] auxConsumptionMW = Arrays.copyOf(consumptionMW, size);
                option = menu(auxConsumptionMW, dateTime, size, args, out);
            } while (option != 7);
        } else {
            PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
            if (args.length == 6) {
                int[] auxConsumptionMW = Arrays.copyOf(consumptionMW, size);
                DefinePeriodNonInteractive(auxConsumptionMW, dateTime, size, args, out);
            } else {
                System.out.println("Parâmetros inválidos");
            }
            out.close();
        }
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
                size = definePeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                break;
            case 2:
                size = definePeriod(consumptionMW, dateTime, size);
                averages(consumptionMW, size, args);
                break;
            case 3:
                size = definePeriod(consumptionMW, dateTime, size);
                MediaMovelSimples(consumptionMW, size, args, out);
                criarGrafico(consumptionMW, size);
                break;
            case 4:
                size = definePeriod(consumptionMW, dateTime, size);
                MediaMovelPesada(consumptionMW, size, args, out);
                criarGrafico(consumptionMW, size);
                break;
            case 5:
                size = definePeriod(consumptionMW, dateTime, size);
                defineOrder(consumptionMW, size, args, out);
                break;
            case 6:
                size = definePeriod(consumptionMW, dateTime, size);
                definePrevision(consumptionMW, dateTime);
                break;
        }
        return option;
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
                size = exchangeInfoDayPeriods(consumptionMW, size, 0);
                break;
            case 2:
                dayPeriod(consumptionMW, size, 6);
                size = exchangeInfoDayPeriods(consumptionMW, size, 6);
                break;
            case 3:
                dayPeriod(consumptionMW, size, 12);
                size = exchangeInfoDayPeriods(consumptionMW, size, 12);
                break;
            case 4:
                dayPeriod(consumptionMW, size, 18);
                size = exchangeInfoDayPeriods(consumptionMW, size, 18);
                break;
            case 5:
                size = dailyPeriod(consumptionMW, size);
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

    public static void definePrevision(int[] consumptionMW, LocalDateTime[] dateTime) {
        System.out.println("Que dia pretende prever?");
        //Podes continuar a partir daqui. Já está a pedir a resolução temporal no menu()
        //Sugiro que faças passar algum parâmetro para este método para saberes com que resolução estás a trabalhar
    }

    //ordena de forma crescente ou decrescente conforme escolha do utilizador
    public static void defineOrder(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        int start = 0, order;
        if (args.length == 6) {
            order = Integer.parseInt(args[3]);
        } else {
            System.out.printf("De que forma pretende ordenar? %n"
                    + "1. Crescente; %n"
                    + "2. Decrescente. %n");
            order = sc.nextInt();
        }

        switch (order) {
            case 1:
                mergeSort(consumptionMW, start, size);
                break;
            case 2:
                inverseMergeSort(consumptionMW, start, size);
                break;
            default:
                System.out.println("Parâmentro de ordenação inválido.");
                if (args.length == 6) {
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
    }

    public static void averages(int[] consumptionMW, int size, String[] args) throws FileNotFoundException {
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
        if (args.length == 6) {
            PrintWriter out = new PrintWriter(new File(OUTPUT_FILE));
            System.out.println("Média : " + (consumptionSum / size) + " " + "MW");
            out.println("Média : " + (consumptionSum / size) + " " + "MW");
            out.println("Quantidade de valores dentro da média: " + averageValues);
            out.println("Quantidade de valores acima da média: " + aboveAverageValues);
            out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
            out.close();
        }
        System.out.println("Média : " + (consumptionSum / size) + " " + "MW");
        System.out.println("Quantidade de valores próximos da média: " + averageValues);
        System.out.println("Quantidade de valores acima da média: " + aboveAverageValues);
        System.out.println("Quantidade de valores abaixo da média: " + belowAverageValues);
    }

    //calcula consumos diários
    public static int dailyPeriod(int[] consumptionMW, int size) throws FileNotFoundException {
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
        size = exchangeInfoDays(consumptionMW, size, NUM_HOURS);
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

    public static int exchangeInfoDayPeriods(int[] consumptionMW, int size, int start) {
        int i, idx2 = start;
        for (i = 0; i < size / NUM_HOURS; i++) {
            //trocar consumos
            consumptionMW[i] = consumptionMW[idx2];
            idx2 += NUM_HOURS;
        }
        size = i;
        return size;
    }

    //troca informação dos dias
    public static int exchangeInfoDays(int[] consumptionMW, int size, int period) {
        int i;
        for (i = 1; i < size / period; i++) {
            int idx2 = i * period;
            //trocar consumos
            consumptionMW[i] = consumptionMW[idx2];
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

    private static double[] MediaMovelSimples(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        int n;
        boolean nonInteractiveInvalidInput = false;
        if (args.length == 6) {
            n = Integer.parseInt(args[4]);
        } else {
            System.out.println("Defina o parâmetro n: ");
            n = sc.nextInt();
        }
        while (n <= 0 || n > consumptionMW.length) {
            if (args.length == 6) {
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
        if (nonInteractiveInvalidInput == false) {
            for (int k = 0; k < size - n; k++) {
                for (int i = k; i < k + n; i++) {
                    mediaMovelSimples[k] += consumptionMW[i];
                }
                mediaMovelSimples[k] /= n;
            }
            criarGraficoMedias(consumptionMW, mediaMovelSimples, mediaMovelSimples.length);
            absoluteError(consumptionMW, mediaMovelSimples, mediaMovelSimples.length);
            previsionMediaSimples(consumptionMW, n, n);
        }
        return mediaMovelSimples;
    }

    public static double[] MediaMovelPesada(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        double alpha;
        double[] consumptionNewMW = new double[size];
        boolean nonInteractiveInvalidInput = false;
        if (args.length == 6) {
            alpha = Integer.parseInt(args[4]);
        } else {
            System.out.println("Insira o valor de α (entre 0 e 1): ");
            alpha = sc.nextDouble();
        }

        while (alpha < 0 || alpha > 1) {
            if (args.length == 6) {
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
        for (int i = size - 1; i > 0; i--) {
            consumptionNewMW[0] = consumptionMW[0];
            consumptionNewMW[i] = (alpha * consumptionMW[i]) + ((1 - alpha) * consumptionNewMW[i - 1]);
            consumptionNewMW[size - 1] = consumptionMW[size - 1];
        }
        // criar 1 gráfico com os valores inicias e o valor de α
        criarGraficoMedias(consumptionMW, consumptionNewMW, size);
        previsionMediaMovelPesada(consumptionMW, consumptionNewMW, size, alpha);
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

    private static void criarGraficoMedias(int[] grafico1, double[] grafico2, int size) {
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

        System.out.println("Pretende gravar o gráfico? 1.Sim 2.Não");
        int op = sc.nextInt();

        if (op != 1 && op != 2) {
            do {
                System.out.println("Pretende gravar o gráfico? 1.Sim 2.Não");
                op = sc.nextInt();
            } while (op == 1 || op == 2);
        }

        // ainda a desenvolver a parte de guardar em png.
        if (op == 1) {

            double[][] values = new double[3][2];
            values[0][0] = 0.1;
            values[0][1] = 0.3;
            values[1][0] = 0.4;
            values[1][1] = 0.3;
            values[2][0] = 0.5;
            values[2][1] = 0.5;

            double[][] values2 = new double[3][2];
            values2[0][0] = 0.2;
            values2[0][1] = 0.0;
            values2[1][0] = 0.7;
            values2[1][1] = 0.1;
            values2[2][0] = 0.6;
            values2[2][1] = 0.5;

            PlotStyle styleDeleted = new PlotStyle();
            styleDeleted.setStyle(Style.LINES);
            styleDeleted.setLineType(NamedPlotColor.GRAY80);

            PlotStyle styleExist = new PlotStyle();
            styleExist.setStyle(Style.LINES);
            styleExist.setLineType(NamedPlotColor.BLACK);

            DataSetPlot setDeleted = new DataSetPlot(values);
            setDeleted.setPlotStyle(styleDeleted);
            setDeleted.setTitle("deleted EMs");

            DataSetPlot setExist = new DataSetPlot(values2);
            setExist.setPlotStyle(styleExist);
            setExist.setTitle("remaining EMs");

            // supostamente esta é a parte que interessa, daqui...
            ImageTerminal png = new ImageTerminal();
            File file = new File("Ambiente de trabalho");
            try {
                file.createNewFile();
                png.processOutput(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                System.err.print(ex);
            } catch (IOException ex) {
                System.err.print(ex);
            }

            JavaPlot p1 = new JavaPlot();
            p.setTerminal(png);

            p1.getAxis("x").setLabel("observações");
            p1.getAxis("y").setLabel("consumo energético");
            p1.addPlot(setDeleted);
            p1.addPlot(setExist);
            p1.setTitle("remaining EMs");
            p1.plot();

            try {
                ImageIO.write(png.getImage(), "png", file);
            } catch (IOException ex) {
                System.err.print(ex);
            }
            p.setPersist(false);
            // até aqui, o resto é só esboçar o gráfico.

            System.out.println("Items guardados.");
        } else {
            System.out.println("Nenhum ficheiro guardado.");
        }

    }

    private static void DefinePeriodNonInteractive(int[] consumptionMW, LocalDateTime[] dateTime, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        switch (args[1]) {
            case "11":
                dayPeriod(consumptionMW, size, 0); //TODO: alterar números para constantes
                size = exchangeInfoDayPeriods(consumptionMW, size, 0);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, size, args);
                defineOrder(consumptionMW, size, args, out);
                DefineModel(consumptionMW, size, args, out);
                //falta previsão
                break;

            case "12":
                dayPeriod(consumptionMW, size, 6);
                size = exchangeInfoDayPeriods(consumptionMW, size, 6);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, size, args);
                defineOrder(consumptionMW, size, args, out);
                DefineModel(consumptionMW, size, args, out);
                //falta previsão
                break;
            case "13":
                dayPeriod(consumptionMW, size, 12);
                size = exchangeInfoDayPeriods(consumptionMW, size, 12);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, size, args);
                defineOrder(consumptionMW, size, args, out);
                DefineModel(consumptionMW, size, args, out);
                //falta previsão
                break;
            case "14":
                dayPeriod(consumptionMW, size, 18);
                size = exchangeInfoDayPeriods(consumptionMW, size, 18);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, size, args);
                defineOrder(consumptionMW, size, args, out);
                DefineModel(consumptionMW, size, args, out);
                //falta previsão
                break;
            case "2":
                size = dailyPeriod(consumptionMW, size);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, size, args);
                defineOrder(consumptionMW, size, args, out);
                DefineModel(consumptionMW, size, args, out);
                //falta previsão
                break;
            case "3":
                size = monthlyPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, size, args);
                defineOrder(consumptionMW, size, args, out);
                DefineModel(consumptionMW, size, args, out);
                //falta previsão
                break;
            case "4":
                size = annualPeriod(consumptionMW, dateTime, size);
                criarGrafico(consumptionMW, size);
                averages(consumptionMW, size, args);
                defineOrder(consumptionMW, size, args, out);
                DefineModel(consumptionMW, size, args, out);
                //falta previsão
                break;

        }
    }

    public static void DefineModel(int[] consumptionMW, int size, String[] args, PrintWriter out) throws FileNotFoundException {
        switch (args[2]) {
            case "1":
                MediaMovelSimples(consumptionMW, size, args, out);
                criarGrafico(consumptionMW, size);
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

    public static void previsionMediaSimples(int[] consumptionMW, int size, int n) throws FileNotFoundException {
        double sum = 0, finalSum = 0;
        for (int k = 1; k <= n - 1; k++) {
            sum = consumptionMW[k - 1] + sum;
            finalSum = sum - k;
        }
        System.out.println(finalSum * (1) / (n) + "MW. ");
    }

    public static void previsionMediaMovelPesada(int[] consumptionMW, double[] consumptionNewMW, int size, double alpha) {
        for (int i = size - 1; i > 0; i--) {
            consumptionNewMW[i] = (alpha * consumptionMW[i - 1]) + (1 - alpha) * consumptionNewMW[i - 1];
        }
        System.out.println(consumptionNewMW[0]);
    }
}
