package br.com.sisaudcon.saam.saam_sped_cnd.domain.exception;

public class ClienteNaoAutorizadoException extends RuntimeException {
    public ClienteNaoAutorizadoException(String msg) {
        super(msg);
    }
}