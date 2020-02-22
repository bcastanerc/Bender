import static org.junit.Assert.assertEquals;

public class Bender {

    MapFormer mP;

    // Constructor: ens passen el mapa en forma d'String

    public Bender(String mapa) {

        mP = new MapFormer(mapa);
        System.out.println(this.mP.toString());

    }

        // Navegar fins a l'objectiu («$»).
        // El valor retornat pel mètode consisteix en una cadena de
        // caràcters on cada lletra pot tenir
        // els valors «S», «N», «W» o «E»,
        // segons la posició del robot a cada moment.

    public String run() {

        char[] direcciones = {'S','E','N','W'};
        char[] direccionesInvertidas = {'N','W','S','E'};

        char posicion = this.mP.formedMap[this.mP.benderY][this.mP.benderX];
        String result = "";

        while(posicion != '$'){
            posicion = this.mP.formedMap[this.mP.benderY][this.mP.benderX];


            this.mP.setBenderX(this.mP.benderX);
            this.mP.setBenderY(this.mP.benderY+1);
        }

        return result;
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

class MapFormer{

    //attr.
    char[][] formedMap;
    int benderX = 0;
    int benderY = 0;


    MapFormer(String mapa){
        mapa = mapa + "\n";
        //Divide el String y lo mete en un Array dividiendo por los \n
        String[] arr = mapa.split("\n");
        formedMap = new char[arr.length][countColumns(mapa)];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length(); j++) {
                formedMap[i][j] = arr[i].charAt(j);
                if (arr[i].charAt(j) == 'X'){
                    benderX = j;
                    benderY = i;
                }
            }
        }
    }

    public void setBenderX(int benderX){
        this.benderX = benderX;
    }

    public void setBenderY(int benderY){
        this.benderY = benderY;
    }

    public int getBenderX(){
        return this.benderX;
    }

    public int getBenderY(){
        return this.benderY;
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
        for (char[] chars : this.formedMap) {
            for (int j = 0; j < this.formedMap[0].length; j++) {
                // Coje los valores de cada posicion del mapa y los pone en un String.
                strMap.append(chars[j]);
            }
            strMap.append('\n');
        }
        return strMap.toString();
    }
}






