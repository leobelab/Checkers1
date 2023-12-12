
package edu.upc.epsevg.prop.checkers;

import edu.upc.epsevg.prop.checkers.GameStatus;

/**
 *
 * @author Usuari
 */
public class ElMeuStatus extends GameStatus {
    
    public ElMeuStatus(int [][] tauler){
        super(tauler);
    }
    
     public ElMeuStatus(GameStatus gs){
        super(gs);
    }
    //Si volem fer el getHash ho fem millor aquí per no embrutar el codi.
}
