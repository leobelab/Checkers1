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
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Aquesta classe representa un jugador que utilitza l'algorisme MiniMax per
 * prendre decisions en el joc de Checkers. Implementa les interfícies IPlayer i
 * IAuto.
 *
 * @author Leo Benítez Labit
 * @author Pol Laguna Soto
 */
public class PlayerMiniMax implements IPlayer, IAuto {

    private String name;
    private GameStatus s;
    private int profunditat=8;
    public static PlayerType playerteu;
    public static PlayerType playeradversari;
    private int nodes_explorats = 0;

    /**
    * Nom del jugador.
    * 
    * @param name El nom del jugador Minimax.
    */
    public PlayerMiniMax(String name) {
        this.name = name;
    }

    /**
    * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
    * de joc.
    */
    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
    * Decideix el moviment a executar del jugador donat un tauler.
    * 
    *
    * @param s Tauler i estat actual de joc.
    * @return el moviment que fa el jugador.
    */
    @Override
    public PlayerMove move(GameStatus s) {
        Date start = new Date();
        playerteu = s.getCurrentPlayer();
        playeradversari = PlayerType.opposite(playerteu);
        List<Point> millor_jugada = minMax(s);
        
        Date end = new Date();
        long duration = end.getTime() - start.getTime();
        System.out.println("El moviment ha durat: " + duration + " milisegons en calcular-se!");
        return new PlayerMove( millor_jugada, nodes_explorats, profunditat, SearchType.MINIMAX);  
        
    }
    

    /**
    * Funció simple per aconseguir el nom del jugador.
    * 
    * @return el nom del jugador.
    */
    @Override
    public String getName() {
        return "PlayerMiniMax";
    }

    
    /**
    * Implementa l'algorisme MiniMax per trobar el millor moviment en el tauler
    * actual.
    *
    * @param s Tauler i estat actual de joc.
    * @return La millor seqüència de moviments.
    */
    private List<Point> minMax(GameStatus s) {
        int costActual = -20001;
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
    * Calcula el valor mínim en un estat del joc amb la recursivitat.
    *
    * @param s Tauler i estat actual de joc.
    * @param depth La profunditat que ens queda per explorar.
    * @param alpha Límit inferior de la poda alfa-beta.
    * @param beta Límit superior de la poda alfa-beta.
    * @return El valor mínim en l'estat actual joc.
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
        // Itera a partir de les possibles jugades que es poden realitzar al tauler actual.
        for (int i = 0; i < lol.size() && !aturat; i++) {
            GameStatus aux = new GameStatus(s);
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
    * Calcula el valor màxim en un estat del joc amb la recursivitat.
    * 
    * @param s Tauler i estat actual de joc.
    * @param depth La profunditat que ens queda per explorar.
    * @param alpha Límit inferior de la poda alfa-beta.
    * @param beta Límit superior de la poda alfa-beta.
    * @return El valor màxim en l'estat actual joc.
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
        // Itera a partir de les possibles jugades que es poden realitzar al tauler actual.
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
    
    /**
    * Converteix la llista de nodes amb els moviments en una llista de llista de
    * punts amb les jugades.
    * 
    * @param moves Llista amb tots els possibles moviments d'un jugador.
    * @return Lista de llistes de punts que guarden totes les jugades que pot fer.
    */
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
    
    /**
    * Converteix la llista de nodes amb els moviments en una llista de llista de
    * punts amb les jugades.
    * 
    * @param node MoveNode on tenim els possibles moviments d'una peça.
    * @param lp Llista de punts on guardarem el camí que es va formant en una tirada.
    * @param lol_aux Llista de llista de punts on guardarem totes les lp quan estiguin
    * formades.
    */
    private void ds( MoveNode node, List<Point> lp, List<List<Point>> lol_aux){
        
        lp.add(node.getPoint());
        if(!node.getChildren().isEmpty()) {
            ds(node.getChildren().get(0), new ArrayList<>(lp), lol_aux);
            if(node.getChildren().size() > 1) {
                ds(node.getChildren().get(1), new ArrayList<>(lp), lol_aux);
            }
        }
        else {
            lol_aux.add(lp);
        }
    } 
    
    /**
    * Calcula el nombre de fitxes d'un jugador.
    * 
    * @param s Tauler i estat actual de joc.
    * @param player Jugador sobre el qual realitzarem els calculs.
    * @return El nombre de fitxes del jugador player.
    */
    private static int n_fitxes (GameStatus s, PlayerType player){ 
        
        int n_fitxes = 0;
        for(int i = 0; i < s.getSize(); i++) {
            for(int j = 0; j < s.getSize(); j++){
                if(s.getPos(i, j).getPlayer() == player) {
                    ++n_fitxes;
                }
            }
        }
        return n_fitxes;
    }
    
    /**
    * Calcula el valor heurístic basat en diferents paràmetres.
    * 
    * @param s Tauler i estat actual de joc.
    * @return El valor heurístic segons el GameStatus donat.
    */
    public int heuristica (GameStatus s){
        
        int h=0;
        nodes_explorats++;
        int diferencia_fitxes = nombre_fitxes(s, playerteu) - nombre_fitxes(s, playeradversari);
        int diferencia_peons_segurs = nombre_fitxes_segures(s, playerteu) - nombre_fitxes_segures(s, playeradversari);
        int diferencia_moveable_fitxes = nombre_moveable_fitxes(s, playerteu) - nombre_moveable_fitxes(s, playeradversari);
        //int diferencia_promotion_line = nombre_promotion_line(s, playerteu) - nombre_promotion_line(s, playeradversari);
        
        h += diferencia_fitxes + diferencia_peons_segurs + diferencia_moveable_fitxes;
        return h;
    }
    
    /**
    * Calcula part del valor heurístic basat en el nombre de peons i reines d'un jugador, 
    * afegint valor a aquests valors si tenim menys de 6 fites totals.
    * 
    * @param s Tauler i estat actual de joc.
    * @param player Jugador sobre el qual realitzarem els calculs.
    * @return El valor heurístic sobre el nombre de fixtes del jugador player.
    */
    private static int nombre_fitxes (GameStatus s, PlayerType player){ 
        int n = 0;
        int meitat = s.getSize()/2;
        boolean menysDeSis = false;
        if(n_fitxes(s, player) < 6) menysDeSis = true;
        for(int i = 0; i < s.getSize(); i++) {
            for(int j = 0; j < s.getSize(); j++){
                if(s.getPos(i, j).getPlayer() == player) {
                    ++n;
                    if(s.getPos(i, j).isQueen()) ++n;
                    if(menysDeSis) {
                        n += 2;
                        if(s.getPos(i, j).isQueen()) n += 2;
                    }
                }
            }
        }
        return n;
    }
    
    /**
    * Calcula part del valor heurístic basat en el nombre de peons i reines segures 
    * d'un jugador (amb aixó ens referim a que no tingui cap fitxa oposada que li pugui 
    * matar en la següent jugada), afegint valor a aquests valors si tenim menys 
    * de 6 fitxes totals.
    * 
    * @param s Tauler i estat actual de joc.
    * @param player Jugador sobre el qual realitzarem els calculs.
    * @return El valor heurístic sobre el nombre de fixtes segures del jugador player.
    */
    private static int nombre_fitxes_segures (GameStatus s, PlayerType player){ 
        int n = 0;
        int c;
        boolean segur;
        boolean menysDeSis = false;
        if(n_fitxes(s, player) < 6) menysDeSis = true;
        PlayerType adversari = PlayerType.opposite(player);
        for(int i = 0; i < s.getSize(); i++) {
            for(int j = 0; j < s.getSize(); j++){
                if(s.getPos(i, j).getPlayer() == player) {
                    int dir = s.getYDirection(player);
                    c = i+1;
                    segur = true;
                    if(c < s.getSize() && (c-2) > 0 && (j-dir) >= 0 && (j-dir) < s.getSize() && (j+dir) >=0 && (j+dir) < s.getSize()) {
                        if(s.getPos(i+1, j+dir).getPlayer() == adversari ) {
                                if(s.getPos(i-1, j-dir) == EMPTY) segur = false;
                        }
                        if(segur && s.getPos(i-1, j+dir).getPlayer() == adversari) {
                            if(s.getPos(i+1, j-dir) == EMPTY) segur = false;
                        }
                        if(segur && s.getPos(i+1, j-dir).getPlayer() == adversari && s.getPos(i-dir,j+1).isQueen()){
                                if(s.getPos(i-1, j+dir) == EMPTY) segur = false;
                        }
                        if(segur && s.getPos(i-1, j-dir).getPlayer() == adversari && s.getPos(i-dir,j-1).isQueen()){
                            if(s.getPos(i+1, j+dir) == EMPTY) segur = false;
                        }
                    }
                    if(menysDeSis) n += 3;
                    else if(segur) {
                        n += 4;
                        if(s.getPos(i, j).isQueen()) ++n;
                    }
                }
                
            }
        }
        return n;
    }

    /**
    * Calcula part del valor heurístic basat en el nombre de peons i reines que es
    * poden moure, a no ser que tinguem menys de sis fitxes.
    * 
    * @param s Tauler i estat actual de joc.
    * @param player Jugador sobre el qual realitzarem els calculs.
    * @return El valor heurístic sobre el nombre de fixtes segures del jugador player.
    */
    private static int nombre_moveable_fitxes (GameStatus s, PlayerType player){ 
        int n = 0;
        int c;
        boolean moveable;
        boolean menysDeSis = false;
        if(n_fitxes(s, player) < 6) menysDeSis = true;
        PlayerType adversari = PlayerType.opposite(player);
        for(int i = 0; i < s.getSize(); i++) {
            for(int j = 0; j < s.getSize(); j++){
                if(s.getPos(i, j).getPlayer() == player) {
                    int dir = s.getYDirection(player);
                    c = i+1;
                    moveable = false;
                    if((j+dir) >=0 && (j+dir) < s.getSize()) {
                        if(c < s.getSize() && s.getPos(i+1, j+dir) == EMPTY) moveable = true; 
                        else if ((c-2) > 0 && s.getPos(i-1, j+dir) == EMPTY) moveable = true;
                    }
                    if(!moveable && s.getPos(i, j).isQueen() && (j-dir) >= 0 && (j-dir) < s.getSize()) {
                        if(c < s.getSize() && s.getPos(i+1, j-dir) == EMPTY) moveable = true; 
                        else if ((c-2) > 0 && s.getPos(i-1, j-dir) == EMPTY) moveable = true;
                    }
                    if(!menysDeSis) {
                        if(moveable) n += 1;
                        if(s.getPos(i, j).isQueen() && moveable) ++n;
                    }

                }
                
            }
        }
        return n;
    }
    
     /*private static int nombre_promotion_line(GameStatus s, PlayerType player){ 
        int n = 0;
        int i = 0;
        if(player == PLAYER2) i = s.getSize()-1;
        for(int j = 0; j < s.getSize(); j++) {
            if(s.getPos(i, j) == EMPTY) ++n;
                
        }
        return n;
    }*/

}
