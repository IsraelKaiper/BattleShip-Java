import java.util.Random;
import java.util.Scanner;

public class BattleShipGame {
    // Tamanho do mapa.
    private static final int MAP_SIZE = 10;
    // Número de navios.
    private static final int NUM_SHIPS = 10;
    // Tamanho dos navios.
    private static final int[] SHIP_LENGTHS = { 4, 3, 3, 2, 2, 2, 1, 1, 1, 1 };

    // Símbolos usados.
    private static final char WATER = '~';
    private static final char SHIP = 'S';
    private static final char HIT = 'X';
    private static final char MISS = 'O';

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Batalha Naval");

        System.out.println("Escolha o modo de jogo:");
        System.out.println("1 - Jogador vs. Computador");
        System.out.println("2 - Jogador vs. Jogador");

        int gameMode = scanner.nextInt();

        // Criação dos mapas para cada jogador.
        char[][] player1Map = createMap();
        char[][] player2Map = createMap();

        // Alocação dos navios.
        allocateShips(player1Map, gameMode);
        if (gameMode != 1) { // Se o gamemode for diferente de 1 vai ter q alocar os barcos do player 2 tambem.
            allocateShips(player2Map, gameMode);
        } else {
            allocateShipsRandomly(player2Map);
        }

        boolean isGameOver = false;
        boolean isPlayer1Turn = true;

        while (!isGameOver) { // enquanto o jogo estiver nao for terminado
            char[][] currentMap;
            char[][] opponentMap;

            if (isPlayer1Turn) { //turno do player 1 mapa do player 1
                System.out.println("Jogador 1, é a sua vez!");
                currentMap = player1Map;
                opponentMap = player2Map;
            } else { // turno do outro player mapa do outro player
                System.out.println("Jogador 2, é a sua vez!");
                currentMap = player2Map; 
                opponentMap = player1Map;
            }

            System.out.println("Seu mapa:");
            printMap(currentMap, true); // Mostra apenas seus barcos

            System.out.println("Mapa do oponente:");
            printMap(opponentMap, !isPlayer1Turn); // Mostra os barcos do oponente pro jogador 2

            int[] targetCoordinates;

            if (isPlayer1Turn || gameMode == 2) {
                targetCoordinates = getTargetCoordinates(scanner); // se o modo for o 2 vai ter q pedir as coordenadas
            } else {
                targetCoordinates = getRandomTargetCoordinates(); // se for o outro modo as coordenadas sao pegas aleatoriamente
                System.out.println("O computador escolheu as coordenadas: (" + targetCoordinates[0] + ", " + targetCoordinates[1] + ")");
            }

            int row = targetCoordinates[0];
            int col = targetCoordinates[1];

            char target = opponentMap[row][col];
            if (target == SHIP) {
                System.out.println("Acertou um navio!");
                opponentMap[row][col] = HIT;

                // Atualiza o mapa do oponente no mapa do jogador atual
                if (isPlayer1Turn) {
                    player2Map[row][col] = HIT;
                } else {
                    player1Map[row][col] = HIT;
                }

                // Verifica se o jogo já terminou
                if (isGameFinished(opponentMap)) {
                    isGameOver = true;
                    System.out.println("Jogador " + (isPlayer1Turn ? "1" : "2") + " venceu!");
                }
            } else if (target == WATER) {
                System.out.println("Acertou na água!");
                opponentMap[row][col] = MISS;

                // Atualiza o mapa do oponente no mapa do jogador atual
                if (isPlayer1Turn) {
                    player2Map[row][col] = MISS;
                } else {
                    player1Map[row][col] = MISS;
                }
            } else {
                System.out.println("Você já atirou nessa posição, tente novamente.");
            }

            isPlayer1Turn = !isPlayer1Turn;
        }
    }

    // Método para alocar os barcos manualmente ou automaticamente
    private static void allocateShips(char[][] map, int gameMode) {
        System.out.println("Alocação dos navios:");

        boolean isManualPlacement = false;
        Scanner scanner = new Scanner(System.in);

        // Verifica se o jogador deseja alocar os barcos manualmente
        if (gameMode != 1) {
            System.out.println("Deseja alocar os barcos manualmente? (S/N)");
            String answer = scanner.nextLine().trim().toUpperCase();

            if (answer.equals("S")) {
                isManualPlacement = true;
            }
        }

        if (isManualPlacement) {
            allocateShipsManually(map, scanner);
        } else {
            allocateShipsRandomly(map);
        }
    }

    // Método para alocar os barcos manualmente
    private static void allocateShipsManually(char[][] map, Scanner scanner) {
        for (int shipSize : SHIP_LENGTHS) {
            System.out.println("Aloque um navio de tamanho " + shipSize);
            printMap(map, true); // Mostra apenas seus barcos

            boolean isValidPlacement = false;
            while (!isValidPlacement) {
                System.out.print("Informe a linha (0-9): ");
                int row = scanner.nextInt();

                System.out.print("Informe a coluna (0-9): ");
                int col = scanner.nextInt();

                System.out.print("Informe a direção (0 - horizontal, 1 - vertical): ");
                int direction = scanner.nextInt();

                isValidPlacement = validateShipPlacement(map, row, col, direction, shipSize);

                if (isValidPlacement) {
                    placeShip(map, row, col, direction, shipSize);
                    printMap(map, true); // Mostra apenas seus barcos
                } else {
                    System.out.println("Posição inválida para o navio, tente novamente.");
                }
            }
        }
    }

    // Método para alocar os barcos aleatoriamente
    private static void allocateShipsRandomly(char[][] map) {
        Random random = new Random();

        for (int shipSize : SHIP_LENGTHS) {
            boolean isValidPlacement = false;
            while (!isValidPlacement) {
                int row = random.nextInt(MAP_SIZE);
                int col = random.nextInt(MAP_SIZE);
                int direction = random.nextInt(2); // 0 - horizontal, 1 - vertical

                isValidPlacement = validateShipPlacement(map, row, col, direction, shipSize);

                if (isValidPlacement) {
                    placeShip(map, row, col, direction, shipSize);
                }
            }
        }
    }

    // Método para obter as coordenadas do alvo do jogador
    private static int[] getTargetCoordinates(Scanner scanner) {
        System.out.print("Informe a linha para atacar (0-9): ");
        int row = scanner.nextInt();

        System.out.print("Informe a coluna para atacar (0-9): ");
        int col = scanner.nextInt();

        return new int[] { row, col };
    }

    // Método para obter as coordenadas do alvo aleatoriamente para o computador
    private static int[] getRandomTargetCoordinates() {
        Random random = new Random();
        int row = random.nextInt(MAP_SIZE);
        int col = random.nextInt(MAP_SIZE);
        return new int[] { row, col };
    }

    // Método para validar a alocação do navio
    private static boolean validateShipPlacement(char[][] map, int row, int col, int direction, int shipSize) {
        if (direction == 0) {
            // Verifica se a posição do navio ultrapassa o limite do mapa na horizontal
            if (col + shipSize > MAP_SIZE) {
                return false;
            }

            // Verifica se a posição do navio está ocupada por outro navio na horizontal
            for (int i = col; i < col + shipSize; i++) {
                if (map[row][i] != WATER) {
                    return false;
                }
            }
        } else {
            // Verifica se a posição do navio ultrapassa o limite do mapa na vertical
            if (row + shipSize > MAP_SIZE) {
                return false;
            }

            // Verifica se a posição do navio está ocupada por outro navio na vertical
            for (int i = row; i < row + shipSize; i++) {
                if (map[i][col] != WATER) {
                    return false;
                }
            }
        }

        return true;
    }

    // Método para alocar o navio no mapa
    private static void placeShip(char[][] map, int row, int col, int direction, int shipSize) {
        if (direction == 0) {
            // Alocar o navio na horizontal
            for (int i = col; i < col + shipSize; i++) {
                map[row][i] = SHIP;
            }
        } else {
            // Alocar o navio na vertical
            for (int i = row; i < row + shipSize; i++) {
                map[i][col] = SHIP;
            }
        }
    }

    // Método para imprimir o mapa
    private static void printMap(char[][] map, boolean showShips) {
        System.out.print("   ");
        for (int i = 0; i < MAP_SIZE; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < MAP_SIZE; i++) {
            System.out.print(i + " |");
            for (int j = 0; j < MAP_SIZE; j++) {
                char symbol;
                if (map[i][j] == SHIP && !showShips) {
                    symbol = WATER;
                } else {
                    symbol = map[i][j];
                }
                System.out.print(symbol + " ");
            }
            System.out.println("|");
        }
        System.out.println("   ---------------------");
    }

    // Método para verificar se o jogo terminou
    private static boolean isGameFinished(char[][] map) {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                if (map[i][j] == SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    // Método para criar o mapa vazio
    private static char[][] createMap() {
        char[][] map = new char[MAP_SIZE][MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                map[i][j] = WATER;
            }
        }
        return map;
    }
}
