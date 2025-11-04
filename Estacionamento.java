package estacionamentoAula;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class Estacionamento {
    static Scanner sc = new Scanner(System.in);

//configuração e matriz 
static int corredores;
static int vagasPorCorredor;
static String [][] vagas; //formato hora:minuto;

//tarifas (carro, moto e van)
static double base30Carro;
static double add30Carro;
static final double F_MOTO = 0.7;
static final double F_VAN = 1.3;

//financeiros (para veículos que já sairam)
static int qtdMotoSaiu = 0;
static int qtdCarroSaiu = 0;
static int qtdVanSaiu = 0;
static double valorMotoSaiu = 0.0;
static double valorCarroSaiu = 0.0;
static double valorVanSaiu = 0.0;

static DecimalFormat df = new DecimalFormat("0.00");

public static void main(String[] args) {
    System.out.println("\n----Sistema de Gerenciamento de Estacionamento----\n");
    inicializarSistema();
    menuPrincipal ();
}

//---Inicialização---
static void inicializarSistema () {
    while (true) {
    try {
        System.out.println("Informe o valor dos primeiros 30 minutos (R$): ");
        base30Carro = Double.parseDouble(sc.nextLine().replace(',', '.'));
        if (add30Carro < 0) throw new NumberFormatException();
        break;
    } catch (Exception e) {
        System.out.println("Valor inválido. Tente novamnente");
    }
}
    while (true) {
        try {
            System.out.println("Informe o valor de cada 30 minutos adicionais (R$): ");
            add30Carro = Double.parseDouble(sc.nextLine().replace(',', '.'));
            if (add30Carro < 0) throw new NumberFormatException();
            break;
        } catch (Exception e ) {
            System.out.println("Valor Inválido. Tente novamente. ");
        } 
    }
    System.out.println("-----------");
    System.out.printf("Tarifas calculadas (Carro = R$%s / R$%s) \n ",
        df.format(base30Carro), df.format(add30Carro));
    System.out.printf("Moto = R$%S / R$%s (%.0f%%)\n ",
        df.format(base30Carro * F_MOTO), df.format(add30Carro * F_MOTO), F_MOTO*100);
    System.out.printf("Van = R$%S / R$%s (%.0f%%)\n ",
         df.format(base30Carro * F_VAN), df.format(add30Carro * F_VAN), F_VAN*100);

// Dimensões do estacionamento: corredores (linhas), vagas por corredor 
System.out.println("-----------");
corredores = lerInteiroComIntervalo ("Quantidade de corredores (5 a 15): ", 5,15);
vagasPorCorredor = lerInteiroComIntervalo ("Vagas por corredor (5 a 20): ", 5, 20);

vagas = new String[corredores][vagasPorCorredor];
System.out.println("-----------");
System.out.println("Estacionamento criado: " + corredores + " corredores x " + vagasPorCorredor + " vagas");
}

    static int lerInteiroComIntervalo (String prompt, int min, int max) {
        while (true) {
            try {
                System.out.println(prompt);
                int v = Integer.parseInt (sc.nextLine());
                if (v < min || v > max) {
                    System.out.println("Valor fora do intervalo");
                    continue;
                }
                return v;
            } catch (Exception e) {
                System.out.println("Entrada Inválida.");
            }
            
        }
    }

// ---menu
static void menuPrincipal () {
    int opcao;
    do {
        System.out.println("\n---MENU PRINCIPAL DO SISTEMA---");
        System.out.println("1. Carregar Dados");
        System.out.println("2. Consultar Vaga");
        System.out.println("3. Entrada de Veiculo");
        System.out.println("4. Sáida de Veiculo");
        System.out.println("5. Ocupaçao do Estacionamento");
        System.out.println("6. Financeiro");
        System.out.println("7. Salvar Dados");
        System.out.println("8. Integrantes");
        System.out.println("9. Sair");
        System.out.println("Opçao:  ");
        try {
            opcao = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            opcao = -1;
        }
        switch (opcao) {
            case 1: carregarDados ();
        break;
            case 2: consultarVaga();
            break;
            case 3: entrada ();
            break;
            case 4: saida ();
            break;
            case 5: ocupacao();
            break;
            case 6: financeiro();
            break;
            case 7: salvarDados ();
            break;
            case 8: mostrarIntegrantes ();
            break;
            case 9: System.out.println("Encerrando...");
            break;
            default: System.out.println("Opção inválida. ");
        }
    } while (opcao != 9);
}

//-opção 1: carregar dados----
static void carregarDados () {
    System.out.println("Nome do arquivo a carregar: ");
    String nome = sc.nextLine().trim();
    File f = new File(nome);
    if (!f.exists()) {
        System.out.println("Arquivo não encontrado.");
        return;
    }
    //matriz atual
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        //zerar
        vagas = new String[corredores][vagasPorCorredor];
        String linha;
        while ((linha = br.readLine()) !=null) {
            linha = linha.trim();
            if (linha.isEmpty()) continue;
            //formato C:08:15
            String [] parts = linha.split("=");
            if (parts.length !=2 ) continue;
            String id = parts [0].trim().toUpperCase();
            String conteudo = parts [1].trim();
            int [] pos = converterVagaParaIndices (id);
            if (pos == null) continue;
            //validar formato
            String [] sub = conteudo.split(":");
            if (sub.length !=3 ) continue;
            String tipo = sub [0].toUpperCase();
            if 
                (!(tipo.equals("M") || tipo.equals("C") || tipo.equals("V"))) continue;
            //armazenar (M:hora:minuto)
            vagas [pos [0]][pos[1]] = tipo + ":" + Integer.parseInt(sub[1]) + ":" + Integer.parseInt(sub[2]);     
            }
            System.out.println("Dados carregados e matriz sobrescrita. ");    
        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

//--opção 2: Consultar vaga:
static void consultarVaga() {
    System.out.println("Informe a vaga (ex: A1): ");
    String id = sc.nextLine().trim().toUpperCase();
        int[] pos = converterVagaParaIndices(id);
        if (pos == null) return;
        String conteudo = vagas[pos[0]][pos[1]];
        if (conteudo == null) {
            System.out.println("Vaga " + id + " está liberada.");
        } else {
            String[] s = conteudo.split(":");
            String tipo = s[0];
            String hora = formatarDoisDigitos(Integer.parseInt(s[1]));
            String minuto = formatarDoisDigitos(Integer.parseInt(s[2]));
            System.out.println("Vaga " + id + " ocupada. Tipo: " + tipo + " - Entrada: " + hora + ":" + minuto);
        }
    }

    // opção 3: entrada ---
    static void entrada() {
        System.out.print("Tipo de veículo (M - moto / C - carro / V - van): ");
        String tipo = sc.nextLine().trim().toUpperCase();
        if (!(tipo.equals("M") || tipo.equals("C") || tipo.equals("V"))) {
            System.out.println("Tipo inválido. Retornando ao menu.");
            return;
        }
        // hora e minuto de entrada
        System.out.print("Hora de entrada (0-23): ");
        int he;
        try { he = Integer.parseInt(sc.nextLine()); if (he < 0 || he > 23) throw new Exception(); }
        catch (Exception e) { System.out.println("Hora inválida. Retornando."); return; }
        System.out.print("Minuto de entrada (0-59): ");
        int me;
        try { me = Integer.parseInt(sc.nextLine()); if (me < 0 || me > 59) throw new Exception(); }
        catch (Exception e) { System.out.println("Minuto inválido. Retornando."); return; }

        System.out.print("Deseja informar vaga exata (ex A1) / corredor (letra) / nenhuma? (E (exata) /C (corredor /N (não quero)): ");
        String modo = sc.nextLine().trim().toUpperCase();
        switch (modo) {
            case "E": // exata
                System.out.print("Informe a vaga (ex A1): ");
                String id = sc.nextLine().trim().toUpperCase();
                int[] pos = converterVagaParaIndices(id);
                if (pos == null) return;
                if (vagas[pos[0]][pos[1]] != null) {
                    System.out.println("A vaga informada já está ocupada.");
                    return;
                }
                vagas[pos[0]][pos[1]] = tipo + ":" + he + ":" + me;
                System.out.println("Entrada registrada em " + id);
                break;
            case "C": // corredor (letra)
                System.out.print("Informe a letra do corredor (ex A): ");
                String letra = sc.nextLine().trim().toUpperCase();
                if (letra.length() != 1 || letra.charAt(0) < 'A' || letra.charAt(0) > 'Z') {
                    System.out.println("Corredor inválido.");
                    return;
                }
                int linha = letra.charAt(0) - 'A';
                if (linha < 0 || linha >= corredores) {
                    System.out.println("Corredor inexistente.");
                    return;
                }
                boolean entrou = false;
                for (int j = 0; j < vagasPorCorredor; j++) {
                    if (vagas[linha][j] == null) {
                        vagas[linha][j] = tipo + ":" + he + ":" + me;
                        System.out.println("Entrada registrada em " + letra + (j+1));
                        entrou = true;
                        break;
                    }
                }
                if (!entrou) System.out.println("Não há vaga livre no corredor " + letra);
                break;
            case "N": // nenhuma - procurar desde A1
                boolean ok = false;
                for (int i = 0; i < corredores && !ok; i++) {
                    for (int j = 0; j < vagasPorCorredor; j++) {
                        if (vagas[i][j] == null) {
                            vagas[i][j] = tipo + ":" + he + ":" + me;
                            char c = (char)('A' + i);
                            System.out.println("Entrada registrada em " + c + (j+1));
                            ok = true;
                            break;
                        }
                    }
                }
                if (!ok) System.out.println("Estacionamento lotado. Não foi possível registrar entrada.");
                break;
            default:
                System.out.println("Opção inválida. Retornando ao menu.");
        }
    }

    // opção 4: saída ---
    static void saida() {
        System.out.print("Informe a vaga da saída (ex A1): ");
        String id = sc.nextLine().trim().toUpperCase();
        int[] pos = converterVagaParaIndices(id);
        if (pos == null) return;
        String conteudo = vagas[pos[0]][pos[1]];
        if (conteudo == null) {
            System.out.println("Vaga vazia.");
            return;
        }
        String[] s = conteudo.split(":");
        String tipo = s[0];
        int he = Integer.parseInt(s[1]);
        int me = Integer.parseInt(s[2]);

        System.out.println("Entrada registrada: " + formatarDoisDigitos(he) + ":" + formatarDoisDigitos(me) + " (tipo " + tipo + ")");
        // hora/minuto de saída (maior que entrada)
        System.out.print("Hora de saída (0-23): ");
        int hs;
        try { hs = Integer.parseInt(sc.nextLine()); if (hs < 0 || hs > 23) throw new Exception(); }
        catch (Exception e) { System.out.println("Hora inválida."); return; }
        System.out.print("Minuto de saída (0-59): ");
        int ms;
        try { ms = Integer.parseInt(sc.nextLine()); if (ms < 0 || ms > 59) throw new Exception(); }
        catch (Exception e) { System.out.println("Minuto inválido."); return; }

        int minutosEntrada = he * 60 + me;
        int minutosSaida = hs * 60 + ms;
        if (minutosSaida <= minutosEntrada) {
            System.out.println("Horário de saída deve ser posterior ao de entrada.");
            return;
        }
        int durMin = minutosSaida - minutosEntrada;
        int horas = durMin / 60;
        int minutos = durMin % 60;
        System.out.printf("Tempo de permanência: %02d:%02d (total %d minutos)\n", horas, minutos, durMin);

        double valor = calcularValorAPagar(tipo, durMin);
        System.out.println("Valor a pagar: R$" + df.format(valor));

        System.out.print("Deseja liberar a vaga? (S/N): ");
        String resp = sc.nextLine().trim().toUpperCase();
        if (resp.equals("S")) {
            // atualizar financeiro
            switch (tipo) {
                case "M" -> { qtdMotoSaiu++; valorMotoSaiu += valor; }
                case "C" -> { qtdCarroSaiu++; valorCarroSaiu += valor; }
                case "V" -> { qtdVanSaiu++; valorVanSaiu += valor; }
            }
            vagas[pos[0]][pos[1]] = null;
            System.out.println("Vaga " + id + " liberada.");
        } else {
            System.out.println("Vaga mantida ocupada.");
        }
    }

    static double calcularValorAPagar(String tipo, int minutosTotais) {
        double base, add;
        if (tipo.equals("C")) {
            base = base30Carro;
            add = add30Carro;
        } else if (tipo.equals("M")) {
            base = base30Carro * F_MOTO;
            add = add30Carro * F_MOTO;
        } else { // V
            base = base30Carro * F_VAN;
            add = add30Carro * F_VAN;
        }
        if (minutosTotais <= 30) return round2(base);
        int blocos = (int) Math.ceil((minutosTotais - 30) / 30.0);
        return round2(base + blocos * add);
    }

    // --- opção 5: ocupação ---
    static void ocupacao() {
        // mapa
        System.out.println("\nMapa de vagas:");
        // header números
        System.out.print("   ");
        for (int j = 0; j < vagasPorCorredor; j++) {
            System.out.printf("%3d", j+1);
        }
        System.out.println();
        for (int i = 0; i < corredores; i++) {
            char letra = (char)('A' + i);
            System.out.print(letra + "  ");
            for (int j = 0; j < vagasPorCorredor; j++) {
                String v = vagas[i][j];
                if (v == null) System.out.print("  ·");
                else {
                    String t = v.split(":")[0];
                    System.out.print("  " + t);
                }
            }
            System.out.println();
        }

        // resumo
        int totalSlots = corredores * vagasPorCorredor;
        int occMoto = 0, occCarro = 0, occVan = 0;
        for (int i = 0; i < corredores; i++) for (int j = 0; j < vagasPorCorredor; j++) {
            String v = vagas[i][j];
            if (v != null) {
                String t = v.split(":")[0];
                if (t.equals("M")) occMoto++;
                else if (t.equals("C")) occCarro++;
                else if (t.equals("V")) occVan++;
            }
        }
        int ocupadas = occMoto + occCarro + occVan;
        int livres = totalSlots - ocupadas;
        System.out.println();

        imprimirBar("Moto   ", occMoto, totalSlots);
        imprimirBar("Carro  ", occCarro, totalSlots);
        imprimirBar("Van    ", occVan, totalSlots);
        System.out.println("------------------------------------------");
        imprimirBar("Ocupadas", ocupadas, totalSlots);
        imprimirBar("Livres  ", livres, totalSlots);
    }

    static void imprimirBar(String label, int quantidade, int total) {
        double pct = total == 0 ? 0 : (100.0 * quantidade / total);
        int maxBar = 20; // largura da barra
        int filled = (int) Math.round((double)quantidade / total * maxBar);
        if (total == 0) filled = 0;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) bar.append("=");
        for (int i = filled; i < maxBar; i++) bar.append(".");
        System.out.printf("%-8s: %3d – %5.1f%% |%s| (%d vagas de %d)%n",
                label, quantidade, pct, bar.toString(), quantidade, total);
    }

    // --- opção 6: financeiro ---
    static void financeiro() {
        System.out.println("\nRelatório Financeiro (veículos que saíram):");
        System.out.println("Veículo   Quant  Valor(R$)");
        System.out.println("---------------------------");
        System.out.printf("Moto     %6d  %8s%n", qtdMotoSaiu, df.format(valorMotoSaiu));
        System.out.printf("Carro    %6d  %8s%n", qtdCarroSaiu, df.format(valorCarroSaiu));
        System.out.printf("Van      %6d  %8s%n", qtdVanSaiu, df.format(valorVanSaiu));
        System.out.println("---------------------------");
        int totalq = qtdMotoSaiu + qtdCarroSaiu + qtdVanSaiu;
        double totalt = valorMotoSaiu + valorCarroSaiu + valorVanSaiu;
        System.out.printf("Total    %6d  %8s%n", totalq, df.format(totalt));
    }

    // --- opção 7: salvar dados ---
    static void salvarDados() {
        System.out.print("Nome do arquivo para salvar: ");
        String nome = sc.nextLine().trim();
        File f = new File(nome);
        if (f.exists()) {
            System.out.print("Arquivo existe. Deseja sobrescrever? (S/N): ");
            String r = sc.nextLine().trim().toUpperCase();
            if (!r.equals("S")) {
                System.out.println("Operação cancelada.");
                return;
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            // grava somente vagas ocupadas no formato: A2=C:08:15
            for (int i = 0; i < corredores; i++) {
                for (int j = 0; j < vagasPorCorredor; j++) {
                    if (vagas[i][j] != null) {
                        char letra = (char)('A' + i);
                        String id = "" + letra + (j+1);
                        String[] s = vagas[i][j].split(":");
                        String tipo = s[0];
                        String hora = formatarDoisDigitos(Integer.parseInt(s[1]));
                        String minuto = formatarDoisDigitos(Integer.parseInt(s[2]));
                        bw.write(id + "=" + tipo + ":" + hora + ":" + minuto);
                        bw.newLine();
                    }
                }
            }
            System.out.println("Dados salvos em " + nome);
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    // --- opção 8: integrantes ---
    static void mostrarIntegrantes() {
        System.out.println("\nIntegrantes do grupo:");
        System.out.println("- Leticia Haussmann Nor");
    }

    // extras
    static int[] converterVagaParaIndices(String vaga) {
        try {
            vaga = vaga.trim().toUpperCase();
            if (vaga.length() < 2) throw new Exception();
            char letra = vaga.charAt(0);
            if (letra < 'A' || letra > 'Z') throw new Exception();
            int linha = letra - 'A';
            int numero = Integer.parseInt(vaga.substring(1));
            int coluna = numero - 1;
            if (linha < 0 || linha >= corredores || coluna < 0 || coluna >= vagasPorCorredor) {
                System.out.println("Vaga inexistente.");
                return null;
            }
            return new int[]{linha, coluna};
        } catch (Exception e) {
            System.out.println("Formato inválido de vaga. Use exemplo A1.");
            return null;
        }
    }

    static String formatarDoisDigitos(int x) {
        if (x < 10) return "0" + x;
        return String.valueOf(x);
    }

    static double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}

    






