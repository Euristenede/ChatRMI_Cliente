/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmichat_cliente;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
/**
 *
 * @author euris
 */
//Gerencia o arquivo de configuração do cliente
public class ClienteConfiguracaoArquivo {
    //Nome do arquivo de configuração
    private static final String CONFIG_FILE = "cliente_configuracao.txt";
    //Nome da propriedade que define o nome do servidor
    private static final String PROP_SERVER = "servidor";
    //Nome da propriedade que define a porta do RMI Registry
    private static final String  PROP_PORT = "porta";
    //Nome padrão do servidor
    private static final String DEFAULT_SERVER = "localhost";
    //Porta padrão do RMI Registry
    private static final String DEFAULT_PORT = "1909";
    //Objeto Properties com as propriedades do servidor
    private static Properties propriedade;
    
    static{
        //Carrega as informações do arquivo para o objeto Properties
        propriedade = new Properties();
        File file = new File(CONFIG_FILE);
        if(file.exists()){
            FileInputStream fis = null;
            try{
                fis = new FileInputStream(file);
                propriedade.load(fis);
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                if(fis != null){
                    try{
                        fis.close();
                    }catch(IOException e){
                        
                    }
                }
            }
        }else{
            //Se o arquivo não existir, define valores padrão para o servidor e a porta
            setServidor(DEFAULT_SERVER);
            setPorta(DEFAULT_PORT);
        }
    }
    //Contrutor privado

    private ClienteConfiguracaoArquivo() throws IOException{
    }
    //Define o servidor
    public static void setServidor(String servidor){
        propriedade.setProperty(PROP_SERVER, servidor);
    }
    //Define a porta
    public static void setPorta(String porta){
        propriedade.setProperty(PROP_PORT, porta);
    }
    //Obtem o servidor
    public static String getServidor(){
        return propriedade.getProperty(PROP_SERVER);
    }
    //Obtem a porta
    public static String getPorta(){
        return propriedade.getProperty(PROP_PORT);
    }
    //Salva as informações no arquivo de configuração
    public static void salvar() throws IOException{
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(CONFIG_FILE);
            propriedade.store(fos, null);
        }finally{
            if(fos != null){
                fos.close();
            }
        }
    }
}
