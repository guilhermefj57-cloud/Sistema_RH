package com.tdsManha.rh.database;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // 1. Define o caminho para o local GRAVÁVEL (pasta do usuário)
    private static final String DB_FOLDER = System.getProperty("user.home") + File.separator + "RH_App_Data";
    private static final String TARGET_DB_PATH = DB_FOLDER + File.separator + "rh_database.db";

    /**
     * Cria e retorna uma NOVA conexão com o banco de dados.
     * Implementa a lógica para copiar o banco de dados inicial
     * (empacotado como recurso) para um local gravável na primeira execução.
     * @return um objeto de Conexão com o banco de dados.
     */
    public static Connection getConnection() {
        File targetDbFile = new File(TARGET_DB_PATH);
        File dbFolder = new File(DB_FOLDER);

        // 2. Verifica se a pasta de dados do aplicativo existe
        if (!dbFolder.exists()) {
            dbFolder.mkdirs(); // Cria o diretório se não existir
        }

        // 3. Verifica se o arquivo de banco de dados JÁ existe no local gravável
        if (!targetDbFile.exists()) {
            try {
                // 4. Se não existe, carrega o arquivo inicial EMPACOTADO (recurso somente leitura)
                InputStream input = DatabaseConnection.class.getResourceAsStream("/rh_database.db");
                
                // >>> ADICIONE ESTE BLOCO DE VERIFICAÇÃO ABAIXO! <<<
                if (input == null) {
                    System.err.println("DIAGNÓSTICO FATAL: O arquivo rh_database.db NÃO foi encontrado dentro do JAR (Classpath).");
                    throw new RuntimeException("Arquivo de banco de dados não encontrado como recurso. Verifique o caminho '/rh_database.db' dentro do JAR.");
                }
                // >>> FIM DO BLOCO DE VERIFICAÇÃO <<<
                
                // 5. Copia o recurso para o local gravável no disco
                Files.copy(input, targetDbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                input.close();
                System.out.println("Banco de dados inicial copiado para: " + TARGET_DB_PATH);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Falha ao inicializar o banco de dados no local gravável.");
                return null;
            }
        }
        
        // 6. Conecta-se sempre ao arquivo NOVO/COPIADO no local gravável
        String url = "jdbc:sqlite:" + TARGET_DB_PATH;

        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erro de conexão com o banco de dados no caminho: " + url);
            return null;
        }
    }
}