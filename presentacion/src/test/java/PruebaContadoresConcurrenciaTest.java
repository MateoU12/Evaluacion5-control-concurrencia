import monedas.api.aplicacion.servicios.*;


import org.junit.jupiter.api.Test;


import java.util.concurrent.*;


import static org.junit.jupiter.api.Assertions.*;


class PruebaContadoresConcurrenciaTest {


    private void ejecutarPrueba(
            IContadorIntentosLogin contador)
            throws InterruptedException {


        int tareas = 1000;
        int hilos = 20;


        ExecutorService executor =
                Executors.newFixedThreadPool(hilos);


        CountDownLatch inicio = new CountDownLatch(1);
        CountDownLatch terminados =
                new CountDownLatch(tareas);


        contador.reiniciar();


        long tiempoInicio = System.nanoTime();


        for (int i = 0; i < tareas; i++) {
            executor.submit(() -> {
                try {
                    inicio.await();
                    contador.registrarIntento();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    terminados.countDown();
                }
            });
        }


        inicio.countDown();
        terminados.await();


        long tiempo = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - tiempoInicio);


        executor.shutdown();


        long obtenido = contador.obtenerTotalIntentos();
        long errores = tareas - obtenido;


        System.out.println(
                "\nEstrategia: " + contador.nombreEstrategia()
                + "\nEsperado: " + tareas
                + "\nObtenido: " + obtenido
                + "\nErrores: " + errores
                + "\nTiempo: " + tiempo + " ms"
        );


        if (!(contador instanceof ContadorIntentosSinControl)) {
            assertEquals(tareas, obtenido);
        }
    }


    @Test
    void compararContadores() throws InterruptedException {


        ejecutarPrueba(new ContadorIntentosSinControl());


        ejecutarPrueba(new ContadorIntentosSincronizado());


        ejecutarPrueba(new ContadorIntentosAtomico());
    }
}
