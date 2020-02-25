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
        char direccionActual = 'S';

        while(posicion != '$'){

            if (!canMove(mP.formedMap[rb.robotY][rb.robotX], direccionActual, mP))

            if (direccionActual == 'S'){
                this.rb.setRobotX(rb.robotX);
                this.rb.setRobotY(rb.robotY+1);
            }

        }
        return result;
    }

    public boolean canMove(Celda c, char direccion, MapFormer map){

        char actual = ' ';

        if (direccion == 'S') actual = map.formedMap[c.posY+1][c.posX].character;

        if (direccion == 'E') actual = map.formedMap[c.posY][c.posX+1].character;

        if (direccion == 'N') actual = map.formedMap[c.posY-1][c.posX].character;

        if (direccion == 'N') actual = map.formedMap[c.posY][c.posX-1].character;


        return actual != '#';
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

class Robot{

    int robotX;
    int robotY;
    char[] direcciones = {'S','E','N','W'};
    char[] direccionesInvertidas = {'N','W','S','E'};

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






