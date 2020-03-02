import java.util.LinkedList;

public class Bender {

    // Bender run() Attributes.
    private MapFormer mP;
    private Robot rb = new Robot();
    private LinkedList<Teleport> teleportMapList = new LinkedList<>();
    private LinkedList<Cell> cellMapList = new LinkedList<>();

    //Bender bestRun() Attributes.
    private Goal goal = new Goal();

    // Constructor: ens passen el mapa en forma d'String

    /**
     * Esta clase crea el mapa, asigna las posiciones de las celdas, asigna los teletransportadores y calcula su más cercano
     * @param map String de entrada el cual define como será el mapa.
     */
    public Bender(String map) {

        mP = new MapFormer(map,rb, teleportMapList, cellMapList, goal);

        for (Teleport actualTeleport : teleportMapList) {
            Teleport nearest = defineNearestTp(actualTeleport, teleportMapList);
            actualTeleport.setNearTpX(nearest.getPosX());
            actualTeleport.setNearTpY(nearest.getPosY());

        }
    }

    /**
     * Calcula cual es el teletransportador más cercano del teletrasportador que entra por parametro y compara las
     * distancias con cada teletransportador de la lista.
     * @param t Teletransportador al cual se le esta calculando cual es el más cercano
     * @param teleportMapList lista de los teleports a los cuales se puede teletransportar
     * @return devuelve el teletransportador que esta más cerca para guardar sus coordenadas.
     */
    private Teleport defineNearestTp(Teleport t, LinkedList<Teleport> teleportMapList){

        // Temporal, cambiar.
        double result = 999999999;
        Teleport auxTp = new Teleport(0,0);

        for (Teleport tn: teleportMapList){
            if(t != tn){
                int x = Math.abs(t.getPosX()-tn.getPosX());
                int y = Math.abs(t.getPosY()-tn.getPosY());
                double auxDistance = x+y;

                if (result == auxDistance ){
                    //TODO Teleports at same distance, not working.
                    double currentAngle =  90 - Math.toDegrees(Math.atan2((t.getPosY()-auxTp.getPosY()),(t.getPosX()-auxTp.getPosX())));
                    double auxAngle = 90 - Math.toDegrees(Math.atan2((t.getPosY()-tn.getPosY()),(t.getPosX()-tn.getPosX())));

                   // double currentAngle = 90 - Math.toDegrees(Math.atan2(t.getPosX()*auxTp.getPosY() - t.getPosY()*auxTp.getPosX(),t.getPosX()*auxTp.getPosX() + t.getPosY()*auxTp.getPosY()));
                   // double auxAngle = 90 - Math.toDegrees(Math.atan2(t.getPosX()*tn.getPosY() - t.getPosY()*tn.getPosX(),t.getPosX()*tn.getPosX() + t.getPosY()*tn.getPosY()));

                    if (Math.abs(currentAngle) > Math.abs(auxAngle)){
                        auxTp = tn;
                    }
                }

                if (result > auxDistance){
                    result = auxDistance;
                    auxTp = tn;
                }
            }
        }
        return auxTp;
    }

    /**
     * Este metodo de Bender simulará que el Robot se mueve por el mapa buscando el camino.
     * @return devuelve un String con las direcciones que ha seguido para llegar a la meta.
     */
    public String run() {

        // Si el mapa no es valido devuelve null;
        if (!this.mP.isValidMap()) return null;

        char currentPosition = this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getCharacter();
        StringBuilder result = new StringBuilder();
        int directionCounter = 0;
        char actualDirection = 'S';

        while(currentPosition != '$'){

            // Define la dirección según si el robot ha pisado un inverso o no.
            if (canMove(this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()], actualDirection)) {
                actualDirection = defineDirection(directionCounter);
                if (actualDirection == '\u0000') return null;
            }

            currentPosition = move(actualDirection);

            int goneByActualCell =this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getGoneBy();

            this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].setGoneBy(goneByActualCell+1);
            result.append(actualDirection);

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
        return result.toString();
    }

    /**
     * Este metodo está implementado para los tests de BenderTest2, en este caso el robot busca el camino más corto.
     * @return devuelve un int  con el minimo de pasos que tiene que dar.
     */
    public int bestRun(){

        int stepsNumber = 1;
        setDistances(stepsNumber, this.goal.getPosY(), this.goal.getPosX());

        while(this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getDistanceFromGoal() == 0){

            for (int i = 0; i < this.mP.getFormedMap().length; i++) {
                for (int j = 0; j < this.mP.getFormedMap()[0].length; j++) {
                    if (this.mP.getFormedMap()[i][j].getDistanceFromGoal() == stepsNumber){
                        setDistances(stepsNumber+1, i, j);
                    }
                }
            }
            stepsNumber++;
        }
        System.out.println(this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getDistanceFromGoal());
        return this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getDistanceFromGoal();
    }

    /**
     * Esta función es la encargada de asignar los numero a las celdas para encontrar el camino más corto, si steps es 1
     * por ejemplo, las celdas de al lado se le asignara el 2, siempre que no sea una pared y no tenga ya un numero asignado.
     * @param steps asigna la cantidad de pasos que se han dado hasta esa celda.
     * @param y coordenada Y de la celda con el valor numerico actual.
     * @param x coordenada X de la celda con el valor numerico actual.
     */
    public void setDistances(int steps, int y, int x){

        // Si la celda que hay abajo de la actual no es una pared y no tiene asignado un número ya
        if ((this.mP.getFormedMap()[y+1][x].getCharacter() != '#') && (this.mP.getFormedMap()[y+1][x].getDistanceFromGoal() == 0)){

            // Si el caracter de la celta es un teletransportador asigna el numero en el teletransportador de salida también
            if (this.mP.getFormedMap()[y+1][x].getCharacter() == 'T'){
                Teleport tpOut = teleportMapList.get(cellMapList.indexOf(this.mP.getFormedMap()[y+1][x]));
                this.mP.getFormedMap()[tpOut.getNearTpY()][tpOut.getNearTpX()].setDistanceFromGoal(steps);
            }
            this.mP.getFormedMap()[y+1][x].setDistanceFromGoal(steps);
        }

        // Si la celda que hay a la derecha de la actual no es una pared y no tiene asignado un número ya
        if ((this.mP.getFormedMap()[y][x+1].getCharacter() != '#' ) && (this.mP.getFormedMap()[y][x+1].getDistanceFromGoal() == 0)){

            // Si el caracter de la celta es un teletransportador asigna el numero en el teletransportador de salida también
            if (this.mP.getFormedMap()[y][x+1].getCharacter() == 'T'){
                Teleport tpOut = teleportMapList.get(cellMapList.indexOf(this.mP.getFormedMap()[y][x+1]));
                this.mP.getFormedMap()[tpOut.getNearTpY()][tpOut.getNearTpX()].setDistanceFromGoal(steps);
            }
            this.mP.getFormedMap()[y][x+1].setDistanceFromGoal(steps);
        }

        // Si la celda que hay aarriba de la actual no es una pared y no tiene asignado un número ya
        if ((this.mP.getFormedMap()[y-1][x].getCharacter() != '#') && (this.mP.getFormedMap()[y-1][x].getDistanceFromGoal() == 0)){

            // Si el caracter de la celta es un teletransportador asigna el numero en el teletransportador de salida también
            if (this.mP.getFormedMap()[y-1][x].getCharacter() == 'T'){
                Teleport tpOut = teleportMapList.get(cellMapList.indexOf(this.mP.getFormedMap()[y-1][x]));
                this.mP.getFormedMap()[tpOut.getNearTpY()][tpOut.getNearTpX()].setDistanceFromGoal(steps);
            }
            this.mP.getFormedMap()[y-1][x].setDistanceFromGoal(steps);
        }

        // Si la celda que hay a la izquierda de la actual no es una pared y no tiene asignado un número ya
        if ((this.mP.getFormedMap()[y][x-1].getCharacter() != '#' ) && (this.mP.getFormedMap()[y][x-1].getDistanceFromGoal() == 0)){

            // Si el caracter de la celta es un teletransportador asigna el numero en el teletransportador de salida también
            if (this.mP.getFormedMap()[y][x-1].getCharacter() == 'T'){
                Teleport tpOut = teleportMapList.get(cellMapList.indexOf(this.mP.getFormedMap()[y][x-1]));
                this.mP.getFormedMap()[tpOut.getNearTpY()][tpOut.getNearTpX()].setDistanceFromGoal(steps);
            }
            this.mP.getFormedMap()[y][x-1].setDistanceFromGoal(steps);
        }
    }


    /**
     * Esta función define la dirección en la que irá el robot, para definir la dirección comprovará si las prioridades
     * están invertidas y depende de si lo estan o no asignaran la dirección correspondiente al valor numerico de directionCounter.
     * @param directionCounter Asignaran la dirección correspondiente al valor numerico de directionCounter
     * @return Devuelve un caracter el cual representa la dirección en la que se moverá
     */
    private char defineDirection(int directionCounter){
        char actualDirection;

        // No es necesario controlar que no pase de 4 ya que no es posible, pero no esta de más.
        if (directionCounter > 4) return '\u0000';

        // Define las direcciones segun el estado del robot.
        if (this.rb.isInverted()){
            actualDirection = this.rb.getIvertedDirections()[directionCounter];
        }else{
            actualDirection = this.rb.getDirections()[directionCounter];
        }

        // Si no puede moverse prueba con las siguiente dirreción en la lista.
        if (canMove(this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()], actualDirection)){
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
    private boolean canMove(Cell c, char direction){

        char actual = ' ';

        // Simula moverse en la dirección indicada.
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
        // Si el caracter de la celda actual es diferente a # entonces se puede mover.
        return actual == '#';
    }

    /**
     * Esta función hará la simulación de que el robot se mueve en la dirección que le decimos, al moverse nos situaremos
     * encima de una celda, la cual tendra un caracter propio, tenemos que comprobar si esa celda es la meta, un teletransportador
     * o un inversor.
     * @param actualDirection Este caracter determina en la dirección que se moverá.
     * @return Devuelve en el caracter que se encuentra actualmente.
     */
    private char move(char actualDirection) {

        // Segun la dirección actual se moverá hacia el S, E, N, W
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
        // devuelve el caracter para saber si estamos encima de un Teletransportador, inversor o la meta
        return this.mP.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getCharacter();
    }
}

/**
 * El objeto MapFormer está compuesto por un Array multidimensional de Celdas y un boolean el cual define si el mapa es valido,
 * para ser valido tiene que cumplir ciertos requisitos.
 */
class MapFormer{

    // Cell atributes.
    private Cell[][] formedMap;
    private boolean validMap = true;

    /**
     * Este es el constructor de MapFormer, dentro del constructor asignaremos a cada posición de un array bidimensional
     * un objeto Celda, además si encontramos el robot 'X' le asignaremos su posición y tambien crearemos un objeto Teleport
     * cada vez que encontremos una 'T' en el mapa, este objeto Teleport tiene las cordenadas que representan en el mapa.
     * @param map És el mapa que nos pasan en forma de String.
     * @param rb Devuelve el mapa formado.
     */
    MapFormer(String map, Robot rb, LinkedList<Teleport> teleportMap, LinkedList<Cell> cellMap, Goal goal){

        //Divide el String y lo mete en un Array dividiendo por los \n
        String[] arr = map.split("\n");
        formedMap = new Cell[arr.length][countColumns(map)];

        // Comprueba que el mapa sea valido.
        if(!mapIsValid(map)){
            rb.setRobotY(1);
            rb.setRobotX(1);
            validMap = false;
        }

        // Recorre el String copiando caracter a caracter.
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length(); j++) {
                char tmpType = arr[i].charAt(j);
                formedMap[i][j] = new Cell(arr[i].charAt(j),i ,j);

                // Crea el objeto Robot y le da su posición de inicio.
                if (tmpType == 'X'){
                    rb.setRobotY(i);
                    rb.setRobotX(j);
                }

                // Añade los teletransportadores a una lista.
                if (tmpType == 'T'){
                    teleportMap.add(new Teleport(i,j));
                    cellMap.add(formedMap[i][j]);
                }

                if (tmpType == '$'){
                    goal.setPosY(i);
                    goal.setPosX(j);
                }
            }
        }
    }

    /**
     * Esta función determina si el mapa es valido para que el robot lo pueda completar.
     * @param map El String que entra por parametro determina de que estará compuesto el mapa.
     * @return  Devuelve un boolean
     */
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

    /**
     * Cuenta cual es la fila con el mayor numero de caracteres en el string para poder hacer el array de ese tamño.
     * @param strMap És el mapa que nos pasan en forma de String.
     * @return Devuelve el numero máximo de caracteres de entre todas las filas.
     */
    public int countColumns(String strMap){
        int columns = 0;

        for (int i = 0, aux = 0; i < strMap.length(); i++) {
            if (strMap.charAt(i) != '\n') aux++;
            else{
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

    public Cell[][] getFormedMap() {
        return formedMap;
    }

    public boolean isValidMap() {
        return validMap;
    }
}

/**
 * El objeto Robot guarda su posición, dirección y su estado para poder llegar a la meta lo iremos trasformando.
 */
class Robot{

    // Robot run() atributes.
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

/**
 * El objeto cell se compone de el caracter actual, el cual representa que hará esa celda, su posición en el mapa,
 * cuantas veces se ha pasado por esta misma casilla para preevenir un bucle infinito y para el metodo bestRun() tenemos distanceFromGoal, para contar los pasos.
 */
class Cell {

    // Cell run() attibutes.
    private char character;
    private int posY;
    private int posX;
    private int goneBy = 0;

    //Cell bestRun() attibutes.
    private int distanceFromGoal = 0;

    Cell(char c, int y, int x){
        this.character = c;
        this.posX = x;
        this.posY = y;
    }

    public int getPosY() {
        return posY;
    }
    public int getPosX() {
        return posX;
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

    public int getDistanceFromGoal() {
        return distanceFromGoal;
    }

    public void setDistanceFromGoal(int distanceFromGoal) {
        this.distanceFromGoal = distanceFromGoal;
    }
}

/**
 * En el objeto Teletransportador guardamos sus coordenadas y cual es el Teletransportador más cercano para no tener
 * que calcularlo cada vez que entramos en uno.
 */
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

    public int getPosY() {
        return posY;
    }
    public int getPosX() {
        return posX;
    }

}

/**
 * El objeto Goal solo es necesario para saber desde que celda empezar a medir las distancias si usamos el metodo
 *  bestRun(), si no es indiferente tenerlo ya que no facilita nada.
 */
class Goal {

    // Goal attributes.
    private int posY;
    private int posX;

    Goal(){

    }

    public int getPosY() {
        return posY;
    }
    public int getPosX() {
        return posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
    public void setPosX(int posX) {
        this.posX = posX;
    }

}







