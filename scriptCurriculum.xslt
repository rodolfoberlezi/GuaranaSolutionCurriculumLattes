<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:strip-space elements="*"/>
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/> 
	<xsl:template match="/">
		<xsl:for-each select="//Publications">
			<xsl:for-each select="Article">
				<ARTIGO>
					<AUTORES><xsl:value-of select="@Authors"/></AUTORES>
					<TITULO><xsl:value-of select="@Title"/></TITULO>					
					<REVISTA><xsl:value-of select="@Journal"/></REVISTA>
					<ANO><xsl:value-of select="@Year"/></ANO>
					<PAGINAS><xsl:value select="@Pages"/></PAGINAS>
					<VOLUME><xsl:value-of select="@Volume"/></VOLUME>
				</ARTIGO>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>