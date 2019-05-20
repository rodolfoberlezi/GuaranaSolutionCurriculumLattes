<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 
    <xsl:strip-space elements="*"/>
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
    <xsl:template match="/">
    <xsl:for-each select="//PRODUCAO-BIBLIOGRAFICA">             
        <xsl:for-each select="TRABALHOS-EM-EVENTOS">
		<TRABALHOS-EM-EVENTOS>
			<xsl:for-each select="TRABALHO-EM-EVENTOS">
				<TRABALHO-EM-EVENTOS>
					<xsl:for-each select="DADOS-BASICOS-DO-TRABALHO">
						<TITULO><xsl:value-of select="@TITULO-DO-TRABALHO"/></TITULO>
					</xsl:for-each>
					<SEQUENCIA><xsl:value-of select="@SEQUENCIA-PRODUCAO"/></SEQUENCIA>
					<AUTORES>
					<xsl:for-each select="AUTORES">	
							<xsl:value-of select="@NOME-COMPLETO-DO-AUTOR"/>
							<xsl:if test="position()!=last()">, </xsl:if>
							<xsl:if test="position()=last()">.</xsl:if>		
					</xsl:for-each>
					</AUTORES>
					<xsl:for-each select="DETALHAMENTO-DO-TRABALHO">
						<EVENTO><xsl:value-of select="@NOME-DO-EVENTO"/></EVENTO>
						<LOCAL><xsl:value-of select="@CIDADE-DO-EVENTO"/></LOCAL>
						<ANAIS><xsl:value-of select="@TITULO-DOS-ANAIS-OU-PROCEEDINGS"/></ANAIS>
						<PAGINAS><xsl:value-of select="@PAGINA-INICIAL"/>-<xsl:value-of select="@PAGINA-FINAL"/></PAGINAS>
					</xsl:for-each>
					<xsl:for-each select="DADOS-BASICOS-DO-TRABALHO">
						<ANO><xsl:value-of select="@ANO-DO-TRABALHO"/></ANO>
					</xsl:for-each>
				</TRABALHO-EM-EVENTOS>
			</xsl:for-each>
			</TRABALHOS-EM-EVENTOS>
		</xsl:for-each>
        <xsl:for-each select="//LIVROS-E-CAPITULOS">
		<LIVROS-E-CAPITULOS>
			<xsl:for-each select="LIVROS-PUBLICADOS-OU-ORGANIZADOS">
			<LIVROS-PUBLICADOS-OU-ORGANIZADOS>
			<xsl:for-each select="LIVRO-PUBLICADO-OU-ORGANIZADO">
				<SEQUENCIA><xsl:value-of select="@SEQUENCIA-PRODUCAO"/></SEQUENCIA>
				<xsl:for-each select="DADOS-BASICOS-DO-LIVRO">				
					<TIPO><xsl:value-of select="@TIPO"/></TIPO>
					<NATUREZA><xsl:value-of select="@NATUREZA"/></NATUREZA>
					<TITULO-DO-LIVRO><xsl:value-of select="@TITULO-DO-LIVRO"/></TITULO-DO-LIVRO>
					<ANO><xsl:value-of select="@ANO"/></ANO>
					<IDIOMA><xsl:value-of select="@IDIOMA"/></IDIOMA>
				</xsl:for-each>
				<xsl:for-each select="DETALHAMENTO-DO-LIVRO">				
					<NUMERO-DE-VOLUMES><xsl:value-of select="@NUMERO-DE-VOLUMES"/></NUMERO-DE-VOLUMES>
					<NUMERO-DE-PAGINAS><xsl:value-of select="@NUMERO-DE-PAGINAS"/></NUMERO-DE-PAGINAS>
					<ISBN><xsl:value-of select="@ISBN"/></ISBN>
					<CIDADE-DA-EDITORA><xsl:value-of select="@CIDADE-DA-EDITORA"/></CIDADE-DA-EDITORA>
					<NOME-DA-EDITORA><xsl:value-of select="@NOME-DA-EDITORA"/></NOME-DA-EDITORA>				
				</xsl:for-each>
				<AUTORES>
				<xsl:for-each select="AUTORES">				
						<xsl:value-of select="@NOME-COMPLETO-DO-AUTOR"/>
						<xsl:if test="position()!=last()">, </xsl:if>
						<xsl:if test="position()=last()">.</xsl:if>				
				</xsl:for-each>
				</AUTORES>			
			</xsl:for-each>
			</LIVROS-PUBLICADOS-OU-ORGANIZADOS>
			</xsl:for-each>
			<xsl:for-each select="CAPITULOS-DE-LIVROS-PUBLICADOS">
			<CAPITULOS-DE-LIVROS-PUBLICADOS>
				<xsl:for-each select="CAPITULO-DE-LIVRO-PUBLICADO">
				<CAPITULO-DE-LIVRO-PUBLICADO>
				<SEQUENCIA-PRODUCAO><xsl:value-of select="@SEQUENCIA-PRODUCAO"/></SEQUENCIA-PRODUCAO>
					<xsl:for-each select="DADOS-BASICOS-DO-CAPITULO">					
						<TIPO><xsl:value-of select="@TIPO"/></TIPO>
						<TITULO-DO-CAPITULO-DO-LIVRO><xsl:value-of select="@TITULO-DO-CAPITULO-DO-LIVRO"/></TITULO-DO-CAPITULO-DO-LIVRO>
						<ANO><xsl:value-of select="@ANO"/></ANO>
						<PAIS-DE-PUBLICACAO><xsl:value-of select="@PAIS-DE-PUBLICACAO"/></PAIS-DE-PUBLICACAO>
						<IDIOMA><xsl:value-of select="@IDIOMA"/></IDIOMA>
					</xsl:for-each>
					<xsl:for-each select="DETALHAMENTO-DO-CAPITULO">					
						<TITULO-DO-LIVRO><xsl:value-of select="@TITULO-DO-LIVRO"/></TITULO-DO-LIVRO>
						<NUMERO-DE-VOLUMES><xsl:value-of select="@NUMERO-DE-VOLUMES"/></NUMERO-DE-VOLUMES>
						<PAGINA-INICIAL><xsl:value-of select="@PAGINA-INICIAL"/></PAGINA-INICIAL>
						<PAGINA-FINAL><xsl:value-of select="@PAGINA-FINAL"/></PAGINA-FINAL>
						<ISBN><xsl:value-of select="@ISBN"/></ISBN>
						<ORGANIZADORES><xsl:value-of select="@ORGANIZADORES"/></ORGANIZADORES>
						<CIDADE-DA-EDITORA><xsl:value-of select="@CIDADE-DA-EDITORA"/></CIDADE-DA-EDITORA>
						<NOME-DA-EDITORA><xsl:value-of select="@NOME-DA-EDITORA"/></NOME-DA-EDITORA>					
					</xsl:for-each>
					<AUTORES>
					<xsl:for-each select="AUTORES">					
						<xsl:value-of select="@NOME-COMPLETO-DO-AUTOR"/>
						<xsl:if test="position()!=last()">, </xsl:if>
						<xsl:if test="position()=last()">.</xsl:if>					
					</xsl:for-each>
					</AUTORES>				
				</CAPITULO-DE-LIVRO-PUBLICADO>
				</xsl:for-each>
			</CAPITULOS-DE-LIVROS-PUBLICADOS>
			</xsl:for-each>
		</LIVROS-E-CAPITULOS>
		</xsl:for-each>
        <xsl:for-each select="//ARTIGOS-PUBLICADOS">
		<ARTIGOS-PUBLICADOS>
			<xsl:for-each select="ARTIGO-PUBLICADO">
				<ARTIGO-PUBLICADO>
					<xsl:for-each select="DADOS-BASICOS-DO-ARTIGO">
						<TITULO><xsl:value-of select="@TITULO-DO-ARTIGO"/></TITULO>
					</xsl:for-each>
					<SEQUENCIA><xsl:value-of select="@SEQUENCIA-PRODUCAO"/></SEQUENCIA>
					<AUTORES>
					<xsl:for-each select="AUTORES">	
							<xsl:value-of select="@NOME-COMPLETO-DO-AUTOR"/>
							<xsl:if test="position()!=last()">, </xsl:if>
							<xsl:if test="position()=last()">.</xsl:if>		
					</xsl:for-each>
					</AUTORES>
					<xsl:for-each select="DETALHAMENTO-DO-ARTIGO">
						<ISSN><xsl:value-of select="@ISSN"/></ISSN>
						<PERIODICO><xsl:value-of select="@TITULO-DO-PERIODICO-OU-REVISTA"/></PERIODICO>
						<PAGINAS><xsl:value-of select="@PAGINA-INICIAL"/>-<xsl:value-of select="@PAGINA-FINAL"/></PAGINAS>
					</xsl:for-each>
					<xsl:for-each select="DADOS-BASICOS-DO-ARTIGO">
						<ANO><xsl:value-of select="@ANO-DO-ARTIGO"/></ANO>
					</xsl:for-each>
				</ARTIGO-PUBLICADO>
			</xsl:for-each>
			</ARTIGOS-PUBLICADOS>
		</xsl:for-each>       
    </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>