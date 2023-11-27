public class Persona extends Thread{
    int genero;
    public Persona(int s) {
        genero = s;
    }


    //devuelve true si el homre puede entrar
    public boolean Hombre() {
        return ((BañosMixtos.ocupados > 0) && (BañosMixtos.ocupados < BañosMixtos.capacidad));
    }


    //devuelve false si la mujer puede entrar
    public boolean Mujer() {
        return ((BañosMixtos.ocupados < 0) && (BañosMixtos.ocupados > BañosMixtos.capacidad));
    }


    public boolean puedeEntrar() {
        // primero comprueba si su genero usa el baño
        // segundo si esta por llegar a la capacidad
        return ((genero > BañosMixtos.ocupados * -1) && (Math.abs(BañosMixtos.capacidad)>Math.abs(BañosMixtos.ocupados)));
    }

    @Override
    public void run() {
        super.run();
        // Variable que indica si la persona está esperando para entrar al baño
        boolean esperandoEntrar = true;

        // La persona se mantiene en la cola hasta que pueda entrar al baño
        while (esperandoEntrar) {
            try {
                // Adquirir el semáforo para garantizar la exclusión mutua
                BañosMixtos.sem.acquire();

                // Verificar si hay alguien usando el baño
                if (BañosMixtos.ocupados == 0) {
                    // Si el baño está vacío, la persona puede entrar
                    esperandoEntrar = false;
                    // Marcar el baño como ocupado por el género de la persona y ajustar la capacidad
                    BañosMixtos.capacidad = (genero > 0) ? Math.abs(BañosMixtos.capacidad) : Math.abs(BañosMixtos.capacidad) * -1;
                } else {
                    // En caso de que el baño esté ocupado, ceder el baño al otro género si está lleno
                    if (BañosMixtos.ocupados == BañosMixtos.capacidad)
                        BañosMixtos.capacidad = (genero < 0) ? Math.abs(BañosMixtos.capacidad) : Math.abs(BañosMixtos.capacidad) * -1;

                    // Verificar si hay baños disponibles para el género de la persona
                    if ((genero > 0 && Hombre()) || (genero < 0 && Mujer()))
                        esperandoEntrar = false;
                    else
                        // Si no hay baños disponibles, liberar el semáforo y continuar esperando
                        BañosMixtos.sem.release();
                }
            } catch (Exception e) {
                // Manejar excepciones
                System.out.println("ERROR: INTERRUMPIDO");
                BañosMixtos.sem.release();
            }
        }

        // La persona pudo entrar al baño
        BañosMixtos.ocupados += genero;
        System.out.println(((genero > 0) ? "Un hombre" : "Una mujer") + " entra al baño, en el baño hay " + Math.abs(BañosMixtos.ocupados));

        // Liberar el semáforo después de ocupar el baño
        BañosMixtos.sem.release();

        try {
            // Simular el tiempo que la persona pasa en el baño
            sleep((int) (Math.random() * 10 + 1) * 100);

            // Adquirir el semáforo nuevamente antes de salir del baño
            BañosMixtos.sem.acquire();

            // La persona pudo hacer sus necesidades, salir del baño y ajustar la ocupación
            BañosMixtos.ocupados -= genero;
            System.out.println(((genero > 0) ? "Un hombre" : "Una mujer") + " sale del baño, en el baño hay " + Math.abs(BañosMixtos.ocupados));
        } catch (Exception e) {
            // Manejar excepciones
            System.out.println("ERROR: INTERRUMPIDO");
            BañosMixtos.sem.release();
        }

        // Liberar el semáforo después de salir del baño
        BañosMixtos.sem.release();
    }
}