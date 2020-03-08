import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;

public class Bender {

    // Bender run() Attributes.
    private Map map;
    private Robot rb = new Robot();
    private LinkedList<Teleport> teleportMapList = new LinkedList<>();
    private LinkedList<Cell> cellMapList = new LinkedList<>();
    private Goal goal = new Goal();

    /**
     * Esta clase crea el mapa, asigna las posiciones de las celdas, asigna los teletransportadores y calcula su más cercano
     * @param map String de entrada el cual define como será el mapa.
     */
    public Bender(String map) {

        // Crea el mapa, asigna posiciones a goal y rb, rellena la lista de celdas y teletransportadores.
        this.map = new Map(map,rb, teleportMapList, cellMapList, goal);
        // Asigna el teletransportador más cercano por cada teletransportador en la lista.
        for (Teleport actualTeleport : teleportMapList) {
             actualTeleport.defineNearestTp(teleportMapList);
        }
    }

    /**
     * Este metodo de Bender simulará que el Robot se mueve por el mapa buscando el camino.
     * @return devuelve un String con las direcciones que ha seguido para llegar a la meta.
     */
    public String run() {

        // Si el mapa es considerado como no valido devuelve null;
        if (!this.map.isValidMap()) return null;

        char currentPosition = this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getCharacter();
        StringBuilder result = new StringBuilder();
        char actualDirection = 'S';

        while(currentPosition != this.map.getFormedMap()[this.goal.getPosY()][this.goal.getPosX()].getCharacter()){
            // Define la dirección según si el robot ha pisado un inversor o no.
            if (cantMove(this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()], actualDirection)) {
                actualDirection = defineDirection(0);
                if (actualDirection == '\u0000') return null;
            }

            // Asigna la nueva posición al Robot.
            currentPosition = move(actualDirection);

            // Suma obtiene la cantidad de veces que se ha pasado por la celda actual.
            int goneByActualCell = this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getGoneBy();
            this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].setGoneBy(goneByActualCell+1);

            // Concatena la dirección en la que se ha movido el Robot.
            result.append(actualDirection);

            // Si el robot pasa más de 8 veces por la misma celda es un bucle infinito.
            if (this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getGoneBy() > 8 ) return null;

            // Si el robot pisa in Inversor se invierte el estado del robot (atributo inverted).
            if (currentPosition == 'I') this.rb.setInverted(!this.rb.isInverted());

            // Si el robot esta e un Teletransportador accederá al atributo que define cual es el más cercano de ese.
            if (currentPosition == 'T'){
                Teleport tpOut = teleportMapList.get(cellMapList.indexOf(this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()]));
                this.rb.setRobotX(tpOut.getNearTpX());
                this.rb.setRobotY(tpOut.getNearTpY());
            }
        }
        return result.toString();
    }

    /**
     * Esta función define la dirección en la que irá el robot, para definir la dirección comprovará si las prioridades
     * están invertidas y depende de si lo estan o no asignaran la dirección correspondiente al valor numerico de directionCounter.
     * @param directionCounter Asignaran la dirección correspondiente al valor numerico de directionCounter
     * @return Devuelve un caracter el cual representa la dirección en la que se moverá
     */
    private char defineDirection(int directionCounter){
        char actualDirection;
        //Controlamos que no pase de 3, si pasa quiere decir que esta encerrado entre paredes.
        if (directionCounter > 3) return '\u0000';

        // Define las direcciones segun el estado del robot.
        if (this.rb.isInverted()){
            actualDirection = this.rb.getIvertedDirections()[directionCounter];
        }else{
            actualDirection = this.rb.getDirections()[directionCounter];
        }

        // Si no puede moverse prueba con las siguiente dirreción en la lista.
        if (cantMove(this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()], actualDirection)){
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
    private boolean cantMove(Cell c, char direction){

        char actual = ' ';

        // Simula moverse en la dirección indicada.
        switch (direction){
            case 'S':
                actual = this.map.getFormedMap()[c.getPosY()+1][c.getPosX()].getCharacter();
                break;
            case 'E':
                actual = this.map.getFormedMap()[c.getPosY()][c.getPosX()+1].getCharacter();
                break;
            case 'N':
                actual = this.map.getFormedMap()[c.getPosY()-1][c.getPosX()].getCharacter();
                break;
            case 'W':
                actual = this.map.getFormedMap()[c.getPosY()][c.getPosX()-1].getCharacter();
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
        return this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].getCharacter();
    }

    /**
     * Este metodo está implementado para los tests de BenderTest2, en este caso el robot busca el camino más corto.
     * @return devuelve un int  con el minimo de pasos que tiene que dar.
     */
    public int bestRun(){

        int stepsNumber = 0;
        // Asigna a las celdas al rededor del robot la distancia de 1.
        this.map.getFormedMap()[this.rb.getRobotY()][this.rb.getRobotX()].setDistanceFromRobot(0);
        while(this.map.getFormedMap()[this.goal.getPosY()][this.goal.getPosX()].getDistanceFromRobot() == -1){
            // Recorre el mapa buscanco las celdas
            for (int i = 0; i < this.map.getFormedMap().length; i++) {
                for (int j = 0; j < this.map.getFormedMap()[i].length; j++) {
                    if (this.map.getFormedMap()[i][j].getDistanceFromRobot() == stepsNumber){
                        // Comprueba la celda de abajo
                        setDistances(stepsNumber+1,i+1,j);
                        // Comprueba la celda de la derecha
                        setDistances(stepsNumber+1,i,j+1);
                        // Comprueba la celda de arriba
                        setDistances(stepsNumber+1,i-1,j);
                        // Comprueba la celda de la izquierda
                        setDistances(stepsNumber+1,i,j-1);
                    }
                }
            }
            stepsNumber++;
        }
        return this.map.getFormedMap()[this.goal.getPosY()][this.goal.getPosX()].getDistanceFromRobot();
    }

    /**
     * Esta función es la encargada de asignar los numero a las celdas para encontrar el camino más corto, si steps es 1
     * por ejemplo, las celdas de al lado se le asignara el 2, siempre que no sea una pared y no tenga ya un numero asignado.
     * @param steps asigna la cantidad de pasos que se han dado hasta esa celda.
     * @param y coordenada Y de la celda con el valor numerico actual.
     * @param x coordenada X de la celda con el valor numerico actual.
     */
    private void setDistances(int steps, int y, int x){
        // Si la celda que hay en las coordenadas de entrada respecto a la actual no es una pared y no tiene asignado un número ya
        // le asignaremos los pasos a la celda.
        if ((this.map.getFormedMap()[y][x].getCharacter() != '#') && (this.map.getFormedMap()[y][x].getDistanceFromRobot() == -1)){
            // Si el caracter de la celta es un teletransportador asigna el numero en el teletransportador de salida también
            if (this.map.getFormedMap()[y][x].getCharacter() == 'T'){
                Teleport tpOut = teleportMapList.get(cellMapList.indexOf(this.map.getFormedMap()[y][x]));
                this.map.getFormedMap()[tpOut.getNearTpY()][tpOut.getNearTpX()].setDistanceFromRobot(steps);
            }
            this.map.getFormedMap()[y][x].setDistanceFromRobot(steps);
        }
    }
}

/**
 * El objeto MapFormer está compuesto por un Array multidimensional de Celdas y un boolean el cual define si el mapa es valido,
 * para ser valido tiene que cumplir ciertos requisitos.
 */
class Map {

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
    Map(@NotNull String map, Robot rb, LinkedList<Teleport> teleportMap, LinkedList<Cell> cellMap, Goal goal){

        //Divide el String y lo mete en un Array dividiendo por los \n
        String[] arr = map.split("\n");

        // Cuenta cual es el máximo de caracteres que hay en una fila.
        int maxColumnes = countColumns(map);

        // Añade paredes a las filas que tienen menos que la fila más larga.
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() < maxColumnes){
                arr[i] =  rightPaddingMap(arr[i],maxColumnes-arr[i].length());
            }
        }

        formedMap = new Cell[arr.length][countColumns(map)];

        // Comprueba que el mapa sea valido.
        if(mapIsValid(map)){
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

                    // Añade la posición de la meta a el objeto meta.
                    if (tmpType == '$'){
                        goal.setPosY(i);
                        goal.setPosX(j);
                    }
                }
            }
        }else validMap = false;
    }

    /**
     * Esta función añade tantas almohadillas como sean necesarias para que el mapa tenga la misma longitud en cada fila.
     * @param line String de la posición del array con menos caracteres.
     * @param totalCharacters total de caracteres que se tienen que agregar a el String.
     * @return devuelve el string con los caracteres añadidos.
     */
    @NotNull
    private String rightPaddingMap(String line, int totalCharacters){
        line = line + "#".repeat(Math.max(0, totalCharacters));
        return line;
    }

    /**
     * Esta función determina si el mapa es valido para que el robot lo pueda completar.
     * @param map El String que entra por parametro determina de que estará compuesto el mapa.
     * @return  Devuelve un boolean
     */
    private boolean mapIsValid(@NotNull String map){

        int robotCounter = 0;
        int goalCounter = 0;
        int teleportCounter = 0;

        for (int i = 0; i < map.length(); i++) {

            if (map.charAt(i) == 'X') robotCounter++;
            if (map.charAt(i) == '$') goalCounter++;
            if (map.charAt(i) == 'T') teleportCounter++;
            if (robotCounter > 1|| goalCounter > 1) return false;
        }
        return robotCounter == 1 && goalCounter == 1 && teleportCounter != 1;
    }

    /**
     * Cuenta cual es la fila con el mayor numero de caracteres en el string para poder hacer el array de ese tamño.
     * @param strMap És el mapa que nos pasan en forma de String.
     * @return Devuelve el numero máximo de caracteres de entre todas las filas.
     */
    private int countColumns(@NotNull String strMap){
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
 * El objeto Robot guarda su posición, dirección y su estado para poder llegar a la meta, se le cambiara la posición
 * cada vez que avance.
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
    private int distanceFromRobot = -1;

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

    public int getDistanceFromRobot() {
        return distanceFromRobot;
    }
    public void setDistanceFromRobot(int distanceFromRobot) {
        this.distanceFromRobot = distanceFromRobot;
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

    /**
     * Calcula cual es el teletransportador más cercano del teletransportador origen y compara las
     * distancias con cada teletransportador de la lista.
     * @param teleportMapList lista de los teleports a los cuales se puede teletransportar
     */
    public void defineNearestTp(@NotNull LinkedList<Teleport> teleportMapList){

        // Creamos un teletransportador con la distanci al maximo posible para que se cambie y las coordenadas a 0,0.
        double distancia = 99999999;
        Teleport tFinal = new Teleport(0,0);

        // Por cada teletransportador en la lista haremos los calculos
        for (Teleport tActual : teleportMapList){
            // La lista se omite a si mismo.
            if (this != tActual){

                // Calculamos la hipotenunsa del triangulo que forma el vector para saber la distancia.
                double x = tActual.getPosX()-this.getPosX();
                double y = tActual.getPosY()-this.getPosY();
                double auxDistance = Math.sqrt((Math.pow(y,2))+(Math.pow(x,2)));

                // Si la distancia entre los dos teletransportadores es la misma usaremos una función para calcular los angulos.
                if (distancia == auxDistance){
                    if (setAngles(this,tFinal.getPosY(),tFinal.getPosX())
                            > setAngles(this,tActual.getPosY(),tActual.getPosX())){
                        tFinal = tActual;
                        distancia = auxDistance;
                    }
                }

                // Si la distancia del actual es menor al Final secambia el Final por actual.
                if (auxDistance < distancia){
                    distancia = auxDistance;
                    tFinal= tActual;
                }
            }
        }
        this.setNearTpY(tFinal.getPosY());
        this.setNearTpX(tFinal.getPosX());
    }

    /**
     * Esta función calcula cual es el angulo del teletransportador actual con referencia al teletransportador central,
     * @param tOrigen Teletransportador central.
     * @param y coordenadas en el plano vertical del teletransportador con el que se calculan los angulos.
     * @param x coordenadas en el plano horizontal del teletransportador con el que se calculan los angulos.
     * @return devuelve los angulos resultantes del calculo.
     */
    private double setAngles(@NotNull Teleport tOrigen, double y, double x){

        double xActual = x -tOrigen.getPosX();
        double yActual = tOrigen.getPosY() - y;
        double anguloActual;

        if ((xActual*yActual)  < 0){
            anguloActual = Math.abs(Math.toDegrees(Math.atan(yActual/xActual)));
        }else{
            anguloActual = Math.abs(Math.toDegrees(Math.atan(xActual/yActual)));
        }

        if(xActual >= 0 && yActual < 0)anguloActual+=90;
        if (xActual < 0 && yActual < 0) anguloActual+= 180;
        if (xActual < 0 && yActual >= 0) anguloActual+=270;
        return anguloActual;
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







