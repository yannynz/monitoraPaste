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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonitoraPaste {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Pasta a ser monitorada
        File pastaMonitorada = new File("C:\\Users\\logonrmlocal\\Downloads\\monitoraPaste-main\\monitoraPaste-main\\teste");

        // Verificar se a pasta existe, se não, criá-la
        if (!pastaMonitorada.exists()) {
            pastaMonitorada.mkdirs();
        }

        // Função para extrair dados do título do arquivo
        // Extrair dados do título do arquivo usando regex
        Pattern pattern = Pattern.compile("NR(\\d+)(\\w+)");

        // Criar e iniciar observador da pasta
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(pastaMonitorada.getPath());
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

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
                            String nr = matcher.group(1);
                            String cliente = matcher.group(2);
                            System.out.println("Novo arquivo detectado: " + nomeArquivoCriado);
                            System.out.println("NR: " + nr);
                            System.out.println("Cliente: " + cliente);
                            System.out.println("---");
                        }
                    }
                }
            }
            watchKey.reset();
        }
    }
}