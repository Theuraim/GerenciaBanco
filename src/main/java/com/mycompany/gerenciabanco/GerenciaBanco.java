/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.gerenciabanco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos Junior
 */
class Usuario {
    
    public static String usuario;
    private static String senha;
    public static String nome;
    public static String sobreNome;
    public static String CPF;
    public static String email;
    public static String telefone;
    public static Connection con;
    public static Boolean logado;
    
    
    public Usuario( Connection c){
        this.con = c;
    }
    
    // Getters
    public Integer getID() {
        try {
            String sql = "SELECT ID_USUARIO" +
                         "  FROM USUARIO U " +
                         " WHERE U.USUARIO = ?" +
                         "   AND U.SENHA = ? " +
                         " LIMIT 1";
            
            int id_usuario = -1;
            //Statement statement = con.createStatement();
            
            PreparedStatement statement = con.prepareStatement(sql);
            
            statement.setString(1, usuario);
            statement.setString(2, senha);
            
            ResultSet result = statement.executeQuery();
            
            while (result.next()){
                 id_usuario = result.getInt(1);
            }
            
            return id_usuario;
        } catch (Exception e){
           System.out.println("Erro ao buscar id do usuario, detalhes: " + e.getMessage()); 
           return -1; 
        }
    }
    
    public String usuario() {
        return usuario;
    }
    
    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobreNome;
    }

    public String getCpf() {
        return CPF;
    }
    
    public static Boolean PossuiUsuarios(){
        String sql = "SELECT COUNT(1) FROM USUARIO";
        
        int count = 0;
        
        try {
            Statement statement = con.createStatement();
            ResultSet result = statement.executeQuery(sql);
            
            while (result.next()){
                 count = count + result.getInt(1);
            }
            
            return count > 0;
        } catch (Exception e) {
            System.out.println("Erro ao validar usuarios no banco de dados, detalhes: " + e.getMessage());
            return false;
        }
    }
    
    public static Boolean CadastrarUsuario(){
        String sql = "INSERT INTO USUARIO (USUARIO, SENHA, NOME, SOBRENOME, CPF, EMAIL, TELEFONE) VALUES(?, ?, ?, ?, ?, ?, ?)";
       
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            
            statement.setString(1, usuario);
            statement.setString(2, senha );
            statement.setString(3, nome);
            statement.setString(4, sobreNome);
            statement.setString(5, CPF);
            statement.setString(6, email);
            statement.setString(7, telefone);
            
            int rows = statement.executeUpdate();
            
            return rows > 0;
        } catch (Exception e) {
            System.out.println("Error inserindo o usuario no banco de dados, detalhes: " + e.getMessage());
            return false;
        }
    }    
    
    public static Boolean ValidarUsuario(){
        try {
            String sql = "SELECT COUNT(1)" +
                         "  FROM USUARIO U " +
                         " WHERE U.USUARIO = ?" +
                         "   AND U.SENHA = ?";
            
            int count = 0;
            //Statement statement = con.createStatement();
            PreparedStatement statement = con.prepareStatement(sql);
            
            statement.setString(1, usuario);
            statement.setString(2, senha);
            
            ResultSet result = statement.executeQuery();
            
            while (result.next()){
                 count = count + result.getInt(1);
            }
            
            return count > 0;
        } catch (Exception e){
           System.out.println("Erro ao validar o usuario no banco de dados, detalhes: " + e.getMessage());
           return false; 
        }
    }
    
    public static void Login(){
       Boolean possuiUsuarios = PossuiUsuarios();
       
       Scanner scanner = new Scanner(System.in);
       
       if (!possuiUsuarios){
            
            System.out.println("Digite seu usuario: ");
            usuario = scanner.nextLine();
            
            System.out.println("Digite sua senha: ");
            senha = scanner.nextLine();
            
            System.out.println("Digite seu seu nome: ");
            nome = scanner.nextLine();
            
            System.out.println("Digite seu seu sobrenome: ");
            sobreNome = scanner.nextLine();
            
            System.out.println("Digite seu seu CPF: ");
            CPF = scanner.nextLine();
            
            System.out.println("Digite seu seu email: ");
            email = scanner.nextLine();
            
            System.out.println("Digite seu seu telefone para contato: ");
            telefone = scanner.nextLine(); 
            
            
            if (!CadastrarUsuario()){
               logado = false;
            } else { 
               logado = true;
            }
       } else {
            System.out.println("Digite seu usuario: ");
            usuario = scanner.nextLine();
            
            System.out.println("Digite sua senha: ");
            senha = scanner.nextLine();
            
            if (!ValidarUsuario()){
                System.out.println("Usuario invalido"); 
                logado = false;
            } else {
               logado = true;
            }
            
       }
    }
    
    public static void Logout(){
       usuario = "";
       senha = "";
       nome = "";
       sobreNome = "";
       CPF = "";
       email = "";
       telefone = "";
       con = null;
       logado = false;
       
       System.out.println("Ate mais :) ");
    }
}

class Movimento{
    
    public static Connection con;
    public static Integer id_usuario;
    
    public Movimento(Connection c, 
                     Integer id_usuario){
        this.con = c;
        this.id_usuario = id_usuario;
    }
    
    public void RegistraMovimento(String tipoMovimento,
                                  double valorMovimentar){
        Boolean registrarMovimento = false;
        
        
        if (tipoMovimento == "DEPOSITO"){
            registrarMovimento = Depositar(valorMovimentar);
        } else{
            registrarMovimento = Sacar(valorMovimentar);
        } 
            
        
        if (registrarMovimento){
            String sql = "INSERT INTO MOVIMENTO (TIPOMOVIMENTO, ID_USUARIO, QUANTIDADEMOVIMENTADA) VALUES(?, ?, ?)";
       
            try {
                PreparedStatement statement = con.prepareStatement(sql);

                statement.setString(1, tipoMovimento);
                statement.setInt(2,id_usuario);
                statement.setDouble(3, valorMovimentar);
              
                int rows = statement.executeUpdate();
                
                if (rows > 0){
                    System.out.println("Movimentacao realizada com sucesso, retornando ao menu principal");
                    return;
                };
            } catch (Exception e) {
                System.out.println("Error ao gravar historico da movimentacao, detalhes: " + e.getMessage());
                return;
            }  
        }
    }
    
    public void ExtratoMovimentacoes(){
       try {
           String sql;
           sql = "SELECT M.TIPOMOVIMENTO,"
                   + "   (SELECT U.USUARIO "
                   + "      FROM USUARIO U"
                   + "      WHERE U.ID_USUARIO = M.ID_USUARIO) AS USUARIO,"
                   + "   M.QUANTIDADEMOVIMENTADA" +
                   "  FROM MOVIMENTO M " +
                   " WHERE M.ID_USUARIO = ?" +
                   " LIMIT 10";
                                  
            double valor = 0.0;
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id_usuario);
            
            ResultSet result = statement.executeQuery();
            
            while (result.next()){
                 System.out.println("O usuario: " + result.getString(2) + " realizou um : " + result.getString(1) + 
                                    " No valor de: R$ " + result.getDouble(3));
            }      
        } catch (Exception e){
           System.out.println("Erro ao realizar extrato de movimentacoes, detalhes: " + e.getMessage());
           return; 
        } 
    }
    
    public double Extrato(){
       try {
           String sql;
           sql = "SELECT COALESCE(SUM(S.VALOR), 0) AS VALOR" +
                   "  FROM SALDO S " +
                   " WHERE S.ID_USUARIO = ?";
                                  
            double valor = 0.0;
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id_usuario);
            ResultSet result = statement.executeQuery();
            
            while (result.next()){
                 valor = valor + result.getDouble(1);
            }
            
            
            System.out.println("Voce possui um saldo de: " + valor);
            
            return valor;
            
        } catch (Exception e){
           System.out.println("Erro ao realizar extrato, detalhes: " + e.getMessage());
           return 0.0; 
        } 
    }
    
    private Boolean Depositar(double valorMovimentar){
        double valorAtual = Extrato();
        Boolean retorno = false;
        
        
        if (valorAtual == 0.0){
            String sql = "INSERT INTO SALDO (ID_USUARIO, VALOR) VALUES(?, ?)";
       
            try {
                PreparedStatement statement = con.prepareStatement(sql);

                statement.setInt(1,id_usuario);
                statement.setDouble(2, valorMovimentar);
              
                int rows = statement.executeUpdate();

                double totalConta = valorAtual + valorMovimentar;
                
                if (rows > 0){
                    System.out.println("Deposito de: R$ " + valorMovimentar + " feito com sucesso");
                    System.out.println("O valor atual que sua conta possui é: R$ " + totalConta);
                    retorno = true;
                };
            } catch (Exception e) {
                System.out.println("Error ao fazer deposito, detalhes: " + e.getMessage());
                retorno = false;
            } 
        } else {
            String sql = "UPDATE SALDO SET VALOR = VALOR + ? WHERE VALOR > 0 AND ID_USUARIO = ?";
       
            try {
                PreparedStatement statement = con.prepareStatement(sql);

                statement.setDouble(1, valorMovimentar);
                statement.setInt(2,id_usuario);
              
                int rows = statement.executeUpdate();
                
                double totalConta = valorAtual + valorMovimentar;
                if (rows > 0){
                    System.out.println("O deposito de: R$ " + valorMovimentar + " feito com sucesso");
                    System.out.println("O valor atual que sua conta possui é: R$ " + totalConta);
                    retorno = true;
                };
            } catch (Exception e) {
                System.out.println("Error ao fazer deposito, detalhes: " + e.getMessage());
                retorno = false;
            } 
        } 
        
        return retorno;
    }
    
    private Boolean Sacar(double valorMovimentar){
        double valorAtual = Extrato();  
        Boolean retorno = false;
       
        if (valorAtual >= valorMovimentar){
            String sql = "UPDATE SALDO SET VALOR = VALOR - ? WHERE VALOR > 0 AND ID_USUARIO = ?";
       
            try {
                PreparedStatement statement = con.prepareStatement(sql);

                statement.setDouble(1, valorMovimentar);
                statement.setInt(2,id_usuario);
              
                int rows = statement.executeUpdate();
                
                double totalConta = valorAtual - valorMovimentar;
                if (rows > 0){
                    System.out.println("O saque de: R$ " + valorMovimentar + " feito com sucesso");
                    System.out.println("O valor atual que sua conta possui é: R$ " + totalConta);
                    retorno = true;
                };
            } catch (Exception e) {
                System.out.println("Error ao fazer saque, detalhes: " + e.getMessage());
                retorno = false;
            }  
        } else {
           System.out.println("O saldo que que você possui na conta atualmente não é o suficiente para sacar R$ " + valorMovimentar); 
           retorno = false;
        }
        
        return retorno;
    }
}

public class GerenciaBanco {

    public static void main(String[] args) {
        try {
            String jdbcURL = "jdbc:postgresql://localhost:1080/GerenciaBanco?user=postgres&password=usersystem&ssl=false";
            
            Class.forName("org.postgresql.Driver");
            Connection connection  = DriverManager.getConnection(jdbcURL);
            
            Usuario usuario = new Usuario(connection);
            
            usuario.Login();
            
            Movimento movimento = new Movimento(connection,
                                                usuario.getID());
            
            while (usuario.logado){
               Scanner scanner = new Scanner(System.in);
               System.out.println(" 1 - Extrato de saldo bancario");
               System.out.println(" 2 - Extrato de movimentacoes");
               System.out.println(" 3 - Depositar");
               System.out.println(" 4 - Sacar");
               System.out.println(" 5 - Sair");
               
               System.out.println(" ");
               System.out.println("digite uma das opções: ");
               
               try{
                    Integer opcao = -1; 
                            opcao = Integer.parseInt(scanner.nextLine()); 
                    
                    switch (opcao){
                        case 1: 
                            movimento.Extrato();
                            opcao = -1;    
                        break;
                        case 2: 
                            movimento.ExtratoMovimentacoes();
                            opcao = -1;
                        break;
                        case 3:
                            System.out.println(" Digite o quanto deseja depositar: "); 
                            try {
                               double valorDepositar = Double.valueOf(scanner.nextLine().replace("(?:[^\\d\\,])", "").replace(",", ""));
                               movimento.RegistraMovimento("DEPOSITO", valorDepositar);
                               opcao = -1;
                            } catch (Exception e){
                                System.out.println("Valor invalido"); 
                                continue;
                            }
                        break;
                        case 4:
                            System.out.println(" Digite o quanto deseja sacar: "); 
                            try {
                               double valorSacar = Double.valueOf(scanner.nextLine().replace("(?:[^\\d\\,])", "").replace(",", ""));
                               movimento.RegistraMovimento("SAQUE", valorSacar);
                               opcao = -1;
                            } catch (Exception e){
                                System.out.println("Valor invalido"); 
                                continue;
                            }
                        break;
                        case 5:
                            usuario.Logout();
                            break;
                    }
               }catch (Exception e) {
                    System.out.println("Opção invalida: ");  
                    continue;
               }
            }
            
            //System.out.println("Connected to PostgreSQL server");
        } catch (Exception e) {
            System.out.println("Erro ao connectar no servidor PostgreSQL, detalhes: " + e.getMessage());
            return;
        }
    }
}
