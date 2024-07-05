	

# Paint Colaborativo com Protocolo TCP em Java

## Descrição

Este projeto é um aplicativo de pintura colaborativo desenvolvido em Java utilizando Swing para a interface gráfica e o protocolo TCP para comunicação em rede. Vários usuários podem se conectar ao servidor e desenhar simultaneamente em um canvas compartilhado.

## Pré-requisitos

- **Java 8** ou superior
- **IDE** (como IntelliJ IDEA, Eclipse, etc.)

## Instalação

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/Taih-Marques/Projeto-Redes
   cd repo
   ```

2. **Compile o projeto:**
   ```bash
   javac -cp src src/Cliente/Cliente.java 
   javac -cp src src/Servidor/Servidor.javas
   ```

## Como Usar

- Inicie o servidor executando Serveidor.java.
- Inicie vários clientes executando Cliente.java.

3. **Comece a desenhar:**
   - Use as ferramentas de desenho fornecidas para criar arte colaborativa. 
  - Um jogador começa a desenhar e os outros tentam adivinhar.
  - O primeiro a acertar a palavra vence a rodada e o jogo continua


## Arquitetura do Sistema

### ServerService.java
- **Função:** Gerencia conexões dos clientes, inicia a partida e a gerencia. Isso inclue:
- Evitar que jogadores tenham o nome repetido;
- Verificar se o número mínimo de jogadores foi atingido (2 jogadores)
- Escolhe aleatoriamente um dos jogadores como desenhista
- Atribuir ao desenhista um desenho
- Verificar se houve acerto e encerrar o jogo, comunicando o jogador vencedor.

-ServidorService se comunica com ClienteService via a classe interna SocketListener. Ela é responsável por receber as mensagens do cliente e responder adequadamente.

- **Porta padrão:**  5050

### ClienteService.java
- **Função:** Instância um socket conectado ao servidor e passa para JogadorFrame, além de enviar mensagens para o mesmo.


### JogadorFrame.java
- **Função:** Recebe as entradas do usuário como:
- O desenho quando o jogador é o desenhista
- Os chutes quando o jogador não é o desenhista
- O nome de usuário no início da partida
- Os comandos relativos a conexão como se conectar, sair e reiniciar

-E as mensagens oriundas do servidor. Através da classe ListenerServer que as interpreta e toma as medidas necessárias. Por exemplo: se o jogador não for escolhido como desenhista, ela desativa as entradas de mouse para desenho e habilita a entrada para os chutes.

### Paint.java
- **Função:** Classe responsável por construir um desenho de acordo com o input do usuário.

## Desenhavel.java

-Gerencia os desenhos feitos, permitindo que sejam refeitos, desfeitos ou que toda a tela seja limpa
- Gerencia as cores de contorno e de preenchimento
- Permite o uso de diversas formas como traço livre, quadriláteros e elipses. 


### Mensagem.java
- **Função:** Representa uma mensagem sendo trocada entre o cliente e o servidor. Nela vai o id de origem, o conteúdo textual da mensagem, um array de Desenhavel, isto é, as figuras que são desenhadas na tela e uma constante Acao que indica o propósito da mensagem. Entre os valores de Acao está:

- DESENHO: indica que o conteúdo sendo transmitido é um desenho;
- CHUTE: indica que o conteúdo é um chute.
- PERDEU: comunica a derrota para um dos jogadores
- GANHOU: indica que o jogador descobriu o desenho
- CONECTAR: uma solicitação de conexão com o servidor
- RECUSADO: resposta negativa à solicitação de conectar.
- ROLE_DESENHISTA: comunica que o jogador é o desenhista da partida
- RECOMECER: comunica o inicio de uma nova partida com os jogadores atuais
- DESENHO_ADIVINHADO: comunica que o desenho sendo feito foi adivinhado
- DESENHISTA_SAIU: comunica que o desenhista da partida saiu


### Protocolo da camada de transporte
- **TCP:** Garante a entrega confiável das mensagens entre o servidor e os clientes.

### Protocolo da camada de aplicação:

O servidor e o cliente transmitem instancias da classe Mensagem. O principal campo é ação que indica o propósito da mensagem e permite que as partes a interprete. Id indica qual cliente enviou a mensagem e é usado pelo servidor para controle. Conteúdo é o conteúdo textual da mensagem, podendo ser mensagens do servidor para o cliente ou do cliente para o servidor. FormasDesenhadas é uma coleção de Desenhavel, representando os objetos que foram desenhados pelo desenhista.

### A seguir, as ações do servidor:

O cliente envia uma mensagem para o servidor com seu nome de usuário e ação CONECTAR. </br>
O servidor verifica a disponibilidade do nome. Caso esteja disponível, ele responde com CONECTAR e com uma mensagem indicando a espera de mais jogadores.</br>
Um jogador pode se desconectar a qualquer momento. O jogador que se desconectou recebe uma mensagem com ação DESCONECTAR. </br>
Caso o desenhista se desconectou, os outros jogadores recebem uma mensagem de ação DESENHISTA_SAIU, indicando que o jogo não pode continuar. </br>
Caso negativo, ele responde com RECUSADO. </br>
Após o ServidorService verifica se há o número suficiente de jogadores, 2 ou mais. </br>
Caso negativo, ele aguarda até que mais alguém se conecte. </br>
Caso positivo, ele sorteia aleatoriamente um desenhista e um desenho. </br>
O jogador desenhista recebe uma mensagem com ROLE_DESENHISTA e o objeto a ser desenhado em conteúdo. </br>
Os demais recebem uma mensagem com ROLE_ADIVINHADOR e a mensagem de tente adivinhar o desenho no conteúdo. </br>
O servidor recebe o desenho do desenhista com a ação DESENHO e um array de Desenhavel; os objetos por ele desenhados. Ele então transmite essa mensagem para os demais jogadores. </br>
Ele recebe também os chutes dos jogadores. Cada cliente preenche a mensagem com o seu id, o chute em conteúdo e a ação CHUTE. </br>
O servidor verifica se o chute está correto. </br>
Caso algum jogador acerte, ele recebe uma mensagem com ação GANHOU. Os demais jogadores recebem uma mensagem com PERDEU. </br>
O desenhista recebe uma mensagem com DESENHO_ADIVINHADO. </br>
Assim o jogo se encerra e caso algum jogador queira continuar, é feito um novo sorteio. </br>

### Agora, as ações do cliente:

O cliente envia uma mensagem com ação CONECTAR e o id do jogador para o servidor. </br>
Caso a resposta seja RECUSADO, o cliente exibe o conteúdo da mensagem e aguarda uma ação do usuário. </br>
Caso seja conectado, o cliente bloqueia os botões de conectar e habilitar o de desconectar. Em seguida, aguarda o jogo ser iniciado. </br>
Quando ele recebe uma mensagem com ação ROLE_DESENHISTA, ele habilita a entrada para desenho e exibe o conteúdo com o tema do desenho para o usuário </br>
Já quando a ação é ROLE_ADIVINHADOR, ele habilita a entrada para chutes e exibe a mensagem do servidor </br>
Quando o desenhista desenha, o cliente obtém a lista de formas desenhadas e envia uma mensagem para o servidor com ação DESENHO. </br>
Quando o jogador não é o desenhista e recebe uma mensagem com ação DESENHO, ele exibe todo o conteúdo de formasDesenhadas na tela. </br>
Quando o jogador é o adivinhador e faz um chute, o cliente cria uma mensagem com o seu id, o conteúdo como o chute e ação CHUTE </br>
Quando qualquer tipo de jogador uma mensagem com ação CHUTE, ele exibe o conteúdo na área de chutes </br>
Quando o desenhista cliente recebe uma mensagem com DESENHO_ADIVINHADO, ele exibe uma mensagem de vitória na tela com o nome de quem adivinhou </br>
Quando o adivinhador recebe uma mensagem de GANHOU na tela, ele exibe uma mensagem de vitória. E de derrota quando a ação é PERDEU. </br>
Quando a ação é desenhista saiu, ele habilita a opção de reiniciar o jogo </br>
Quando o jogador solicita a saída, ele cria uma mensagem com ação de DESCONECTAR com o id de seu jogador e aguarda receber uma resposta de DESCONECTAR. </br>
Quando ele recebe, o cliente fecha o socket e reabilita a opção de conectar. </br>


## FUNCIONAMENTO DO SOFTWARE:
O software relata uma simulação voltada para um jogo de adivinhação de desenho. O programa funciona da seguinte forma: o jogo é composto por dois jogadores (desenhista e adivinhador) que estarão do lado cliente do servidor. O desenhista irá desenhar um desenho que será sorteado de forma aleatória pelo programa, e o adivinhador tentará adivinhar esse desenho através de "chutes". No momento em que o mesmo acerta o nome do desenho, mostra-se uma mensagem de "acerto" e o jogo é reiniciado.

### Por que usar o TCP:
Principalmente porque a aplicação não admite perda de pacotes. É preciso que o desenho feito no jogador ‘A’ chegue a todos os outros jogadores sem perda.

## Decisão pelo Uso do Protocolo TCP

### Motivação

A escolha do protocolo TCP (Transmission Control Protocol) para o desenvolvimento deste jogo de pintura colaborativo foi baseada em várias considerações técnicas e requisitos específicos do projeto. A seguir, são destacadas as principais razões para essa escolha:

### Confiabilidade

O TCP é um protocolo orientado a conexão que garante a entrega confiável dos dados. Em um ambiente de jogo colaborativo, onde a precisão e a consistência das mensagens são cruciais (como os comandos de desenho e as tentativas de adivinhação), o TCP assegura que todas as mensagens sejam entregues na ordem correta e sem perdas.

### Controle de Fluxo e Congestionamento

O TCP inclui mecanismos de controle de fluxo e controle de congestionamento, que são essenciais para manter a estabilidade e o desempenho da rede, especialmente em ambientes onde múltiplos jogadores estão se comunicando simultaneamente. Esses mecanismos ajudam a evitar a sobrecarga da rede e garantem uma experiência de jogo suave para todos os participantes.

### Integridade dos Dados

O TCP verifica a integridade dos dados através de checksums, assegurando que qualquer dado corrompido durante a transmissão seja detectado e retransmitido. Essa característica é fundamental para um jogo de pintura colaborativo, onde a precisão dos comandos de desenho é essencial para a jogabilidade.

### Estabelecimento de Conexão

O processo de estabelecimento de conexão do TCP (handshake de três vias) garante que uma conexão seja corretamente iniciada e validada antes da troca de dados, o que é importante para manter a segurança e a consistência do estado do jogo entre o servidor e os clientes.

### Comparação com UDP

Embora o UDP (User Datagram Protocol) ofereça menor latência devido à sua natureza sem conexão e não confiável, ele não seria adequado para este projeto devido à falta de garantias de entrega e ordem das mensagens. Em um jogo de pintura colaborativo, a perda de mensagens ou a recepção fora de ordem pode resultar em inconsistências significativas e comprometer a experiência do usuário.

### Conclusão

Considerando os requisitos de confiabilidade, controle de fluxo, integridade dos dados e estabelecimento de conexão, o protocolo TCP foi a escolha natural para este projeto. Ele proporciona um equilíbrio entre desempenho e segurança, garantindo que todos os jogadores tenham uma experiência de jogo fluida e consistente.


## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

## Autores

- **João Victor** - *Desenvolvedor* -
 [GitHub](https://github.com/JoaoVictor55)
- **Tainah Marques** - *Desenvolvedora* -
[GitHub](https://github.com/Taih-Marques)
- **Gabriel** - *Desenvolvedor* -
 [GitHub](https://github.com/gabrielcarvalh00)

## Agradecimentos

- Agradecimentos a mim porque sem mim eu nao seria eu.

