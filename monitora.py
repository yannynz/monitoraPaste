import os
import re
try:
    import watchdog
except ImportError:
    print("A API Watchdog não está instalada.")
    print("Instalando a API Watchdog e suas dependências...")
    os.system("pip install watchdog")

# Verificar se as dependências também estão instaladas
try:
    import pywin32
except ImportError:
    print("A biblioteca pywin32 não está instalada.")
    print("Instalando a biblioteca pywin32...")
    os.system("pip install pywin32")

try:
    import psutil
except ImportError:
    print("A biblioteca psutil não está instalada.")
    print("Instalando a biblioteca psutil...")
    os.system("pip install psutil")

print("A API Watchdog e suas dependências estão instaladas.")

from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

# Pasta a ser monitorada
pasta_monitorada = "~\Downloads\monitoraPaste-main\monitoraPaste-main\teste"

# Função para extrair dados do título do arquivo
# Extrair dados do título do arquivo usando regex
def extrair_dados(arquivo):
    # Definir a regex
    regex = r"NR(\d+)(\w+)"
    # Extrair os dados usando a regex
    match = re.search(regex, arquivo)
    if match:
        nr = match.group(1)
        cliente = match.group(2)
        return nr, cliente
    else:
        return None, None

# Testar a função com um exemplo
# nome_arquivo = "NR508416SUTTO.txt"
# nr, cliente = extrair_dados(nome_arquivo)

if nr and cliente:
    print(f"NR: {nr}")
    print(f"Cliente: {cliente}")
else:
    print("Dados não encontrados no título do arquivo.")

# Classe para manipular eventos da pasta monitorada
class ManipuladorEventos(FileSystemEventHandler):
    def on_created(self, event):
        # Verificar se o evento foi na pasta monitorada e se é um arquivo
        if event.src_path.startswith(pasta_monitorada) and os.path.isfile(event.src_path):
            # Extrair dados do título do arquivo
            nome_arquivo = os.path.basename(event.src_path)
            nr, cliente = extrair_dados(nome_arquivo)
            # Exibir dados em tempo real
            print(f"Novo arquivo detectado: {nome_arquivo}")
            print(f"NR: {nr}")
            print(f"Cliente: {cliente}")
            print("---")

# Criar e iniciar observador da pasta
observer = Observer()
observer.schedule(ManipuladorEventos(), pasta_monitorada, recursive=True)
observer.start()

# Manter o script em execução
try:
    while True:
        time.sleep(1)
except KeyboardInterrupt:
    observer.stop()
    observer.join()
