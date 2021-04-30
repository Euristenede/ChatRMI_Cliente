/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmichat_cliente;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import rmichat_comun.UIUtilitarios;
/**
 *
 * @author euris
 */
public class ConfirmacaoDialogo extends JDialog implements ActionListener{
    //Componentes da tela
    private JTextField txtServidor;
    private JTextField txtPorta;
    private JButton btnOk;
    private JButton btnCancelar;

    public ConfirmacaoDialogo() {
        //Define o titulo
        setTitle("Configurações");
        //Define como modal (não pode perder o foco enquanto estiver aberta)
        setModal(true);
        //Evita o redimensionamento
        setResizable(false);
        //Define o tamanho
        setSize(400, 150);
        //Contraliza a tela
        UIUtilitarios.centralizarJanela(this);
        //Se a janela for fechada, faz o dispose dela (elimina a janela)
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //Define o layout como BerderLayout
        setLayout(new BorderLayout());
        //Cria o painel inde serão adicionadas as informaç~pes dos parâmetros para serem alterados
        //O layout aqui é o GridBagLayout, muito utilizado em formulários (permite montar uma grade) e
        //ter tamanhos diferenciados
        JPanel paramPanel = new JPanel(new GridBagLayout());
        //Cria uma borda utilizando uma linha ao redor do painel
        paramPanel.setBorder(BorderFactory.createTitledBorder(""));
        //Cria um GridBagConstraints, utilizando pelo GridBagLayout
        GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0, 0, 
                                                      GridBagConstraints.LINE_END, GridBagConstraints.NONE, 
                                                      new Insets(2, 2, 2, 2), 0, 0);
        //Adiciona a label do nome do servidor
        paramPanel.add(new JLabel("Servidor de chat: "), c);
        //Adiciona a caixa de texto do servidor
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        txtServidor = new JTextField(20);
        txtServidor.setText(ClienteConfiguracaoArquivo.getServidor());//Lê o servidor de arquivo de configuração
        paramPanel.add(txtServidor, c);
        //Adiciona a label da porta do servidor
        c.gridx = 0;
        c.gridy = 1; 
        c.anchor = GridBagConstraints.LINE_START;
        paramPanel.add(new JLabel("Porta do Servidor"), c);
        //Adiciona a caixa de texto da porta
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        txtPorta = new JTextField(20);
        txtPorta.setText(ClienteConfiguracaoArquivo.getPorta());//Lê a porta do arquivo de configuração
        paramPanel.add(txtPorta, c);
        //Adiciona o painel ao centro da janela
        add(BorderLayout.CENTER, paramPanel);
        //Cria um painel para os botões "OK" e "Cancelar"
        JPanel buttonPanel = new JPanel();
        //Adiciona o botão "OK" ao painel
        btnOk = new JButton("Ok");
        btnOk.addActionListener(this);
        buttonPanel.add(btnOk);
        //Adiciona o botão "Cancelar" ao painel
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(this);
        buttonPanel.add(btnCancelar);
        //Adiciona o painel na parte inferior da janela
        add(BorderLayout.SOUTH, buttonPanel);
        //Exibe a tela
        setVisible(true);
    }   
    
    
    @Override
    public void actionPerformed(ActionEvent event) {
        if(event.getSource() == btnOk){
            //Houve um clique no botão "Ok"
            String servidor = txtServidor.getText();
            //Valida se o servidor foir preenchodo
            if(servidor == null || servidor.trim().isEmpty()){
                UIUtilitarios.disparaAlerta(this, "Dados inválidos", "Forneça um nome de servidor");
                return;
            }
            String porta = txtPorta.getText();
            //Valida se a porta foi preenchida
            if(porta == null || porta.trim().isEmpty()){
                UIUtilitarios.disparaAlerta(this, "Dados inválidos", "Forneça uma porta");
            }
            try{
                //Valida se a porta é um número válido
                int numeroPorta = Integer.parseInt(porta);
                //Valida se a porta está em um intervalo válido
                if (numeroPorta < 1 || numeroPorta > 65635){
                    UIUtilitarios.disparaAlerta(this, "Dados inválidos", "A porta não está em um intervalo válido");
                    return;
                }
            }catch(NumberFormatException e){
                UIUtilitarios.disparaAlerta(this, "Dados inválidos", "A porta não é um número valido");
                return;
            }
            //Defineos novos valores de porta e servidor e salva estas informações no arquivo de configuração
            ClienteConfiguracaoArquivo.setServidor(servidor);
            ClienteConfiguracaoArquivo.setPorta(porta);
            try{
                ClienteConfiguracaoArquivo.salvar();
            }catch(IOException e){
                UIUtilitarios.disparaException(this, e);
            }
            //Fecha janela
            dispose();
        }else if(event.getSource() == btnCancelar){
            //Se o botão "Cancelar" for precionado, fecha a janela
            dispose();
        }
    }
    
}
