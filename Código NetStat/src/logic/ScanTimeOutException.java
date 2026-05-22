package logic;

import java.io.IOException;

public class ScanTimeOutException extends IOException {

    public ScanTimeOutException(String mensaje) {
        super(mensaje);

    }

}