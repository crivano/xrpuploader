# xrpuploader

Para executar siga o procedimento abaixo:

1. Instale o [Java](https://www.java.com/pt_BR/download/) se não tiver ele na sua máquina

2. Copie o arquivo [XrpUploader.java](https://raw.githubusercontent.com/crivano/xrpuploader/master/src/br/com/xrp/uploader/XrpUploader.java) para o diretório onde estão os arquivos PDF

3. Abra o Prompt de Comando, vá até o diretório e execute: 
```
javac XrpUploader.java
```

4. Execute: 
```
java XrpUploader *URL* "*LISTA DE CAMPOS SEPARADA POR VÍRGULA*"
```

O primeiro parâmetro é a URL que receberá os documentos, isso tem que ser obtido da equipe responsável pelo XRP.

O segundo parâmetro é uma indicação de quais campos serão identificados no nome do arquivo. 
Por exemplo, um arquivo chamado 2018_12_01_01_17.pdf poderia ter seus campos identificados por "ANO,MES,DIA,LIVRO,PAGINA".
