import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.core.cmd.CreateDB;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

import java.io.File;

import org.xmldb.api.base.Collection;

/**
 * Created by 45858000w on 27/03/17.
 */



/*Para empezar debes habrir en el terminal :/home/53638138e/Baixades/basex/bin == > basexhttp

En el explorer = > http://localhost:8984/dba/databases

Usuario : admin  Pasword : admin

//Para crear una Base de datos nueva : => mira AddExample2.java

Si peta Importar Librerias : => xmldb y Base X 86

*/


public class Main {

    public static String rutaxml = "/media/45858000w/PenCristianJ/M06/ExamenUf3/UF3-ExamenF-Plantes.xml";
    /**
     * Runs the example code.
     * @param args (ignored) command-line arguments
     * @throws Exception exception
     */
    public static void main(final String... args) throws Exception {
        System.out.println("=== ServerCommands ===");

        // Start server on default port 1984
        //BaseXServer server = new BaseXServer();

        // Create a client session with host name, port, user name and password
        System.out.println("\n* Create a client session.");

        try(ClientSession session = new ClientSession("localhost", 1984, "admin", "admin")) {

            // Create a database
            System.out.println("\n* Create a database.");
            //Aqui se cambia segun la base de datos
            session.execute(new CreateDB("Plantes", rutaxml));

            //Aqui se cambia la query
            System.out.println("--------------------------");
            System.out.println("nom de la planta de la que tenim més estoc.");
            System.out.println("--------------------------");
            try(ClientQuery query = session.query("let $max := max(/CATALOG/PLANT/AVAILABILITY),      $nombre := /CATALOG/PLANT[AVAILABILITY=$max]/COMMON/text() return <resultado>  <nombre>{$nombre}</nombre> <max>{$max}</max>  </resultado>")) {
                System.out.println(query.execute());
            }



            System.out.println("--------------------------");
            System.out.println("Quantes plantes en total hi ha a l'estoc?:");
            System.out.println("--------------------------");
            //aqui va la consulta
            try(ClientQuery query = session.query("let $plantas := /CATALOG/PLANT return <total>{sum($plantas/AVAILABILITY)}</total>")) {
                System.out.println(query.execute());
                System.out.println();
            }

            session.setOutputStream(System.out);
            session.setOutputStream(System.out);


            // Reset output stream
            session.setOutputStream(null);


            System.out.println("--------------------------");
            System.out.println("saber el preu de tot l'estoc, planta per plant:");
            System.out.println("--------------------------");

            try(ClientQuery query = session.query("for $plantas in /CATALOG/PLANT  return     <plant> De la planta {$plantas/COMMON/text()} tenim {$plantas/AVAILABILITY/text()} exemplars. Cada exemplar te un preu de {$plantas/PRICE/text()} .Per tant per la venta de tot l'stock ingresarem {$plantas/AVAILABILITY/text() * xs:double(fn:substring($plantas/PRICE/text(),2))} dolars </plant>")) {
                System.out.println(query.execute());
                System.out.println();
            }

        }

        // Stop the server
        System.out.println("\n* Stop the server.");

        //server.stop();

        afegirFitxer();
    }

    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private static String driver = "org.exist.xmldb.DatabaseImpl";

    private static void afegirFitxer() throws XMLDBException,
            ClassNotFoundException, IllegalAccessException, InstantiationException{
        File f = new File(rutaxml);

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");

        // crear el manegador
        DatabaseManager.registerDatabase(database);

        // adquirir la col·lecció que volem tractar
        Collection col = DatabaseManager.getCollection(URI+"/db","admin","admin");
        System.out.println(col.getName());

        //Creem la col·lecció on guardarem el recurs
        CollectionManagementService colmgt = (CollectionManagementService) col.getService("CollectionManagementService", "1.0");
        //l'hi donem un nom a la nova col·lecció
        col = colmgt.createCollection("CristianJavierPACO");

        //afegir el recurs que farem servir
        Resource res = col.createResource("UF3-ExamenF-Plantes.xml","XMLResource");
        res.setContent(f);
        col.storeResource(res);



        //Creamos un nuevo recurso vacio dentro del programa y le damos el recurso mondial2.xml que esta dentro de la col·lección
        Resource res2 = col.getResource("UF3-ExamenF-Plantes.xml");

        //Mostramos el contenido de un recurso por pantalla
        System.out.println( res2.getContent() );


    }
}



