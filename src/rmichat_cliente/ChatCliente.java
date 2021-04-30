/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmichat_cliente;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import rmichat_comun.ChatException;
import rmichat_comun.UIUtilitarios;
/**
 *
 * @author euris
 */
public class ChatCliente extends JFrame implements ActionListener, WindowListener{
    //Titulo da aplicação
    private static final String TITLE = "Aplicação ChatRMI";
    //Componentes de tela
    private JTextField txtNome;
    private JButton btnConectar;
    private JButton btnDesconectar;
    private JTextArea txtMensagens;
    private JTextField txtMensagem;
    private JButton btnEnviar;
    private JMenuItem itemConfiguracao;
    //Objeto que faz a invocação do servidor de forma remota
    private InvocarServidor invocarServidor;
    
    public ChatCliente(){
        //Chama o construtor da superclasse passando o titulo da janela
        super(TITLE);
        //Define que a aolicação será finalizada quando o janela for fechada
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Evita o redimencionamento da janela
        setResizable(false);
        //Define o tamanho da janela
        setSize(500, 300);
        //Centraliza a janela na tela
        UIUtilitarios.centralizarJanela(this);
        //Adiciona um listener de eventos de janela
        addWindowListener(this);
        //Define o layout como BorderLayout
        setLayout(new BorderLayout());
        //Cria um painel que vai conter o nome e os botões "Conectar" e Desconectar
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //Adiciona os componentes relacionados ao nome no painel
        infoPanel.add(new JLabel("Nome: "));
        txtNome = new JTextField(20);
        txtNome.addActionListener(this); //Se o ENTER for precionado, disparará o evento
        infoPanel.add(txtNome);
        //Adiciona o botão "Conectar" no painel
        btnConectar = new JButton("Conectar");
        btnConectar.addActionListener(this);
        infoPanel.add(btnConectar);
        //Adiciona o botão "Desconectar" no painel
        btnDesconectar = new JButton("Desconectar");
        btnDesconectar.addActionListener(this);
        infoPanel.add(btnDesconectar);
        //Adiciona o painel na parte superior
        add(BorderLayout.NORTH, infoPanel);
        //Cria um painel onde ficará o TextArea com as mensagens enviadas/recebidas
        JPanel todasMsgPanel = new JPanel(new GridLayout(1, 1));
        //Cria o TextArea
        txtMensagens = new JTextArea(10, 20);
        //Não permite edição
        txtMensagens.setEditable(false);
        //Por padrão o TextArea não aceita scroll. Então um JScrollPanel é criado, pois ele adiciona
        //Esta capacidade ao componente
        JScrollPane scrollPane = new JScrollPane(txtMensagens);
        //Adiciona o JScrollPane ao painel
        todasMsgPanel.add(scrollPane);
        //Adiciona o painel no centro da janela
        add(BorderLayout.CENTER, todasMsgPanel);
        //Cria um painel que vai conter a mensagem a ser digitada e o botão "Enviar"
        JPanel msgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //Adiciona os componentes relacionados a digitação da mensgem
        msgPanel.add(new JLabel("Mensagem: "));
        txtMensagem = new JTextField(20);
        txtMensagem.addActionListener(this);//Se o ENTER for percionado, diparará o evento
        msgPanel.add(txtMensagem);
        //Adiciona o botão "Enviar" ao painel
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(this);
        msgPanel.add(btnEnviar);
        //Adiciona o painel na parte de baixo da tela
        add(BorderLayout.SOUTH, msgPanel);
        //Cria barra de menu
        JMenuBar menuBar = new JMenuBar();
        //Cria o menu "Editar" -> "Configurações"
        JMenu menuEditar = new JMenu("Editar");
        menuEditar.setMnemonic('E');
        itemConfiguracao = new JMenuItem("Cofigurações...");
        itemConfiguracao.setMnemonic('C');
        itemConfiguracao.addActionListener(this);
        menuEditar.add(itemConfiguracao);
        //Adiciona o menu na barra e a barra na janela
        menuBar.add(menuEditar);
        setJMenuBar(menuBar);
        //Indica que o servidor que o cliente está iniciamlemte desconectado
        setConectado(false);
        //Exibe a janela
        setVisible(true);
    }
    //Dependendo do estado atual do clente (conectado ou desconectado), os componentes da tela devem
    //Comportar de forma diferente. Este método faz estes ajustes.
    private void setConectado(boolean conectado){
        if (!conectado){
            txtNome.setEnabled(true);
            txtNome.setText("");
            btnConectar.setEnabled(true);
            btnDesconectar.setEnabled(false);
            txtMensagem.setEnabled(false);
            btnEnviar.setEnabled(false);
            txtMensagem.setText("");
            txtMensagens.setText("");
            setTitle(TITLE);
            txtNome.requestFocus();
        }else{
            txtNome.setEnabled(false);
            btnConectar.setEnabled(false);
            btnDesconectar.setEnabled(true);
            txtMensagem.setEnabled(true);
            btnEnviar.setEnabled(true);
            setTitle(String.format("%s [Conectado em %s:%s]", TITLE, ClienteConfiguracaoArquivo.getServidor(), ClienteConfiguracaoArquivo.getPorta()));
            txtMensagem.requestFocus();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        try{
            if(event.getSource() == txtNome || event.getSource() == btnConectar){
                //Um ENTER foi pressionado quando o foco estava na digitação do nome ou o botão ENTER foi clicado
                String nome = txtNome.getText();
                //Verifica se houve a digitação do nome do servidor
                if(nome != null && !nome.trim().isEmpty()){
                    //Lê o nome do servidor do arquivo de configuração
                    String servidor = ClienteConfiguracaoArquivo.getServidor();
                    //Lê a porta RMI do arquivo de configuração
                    int porta = Integer.parseInt(ClienteConfiguracaoArquivo.getPorta());
                    //Cria o ServerInvoker, responsável pelas chamadas ao objeto remoto
                    invocarServidor = new InvocarServidor(servidor, porta, nome, this);
                    //Coneta no servidor
                    invocarServidor.conectar();
                    //Muda o estado do cliente para conectado
                    setConectado(true);
                }else{
                    //Se não houve, devolve o foco para a caixa de digitação do nome
                    txtNome.requestFocus();
                }
            }else if(event.getSource() == btnDesconectar){
                //O botão desconectar foi clicado
                //Desconecta o servidor
                invocarServidor.desconectar();
                //Muda o estado do cliente para desconectado
                setConectado(false);
            }else if(event.getSource() == txtMensagem || event.getSource() == btnEnviar){
                //Um ENTER foi pressionado quando o foco estava na digitação da mensagem ou o botão "Enviar" foi clicado
                String mensagem = txtMensagem.getText();
                //Verifica se a mensagem é vazia
                if(mensagem != null && !mensagem.trim().isEmpty()){
                    //Envia a mensagem digitada
                    invocarServidor.enviarMensagem(mensagem);
                    //Limpa mensagem
                    txtMensagem.setText("");
                    //Retorna o foco para a caixa de digitação da mensagem
                    txtMensagem.requestFocus();
                }else{
                    //Se a mesnagem for vazia, retorna o foco para a caixa de digitação da mensagem
                    txtMensagem.requestFocus();
                }
            }else if(event.getSource() == itemConfiguracao){
                //A opção do menu "Editar" -> "Configuração" foi escolhida
                //Abre a tela de configurações
                new ConfirmacaoDialogo();
            }
        }catch(ChatException e){
            //Se ocorrer uma exceção, mostra na tela em uma caixa de diálogo
            UIUtilitarios.disparaException(this, e);
        }
    }
    //Chamado qunado uma mensagem veio do sevidor e precisa ser mostrada na tela
    public void mostrarMensagemDoServidor(String mensagem){
        //Adiciona a mensagem ao TextArea de mensagem
        txtMensagens.append(mensagem + "\n");
    }
    //Chamado quando o servidor está terminando
    public void handleDesligarServidor(){
        //Mostra uma caixa de dialogo de alerta avisando que o servidor saiu do ar
        UIUtilitarios.disparaAlerta(this, "Servidor fora ar", "O servidor do chat saiu do ar");
        //Muda o estado do cliente para desconectado
        setConectado(false);
    }
    //Invocando quando a janela está prestes a ser fechada
    @Override
    public void windowClosing(WindowEvent event) {
        try{
            if (invocarServidor != null){
                //Desconecta o cliente do servidor antes de sair
                invocarServidor.desconectar();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }
    
    //Método main
    public static void main(String[] args) {
        //Abra a interface gráfica do cliente
        new ChatCliente();
    }
}
