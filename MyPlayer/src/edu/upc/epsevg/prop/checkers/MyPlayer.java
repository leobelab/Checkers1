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
            List<List<Point>> lol_aux = ds(node);
            lol.addAll(lol_aux);

        }
        
        return lol;
       
    }
    
    private List<List<Point>> ds( MoveNode node){
        
        List<List<Point>> lol = new List<List<Point>>();
        
        List<Point> lp = new List<Point>();
        lp.add(node.getPoint());
        if(!node.getChildren().isEmpty()) {
            ds(node.getChildren().get(0));
            if(node.getChildren().size() > 1) {
                ds(node.getChildren().get(1));
            }
        }
        else {
            
        }
        
    }
    
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
