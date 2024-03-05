import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public static void main(String[] args) throws IOException, InterruptedException, SQLException {
    // Pasta a ser monitorada
    File pastaMonitorada = new File("C:\\Users\\logonrmlocal\\Downloads\\monitoraPaste\\teste");

    // Verificar se a pasta existe, se não, criá-la
    if (!pastaMonitorada.exists()) {
        System.out.println("a pasta não existe");
    }

    // Função para extrair dados do título do arquivo
    // Extrair dados do título do arquivo usando regex
    Pattern pattern = Pattern.compile("NR(\\d+)(\\w+)");

    // Criar e iniciar observador da pasta
    WatchService watchService = FileSystems.getDefault().newWatchService();
    Path path = Paths.get(pastaMonitorada.getPath());
    path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

    // Create a connection to the database
    String url = "jdbc:oracle:thin:@orcl.fiap.com.br:1521:orcl";
    String username = "rm93609";
    String password = "220804";
    Connection conn = DriverManager.getConnection(url, username, password);

    // Manter o script em execução
    while (true) {
        WatchKey watchKey = watchService.take();
        for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
            if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                Path arquivoCriado = path.resolve((Path) watchEvent.context());
                if (Files.isRegularFile(arquivoCriado)) {
                    String nomeArquivoCriado = arquivoCriado.getFileName().toString();
                    Matcher matcher = pattern.matcher(nomeArquivoCriado);
                    if (matcher.find()) {
                        String cliente = matcher.group(2);
                        Date lastModifiedDate = new Date(Files.getLastModifiedTime(arquivoCriado).toMillis());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String creationDate = dateFormat.format(lastModifiedDate);
                        String nrStr = matcher.group(1);
                        final int nr = Integer.parseInt(nrStr);
                        String sql = "INSERT INTO Delivery (NR, CLIENTE, DATACRIACAO,) VALUES (?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, nr);
                        pstmt.setString(2, cliente);
                        pstmt.setDate(3, new java.sql.Date(lastModifiedDate.getTime()));
                        pstmt.executeUpdate();
                        System.out.println("Novo arquivo detectado: " + nomeArquivoCriado);
                        System.out.println("NR: " + nr);
                        System.out.println("Cliente: " + cliente);
                        System.out.println("Creation Date: " + creationDate);
                        System.out.println("---");
                    }
                }
            }
        }
    }
}