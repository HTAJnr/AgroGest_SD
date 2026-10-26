# DEPLOY.md — AgroGest: Deploy com systemd (porta 8081)

> Testado em: Ubuntu 22.04 LTS · OpenJDK 25 · Tomcat 10.1 · Outubro 2026

---

## Pré-requisitos

| O quê | Valor |
|---|---|
| Tomcat instalado em | `/opt/tomcat` |
| Java (JAVA_HOME) | `/usr/lib/jvm/java-25-openjdk-amd64` |
| Porta de produção | **8081** |
| Contexto | `/agrogest` |
| Ficheiro de dados | `/opt/tomcat/data/agrogest.ser` |
| WAR gerado por | `mvn clean package` → `target/agrogest.war` |

---

## Passos (executar por ordem; requerem `sudo`)

### 1. Parar o Tomcat em modo desenvolvimento

```bash
tomcat stop
```

### 2. Criar o utilizador de sistema `tomcat`

```bash
sudo useradd -r -U -d /opt/tomcat -s /usr/sbin/nologin tomcat
```

### 3. Transferir a propriedade dos ficheiros para o utilizador `tomcat`

```bash
sudo chown -R tomcat:tomcat /opt/tomcat
sudo chmod +x /opt/tomcat/bin/*.sh
sudo mkdir -p /opt/tomcat/data
sudo chown tomcat:tomcat /opt/tomcat/data
```

### 4. Mudar a porta de 8080 para 8081 no `server.xml`

Editar `/opt/tomcat/conf/server.xml`. Localizar a linha:

```xml
<Connector port="8080" protocol="HTTP/1.1"
```

Mudar `8080` para `8081`. Existem duas ocorrências no bloco do Connector HTTP — mudar ambas.

Verificar depois:

```bash
grep -n 'port=' /opt/tomcat/conf/server.xml
```

### 5. Copiar a unidade systemd para o local correcto

```bash
sudo cp deploy/tomcat.service /etc/systemd/system/tomcat.service
sudo systemctl daemon-reload
```

### 6. Activar e iniciar o serviço

```bash
sudo systemctl enable --now tomcat
sudo systemctl status tomcat
```

### 7. Fazer deploy do WAR

Compilar o projecto e copiar o WAR:

```bash
mvn clean package
sudo rm -rf /opt/tomcat/webapps/agrogest /opt/tomcat/webapps/agrogest.war
sudo cp target/agrogest.war /opt/tomcat/webapps/
sudo chown tomcat:tomcat /opt/tomcat/webapps/agrogest.war
sudo systemctl restart tomcat
```

Abrir no browser: `http://localhost:8081/agrogest/`

### 8. (Opcional) Abrir a porta no firewall

Se `ufw` estiver activa:

```bash
sudo ufw allow 8081/tcp
sudo ufw status
```

### 9. Verificar os logs

```bash
# Log do serviço systemd
journalctl -u tomcat -e

# Log do Catalina (saída do Tomcat)
sudo tail -f /opt/tomcat/logs/catalina.out
```

---

## Teste de persistência

1. Criar dados na aplicação (registo, produtos, vendas).
2. Reiniciar o serviço:
   ```bash
   sudo systemctl restart tomcat
   ```
3. Verificar que os dados continuam e que o próximo `id` de factura é `n+1`.
4. Confirmar o ficheiro de dados:
   ```bash
   sudo ls -la /opt/tomcat/data/
   ```

---

## Gestão do serviço

| Comando | O que faz |
|---|---|
| `sudo systemctl start tomcat` | Inicia o Tomcat |
| `sudo systemctl stop tomcat` | Para o Tomcat (aguarda `TimeoutStopSec=60 s` para gravar os dados) |
| `sudo systemctl restart tomcat` | Para e reinicia |
| `sudo systemctl status tomcat` | Mostra o estado |
| `sudo systemctl enable tomcat` | Activa o arranque automático com o sistema |
| `sudo systemctl disable tomcat` | Desactiva o arranque automático |

> **Atenção:** depois de transferir a propriedade para o utilizador `tomcat` (passo 3), **não usar a função `tomcat` do `~/.bashrc`** em paralelo com o `systemctl`. A função usa o teu utilizador e pode criar ficheiros com permissões erradas.

---

## Explicação da unidade systemd (`tomcat.service`)

```ini
[Unit]
Description=Apache Tomcat 10.1 (AgroGest)
After=network.target          # Só arranca depois da rede estar pronta
```

```ini
[Service]
Type=forking                  # O processo pai termina após lançar o filho (comportamento do startup.sh)
User=tomcat                   # Corre com o utilizador sem privilégios "tomcat"
Group=tomcat
Environment=JAVA_HOME=…       # Diz ao Tomcat onde está o JDK
Environment=CATALINA_HOME=…   # Pasta raiz do Tomcat
Environment=CATALINA_BASE=…   # Pasta de configuração (igual à HOME neste caso)
Environment=CATALINA_PID=…    # Ficheiro com o PID do processo — o systemd usa-o para saber se morreu
PIDFile=…                     # Confirma ao systemd qual o ficheiro de PID
ExecStart=…/startup.sh        # Comando para iniciar
ExecStop=…/shutdown.sh 30     # Comando para parar (30 s de espera graciosa)
TimeoutStopSec=60             # O systemd espera 60 s antes de matar o processo à força
                               # IMPORTANTE: a paragem gracosa do Tomcat grava o agrogest.ser;
                               # se o systemd matar o processo antes, perdemos dados.
Restart=on-failure            # Reinicia automaticamente se o processo morrer com erro
RestartSec=5                  # Espera 5 s antes de tentar reiniciar
SuccessExitStatus=143         # SIGTERM (128+15=143) é saída normal para Java; não marcar como falha
```

```ini
[Install]
WantedBy=multi-user.target    # Arranca no runlevel normal (texto + rede, sem GUI obrigatória)
```

---

## Credenciais de teste

| Perfil | Email | Palavra-passe |
|---|---|---|
| Administrador | `admin@agro.mz` | `admin234` |
| Cliente demo | `joao@teste.mz` | `1234` |

---

## Ficheiros relevantes

| Ficheiro | Função |
|---|---|
| `deploy/tomcat.service` | Unidade systemd (fonte) |
| `/etc/systemd/system/tomcat.service` | Unidade instalada no sistema |
| `/opt/tomcat/conf/server.xml` | Configuração do Tomcat (porta 8081) |
| `/opt/tomcat/data/agrogest.ser` | Dados serializados em produção |
| `/opt/tomcat/data/agrogest.ser.bak` | Cópia de segurança automática antes de cada gravação |
| `/opt/tomcat/logs/catalina.out` | Log principal do Tomcat |
