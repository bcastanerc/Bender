import static org.junit.Assert.assertEquals;

public class Bender {

    MapFormer mP;
    Robot rb = new Robot();

    // Constructor: ens passen el mapa en forma d'String

    public Bender(String mapa) {

        mP = new MapFormer(mapa,rb);
        System.out.println(this.mP.toString());

    }

        // Navegar fins a l'objectiu («$»).
        // El valor retornat pel mètode consisteix en una cadena de
        // caràcters on cada lletra pot tenir
        // els valors «S», «N», «W» o «E»,
        // segons la posició del robot a cada moment.

    public String run() {

        char posicion = this.mP.formedMap[this.rb.robotY][this.rb.robotX].character;
        String result = "";
        int contadorDireccion = 0;
        char direccionActual;


        while(posicion != '$'){

            // Define la dirección según si el robot ha pisado un inverso o no.
            if (rb.inverted){
                direccionActual = rb.direccionesInvertidas[contadorDireccion];
            }else{
                direccionActual = rb.direcciones[contadorDireccion];
            }

            if (canMove(mP.formedMap[rb.robotY][rb.robotX], direccionActual, mP)){
                posicion = move(direccionActual);
                result += direccionActual;
            }else{
                contadorDireccion++;
            }
        }
        return result;
    }

    public String defineDirection(){

        return
    }

    /**
     * Esta función se encarga de que el robot simule moverse en la dirección que lo haria, cuando simula la dirección
     * lo que buscamos es que no se ponga encima de una pared, para esto necesitamos el mapa en el cual nos moveremos,
     * donde se encuentra actualmente el robot y la dirección en la que intenta ir.
     * @param c Representa la celda en la cual se encuentra el robot actualmente.
     * @param direction Este caracter determina en la dirección que provará moverse.
     * @param map Es el mapa en el cual se mueve el robot
     * @return devuelve un boolean, true si el robot se puede mover o false si es una pared ya que no se podrá mover.
     */
    public boolean canMove(Celda c, char direction, MapFormer map){

        char actual = ' ';

        if (direction == 'S') actual = map.formedMap[c.posY+1][c.posX].character;

        if (direction == 'E') actual = map.formedMap[c.posY][c.posX+1].character;

        if (direction == 'N') actual = map.formedMap[c.posY-1][c.posX].character;

        if (direction == 'W') actual = map.formedMap[c.posY][c.posX-1].character;

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
                this.rb.setRobotY(rb.robotY+1);
                this.rb.setRobotX(rb.robotX);
                break;

            case 'E':
                this.rb.setRobotY(rb.robotY);
                this.rb.setRobotX(rb.robotX+1);
                break;

            case 'N':
                this.rb.setRobotY(rb.robotY-1);
                this.rb.setRobotX(rb.robotX);
                break;

            case 'W':
                this.rb.setRobotY(rb.robotY);
                this.rb.setRobotX(rb.robotX-1);
                break;
        }
        return this.mP.formedMap[rb.robotY][rb.robotX].character;
    }


    public static void main(String[] args) {
            String mapa = "" +
                    "#######\n" +
                    "# X   #\n" +
                    "#     #\n" +
                    "#     #\n" +
                    "#     #\n" +
                    "# $   #\n" +
                    "#     #\n" +
                    "#######";
            Bender bender = new Bender(mapa);
           bender.run()     ;
    }
}

/**
 * El objeto Robot guarda su posición, dirección y su estado para poder llegar a la meta lo iremos trasformando.
 */
class Robot{

    int robotX;
    int robotY;
    char[] direcciones = {'S','E','N','W'};
    char[] direccionesInvertidas = {'N','W','S','E'};
    boolean inverted;

    Robot(){
    }

    public void setRobotX(int x){
        this.robotX = x;
    }
    public void setRobotY(int y){
        this.robotY = y;
    }
    public int getRobotX(){
        return this.robotX;
    }
    public int getRobotY(){
        return this.robotY;
    }
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
    public boolean isInverted() {
        return inverted;
    }
}

class Celda{

    char character;
    int posY;
    int posX;

    Celda(char c, int y, int x){
        this.character = c;
        this.posX = x;
        this.posY = y;
    }
}

class MapFormer{

    //attr.
    Celda[][] formedMap;

    MapFormer(String mapa, Robot rb){
        mapa = mapa + "\n";
        //Divide el String y lo mete en un Array dividiendo por los \n
        String[] arr = mapa.split("\n");
        formedMap = new Celda [arr.length][countColumns(mapa)];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length(); j++) {
                formedMap[i][j] = new Celda(arr[i].charAt(j),i ,j);
                if (arr[i].charAt(j) == 'X'){
                   rb.setRobotX(j);
                   rb.setRobotY(i);
                }
            }
        }
    }

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

    @Override
    public String toString(){
        StringBuilder strMap = new StringBuilder();
        // Recorre el mapa.
        for (Celda[] chars : this.formedMap) {
            for (int j = 0; j < this.formedMap[0].length; j++) {
                // Coje los valores de cada posicion del mapa y los pone en un String.
                strMap.append(chars[j].character);
            }
            strMap.append('\n');
        }
        return strMap.toString();
    }
}






