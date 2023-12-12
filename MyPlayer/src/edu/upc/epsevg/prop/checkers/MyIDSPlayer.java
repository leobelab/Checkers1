package edu.upc.epsevg.prop.checkers.players;


import edu.upc.epsevg.prop.checkers.CellType;
import static edu.upc.epsevg.prop.checkers.CellType.EMPTY;
import static edu.upc.epsevg.prop.checkers.CellType.P1;
import static edu.upc.epsevg.prop.checkers.CellType.P1Q;
import static edu.upc.epsevg.prop.checkers.CellType.P2;
import static edu.upc.epsevg.prop.checkers.CellType.P2Q;
import edu.upc.epsevg.prop.checkers.GameStatus;
import edu.upc.epsevg.prop.checkers.IAuto;
import edu.upc.epsevg.prop.checkers.IPlayer;
import edu.upc.epsevg.prop.checkers.MoveNode;
import edu.upc.epsevg.prop.checkers.PlayerMove;
import edu.upc.epsevg.prop.checkers.PlayerType;
import static edu.upc.epsevg.prop.checkers.PlayerType.PLAYER1;
import static edu.upc.epsevg.prop.checkers.PlayerType.PLAYER2;
import edu.upc.epsevg.prop.checkers.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Jugador aleatori
 * @author LBL
 */
public class MyIDSPlayer implements IPlayer, IAuto {

    private String name;
    private GameStatus s;
    private int profunditat=8;
    private PlayerType playerteu;
    private PlayerType playeradversari;


    public MyIDSPlayer(String name) {
        this.name = name;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(GameStatus s) {
        playerteu = s.getCurrentPlayer();
        playeradversari = PlayerType.opposite(playerteu);
        List<Point> millor_jugada = minMax(s);
        //System.out.println("Estats explorats: " + nodesExplorats);
        
        /*Random rand = new Random();
        int q = rand.nextInt(moves.size());
        List<Point> points = new ArrayList<>();
        MoveNode node = moves.get(q);
        points.add(node.getPoint());
        
        while(!node.getChildren().isEmpty()) {
            int c = rand.nextInt(node.getChildren().size());
            node = node.getChildren().get(c);
            points.add(node.getPoint());
        }*/
        return new PlayerMove( millor_jugada, 0L, 8, SearchType.MINIMAX);         
        
        /*
         List<MoveNode> moves =  s.getMoves();

        Random rand = new Random();
        int q = rand.nextInt(moves.size());
        List<Point> points = new ArrayList<>();
        MoveNode node = moves.get(q);
        points.add(node.getPoint());
        
        while(!node.getChildren().isEmpty()) {
            int c = rand.nextInt(node.getChildren().size());
            node = node.getChildren().get(c);
            points.add(node.getPoint());
        }
        */
        
    }
    

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Random(" + name + ")";
    }

    
    /**
     * Implementa l'algorisme MiniMax per trobar el millor moviment en el tauler
     * actual.
     *
     * @param t El tauler actual del joc.
     * @return La columna on es realitzarà el millor moviment.
     */
    private List<Point> minMax(GameStatus s) {
        int costActual = -20000;
        List<MoveNode> moves =  s.getMoves();
        List<Point> points = new ArrayList<>();
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        List<List<Point>> lol = calmov(moves);
        for (int i = 0; i <  lol.size(); i++) {
            GameStatus aux = new GameStatus(s);
            List<Point> intent = lol.get(i);
            aux.movePiece(intent);

            int valorHeuristic = minValor(aux,profunditat-1, alpha, beta);
            if (valorHeuristic > costActual) {
                costActual = valorHeuristic;
                points = intent;
            }
            alpha = Math.max(alpha, costActual);
        }
        
        return points;
    }
    
    /**
     * Calcula el valor mínim en un estat del joc utilitzant recursió.
     *
     * @param t El tauler actual del joc.
     * @param colTirada La columna de la tirada actual.
     * @param depth La profunditat restant per explorar.
     * @param alpha Límit inferior actual per a la poda alfa-beta.
     * @param beta Límit superior actual per a la poda alfa-beta.
     * @return El valor mínim calculat per a l'estat actual.
     */
    private int minValor(GameStatus s, int depth, int alpha, int beta) {
        int millorMoviment = 20000;

        // Verifica si la solució ja s'ha trobat per al jugador màxim.
        if (s.checkGameOver()) {
            if(s.GetWinner() == playerteu) return millorMoviment;
            else return 0; //CAS D'EMPAT
        }
        
        // Verifica si s'ha arribat a la profunditat màxima o si no es poden fer més moviments.
        if (depth == 0) {
            return heuristica(s);
        }
        
        boolean aturat = false;

        List<MoveNode> moves =  s.getMoves();
        List<Point> points = new ArrayList<>();
        List<List<Point>> lol = calmov(moves);        
        // Itera a través de les columnes del tauler per a les possibles moviments.
        for (int i = 0; i < lol.size() && !aturat; i++) {
            GameStatus aux = new GameStatus(s);

            // Verifica si el moviment és possible.
            
                List<Point> intent = lol.get(i);
                aux.movePiece(intent);
                int fhMax = maxValor(aux, depth - 1, alpha, beta);

                // Actualitza el millor moviment amb el valor mínim.
                millorMoviment = Math.min(millorMoviment, fhMax);

                // Realitza la poda alfa-beta.
                beta = Math.min(millorMoviment, beta);

                // Verifica si es pot parar la cerca actual amb la poda alfa-beta.
                if (alpha >= beta) {
                    aturat = true;
                }
        }

        return millorMoviment;
    }
    
        /**
     * Calcula el valor màxim en un estat del joc utilitzant recursió.
     *
     * @param t El tauler actual del joc.
     * @param colTirada La columna de la tirada actual.
     * @param depth La profunditat restant per explorar.
     * @param alpha Límit inferior actual per a la poda alfa-beta.
     * @param beta Límit superior actual per a la poda alfa-beta.
     * @return El valor màxim calculat per a l'estat actual.
     */
    private int maxValor(GameStatus s, int depth, int alpha, int beta) {
        int valorHeuristic = -20000;

        // Verifica si la solució ja s'ha trobat per al jugador mínim.
        if (s.checkGameOver()) {
            if(s.GetWinner() == playeradversari) return valorHeuristic;
            else return 0; //CAS D'EMPAT
        }

        // Verifica si s'ha arribat a la profunditat màxima o si no es poden fer més moviments.
        if (depth == 0) {
            return heuristica(s);
        }

        boolean aturat = false;

        List<MoveNode> moves =  s.getMoves();
        List<Point> points = new ArrayList<>();
        List<List<Point>> lol = calmov(moves);
        // Itera a través de les columnes del tauler per a les possibles moviments.
        for (int i = 0; i < lol.size() && !aturat; i++) {
            GameStatus aux = new GameStatus(s);

            List<Point> intent = lol.get(i);
            aux.movePiece(intent);
            int fhMin = minValor(aux, depth - 1, alpha, beta);

            // Actualitza el valor heurístic amb el valor màxim.
            valorHeuristic = Math.max(valorHeuristic, fhMin);

            // Realitza la poda alfa-beta si està habilitada.

            alpha = Math.max(valorHeuristic, alpha);
            // Verifica si es pot parar la cerca actual amb la poda alfa-beta.
            if (alpha >= beta) {
                aturat = true;
            }
        }

        return valorHeuristic;
    }
    
    private List<List<Point>> calmov( List<MoveNode> moves){
        
        List<List<Point>> lol = new ArrayList<List<Point>>();
        
        for(int i = 0; i < moves.size(); i++){
            
            MoveNode node = moves.get(i);
            List<Point> lp = new ArrayList<Point>();
            List<List<Point>> lol_aux = new ArrayList<List<Point>>();
            ds(node, lp, lol_aux);
            lol.addAll(lol_aux);

        }
        
        return lol;
       
    }
    
    private void ds( MoveNode node, List<Point> lp, List<List<Point>> lol_aux){
        
        //List<List<Point>> lol = new List<List<Point>>();
        
        //List<Point> lp = new List<Point>();
        lp.add(node.getPoint());
        if(!node.getChildren().isEmpty()) {
            ds(node.getChildren().get(0), new ArrayList<>(lp), lol_aux);
            if(node.getChildren().size() > 1) {
                ds(node.getChildren().get(1), new ArrayList<>(lp), lol_aux);
            }
        }
        else {
            lol_aux.add(lp); //podemos lol_aux.add(new ArrayList<>(lp))
        }
    } 
    
    private int heuristica (GameStatus s){
        
        int h=0;
        
        int diferencia_fitxes = nombre_fitxes(s, playerteu) - nombre_fitxes(s, playeradversari);
        int diferencia_peons_segurs = nombre_fitxes_segures(s, playerteu) - nombre_fitxes_segures(s, playeradversari);
        int diferencia_moveable_peons = nombre_moveable_fitxes(s, playerteu) - nombre_moveable_fitxes(s, playeradversari);
        int diferencia_promotion_line = nombre_promotion_line(s, playerteu) - nombre_promotion_line(s, playeradversari);
        
        h += diferencia_fitxes + diferencia_peons_segurs + diferencia_moveable_peons;
        return h;
    }
    
    private int nombre_fitxes (GameStatus s, PlayerType player){ 
        int n = 0;
        for(int i = 0; i < s.getSize(); i++) {
            for(int j = 0; j < s.getSize(); j++){
                if(s.getPos(i, j).getPlayer() == player) {
                    ++n;
                    if(s.getPos(i, j).isQueen()) ++n;
                }
                
            }
        }
        return n;
    }
    
    private int nombre_fitxes_segures (GameStatus s, PlayerType player){ 
        int n = 0;
        int c;
        boolean segur;
        PlayerType adversari = PlayerType.opposite(player);
        for(int i = 0; i < s.getSize(); i++) {
            for(int j = 0; j < s.getSize(); j++){
                if(s.getPos(i, j).getPlayer() == player) {
                    int dir = s.getYDirection(player);
                    c = j+1;
                    segur = true;
                    if(c < s.getSize() && (c-2) > 0 && (i-dir) >= 0 && (i-dir) < s.getSize() && (i+dir) >=0 && (i+dir) < s.getSize()) {
                        if(s.getPos(i+dir, j+1).getPlayer() == adversari ) {
                            if(s.getPos(i-dir, j-1) == EMPTY) segur = false;
                        }
                        if(segur && s.getPos(i+dir, j-1).getPlayer() == adversari) {
                            if(s.getPos(i-dir, j+1) == EMPTY) segur = false;
                        }
                        if(segur && s.getPos(i-dir, j+1).getPlayer() == adversari && s.getPos(i-dir,j+1).isQueen()){
                            if(s.getPos(i+dir, j-1) == EMPTY) segur = false;
                        }
                        if(segur && s.getPos(i-dir, j-1).getPlayer() == adversari && s.getPos(i-dir,j-1).isQueen()){
                            if(s.getPos(i+dir, j+1) == EMPTY) segur = false;
                        }
                    }
                    if(segur) n += 2;
                    if(s.getPos(i, j).isQueen() && segur) ++n;
                }
                
            }
        }
        return n;
    }

    private int nombre_moveable_fitxes (GameStatus s, PlayerType player){ 
        int n = 0;
        int c;
        boolean moveable;
        PlayerType adversari = PlayerType.opposite(player);
        for(int i = 0; i < s.getSize(); i++) {
            for(int j = 0; j < s.getSize(); j++){
                if(s.getPos(i, j).getPlayer() == player) {
                    int dir = s.getYDirection(player);
                    c = j+1;
                    moveable = false;
                    if((i+dir) >=0 && (i+dir) < s.getSize()) {
                        if(c < s.getSize() && s.getPos(i+dir, j+1) == EMPTY) moveable = true; 
                        else if ((c-2) > 0 && s.getPos(i+dir, j-1) == EMPTY) moveable = true;
                    }
                    if(!moveable && s.getPos(i, j).isQueen() && (i-dir) >= 0 && (i-dir) < s.getSize()) {
                        if(c < s.getSize() && s.getPos(i-dir, j+1) == EMPTY) moveable = true; 
                        else if ((c-2) > 0 && s.getPos(i-dir, j-1) == EMPTY) moveable = true;
                    }
                    if(moveable) n += 3;
                    if(s.getPos(i, j).isQueen() && moveable) ++n;
                }
                
            }
        }
        return n;
    }
    
     private int nombre_promotion_line(GameStatus s, PlayerType player){ 
        int n = 0;
        int i = 0;
        if(player == PLAYER2) i = s.getSize()-1;
        for(int j = 0; j < s.getSize(); j++) {
            if(s.getPos(i, j) == EMPTY) ++n;
                
        }
        return n;
    }
    
    
    
    
    
    /**
     * Calcula el valor mínim en un estat del joc utilitzant recursió.
     *
     * @param t El tauler actual del joc.
     * @param colTirada La columna de la tirada actual.
     * @param depth La profunditat restant per explorar.
     * @param alpha Límit inferior actual per a la poda alfa-beta.
     * @param beta Límit superior actual per a la poda alfa-beta.
     * @return El valor mínim calculat per a l'estat actual.
     */
    /*private int minValor(Tauler t, int colTirada, int depth, int alpha, int beta) {
        int millorMoviment = 10000;

        // Verifica si la solució ja s'ha trobat per al jugador màxim.
        if (t.solucio(colTirada, jugadorMaxim)) {
            return millorMoviment;
        }

        // Verifica si s'ha arribat a la profunditat màxima o si no es poden fer més moviments.
        if (depth == 0 || !t.espotmoure()) {
            return heuristica(t);
        }

        boolean parar_busca = false;

        // Itera a través de les columnes del tauler per a les possibles moviments.
        for (int i = 0; i < t.getMida() && !parar_busca; i++) {
            Tauler aux = new Tauler(t);

            // Verifica si el moviment és possible.
            if (aux.movpossible(i)) {
                aux.afegeix(i, jugadorMinim);
                int fhMax = maxValor(aux, i, depth - 1, alpha, beta);

                // Actualitza el millor moviment amb el valor mínim.
                millorMoviment = Math.min(millorMoviment, fhMax);

                // Realitza la poda alfa-beta si està habilitada.
                if (poda) {
                    beta = Math.min(millorMoviment, beta);

                    // Verifica si es pot parar la cerca actual amb la poda alfa-beta.
                    if (alpha >= beta) {
                        parar_busca = true;
                    }
                }
            }
        }

        return millorMoviment;
    }
    */
    
    
    
    
    
    
    

}
