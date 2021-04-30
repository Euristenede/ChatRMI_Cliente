/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmichat_cliente;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import rmichat_comun.ChatException;
import rmichat_comun.ClienteCallback;
import rmichat_comun.InformacaoCliente;
import rmichat_comun.OperacoesServidor;
/**
 *
 * @author euris
 */
//Classe responsável pelo acesso ao servidor, chamando métodos no objeto remoto.
public class InvocarServidor {
    //URL de lookup para buscar o objeto remoto
    private String lookupURL;
    //Variável que guarda a referÊncia ao objeto remoto
    private OperacoesServidor operacoesServidor;
    //Informações do cliente
    private InformacaoCliente infoCliente;
    //Estado atual (Conecado ou desconectado)
    boolean conectado = false;
    //Construtor
    public InvocarServidor(String servidor, int porta, String nome, ChatCliente frame) throws ChatException{
        try{
            //Monta a URL de lookup
            lookupURL = "rmi://"+servidor+":"+porta+"/"+OperacoesServidor.SERVER_OBJ_NAME;
            //Cria o objeto de callback. Este objeto é usado pelo servidor para enviar dados ao cliente
            ClienteCallbackImpl callbackImpl = new ClienteCallbackImpl(frame, this);
            //Como o cliente e o servidor podem estar em comptadores diferentes, fornecer o objeto
            //Para que o servidor possa chamar o cliente remotamente, ele precisa de um stub do cliente
            //obtêm uma referência ao stub do objeto de callback. É ele quem deve ser fornecido ao servidor.
            ClienteCallback callback = (ClienteCallback) UnicastRemoteObject.toStub(callbackImpl);
            //Cria o objeto ClienteInfo, com as informações do cliente
            infoCliente = new InformacaoCliente(nome, callback);
        }catch(RemoteException e){
            throw  new ChatException("Erro ao criar o objeto do callback", e);
        }
    }
    //Faz conexão com o servidor
    public void conectar() throws ChatException{
        try{
          //Procura pelo objeto remoto no RMI Registry
          operacoesServidor = (OperacoesServidor) Naming.lookup(lookupURL);
          //Chama o método de conexão, passando os dados do cliente
          operacoesServidor.connect(infoCliente);
          //Marca o cliente como conectado
          conectado = true;
        }catch(Exception e){
            throw  new ChatException("Erro ao se conectar ao servidor", e);
        }
    }
    //Desconecta o cliente do servidor
    public void desconectar() throws ChatException{
        if(conectado){
            try {
                //Realiza a desconexão
                operacoesServidor.disconect(infoCliente, null);
            } catch (RemoteException ex) {
                Logger.getLogger(InvocarServidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Marca o cliente como desconectado
            conectado = false;
        }
    }
    //Envia uma mensagem ao servidor, que está distribuida entre outros clientes
    public void enviarMensagem(String mensagem) throws ChatException{
        try{
            //Envia a mensagem ao servidor
            operacoesServidor.enviarMensagem(infoCliente, mensagem);
        }catch(RemoteException e){
            throw new ChatException("Erro ao enviar mensagem ao servidor", e);
        }
    }
    //Define se o cliente está conectado ou desconectado
    public void setConectado(boolean  conectado){
        this.conectado = conectado;
    }
}
