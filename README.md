# XrpUploader

Para executar siga o procedimento abaixo:

1. Instale o [Java](https://www.java.com/pt_BR/download/) se não tiver ele na sua máquina

2. Copie o arquivo [XrpUploader.java](https://raw.githubusercontent.com/crivano/xrpuploader/master/src/br/com/xrp/uploader/XrpUploader.java) para o diretório onde estão os arquivos PDF

3. Abra o Prompt de Comando, vá até o diretório e execute: 
```
javac XrpUploader.java
```

4. Execute: 
```
java XrpUploader *DIRETÓRIO* *URL*
```

O primeiro parâmetro é o diretório onde se encontram os arquivos PDF. Subdiretórios também serão considerados.

O segundo parâmetro é a URL que receberá os documentos, isso tem que ser obtido da equipe responsável pelo XRP.

A URL inclui uma indicação de quais campos serão identificados no nome do arquivo. 
Por exemplo, um arquivo chamado 2018_12_01_01_17.pdf poderia ter seus campos identificados por "ANO,MES,DIA,LIVRO,PAGINA".

### Campos Especiais

Alguns nomes de campos têm um significado especial para o XRP, conforme lista abaixo:

* ANO_MES_DIA: data completa no formato AAAAMMDD, ex.: 20181231
* ANO_MES: ano e mês no formato AAAAMM, ex.: 201812
* ANO: ano no formato AAAA, ex.: 2018
* MES: mês no formato MM, ex.: 12
* DIA: ano no formato DD, ex.: 07

Se o mês for omitido e apenas o ano for informado, será assumido janeiro. Se o dia for omitido, será assumido o dia primeiro.

### Arquivos de Controle

Quando o XrpUploader é executado, ele apresenta no console a quantidade total de arquivos PDF encontrados nos diretórios e cada arquivo conforme faz o upload para o XRP. No final, apresenta a quantidade total enviada e também a quantidade de arquivos que não foram enviados devido a algum erro.

Além de apresentar essas informações em tela, ele grava um arquivo com a lista de PDFs enviados, este arquivo se chama xrp-processed-files.txt. Sempre que o XrpUploader é reiniciado, ele lê o xrp-processed-files.txt, vê quais os arquivos que já foram enviados e evita enviá-los novamente. Dessa forma, é possível parar o processamento com ```Ctrl-C``` e retomá-lo mais a frente sem correr o risco de enviar duas vezes o mesmo PDF.

O XrpUploader também gera um arquivo chamado xrp-unprocessed-files.txt, onde ficam registrados todos os arquivos que não foram enviados devido a algum erro.
