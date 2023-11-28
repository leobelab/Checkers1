package edu.upc.epsevg.prop.checkers.players;


import edu.upc.epsevg.prop.checkers.CellType;
import edu.upc.epsevg.prop.checkers.GameStatus;
import edu.upc.epsevg.prop.checkers.IAuto;
import edu.upc.epsevg.prop.checkers.IPlayer;
import edu.upc.epsevg.prop.checkers.MoveNode;
import edu.upc.epsevg.prop.checkers.PlayerMove;
import edu.upc.epsevg.prop.checkers.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Jugador aleatori
 * @author LBL
 */
public class MyPlayer implements IPlayer, IAuto {

    private String name;
    private GameStatus s;
    private int profunditat=4;

    public MyPlayer(String name) {
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
        return new PlayerMove( points, 0L, 0, SearchType.RANDOM);         
        
        /*
        
        int millorMove = minMax(s);
        System.out.println("Estats explorats: " + nodesExplorats);
        return millorMove;
        
        */
        
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
    
    private List<List<Point>> calmov( List<MoveNode> moves){
        
        List<List<Point>> lol = new List<List<Point>>();
        
        for(int i = 0; i < moves.size(); i++){
            
            MoveNode node = moves.get(i);
            List<Point> lp = new List<Point>();
            List<List<Point>> lol_aux = ds(node, lp);
            lol.addAll(lol_aux);

        }
        
        return lol;
       
    }
    
    private List<List<Point>> ds( MoveNode node, List<Point> lp){
        
        //List<List<Point>> lol = new List<List<Point>>();
        
        //List<Point> lp = new List<Point>();
        lp.add(node.getPoint());
        if(!node.getChildren().isEmpty()) {
            ds(node.getChildren().get(0));
            if(node.getChildren().size() > 1) {
                ds(node.getChildren().get(1));
            }
        }
        else {
            afegir lp a llista general
        }
        return llistageneral;
    }
    
    //funciones min max
    
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
    /*private int maxValor(Tauler t, int colTirada, int depth, int alpha, int beta) {
        int valorHeuristic = -10000;

        // Verifica si la solució ja s'ha trobat per al jugador mínim.
        if (t.solucio(colTirada, jugadorMinim)) {
            return valorHeuristic;
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
                aux.afegeix(i, jugadorMaxim);
                int fhMin = minValor(aux, i, depth - 1, alpha, beta);

                // Actualitza el valor heurístic amb el valor màxim.
                valorHeuristic = Math.max(valorHeuristic, fhMin);

                // Realitza la poda alfa-beta si està habilitada.
                if (poda) {
                    alpha = Math.max(valorHeuristic, alpha);

                    // Verifica si es pot parar la cerca actual amb la poda alfa-beta.
                    if (alpha >= beta) {
                        parar_busca = true;
                    }
                }
            }
        }

        return valorHeuristic;
    }
    */
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

    
    private int heuristica (Gamestatus s){
        
        int h=0;
        int peo=0;
        
        for(int f=0; f < s.getSize(); f++){
            for(int c=0;c < s.getSize();c++){
                
                CellType ct = new CellType();
                ct = s.getPos(c,f);
                
                if(s.getCurrentPlayer()== PLAYER1){
                    if(ct == P1){
                        peo++;
                    }
                    if(ct == P2){
                        peo--;
                    }
                    
                }
                if(s.getCurrentPlayer()== PLAYER2){
                    
                }
                
                
            }
        }
        
        return h;
    }
    

}
