<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml">
 
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
 
  <xsl:template match="/items">
    <html>
      <head> <title>LendList Export</title> </head>
      <body>
        <h1><a href="https://play.google.com/store/apps/details?id=de.raphaelmichel.lendlist"><xsl:text>LendList</xsl:text></a><xsl:text> Export</xsl:text></h1>
        <table>
			<thead>
				<tr>
					<th>ID</th><th>Direction</th><th>Thing</th><th>Person</th><th>Returned</th><th>Date</th><th>Until</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="item">
					<xsl:sort select="id" />
				</xsl:apply-templates>
			</tbody>
        </table>
      </body>
    </html>
  </xsl:template>
 
  <xsl:template match="item">
	<tr>
		<td><xsl:value-of select="@id" /></td>
		<td><xsl:value-of select="@direction" /></td>
		<td><xsl:value-of select="thing" /></td>
		<td><xsl:value-of select="person" /></td>
		<td><xsl:value-of select="@returned" /></td>
		<td><xsl:value-of select="date" /></td>
		<td><xsl:value-of select="until" /></td>
	</tr>
  </xsl:template>
 
</xsl:stylesheet>
