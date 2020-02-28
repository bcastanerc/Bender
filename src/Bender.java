import java.util.Iterator;
import java.util.LinkedList;

public class Bender {

    private MapFormer mP;
    private Robot rb = new Robot();
    private LinkedList<Teleport> teleportMapList = new LinkedList<Teleport>();
    private LinkedList<Celda> cellMapList = new LinkedList<Celda>();

    // Constructor: ens passen el mapa en forma d'String

    /**
     *
     * @param mapa
     */
    public Bender(String mapa) {

        if (!mapIsValid(mapa)) run();
        mP = new MapFormer(mapa,rb, teleportMapList, cellMapList);

        Iterator<Teleport> teleportMapListIt = teleportMapList.iterator();
        while(teleportMapListIt.hasNext()){
            Teleport tActual = teleportMapListIt.next();
            Teleport nearest = defineNearestTp(tActual, teleportMapList);
            tActual.nearTpX = nearest.posX;
            tActual.nearTpY = nearest.posY;

        }
    }

    public boolean mapIsValid(String str){

        int robotCount = 0;
        int goalCount = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 'X') {
                robotCount++;
                if (robotCount > 1) return false;
            }
            if (str.charAt(i) == '$'){
                goalCount++;
                if (goalCount > 1) return false;
            }
        }

        return true;
    }

    public Teleport defineNearestTp(Teleport t, LinkedList teleportMapList){

        Iterator<Teleport> teleportMapListIt = teleportMapList.iterator();
        double result = 999999999;
        Teleport auxTlp = new Teleport(0,0);


        while (teleportMapListIt.hasNext()){

           Teleport tn = teleportMapListIt.next();

            int x = Math.abs(tn.posX-t.posX);
            int y = Math.abs(tn.posY-t.posY);
            double auxDistance = Math.sqrt((x+x)+(y+y));

            //TODO Teleports at same distance, not working.
            if (result == auxDistance ){
                double currentAngle = Math.pow(Math.tan((t.posX-auxTlp.posX)*(t.posY-auxTlp.posY)),2);
                double auxAngle = Math.pow(Math.tan((t.posX-tn.posX)*(t.posY-tn.posY)),2);
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

        // Navegar fins a l'objectiu («$»).
        // El valor retornat pel mètode consisteix en una cadena de
        // caràcters on cada lletra pot tenir
        // els valors «S», «N», «W» o «E»,
        // segons la posició del robot a cada moment.

    /**
     *
     * @return
     */
    public String run() {

        char posicion = this.mP.formedMap[this.rb.robotY][this.rb.robotX].character;
        String result = "";
        int contadorDireccion = 0;
        char direccionActual = 'S';

        while(posicion != '$'){

            // Define la dirección según si el robot ha pisado un inverso o no.
            if (!canMove(this.mP.formedMap[this.rb.robotY][this.rb.robotX], direccionActual)) {
                direccionActual = defineDirection(contadorDireccion);
                if (direccionActual == '\u0000') return null;
            }
            posicion = move(direccionActual);
            int pasadasCeldaActual =this.mP.formedMap[this.rb.robotY][this.rb.robotX].pasadas;
            this.mP.formedMap[this.rb.robotY][this.rb.robotX].setPasadas(pasadasCeldaActual+1);
            result += direccionActual;

            // Si el robot pasa más de 8 veces por la misma celda es un bucle infinito.
            if (this.mP.formedMap[this.rb.robotY][this.rb.robotX].pasadas > 8 ) return null;

            // Si el robot pisa in Inversor se invierte el estado del robot (inverted).
            if (posicion == 'I') rb.inverted = !rb.inverted;

            // Si el robot esta e un Teletransportador accederá al atributo que define cual es el más cercano de ese.
            if (posicion == 'T'){
                Teleport tpOut = teleportMapList.get(cellMapList.indexOf(this.mP.formedMap[this.rb.robotY][this.rb.robotX]));
                this.rb.setRobotX(tpOut.nearTpX);
                this.rb.setRobotY(tpOut.nearTpY);
            }
        }
        System.out.println(result);
        return result;
    }



    /**
     * Esta función define la dirección en la que irá el robot, para definir la dirección comprovará si las prioridades
     * están invertidas y depende de si lo estan o no asignaran la dirección correspondiente al valor numerico de directionCounter.
     * @param directionCounter Asignaran la dirección correspondiente al valor numerico de directionCounter
     * @return Devuelve un caracter el cual representa la dirección en la que se moverá
     */
    public char defineDirection(int directionCounter){
        char direccionActual = ' ';

        if (directionCounter > 4) return '\u0000';

        if (this.rb.inverted){
            direccionActual = this.rb.getDireccionesInvertidas()[directionCounter];
        }else{
            direccionActual = this.rb.getDirecciones()[directionCounter];
        }

        if (!canMove(this.mP.formedMap[this.rb.robotY][this.rb.robotX], direccionActual)){
            direccionActual = defineDirection(directionCounter+1);
        }


        return direccionActual;
    }

    /**
     * Esta función se encarga de que el robot simule moverse en la dirección que lo haria, cuando simula la dirección
     * lo que buscamos es que no se ponga encima de una pared, para esto necesitamos el mapa en el cual nos moveremos,
     * donde se encuentra actualmente el robot y la dirección en la que intenta ir.
     * @param c Representa la celda en la cual se encuentra el robot actualmente.
     * @param direction Este caracter determina en la dirección que provará moverse.
     * @return devuelve un boolean, true si el robot se puede mover o false si es una pared ya que no se podrá mover.
     */
    public boolean canMove(Celda c, char direction){

        char actual = ' ';

        switch (direction){
            case 'S':
                actual = this.mP.formedMap[c.posY+1][c.posX].character;
                break;
            case 'E':
                actual = this.mP.formedMap[c.posY][c.posX+1].character;
                break;
            case 'N':
                actual = this.mP.formedMap[c.posY-1][c.posX].character;
                break;
            case 'W':
                actual = this.mP.formedMap[c.posY][c.posX-1].character;
                break;
        }

        return actual != '#';
    }

    /**
     * Esta función hará la simulación de que el robot se mueve en la dirección que le decimos, al moverse nos situaremos
     * encima de una celda, la cual tendra un caracter propio, tenemos que comprobar si esa celda es la meta, un teletransportador
     * o un inversor.
     * @param direccionActual Este caracter determina en la dirección que se moverá.
     * @return Devuelve en el caracter que se encuentra actualmente.
     */
    public char move(char direccionActual) {
        switch (direccionActual){
            case 'S':
                this.rb.setRobotY(this.rb.robotY+1);
                this.rb.setRobotX(this.rb.robotX);
                break;

            case 'E':
                this.rb.setRobotY(this.rb.robotY);
                this.rb.setRobotX(this.rb.robotX+1);
                break;

            case 'N':
                this.rb.setRobotY(this.rb.robotY-1);
                this.rb.setRobotX(this.rb.robotX);
                break;

            case 'W':
                this.rb.setRobotY(this.rb.robotY);
                this.rb.setRobotX(this.rb.robotX-1);
                break;
        }
        return this.mP.formedMap[this.rb.robotY][this.rb.robotX].character;
    }

    public static void main(String[] args) {
        String mapa = "" +
                "   #######\n" +
                "   # XTI #\n" +
                "   #    $#\n" +
                "####    #####\n" +
                "#          T#\n" +
                "####     ####\n" +
                "   #    I#\n" +
                "   #     #\n" +
                "   #######";
        Bender bender = new Bender(mapa);
         bender.run();
    }
}

/**
 * El objeto Robot guarda su posición, dirección y su estado para poder llegar a la meta lo iremos trasformando.
 */
class Robot{

    int robotX;
    int robotY;
    private char[] direcciones = {'S','E','N','W'};
    private char[] direccionesInvertidas = {'N','W','S','E'};
    boolean inverted;

    Robot(){
    }

    public void setRobotX(int x){
        this.robotX = x;
    }
    public void setRobotY(int y){
        this.robotY = y;
    }

    public char[] getDirecciones() {
        return direcciones;
    }

    public char[] getDireccionesInvertidas() {
        return direccionesInvertidas;
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
}

class Celda{

    char character;
    int posY;
    int posX;
    int pasadas;

    Celda(char c, int y, int x){
        this.character = c;
        this.posX = x;
        this.posY = y;
        this.pasadas = 0;
    }

    public void setPasadas(int pasadas) {
        this.pasadas = pasadas;
    }

    public int getPasadas() {
        return pasadas;
    }
}

class Teleport {

    int posY;
    int posX;
    int nearTpY;
    int nearTpX;

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

    Celda[][] formedMap;

    /**
     * Este es el constructor de MapFormer, dentro del constructor asignaremos a cada posición de un array bidimensional
     * un objeto Celda, además si encontramos el robot 'X' le asignaremos su posición y tambien crearemos un objeto Teleport
     * cada vez que encontremos una 'T' en el mapa, este objeto Teleport tiene las cordenadas que representan en el mapa.
     * @param map És el mapa que nos pasan en forma de String.
     * @param rb Devuelve el mapa formado.
     */
    MapFormer(String map, Robot rb, LinkedList<Teleport> teleportMap, LinkedList<Celda> celdaMap){

        // Añadimos un "\n" para poder usar la funcion split.
        map = map + "\n";

        //Divide el String y lo mete en un Array dividiendo por los \n
        String[] arr = map.split("\n");
        formedMap = new Celda [arr.length][countColumns(map)];

        // Recorre el String copiando caracter a caracter.
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length(); j++) {
                formedMap[i][j] = new Celda(arr[i].charAt(j),i ,j);

                // Crea el objeto Robot y le da su posición de inicio, cuenta cuantos robots hay.
                if (arr[i].charAt(j) == 'X'){
                   rb.setRobotX(j);
                   rb.setRobotY(i);
                }

                // Añade los teletransportadores a una lista.
                if (arr[i].charAt(j) == 'T'){
                    teleportMap.add(new Teleport(i,j));
                    celdaMap.add(formedMap[i][j]);
                }
            }
        }
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
        for (Celda[] celdas : this.formedMap) {
            for (int j = 0; j < this.formedMap[0].length; j++) {
                // Coje los valores de cada posicion del mapa y los pone en un String.
                strMap.append(celdas[j].character);
            }
            strMap.append('\n');
        }
        return strMap.toString();
    }
}






