import java.util.Map;

public class CDN {
    private Map<nodoEstado, baseDatos> servidorSecundario;
    private Map<nodoEstado, baseDatos> servidorPrincipal;
    private grafoPais red;

    public CDN(Map<nodoEstado, baseDatos> servidorSecundario, Map<nodoEstado, baseDatos> servidorPrincipal, grafoPais red) {
        this.servidorSecundario = servidorSecundario;
        this.servidorPrincipal = servidorPrincipal;
        this.red = red;
    }


    public void agregarDatoAServidor(nodoEstado servidor,String nombre, String dato) {
        if (servidor.esServidor) {
            if (servidor.tipoServidor.equals("Principal")) {
                baseDatos bd = servidorPrincipal.get(servidor);
                bd.addDato(nombre, dato);
            } 
            else if (servidor.tipoServidor.equals("Secundario")) {
                baseDatos bd = servidorSecundario.get(servidor);
                bd.addDato(nombre, dato);
            }
        }
    }

    public baseDatos obtenerDatosDeServidor(nodoEstado servidor) {
        baseDatos bd = null;
        if (servidor.esServidor) {
            if (servidor.tipoServidor.equals("Principal")) {
                bd = servidorPrincipal.get(servidor);
            } 
            else if (servidor.tipoServidor.equals("Secundario")) {
                bd = servidorSecundario.get(servidor);
            }
            return bd;
        }
        return bd;
    }

    public String obtenerDato(nodoEstado servidor, String nombre) {
        baseDatos bd = obtenerDatosDeServidor(servidor);
        if (bd != null) {
            return bd.getDato(nombre);
        }
        return null;
    }



//--------------------------------------------------------------------------------------------------------------------------
// --- EL MÉTODO SOLICITADO (IA)
    public void solicitarArchivo(String estadoCliente, String nombreArchivo) {
        nodoEstado mejorServidor = null;
        double menorDistancia = Double.MAX_VALUE;
        String tipoServidorGanador = "";

        // 1. Revisar Servidor Principal
        for (nodoEstado server : servidorPrincipal.keySet()) {
            double distancia = red.rutaMasCortaDijkstra(estadoCliente, server.nombre);
        
            if (distancia != -1 && distancia < menorDistancia) {
                menorDistancia = distancia;
                mejorServidor = server;
                tipoServidorGanador = "PRINCIPAL";
            }
        }

        // 2. Revisar Servidores Secundarios
        for (nodoEstado server : servidorSecundario.keySet()) {
            double distancia = red.rutaMasCortaDijkstra(estadoCliente, server.nombre);

            if (distancia != -1 && distancia < menorDistancia) {
                menorDistancia = distancia;
                mejorServidor = server;
                tipoServidorGanador = "SECUNDARIO";
            }
        }


        // Si se encontró un servidor
        if (mejorServidor != null) {
            // Si ganó secundario, hay que ver si tiene el archivo
            if (tipoServidorGanador.equals("SECUNDARIO")) {
                baseDatos bdReplica = servidorSecundario.get(mejorServidor);
                String archivoEnReplica = bdReplica.getDato(nombreArchivo);
                
                //El archivo no está ya
                if (archivoEnReplica == null) {
                    // Buscamos el contenido original en el Servidor Principal
                    String contenidoOriginal = null;
                    for (baseDatos bdPrincipal : servidorPrincipal.values()) {
                        contenidoOriginal = bdPrincipal.getDato(nombreArchivo);
                        if (contenidoOriginal != null) break; 
                    }

                    // Si el principal tenia ese archivo
                    if (contenidoOriginal != null) {
                        bdReplica.addDato(nombreArchivo, contenidoOriginal);
                    } else {
                        System.out.println("El archivo no existe en el Principal");
                        return; 
                    }
                }
            }

            vista.mostrar(estadoCliente, mejorServidor.nombre, menorDistancia, obtenerDato(mejorServidor, nombreArchivo));
        }

    }
    //--------------------------------------------------------------------------------------------------------------------------

}
