package monedas.api.aplicacion.servicios;


public class ContadorIntentosSinControl
        implements IContadorIntentosLogin {


    private long contador = 0;


    @Override
    public void registrarIntento() {
        contador++;
    }


    @Override
    public long obtenerTotalIntentos() {
        return contador;
    }


    @Override
    public void reiniciar() {
        contador = 0;
    }


    @Override
    public String nombreEstrategia() {
        return "Sin control";
    }
}
