import java.sql.*;
import java.util.Scanner;

public class CrudRelacional {

    // Credenciais do seu banco de dados na AWS
    private static final String HOST = "bancodedados-enzoliborio.c2nun5fvh9bh.us-east-1.rds.amazonaws.com";
    private static final String DATABASE = "BancoDeDados_EnzoLiborio";
    private static final String URL = "jdbc:postgresql://" + HOST + "/" + DATABASE;
    private static final String USER = "professor";
    private static final String PASSWORD = "professor";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do PostgreSQL não encontrado.");
            throw new SQLException(e);
        }
    }

    public static void main(String[] args) {
        
        try (Connection conn = conectar(); Scanner scanner = new Scanner(System.in)) {
            System.out.println("[SUCESSO] Conectado ao Amazon RDS PostgreSQL!");

            int opcao = -1;

            while (opcao != 0) {
                System.out.println("\n==================================");
                System.out.println("       MENU CRUD UNIVERSIDADE     ");
                System.out.println("==================================");
                System.out.println("1 - INSERIR novo aluno");
                System.out.println("2 - LER dados de um aluno");
                System.out.println("3 - ATUALIZAR nota (MC) de um aluno");
                System.out.println("4 - DELETAR um aluno");
                System.out.println("0 - SAIR");
                System.out.print("-> Escolha uma opcao: ");
                
                if (scanner.hasNextInt()) {
                    opcao = scanner.nextInt();
                    scanner.nextLine(); 
                } else {
                    scanner.nextLine();
                    continue;
                }

                System.out.println("----------------------------------");

                switch (opcao) {
                    case 1:
                        inserirDados(conn, scanner);
                        break;
                    case 2:
                        lerDados(conn, scanner);
                        break;
                    case 3:
                        atualizarDados(conn, scanner);
                        break;
                    case 4:
                        deletarDados(conn, scanner);
                        break;
                    case 0:
                        System.out.println("Saindo do sistema. Ate logo!");
                        break;
                    default:
                        System.out.println("Opcao invalida! Tente novamente.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro no banco de dados: " + e.getMessage());
        }
    }

    // ==========================================
    // 1. FUNÇÃO DE INSERIR (CREATE) COM TRANSAÇÃO
    // ==========================================
    private static void inserirDados(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("--- CADASTRAR NOVO ALUNO ---");
        
        System.out.print("Digite o CPF (apenas numeros): ");
        long cpf = scanner.nextLong();
        scanner.nextLine(); 

        System.out.print("Digite o Nome do aluno: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Digite a Matricula do aluno (Ex: 202400028810): ");
        String matricula = scanner.nextLine().trim();

        System.out.print("Digite a nota/MC do aluno (Ex: 8,5): ");
        double mc = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Digite o ID do Curso (Ex: 4 para Eng. de Computacao): ");
        int idCurso = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Digite um ID unico para o vinculo (Ex: 800): ");
        int idVinculo = scanner.nextInt();
        scanner.nextLine();

        // INICIA A TRANSAÇÃO: Ou salva as três tabelas, ou não salva nenhuma.
        conn.setAutoCommit(false);

        try {
            // 1. Tabela USUARIO
            String sqlUsuario = "INSERT INTO universidade.usuario (cpf, nome, login, senha) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {
                stmt.setLong(1, cpf);
                stmt.setString(2, nome);
                stmt.setString(3, "user" + idVinculo); // Login seguro gerado pelo sistema
                stmt.setString(4, "senha123");
                stmt.executeUpdate();
            }

            // 2. Tabela ESTUDANTE
            String sqlEstudante = "INSERT INTO universidade.estudante (mat_estudante, cpf, mc, ano_ingresso) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlEstudante)) {
                stmt.setString(1, matricula);
                stmt.setLong(2, cpf);
                stmt.setDouble(3, mc);
                stmt.setInt(4, 2026);
                stmt.executeUpdate();
            }

            // 3. Tabela VINCULO
            String sqlVinculo = "INSERT INTO universidade.vinculo (idvinculo, mat_estudante, curso, status) VALUES (?, ?, ?, ?::universidade.status_estudante)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlVinculo)) {
                stmt.setInt(1, idVinculo); 
                stmt.setString(2, matricula);
                stmt.setInt(3, idCurso); 
                stmt.setString(4, "Ativo");
                stmt.executeUpdate();
            }

            // Confirma no banco!
            conn.commit();
            System.out.println("-> Aluno [" + matricula + "] inserido com sucesso!");

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("-> [ERRO] Falha ao inserir. Nenhuma informacao foi salva. Detalhe: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ==========================================
    // 2. FUNÇÃO DE LER (READ)
    // ==========================================
    private static void lerDados(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("--- CONSULTAR ALUNO ---");
        System.out.print("Digite a Matricula que deseja buscar (Ex: 202400028810): ");
        String matricula = scanner.nextLine().trim(); 
        
        String sql = "SELECT u.nome, e.mat_estudante, e.mc, c.nome AS curso, v.status " +
                     "FROM universidade.estudante e " +
                     "JOIN universidade.usuario u ON e.cpf = u.cpf " +
                     "JOIN universidade.vinculo v ON e.mat_estudante = v.mat_estudante " +
                     "JOIN universidade.curso c ON v.curso = c.idcurso " +
                     "WHERE e.mat_estudante = ?";
                     
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, matricula);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("\n[RESULTADO DA BUSCA]");
                System.out.println("Nome: " + rs.getString("nome"));
                System.out.println("Matricula: " + rs.getString("mat_estudante"));
                System.out.println("Nota (MC): " + rs.getDouble("mc"));
                System.out.println("Curso: " + rs.getString("curso"));
                System.out.println("Status: " + rs.getString("status"));
            } else {
                System.out.println("-> Aluno nao encontrado!");
            }
        }
    }

    // ==========================================
    // 3. FUNÇÃO DE ATUALIZAR (UPDATE)
    // ==========================================
    private static void atualizarDados(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("--- ATUALIZAR NOTA (MC) ---");
        System.out.print("Digite a Matricula do aluno (Ex: 202400028810): ");
        String matricula = scanner.nextLine().trim(); 

        System.out.print("Digite a nova nota (Ex: 9,5): ");
        double novaNota = scanner.nextDouble();
        scanner.nextLine();
        
        String sql = "UPDATE universidade.estudante SET mc = ? WHERE mat_estudante = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, novaNota);
            stmt.setString(2, matricula);
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                System.out.println("-> Nota atualizada com sucesso!");
            } else {
                System.out.println("-> Aluno nao encontrado no banco de dados!");
            }
        }
    }

    // ==========================================
    // 4. FUNÇÃO DE DELETAR (DELETE)
    // ==========================================
    private static void deletarDados(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("--- DELETAR ALUNO ---");
        System.out.print("Digite a Matricula do aluno a ser deletado (Ex: 202400028810): ");
        String matricula = scanner.nextLine().trim(); 
        
        System.out.print("Digite o CPF desse mesmo aluno para confirmar (apenas numeros): ");
        long cpf = scanner.nextLong();
        scanner.nextLine();

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM universidade.vinculo WHERE mat_estudante = '" + matricula + "'");
            stmt.executeUpdate("DELETE FROM universidade.estudante WHERE mat_estudante = '" + matricula + "'");
            int linhasUsuario = stmt.executeUpdate("DELETE FROM universidade.usuario WHERE cpf = " + cpf);
            
            if (linhasUsuario > 0) {
                System.out.println("-> Aluno e vinculos removidos completamente do banco!");
            } else {
                System.out.println("-> Nenhuma informacao deletada. Verifique se os dados estao corretos.");
            }
        }
    }
}