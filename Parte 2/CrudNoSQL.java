import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrudNoSQL {

    // COLE A SUA URL DA AWS AQUI NOVAMENTE
    private static final String URI = "mongodb+srv://professor:professor123@clusteruniversidade.kxgn39p.mongodb.net/?appName=ClusterUniversidade";
    private static final String DATABASE_NAME = "UniversidadeDB";
    private static final String COLLECTION_NAME = "estudantes";

    public static void main(String[] args) {
        
        // --- ESTAS DUAS LINHAS SILENCIAM A "FOFOCA" DO MONGODB ---
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        // ---------------------------------------------------------

        try (MongoClient mongoClient = MongoClients.create(URI);
             Scanner scanner = new Scanner(System.in)) {
             
            System.out.println("[SUCESSO] Conectado ao MongoDB Atlas na AWS!");
            
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            int opcao = -1;

            while (opcao != 0) {
                System.out.println("\n==================================");
                System.out.println("     MENU CRUD NOSQL (MONGODB)    ");
                System.out.println("==================================");
                System.out.println("1 - INSERIR novo aluno (Documento Embutido)");
                System.out.println("2 - LER dados de um aluno");
                System.out.println("3 - ATUALIZAR nota de um aluno");
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
                        inserirDados(collection, scanner);
                        break;
                    case 2:
                        lerDados(collection, scanner);
                        break;
                    case 3:
                        atualizarDados(collection, scanner);
                        break;
                    case 4:
                        deletarDados(collection, scanner);
                        break;
                    case 0:
                        System.out.println("Saindo do sistema NoSQL. Ate logo!");
                        break;
                    default:
                        System.out.println("Opcao invalida! Tente novamente.");
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao conectar no MongoDB: " + e.getMessage());
        }
    }

    private static void inserirDados(MongoCollection<Document> collection, Scanner scanner) {
        System.out.println("--- CADASTRAR NOVO ALUNO (NoSQL) ---");
        
        System.out.print("Digite a Matricula do aluno (Ex: 202400028810): ");
        String matricula = scanner.nextLine().trim();

        System.out.print("Digite o CPF (apenas numeros): ");
        long cpf = scanner.nextLong();
        scanner.nextLine(); 

        System.out.print("Digite o Nome do aluno: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Digite a nota do aluno (Ex: 8,5): ");
        double mc = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Digite o ID do Curso (Ex: 4 para Eng. de Computacao): ");
        int idCurso = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Digite um ID unico para o vinculo (Ex: 800): ");
        int idVinculo = scanner.nextInt();
        scanner.nextLine();

        Document usuario = new Document("nome", nome)
                .append("login", "user" + idVinculo)
                .append("senha", "senha123");

        Document vinculo = new Document("idvinculo", idVinculo)
                .append("idcurso", idCurso)
                .append("status", "Ativo");

        Document estudante = new Document("_id", matricula) 
                .append("cpf", cpf)
                .append("mc", mc)
                .append("ano_ingresso", 2026)
                .append("usuario", usuario) 
                .append("vinculos", Arrays.asList(vinculo)); 

        try {
            collection.insertOne(estudante);
            System.out.println("-> Aluno [" + matricula + "] inserido com sucesso no MongoDB!");
        } catch (Exception e) {
            System.err.println("-> [ERRO] Falha ao inserir: " + e.getMessage());
        }
    }

    private static void lerDados(MongoCollection<Document> collection, Scanner scanner) {
        System.out.println("--- CONSULTAR ALUNO ---");
        System.out.print("Digite a Matricula que deseja buscar: ");
        String matricula = scanner.nextLine().trim(); 
        
        Document aluno = collection.find(Filters.eq("_id", matricula)).first();
        
        if (aluno != null) {
            System.out.println("\n[RESULTADO DA BUSCA NO MONGODB]");
            System.out.println(aluno.toJson()); 
        } else {
            System.out.println("-> Aluno nao encontrado!");
        }
    }

    private static void atualizarDados(MongoCollection<Document> collection, Scanner scanner) {
        System.out.println("--- ATUALIZAR NOTA ---");
        System.out.print("Digite a Matricula do aluno: ");
        String matricula = scanner.nextLine().trim(); 

        System.out.print("Digite a nova nota (Ex: 9,5): ");
        double novaNota = scanner.nextDouble();
        scanner.nextLine();
        
        var resultado = collection.updateOne(
                Filters.eq("_id", matricula), 
                Updates.set("mc", novaNota)
        );
        
        if (resultado.getModifiedCount() > 0) {
            System.out.println("-> Nota atualizada com sucesso!");
        } else {
            System.out.println("-> Aluno nao encontrado ou nota já era igual.");
        }
    }

    private static void deletarDados(MongoCollection<Document> collection, Scanner scanner) {
        System.out.println("--- DELETAR ALUNO ---");
        System.out.print("Digite a Matricula do aluno a ser deletado: ");
        String matricula = scanner.nextLine().trim(); 
        
        var resultado = collection.deleteOne(Filters.eq("_id", matricula));
        
        if (resultado.getDeletedCount() > 0) {
            System.out.println("-> Aluno removido completamente do MongoDB!");
        } else {
            System.out.println("-> Aluno nao encontrado.");
        }
    }
}