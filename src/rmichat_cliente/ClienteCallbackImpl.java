/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmichat_cliente;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import rmichat_comun.ClienteCallback;
/**
 *
 * @author euris
 */
public class ClienteCallbackImpl extends UnicastRemoteObject implements ClienteCallback{
    //JFrame do cliente
    private ChatCliente frame;
    //Objeto InvocarServidor, utilizado para invocar os métodos no objeto remoto
    private InvocarServidor invocarServidor;
    //Construtor

    protected ClienteCallbackImpl(ChatCliente frame, InvocarServidor invocarServidor) throws RemoteException{
        this.frame = frame;
        this.invocarServidor = invocarServidor;
    }
    
    
    @Override
    public void onEnviandoMensagem(String mensagem) throws RemoteException {
        //Soliicta ao frame que a mensagem seja exibida na tela
        frame.mostrarMensagemDoServidor(mensagem);
    }

    @Override
    public void onServidorDesligando() throws RemoteException {
        //Avisa o frame que o servidor está saindo fora do ar
        frame.handleDesligarServidor();
        //Muda o estado do InvocarServidor para desconectado
        invocarServidor.setConectado(false);
    }
    
}
