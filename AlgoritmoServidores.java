public class AlgoritmoServidores {
    public static void main(String[] args) {
        System.out.println("---Red de Servidores CDN México---");
        grafoPais mexico = crearMexico.crear();
        CDN cdnMexico = crearCND.crear(mexico);

        // Simulación de un cliente en Jalisco solicitando un archivo
        cdnMexico.solicitarArchivo("Querétaro", "Arbok");
    }
}