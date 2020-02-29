import java.util.LinkedList;

public class Bender {

    private MapFormer mP;
    private Robot rb = new Robot();
    private LinkedList<Teleport> teleportMapList = new LinkedList<Teleport>();
    private LinkedList<Cell> cellMapList = new LinkedList<Cell>();

    // Constructor: ens passen el mapa en forma d'String

    /**
     *
     * @param map
     */
    public Bender(String map) {

        mP = new MapFormer(map,rb, teleportMapList, cellMapList);

        for (Teleport actualTeleport : teleportMapList) {
            Teleport nearest = defineNearestTp(actualTeleport, teleportMapList);
            actualTeleport.setNearTpX(nearest.getPosX());
            actualTeleport.setNearTpY(nearest.getPosY());

        }
    }

    public Teleport defineNearestTp(Teleport t, LinkedList<Teleport> teleportMapList){

        double result = 999999999;
        Teleport auxTlp = new Teleport(0,0);


        for (Teleport tn: teleportMapList){
            int x = Math.abs(tn.getPosX()-t.getPosX());
            int y = Math.abs(tn.getPosY()-t.getPosY());
            double auxDistance = Math.sqrt((x+x)+(y+y));

            //TODO Teleports at same distance, not working.
            if (result == auxDistance ){
                double currentAngle = Math.pow(Math.tan((t.getPosX()-auxTlp.getPosX())*(t.getPosY()-auxTlp.getPosY())),2);
                double auxAngle = Math.pow(Math.tan((t.getPosX()-tn.getPosX())*(t.getPosY()-tn.getPosY())),2);
                System.out.println("Current Angle: " + currentAngle + "AuxAngle: " + auxAngle);
                if (currentAngle > auxAngle){
                    auxTlp = tn;
                }
            }
            if ((result > auxDistance) && auxDistance != 0){
                result = auxDistance;
                auxTlp = tn;
            }
        }
        return auxTlp;
    }

    /**
     *
     * @return
     */
    public String run() {

        // Si el mapa no es valido devuelve null;
        if (this.mP.getOriginalMap().length()>1) return null;

        char currentPosition = this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getCharacter();
        String result = "";
        int directionCounter = 0;
        char actualDirection = 'S';

        while(currentPosition != '$'){

            // Define la dirección según si el robot ha pisado un inverso o no.
            if (!canMove(this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()], actualDirection)) {
                actualDirection = defineDirection(directionCounter);
                if (actualDirection == '\u0000') return null;
            }
            currentPosition = move(actualDirection);
            int goneByActualCell =this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getGoneBy();
            this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].setGoneBy(goneByActualCell+1);
            result += actualDirection;

            // Si el robot pasa más de 8 veces por la misma celda es un bucle infinito.
            if (this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getGoneBy() > 8 ) return null;

            // Si el robot pisa in Inversor se invierte el estado del robot (atributo inverted).
            if (currentPosition == 'I') this.rb.setInverted(!this.rb.isInverted());

            // Si el robot esta e un Teletransportador accederá al atributo que define cual es el más cercano de ese.
            if (currentPosition == 'T'){
                Teleport tpOut = teleportMapList.get(cellMapList.indexOf(this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()]));
                this.rb.setRobotX(tpOut.getNearTpX());
                this.rb.setRobotY(tpOut.getNearTpY());
            }
        }
        return result;
    }



    /**
     * Esta función define la dirección en la que irá el robot, para definir la dirección comprovará si las prioridades
     * están invertidas y depende de si lo estan o no asignaran la dirección correspondiente al valor numerico de directionCounter.
     * @param directionCounter Asignaran la dirección correspondiente al valor numerico de directionCounter
     * @return Devuelve un caracter el cual representa la dirección en la que se moverá
     */
    public char defineDirection(int directionCounter){
        char actualDirection = ' ';

        if (directionCounter > 4) return '\u0000';

        if (this.rb.isInverted()){
            actualDirection = this.rb.getIvertedDirections()[directionCounter];
        }else{
            actualDirection = this.rb.getDirections()[directionCounter];
        }

        if (!canMove(this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()], actualDirection)){
            actualDirection = defineDirection(directionCounter+1);
        }

        return actualDirection;
    }

    /**
     * Esta función se encarga de que el robot simule moverse en la dirección que lo haria, cuando simula la dirección
     * lo que buscamos es que no se ponga encima de una pared, para esto necesitamos el mapa en el cual nos moveremos,
     * donde se encuentra actualmente el robot y la dirección en la que intenta ir.
     * @param c Representa la celda en la cual se encuentra el robot actualmente.
     * @param direction Este caracter determina en la dirección que provará moverse.
     * @return devuelve un boolean, true si el robot se puede mover o false si es una pared ya que no se podrá mover.
     */
    public boolean canMove(Cell c, char direction){

        char actual = ' ';

        switch (direction){
            case 'S':
                actual = this.mP.getFormedMap()[c.getPosY()+1][c.getPosX()].getCharacter();
                break;
            case 'E':
                actual = this.mP.getFormedMap()[c.getPosY()][c.getPosX()+1].getCharacter();
                break;
            case 'N':
                actual = this.mP.getFormedMap()[c.getPosY()-1][c.getPosX()].getCharacter();
                break;
            case 'W':
                actual = this.mP.getFormedMap()[c.getPosY()][c.getPosX()-1].getCharacter();
                break;
        }

        return actual != '#';
    }

    /**
     * Esta función hará la simulación de que el robot se mueve en la dirección que le decimos, al moverse nos situaremos
     * encima de una celda, la cual tendra un caracter propio, tenemos que comprobar si esa celda es la meta, un teletransportador
     * o un inversor.
     * @param actualDirection Este caracter determina en la dirección que se moverá.
     * @return Devuelve en el caracter que se encuentra actualmente.
     */
    public char move(char actualDirection) {
        switch (actualDirection){
            case 'S':
                this.rb.setRobotY(this.rb.getRobotY()+1);
                this.rb.setRobotX(this.rb.getRobotX());
                break;

            case 'E':
                this.rb.setRobotY(this.rb.getRobotY());
                this.rb.setRobotX(this.rb.getRobotX()+1);
                break;

            case 'N':
                this.rb.setRobotY(this.rb.getRobotY()-1);
                this.rb.setRobotX(this.rb.getRobotX());
                break;

            case 'W':
                this.rb.setRobotY(this.rb.getRobotY());
                this.rb.setRobotX(this.rb.getRobotX()-1);
                break;
        }
        return this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getCharacter();
    }
}

/**
 * El objeto Robot guarda su posición, dirección y su estado para poder llegar a la meta lo iremos trasformando.
 */
class Robot{

    // Robot atributes.
    private int robotX;
    private int robotY;
    private char[] directions = {'S','E','N','W'};
    private char[] ivertedDirections = {'N','W','S','E'};
    private boolean inverted;

    Robot(){
    }

    public void setRobotX(int x){
        this.robotX = x;
    }
    public void setRobotY(int y){
        this.robotY = y;
    }

    public char[] getDirections() {
        return directions;
    }
    public void setDirections(char[] directions) {
        this.directions = directions;
    }

    public void setIvertedDirections(char[] ivertedDirections) {
        this.ivertedDirections = ivertedDirections;
    }
    public char[] getIvertedDirections() {
        return ivertedDirections;
    }

    public int getRobotX(){
        return this.robotX;
    }
    public int getRobotY(){
        return this.robotY;
    }

    public boolean isInverted() {
        return inverted;
    }
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}

class Cell {

    private char character;
    private int posY;
    private int posX;
    private int goneBy;

    Cell(char c, int y, int x){
        this.character = c;
        this.posX = x;
        this.posY = y;
        this.goneBy = 0;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
    public void setPosX(int posX) {
        this.posX = posX;
    }
    public int getPosY() {
        return posY;
    }
    public int getPosX() {
        return posX;
    }

    public void setCharacter(char character) {
        this.character = character;
    }
    public char getCharacter() {
        return character;
    }

    public void setGoneBy(int goneBy) {
        this.goneBy = goneBy;
    }
    public int getGoneBy() {
        return goneBy;
    }
}

class Teleport {

    // Teleport atributes.
    private int posY;
    private int posX;
    private int nearTpY;
    private int nearTpX;

    Teleport(int y, int x){
        this.posX = x;
        this.posY = y;
    }

    public int getNearTpX() {
        return nearTpX;
    }
    public int getNearTpY() {
        return nearTpY;
    }
    public void setNearTpX(int nearTpX) {
        this.nearTpX = nearTpX;
    }
    public void setNearTpY(int nearTpY) {
        this.nearTpY = nearTpY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
    public void setPosX(int posX) {
        this.posX = posX;
    }
    public int getPosY() {
        return posY;
    }
    public int getPosX() {
        return posX;
    }

}

class MapFormer{

    // Cell atributes.
    private Cell[][] formedMap;
    private boolean validMap;
    private String originalMap;

    /**
     * Este es el constructor de MapFormer, dentro del constructor asignaremos a cada posición de un array bidimensional
     * un objeto Celda, además si encontramos el robot 'X' le asignaremos su posición y tambien crearemos un objeto Teleport
     * cada vez que encontremos una 'T' en el mapa, este objeto Teleport tiene las cordenadas que representan en el mapa.
     * @param map És el mapa que nos pasan en forma de String.
     * @param rb Devuelve el mapa formado.
     */
    MapFormer(String map, Robot rb, LinkedList<Teleport> teleportMap, LinkedList<Cell> cellMap){

        // Añadimos un "\n" para poder usar la funcion split.
        map = map + "\n";
        originalMap = "T";

        //Divide el String y lo mete en un Array dividiendo por los \n
        String[] arr = map.split("\n");
        formedMap = new Cell[arr.length][countColumns(map)];

        // Comprueba que el mapa sea valido.
        if(!mapIsValid(map)){
            rb.setRobotY(1);
            rb.setRobotX(1);
            validMap = false;
            originalMap = map;

        }

        // Recorre el String copiando caracter a caracter.
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length(); j++) {
                formedMap[i][j] = new Cell(arr[i].charAt(j),i ,j);

                // Crea el objeto Robot y le da su posición de inicio, cuenta cuantos robots hay.
                if (arr[i].charAt(j) == 'X'){
                   rb.setRobotY(i);
                   rb.setRobotX(j);
                }

                // Añade los teletransportadores a una lista.
                if (arr[i].charAt(j) == 'T'){
                    teleportMap.add(new Teleport(i,j));
                    cellMap.add(formedMap[i][j]);
                }
            }
        }
    }

    public boolean mapIsValid(String map){

        int robotCounter = 0;
        int goalCounter = 0;
        int teleportCounter = 0;

        for (int i = 0; i < map.length(); i++) {

            if (map.charAt(i) == 'X') robotCounter++;

            if (map.charAt(i) == '$') goalCounter++;

            if (map.charAt(i) == 'T') teleportCounter++;

            // Si hay mas de 1 robot o 1 meta el mapa ya no es valido, no hace falta seguir comprobando el mapa.
            if (robotCounter > 1|| goalCounter > 1) return false;
        }
        // Solo puede haber 1 robot, 1 meta y tiene que haber 0 o mas de 1 teleport.
        return robotCounter == 1 && goalCounter == 1 && teleportCounter != 1;
    }

    public void setFormedMap(Cell[][] formedMap) {
        this.formedMap = formedMap;
    }
    public Cell[][] getFormedMap() {
        return formedMap;
    }

    public String getOriginalMap() {
        return originalMap;
    }
    public void setOriginalMap(String originalMap) {
        this.originalMap = originalMap;
    }

    public void setValidMap(boolean validMap) {
        this.validMap = validMap;
    }
    public boolean isValidMap() {
        return validMap;
    }

    /**
     * Cuenta cual es la fila con el mayor numero de caracteres en el string para poder hacer el array de ese tamño.
     * @param str És el mapa que nos pasan en forma de String.
     * @return Devuelve el numero máximo de caracteres de entre todas las filas.
     */
    public int countColumns(String str){
        int columns = 0;

        for (int i = 0, aux = 0; i < str.length(); i++) {
            if (str.charAt(i) != '\n'){
                aux++;
            }else{
                if (aux > columns){
                    columns = aux;
                }
                aux=0;
            }
        }
        return columns;
    }

    /**
     * Esta función hace Override a el método toString, se ha implementado para poder hacer print del mapa.
     * @return Devuelve un String con el mapa para poder imprimirlo en pantalla.
     */
    @Override
    public String toString(){
        StringBuilder strMap = new StringBuilder();
        // Recorre el mapa.
        for (Cell[] cells : this.formedMap) {
            for (int j = 0; j < this.formedMap[0].length; j++) {
                // Coje los valores de cada posicion del mapa y los pone en un String.
                strMap.append(cells[j].getCharacter());
            }
            strMap.append('\n');
        }
        return strMap.toString();
    }
}






